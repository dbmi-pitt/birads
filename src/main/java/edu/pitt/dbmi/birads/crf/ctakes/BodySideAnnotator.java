package edu.pitt.dbmi.birads.crf.ctakes;

import java.util.*;
import java.util.regex.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.typesystem.type.BodySide;

public class BodySideAnnotator extends JCasAnnotator_ImplBase {
	private Map<String,Pattern> bodySidePatterns;
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		// initialize patterns
		bodySidePatterns = new LinkedHashMap<String,Pattern>();
		bodySidePatterns.put("Overall",Pattern.compile("\\b(overall)\\b",Pattern.CASE_INSENSITIVE));
		bodySidePatterns.put("Bilateral",Pattern.compile("\\b(bilateral|multilateral|both sides|right and left|left and right)\\b",Pattern.CASE_INSENSITIVE));
		bodySidePatterns.put("Left",Pattern.compile("\\b(left|Lt)\\b",Pattern.CASE_INSENSITIVE));
		bodySidePatterns.put("Right",Pattern.compile("\\b(right|Rt)\\b",Pattern.CASE_INSENSITIVE));
	}

	public void process(JCas cas) throws AnalysisEngineProcessException {
		String text = cas.getDocumentText();
		for(String type: bodySidePatterns.keySet()){
			Pattern pt = bodySidePatterns.get(type);
			Matcher mt = pt.matcher(text);
			while(mt.find()){
				BodySide side = new BodySide(cas);
				side.setSide(type);
				side.setBegin(mt.start());
				side.setEnd(mt.end());
				side.addToIndexes();
			}
		}
	}
}
