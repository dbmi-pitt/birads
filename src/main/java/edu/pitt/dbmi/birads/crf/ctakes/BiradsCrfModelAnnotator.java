package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ctakes.core.resource.FileLocator;
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

import cc.mallet.fst.CRF;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import edu.pitt.dbmi.birads.typesystem.type.Birads;

/**
 * run birads model against a generated model
 * 
 * @author tseytlin
 *
 */
public class BiradsCrfModelAnnotator extends JCasAnnotator_ImplBase {
	protected static final String DEFAULT_MODEL_FILE_NAME = "edu/pitt/dbmi/birads/models/CRF.model";
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public static final String PARAM_MODEL_FILE = "ModelFile";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, description = "Input CRF model file from Mallet", mandatory = false)
	private CRF crfModel;
	private int nBestOption = 1;
	private int cacheSizeOption = 100000;
	
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);
		

		// initialize the CRF model
		try {
			String modelPath = DEFAULT_MODEL_FILE_NAME;
			File modelFile = new File("" + uimaContext.getConfigParameterValue(PARAM_MODEL_FILE));
			if(modelFile.exists()){
				modelPath = modelFile.getAbsolutePath();
			}
				
			//FileInputStream fos = new FileInputStream(modelFile);
			InputStream is = FileLocator.getAsStream(modelPath);
			ObjectInputStream oos = new ObjectInputStream(is);
			// change the CRF line to read my local model
			crfModel = (CRF) oos.readObject();
			oos.close();
			is.close();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// get data as token stream 
		List<Annotation> tokens = BiradsUtils.getTokenAnnotations(jCas);
		List<String> data = BiradsUtils.getTokenStream(tokens);
		
		try {
			List<String> labels = applyModel(crfModel, data);
		
			for(int i=0;i<labels.size();i++){
				// found birads category
				if(BiradsUtils.BIRADS_CATEGORY.equals(labels.get(i))){
					Annotation annat = tokens.get(i);
					logger.info("BiRADS category: "+annat.getCoveredText()+" "+annat.getBegin()+":"+annat.getEnd());
					
					// add new annotation
						Birads category = new Birads(jCas);
					category.setBegin(annat.getBegin());
					category.setEnd(annat.getEnd());
					category.setValue(annat.getCoveredText());
					category.addToIndexes();
				}
			}
			
		
			//System.out.println(result);
		} catch (IOException  |ClassNotFoundException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}

	


	
	/**
	 * run CRF model on data
	 * @param crf
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	
	public List<String> applyModel(CRF crf, List<String> data) throws IOException, ClassNotFoundException {
		List<String> labels = new ArrayList<String>();
		
		// get input pipe pipe from the model
		Pipe p = crf.getInputPipe();
		p.setTargetProcessing(false);
		InstanceList testData = new InstanceList(p);
		testData.addThruPipe(new LineGroupIterator(BiradsUtils.getLinesAsReader(data), Pattern.compile("^\\s*$"), true));
		// go over test data
		for (int i = 0; i < testData.size(); i++) {
			Sequence input = (Sequence) testData.get(i).getData();
			Sequence[] outputs = apply(crf, input, nBestOption);
			for(Sequence out: outputs){
				for(int j=0;j<out.size();j++){
					labels.add(out.get(j).toString());
				}
			}
		}
		return labels;
	}
	
	
	public Sequence[] apply(Transducer model, Sequence input, int k) {
		Sequence[] answers;
		if (k == 1) {
			answers = new Sequence[1];
			answers[0] = model.transduce(input);
		} else {
			MaxLatticeDefault lattice = new MaxLatticeDefault(model, input,
					null, cacheSizeOption);
			answers = lattice.bestOutputSequences(k).toArray(new Sequence[0]);
		}
		return answers;
	}

}
