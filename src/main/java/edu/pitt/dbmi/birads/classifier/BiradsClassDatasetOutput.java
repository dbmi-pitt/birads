package edu.pitt.dbmi.birads.classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.crf.ctakes.BiradsUtils;
import edu.pitt.dbmi.birads.typesystem.type.Birads;
import weka.core.Instance;
import weka.core.Instances;


/**
 * This annotator extract a sequence of tokens from CAS to be consumed by Mallet
 * CRF
 */
public class BiradsClassDatasetOutput extends JCasAnnotator_ImplBase {
	private Logger logger = Logger.getLogger(getClass().getName());
	protected static final String DEFAULT_BOW_FILE_NAME = "edu/pitt/dbmi/birads/models/words.txt";
	public static final String PARAM_OUTPUTDIR = "OutputDirectory";
	@ConfigurationParameter(name = PARAM_OUTPUTDIR, description = "Output directory as a apreadsheet", mandatory = true)
	private File outpuDirectory = null;
	public static final String PARAM_BOW_FILE = "BagOfWordsFile";
	@ConfigurationParameter(name = PARAM_BOW_FILE, description = "Input bag of words file", mandatory = false)
	private BiradsClassAttributes attributes;

	
	
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);
		outpuDirectory = new File(""+uimaContext.getConfigParameterValue(PARAM_OUTPUTDIR));
		
		// initialize BagOfWords
		String bowPath = DEFAULT_BOW_FILE_NAME;
		File bowFile = new File("" + uimaContext.getConfigParameterValue(PARAM_BOW_FILE));
		if(bowFile.exists()){
			bowPath = bowFile.getAbsolutePath();
		}
			
		try {
			attributes = new BiradsClassAttributes(FileLocator.getAsStream(bowPath));
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}		
	}
	
	/**
	 * process cas
	 */
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String docId = JCasUtil.selectSingle(jCas, DocumentID.class).getDocumentID();
		List<String> lines = new ArrayList<String>();
		
		List<Birads> categories = BiradsClassModelAnnotator.getBiradsCategories(jCas);
		Instances instances = BiradsClassModelAnnotator.createInstances(attributes,jCas,categories);
		for(Instance instance: instances){
			lines.add(docId+","+instance);
		}
		
		// save header
		File header = new File(outpuDirectory,"HEADER.txt");
		try {
			StringBuffer str = new StringBuffer();
			str.append("DocumentID");
			for(String a: attributes.getAttributeNames()){
				str.append(","+a);
			}
			FileUtils.writeLines(header,Arrays.asList(str));
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		// save document
		File document = new File(outpuDirectory, docId);
		try {
			FileUtils.writeLines(document,lines);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		logger.info("Saved tokens to " + document.getAbsolutePath());
	}
}
	