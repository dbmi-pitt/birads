package edu.pitt.dbmi.birads.crf.ctakes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.ContractionToken;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.NumToken;
import org.apache.ctakes.typesystem.type.syntax.PunctuationToken;
import org.apache.ctakes.typesystem.type.syntax.SymbolToken;
import org.apache.ctakes.typesystem.type.syntax.WordToken;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class BiradsTokenizer extends JCasAnnotator_ImplBase {

	// LOG4J logger based on class name
	private Logger logger = Logger.getLogger(getClass().getName());

	private final LinkedList<List<Character>> contigs = new LinkedList<List<Character>>();

	@Override
	public void initialize(UimaContext uimaContext)
			throws ResourceInitializationException {
		super.initialize(uimaContext);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String content = jCas.getDocumentText();
		buildContigStack(content);
		int caretPos = 0;
		int tokenNumber = 0;
		while (!contigs.isEmpty()) {
			List<Character> contig = contigs.poll();
			System.out.println("Got contig of size " + contig.size());
			System.out.println("Token #" + tokenNumber + " => " + StringUtils.join(contig.toArray()));
			int sPos = caretPos;
			int ePos = sPos + contig.size();
			BaseToken t = null;
			if (Character.isLetter(contig.get(0))) {
				t = new WordToken(jCas);
				int capitalization = (Character.isUpperCase(contig.get(0)
						.charValue())) ? 1 : 0;
				((WordToken) t).setCapitalization(capitalization);
			}
			else if (Character.isDigit(contig.get(0))) {
				t = new NumToken(jCas);
				((NumToken) t).setNumType(1);
			}
			else if (isContraction(contig.get(0))) {
				t = new ContractionToken(jCas);
			}
			else if (isPunctuation(contig.get(0))) {
				t = new PunctuationToken(jCas);
			}
			else if (contig.get(0).charValue() == '\n') {
				t = new NewlineToken(jCas);
			}
			else if (!Character.isWhitespace(contig.get(0))) {
				t = new SymbolToken(jCas);
			}
			if (t != null) {
				t.setBegin(sPos);
				t.setEnd(ePos);
				t.setTokenNumber(tokenNumber++);
				jCas.addFsToIndexes(t);
				t = null;
			}
			// Advance the caret and tokenNumber
			caretPos = ePos;
		}
	}
	
	private boolean isContraction(Character ch) {
		boolean isContraction = false;
		isContraction = isContraction || (ch == '\'');
		return isContraction;
	}

	private boolean isPunctuation(Character ch) {
		boolean isPunctuation = false;
		isPunctuation = isPunctuation || (ch == '[');
		isPunctuation = isPunctuation || (ch == ']');
		isPunctuation = isPunctuation || (ch == '(');
		isPunctuation = isPunctuation || (ch == ')');
		isPunctuation = isPunctuation || (ch == '{');
		isPunctuation = isPunctuation || (ch == '}');
		isPunctuation = isPunctuation || (ch == ':');
		isPunctuation = isPunctuation || (ch == ',');
		isPunctuation = isPunctuation || (ch == '-');
		isPunctuation = isPunctuation || (ch == '!');
		isPunctuation = isPunctuation || (ch == '.');
		isPunctuation = isPunctuation || (ch == '?');
		isPunctuation = isPunctuation || (ch == '‘');
		isPunctuation = isPunctuation || (ch == '’');
		isPunctuation = isPunctuation || (ch == '“');
		isPunctuation = isPunctuation || (ch == '”');
		isPunctuation = isPunctuation || (ch == '“');
		isPunctuation = isPunctuation || (ch == '"');
		isPunctuation = isPunctuation || (ch == ';');
		isPunctuation = isPunctuation || (ch == '/');
		isPunctuation = isPunctuation || (ch == '\\');
		return isPunctuation;
	}

	private void buildContigStack(String content) {
		contigs.clear();
		char[] contentChrs = content.toCharArray();
		List<Character> contig = new ArrayList<Character>();
		for (int idx = 0; idx < contentChrs.length; idx++) {
			char nextChr = contentChrs[idx];
			if (contigs.isEmpty()) {
				contig.add(new Character(nextChr));
				contigs.add(contig);
				contig = new ArrayList<Character>();
			} else if (!Character.isLetter(nextChr)
					&& !Character.isDigit(nextChr)
					&& !isWhitespaceCharacter(nextChr)) {
				contig.add(new Character(nextChr));
				contigs.add(contig);
				contig = new ArrayList<Character>();
			} else if (Character.isLetter(nextChr)
					&& Character.isLetter(contigs.getLast().get(0))) {
				contigs.getLast().add(new Character(nextChr));
			} else if (Character.isDigit(nextChr)
					&& Character.isDigit(contigs.getLast().get(0))) {
				contigs.getLast().add(new Character(nextChr));
			} else if (isWhitespaceCharacter(nextChr)
					&& isWhitespaceCharacter(contigs.getLast().get(0))) {
				contigs.getLast().add(new Character(nextChr));
			} else { // Transition character discovered
				contig.add(new Character(nextChr));
				contigs.add(contig);
				contig = new ArrayList<Character>();
			}
		}
	}
	
	private boolean isWhitespaceCharacter(Character ch) {
		return (ch != '\n') && Character.isWhitespace(ch);
	}
	
	public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
		  return AnalysisEngineFactory.createEngineDescription(BiradsTokenizer.class);
		}

}
