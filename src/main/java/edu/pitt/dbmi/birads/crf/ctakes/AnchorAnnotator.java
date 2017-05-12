package edu.pitt.dbmi.birads.crf.ctakes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.typesystem.type.BiradsAnchor;


public class AnchorAnnotator extends JCasAnnotator_ImplBase {
	//create the pattern
	private Pattern BiradsAnchorPattern =
			Pattern.compile("(birads|bi-rads|category|bi rads|ACR code|BI-RADS|BI-RAD|ACR BI-RADS|BI RADS|BIRADS|ACR Code|CATEGORY|Category)"//, Pattern.CASE_INSENSITIVE
					);
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
	    // The JCas object is the data object inside UIMA where all the 
	    // information is stored. It contains all annotations created by 
	    // previous annotators, and the document text to be analyzed.
	    
	    // get document text from JCas
		String content = aJCas.getDocumentText();
		
		// search for BI-RADS anchor words
		Matcher matcher = BiradsAnchorPattern.matcher(content);
		int pos = 0;
		while (matcher.find(pos)) {
		      // match found - create the match as annotation in 
		      // the JCas with some additional meta information
		      BiradsAnchor annotation = new BiradsAnchor(aJCas);
		      annotation.setBegin(matcher.start());
		      annotation.setEnd(matcher.end());
		      annotation.setAnchor("Anchor");
		      annotation.addToIndexes();
		      pos = matcher.end();
	}
		
	}
	public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
		  return AnalysisEngineFactory.createEngineDescription(AnchorAnnotator.class);
		}
}
