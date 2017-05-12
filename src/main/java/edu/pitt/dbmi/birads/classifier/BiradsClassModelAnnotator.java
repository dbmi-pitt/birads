package edu.pitt.dbmi.birads.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.crf.ctakes.BiradsUtils;
import edu.pitt.dbmi.birads.typesystem.type.Birads;
import edu.pitt.dbmi.birads.typesystem.type.BodySide;
import edu.pitt.dbmi.birads.typesystem.type.LeftBirads;
import edu.pitt.dbmi.birads.typesystem.type.Line;
import edu.pitt.dbmi.birads.typesystem.type.MultiLateralBirads;
import edu.pitt.dbmi.birads.typesystem.type.NonSpecificBirads;
import edu.pitt.dbmi.birads.typesystem.type.OverAllBirads;
import edu.pitt.dbmi.birads.typesystem.type.RightBirads;
import weka.classifiers.*;
import weka.classifiers.misc.InputMappedClassifier;
import weka.classifiers.misc.SerializedClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class BiradsClassModelAnnotator extends JCasAnnotator_ImplBase {
	public static final String MARS_EXAM_TYPE = "\\[MARS EXAM_TYPE\\]";
	public static final String MARS_REPORT_SUBTYPE = "\\[MARS REPORT_SUBTYPE\\]";
	public static final String MARS_DX = "\\[MARS DX\\]";
	
	protected static final String DEFAULT_MODEL_FILE_NAME = "edu/pitt/dbmi/birads/models/PART.model"; 
	protected static final String DEFAULT_BOW_FILE_NAME = "edu/pitt/dbmi/birads/models/words.txt";
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public static final String PARAM_MODEL_FILE = "ModelFile";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, description = "Input Baysean model file from Weka", mandatory = false)
	public static final String PARAM_BOW_FILE = "BagOfWordsFile";
	@ConfigurationParameter(name = PARAM_BOW_FILE, description = "Input bag of words file", mandatory = false)
	
	private Classifier model;
	private BiradsClassAttributes attributes;

	
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);
	
		
		try {
			// initialize the  model
			String modelPath = DEFAULT_MODEL_FILE_NAME;
			File modelFile = new File("" + uimaContext.getConfigParameterValue(PARAM_MODEL_FILE));
			if(modelFile.exists()){
				modelPath = modelFile.getAbsolutePath();
			}
			
			// initialize BagOfWords
			String bowPath = DEFAULT_BOW_FILE_NAME;
			File bowFile = new File("" + uimaContext.getConfigParameterValue(PARAM_BOW_FILE));
			if(bowFile.exists()){
				bowPath = bowFile.getAbsolutePath();
			}
				
			logger.info("Loading model "+modelPath);
			model = loadInputMappedClassifier(bowPath,modelPath);
			
			
			
		} catch ( Exception e) {
			throw new ResourceInitializationException(e);
		}
		
		
	}
	
	private InputMappedClassifier loadInputMappedClassifier(String bowPath, String modelPath) throws Exception {
	    InputMappedClassifier inputMappedClassifier = new InputMappedClassifier();
			
		InputStream is = FileLocator.getAsStream(modelPath);
		Object [] objects = SerializationHelper.readAll(is);
		is.close();
		
		Classifier classifier = (Classifier) objects[0];
		Instances instances = (Instances) objects[1];
		
		inputMappedClassifier.setClassifier(classifier);
	    //inputMappedClassifier.setModelPath(modelPath);
	    inputMappedClassifier.setModelHeader(instances);
	    inputMappedClassifier.setDebug(true);
	    
	    
	    //instances = new Insta
	    attributes = new BiradsClassAttributes(instances,FileLocator.getAsStream(bowPath));		
	    
	   
	    return inputMappedClassifier;
	}
	
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// create a Weka Instance object from CAS
		List<Birads> categories = getBiradsCategories(jCas);
		Instances instances = createInstances(attributes,jCas,categories);
		try {
			// for each instance classify a value from an existing model
			int i=0;
			for(Instance instance: instances){
				double value = model.classifyInstance(instance);
				String side = instances.classAttribute().value((int)value);
				
				Birads category = null;
				if("LeftBirads".equals(side)){
					category = new LeftBirads(jCas);
				}else if("RightBirads".equals(side)){
					category = new RightBirads(jCas);
				}else if("OverAllBirads".equals(side)){
					category = new OverAllBirads(jCas);
				}else if("MultiLateralBirads".equals(side)){
					category = new MultiLateralBirads(jCas);
				}else{
					category = new NonSpecificBirads(jCas);
				}
				Annotation annat = categories.get(i++);
				category.setBegin(annat.getBegin());
				category.setEnd(annat.getEnd());
				category.addToIndexes();
				
				// remove a generic birads as it is being replaced by a more specific one
				annat.removeFromIndexes();
				logger.info("BiRADS category: "+annat.getCoveredText()+" "+side+" "+annat.getBegin()+":"+annat.getEnd());
				
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}

	}
	public static List<Birads> getBiradsCategories(JCas jCas) {
		return new ArrayList<Birads>(JCasUtil.select(jCas, Birads.class));
	}
	
	/**
	 * create a set of instances for this document 
	 * @param cas
	 * @param categories
	 * @return
	 */
	public static Instances createInstances(BiradsClassAttributes attributes, JCas cas,List<Birads> categories) {
		Instances data = new Instances("Data", attributes.getAttributeList(),categories.size());
		data.setClass(attributes.getAttribute("BiradsCategory"));
		int count = 1;
		for(Birads birads: categories){
			data.add(createInstance(attributes,cas,birads,count++,categories.size()));
		}
		return data;
	}
	
	
	private static String getReportSection(String type, String documentText) {
		Pattern pt = Pattern.compile(type+"\\s*$(.+?)$",Pattern.MULTILINE|Pattern.DOTALL);
		Matcher mt = pt.matcher(documentText);
		if(mt.find()){
			return mt.group(1);
		}
		return null;
	}

	
	private static  double isLeft(String examType){
		return Pattern.compile("\\b(left|lt?)\\b").matcher(examType.toLowerCase()).find()?1:0;
	}
	private static double isRight(String examType){
		return Pattern.compile("\\b(right|rt?)\\b").matcher(examType.toLowerCase()).find()?1:0;
	}
	

	private static double isUnilateral(String examType) {
		examType = examType.toLowerCase();
		
		boolean bilateral = examType.contains("bilateral");
		boolean unilateral = examType.contains("unilateral");
		boolean left = isLeft(examType) == 1;
		boolean right = isRight(examType) == 1;
		
		// if unilateral and no bilateral => 1
		// if left or right => 1
		// if unilateral and bilateral  => 0.5
		if(unilateral && !bilateral)
			return 1;
		if(left || right)
			return 1;
		if(unilateral && bilateral)
			return 0.5;
		return 0;
	}
	
	private static double isBilateral(String examType) {
		examType = examType.toLowerCase();
		// if routine mamogram => 1
		// if bilateral and no (unilateral, left or right) = > 1
		// if bilateral and unilateral but no left or right => 0.5
		if(examType.contains("routine") && examType.contains("mammogram"))
			return 1;
		if(examType.contains("bilateral")){
			boolean unilateral = examType.contains("unilateral");
			boolean left = isLeft(examType) == 1;
			boolean right = isRight(examType) == 1;
			
			if(!(unilateral || left || right))
				return 1;
			if(unilateral && !left && !right)
				return 0.5;
		}
		
		return 0;
	}
	
	/**
	 * get study type from report subtype and exam type
	 * @param rs
	 * @return
	 */
	private static String getStudyType(String rs) {
		rs = rs.toUpperCase();
		
		// US, MAMMO, MRI, ?
		// if <desc>.contains ("TOMOSYNTHESIS" or "MAMM") => "MAMMO", 
		// else if <desc>.contains("US " or "ULTRASOUND") => "US", 
		// else if <desc>.contains("MRI" or "MAGNETIC") => "MRI" else "?"
		if(rs.contains("MAGNETIC") || rs.contains("MR"))
			return "MRI";
		if(rs.contains("MAMM") || rs.contains("TOMOSYNTHESIS"))
			return "MAMMO";
		if(rs.contains("ULTRASOUND") || Pattern.compile("\\bUS\\b").matcher(rs).find())
			return "US";
	
		return null;
	}
	private static String getFirstOrLast(int count, int total){
		return total == count? "Last": (count == 1 ? "First": "Middle");
	}
	
	private static Set<String> getWords(Annotation birads) {
		Set<String> words = new HashSet<String>();
		for(Line line: JCasUtil.selectCovering(Line.class, birads)){
			for(String w: line.getCoveredText().split("[^A-Za-z\\.]+")){
				words.add(w.toLowerCase().trim());
			}
		}
		return words;
	}
	
	/**
	 * setup instance
	 * @param cas
	 * @param birads
	 * @param attr
	 * @return
	 */
	private static Instance createInstance(BiradsClassAttributes attributes, JCas cas,Birads birads,int count,int total) {
		Instance instance = new DenseInstance(attributes.getAttributeCount());
		
		// do bag of words
		Set<String> instanceWords = getWords(birads);
		for(String word: attributes.getWordList()){
			instance.setValue(attributes.getAttribute(word),instanceWords.contains(word)?1:0);
		}
		
		String examType = getReportSection(MARS_EXAM_TYPE,cas.getDocumentText());
		String dxFooter = getReportSection(MARS_DX,cas.getDocumentText());
		String reportSubtype = examType + " "+ getReportSection(MARS_REPORT_SUBTYPE,cas.getDocumentText());
		String footer = examType+" "+dxFooter;
		
		if(examType != null){
			instance.setValue(attributes.getAttribute("isLeftFROMStudyTypeORFooter"),isLeft(footer));
			instance.setValue(attributes.getAttribute("isRightFROMStudyTypeORFooter"),isRight(footer));
			instance.setValue(attributes.getAttribute("isBilateralFROMStudyTypeORFooter"),isBilateral(footer));
			instance.setValue(attributes.getAttribute("isUnilateralFROMStudyTypeORFooter"),isUnilateral(footer));
		}
	
		instance.setValue(attributes.getAttribute("BiradsNumber"),BiradsUtils.getBiradNumber(birads));
		instance.setValue(attributes.getAttribute("BiradsCounter"),count);
		instance.setValue(attributes.getAttribute("TotalNumBirad"),total);
		
		String st = getStudyType(reportSubtype);
		if(st == null && dxFooter != null)
			st = getStudyType(dxFooter);
		if(st != null)
			instance.setValue(attributes.getAttribute("StudyTypeFromName"),st);
		
		instance.setValue(attributes.getAttribute("IfOneThenIsLastBiradsRank"),getFirstOrLast(count, total));
	
		
		// get nearest body side
		/*String nearestSide = calculateSide(birads);
		if(nearestSide != null)
			instance.setValue(attributes.getAttribute("NearestSide"),nearestSide);*/
		
		// get left count
		instance.setValue(attributes.getAttribute("LeftCount"),count(cas,"Left"));
		instance.setValue(attributes.getAttribute("RightCount"),count(cas,"Right"));
		instance.setValue(attributes.getAttribute("BilateralCount"),count(cas,"Bilateral"));
		
		// if class is known at this point
		String side = birads.getClass().getSimpleName();
		if(!Birads.class.getSimpleName().equals(side)){
			instance.setValue(attributes.getAttribute("BiradsCategory"),side);
		}
		
		return instance;
	}
	
	private static int count(JCas cas, String side){
		int count = 0;
		for(BodySide s: JCasUtil.select(cas,BodySide.class)){
			if(side.equalsIgnoreCase(s.getSide()))
				count ++;
		}
		
		return count;
	}
	
	
	private static String calculateSide(Annotation annotation) {
		// get a segment where this annotation is mentioned
		Segment segment = null;
		for(Segment s: JCasUtil.selectCovering(Segment.class, annotation)){
			// skip segments that cover the entire document
			if(s.getBegin() == 0 && s.getEnd() == s.getCAS().getDocumentText().length())
				continue;
			segment = s;
		}
		if(segment != null){
			// select nearest annotation in this segment
			BodySide side = getNearestBodySide(JCasUtil.selectCovered(BodySide.class,segment), annotation);
			// if nothing is selected, try to get the nearest side before the segment
			if(side == null){
				side = getNearestBodySide(JCasUtil.selectPreceding(BodySide.class,segment,1), annotation);
			}
			if (side != null)
				return side.getSide();
		}
		return null;
	}
	
	private static BodySide getNearestBodySide(Collection<BodySide> list,Annotation annotation){
		int distance = Integer.MAX_VALUE;
		BodySide side = null;
		for(BodySide ss: list){
			int d = Math.abs(annotation.getBegin()-ss.getBegin());
			// only if distance is shortest AND it is not after exam type
			if(d < distance && !afterExamType(ss)){
				distance = d;
				side = ss;
			}
		}
		return side;
	}
	/**
	 * this is a temporary fix for the fact that MARS EXAM_TYPE that is hard-coded by TIES contains info
	 * that should not be in a segment in the first place
	 * @param ss
	 * @return
	 */
	private static boolean afterExamType(BodySide side) {
		int marsExamTypeOffset = Integer.MAX_VALUE;
		Pattern pt = Pattern.compile(MARS_EXAM_TYPE);
		Matcher mt = pt.matcher(side.getCAS().getDocumentText());
		if(mt.find()){
			marsExamTypeOffset = mt.start();
		}
		return marsExamTypeOffset < side.getBegin();
	}

	
}
