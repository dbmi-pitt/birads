package edu.pitt.dbmi.birads.naivebayes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;


import edu.pitt.dbmi.birads.typesystem.type.Line;

public class LineAnnotator extends JCasAnnotator_ImplBase {
	// create regular expression pattern for Line Annotation
	private Pattern linePattern =
			Pattern.compile("^.*.", Pattern.MULTILINE);
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

	    // get document text from JCas
		String content = aJCas.getDocumentText();
		int counter = 1;
		
		// search for BI-RADS anchor words
		Matcher matcher = linePattern.matcher(content);
		int pos = 0;
		while (matcher.find(pos)) {
		      // match found - create the match as annotation in 
		      // the JCas with some additional meta information
		      Line annotation = new Line(aJCas);
		      annotation.setBegin(matcher.start());
		      annotation.setEnd(matcher.end());
		      annotation.setLineNumber(counter);
		      annotation.addToIndexes();
		      pos = matcher.end();
		      counter++;
	}
	}
	public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
		  return AnalysisEngineFactory.createEngineDescription(LineAnnotator.class);
		}
}
