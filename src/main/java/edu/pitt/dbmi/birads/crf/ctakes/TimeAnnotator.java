package edu.pitt.dbmi.birads.crf.ctakes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.typesystem.type.TimeMonth;

public class TimeAnnotator extends JCasAnnotator_ImplBase {

	//create the patterns
	private Pattern HourPattern = 
			Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm|a.m.|p.m.)", Pattern.CASE_INSENSITIVE);
	private Pattern MonthPattern=
			Pattern.compile("([1-9]|10|11|12)-(\\b(month|year))", Pattern.CASE_INSENSITIVE);
	
	@Override
	  public void process(JCas aJCas) throws AnalysisEngineProcessException {
	    // get document text from JCas
	    String content = aJCas.getDocumentText();
	    
		// search for Hour anchor words
		Matcher matcher = HourPattern.matcher(content);
		int pos = 0;
		while (matcher.find(pos)) {
		      TimeMonth annotation = new TimeMonth(aJCas);
		      annotation.setBegin(matcher.start());
		      annotation.setEnd(matcher.end());
		      annotation.setTime("Time");
		      annotation.addToIndexes();
		      pos = matcher.end();
	    }
	  
	    // search for Month pattern anchor words
		matcher = MonthPattern.matcher(content);
		int pos1 = 0;
		while (matcher.find(pos1)) {
		      // match found - create the match as annotation in 
		      // the JCas with some additional meta information
		      TimeMonth annotation = new TimeMonth(aJCas);
		      annotation.setBegin(matcher.start());
		      annotation.setEnd(matcher.end());
		      annotation.setTime("Time");
		      annotation.addToIndexes();
		      pos1 = matcher.end();
	    }
	  }
	public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
		  return AnalysisEngineFactory.createEngineDescription(TimeAnnotator.class);
		}
}
