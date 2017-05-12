package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.ContractionToken;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.PunctuationToken;
import org.apache.ctakes.typesystem.type.syntax.SymbolToken;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.tcas.Annotation;

import edu.pitt.dbmi.birads.typesystem.type.Birads;
import edu.pitt.dbmi.birads.typesystem.type.BiradsAnchor;
import edu.pitt.dbmi.birads.typesystem.type.BodySide;
import edu.pitt.dbmi.birads.typesystem.type.LeftBirads;
import edu.pitt.dbmi.birads.typesystem.type.MultiLateralBirads;
import edu.pitt.dbmi.birads.typesystem.type.NonSpecificBirads;
import edu.pitt.dbmi.birads.typesystem.type.OverAllBirads;
import edu.pitt.dbmi.birads.typesystem.type.RightBirads;
import edu.pitt.dbmi.birads.typesystem.type.TimeMonth;

public class BiradsUtils {
	public static final String BIRADS_CATEGORY = "BiradsCategory";
	
	
	public static Reader getLinesAsReader(Collection<String> lines){
		return new StringReader(getLinesAsText(lines));
	}
	
	public static String getLinesAsText(Collection<String> lines){
		StringBuffer buffer = new StringBuffer();
		for(String l: lines)
			buffer.append(l+"\n");
		return buffer.toString();
	}
	
	/**
	 * get token stream for a given cas
	 * @param jCas
	 * @return
	 */
	public static List<String> getTokenStream(List<Annotation> annotations){
		List<String> data = new ArrayList<String>();

		for(Annotation annotation :annotations){
			String tokenOutput = createTokenString(annotation);
			data.add(tokenOutput.toString());
		}
		
		return data;
	}
	
	public static String getBiRADS_Results(JCas cas){
		StringBuffer str = new StringBuffer();
		String category = "";
		String side = "";
		Iterator<Annotation> it = cas.getAnnotationIndex(Birads.type).iterator();
		while(it.hasNext()){
			Birads birads = (Birads) it.next();
			category = birads.getCoveredText();
			String span = birads.getBegin()+":"+birads.getEnd();
			if(birads instanceof LeftBirads){
				side = "Left";
			}else if(birads instanceof RightBirads){
				side = "Right";
			}else if(birads instanceof OverAllBirads){
				side = "Overall";
			}else if(birads instanceof MultiLateralBirads){
				side = "Bilateral";
			}
			str.append("BiRADS\t"+category+"\t"+side+"\t"+span+"\n");
		}
		return str.toString();
	}
	
	public static String getBiRADS_As_Speadsheet(JCas cas, String delim){
		StringBuffer str = new StringBuffer();
		String id = getDocumentID(cas);
		String category = "";
		String side = "";
	
		Iterator<Annotation> it = cas.getAnnotationIndex(Birads.type).iterator();
		while(it.hasNext()){
			Birads birads = (Birads) it.next();
			category = birads.getCoveredText();
			String span = birads.getBegin()+":"+birads.getEnd();
			if(birads instanceof LeftBirads){
				side = "Left";
			}else if(birads instanceof RightBirads){
				side = "Right";
			}else if(birads instanceof OverAllBirads){
				side = "Overall";
			}else if(birads instanceof MultiLateralBirads){
				side = "Bilateral";
			}
			str.append(id+delim+category+delim+side+delim+span+"\n");
		}
		return str.toString();
	}
	
	public static String getDocumentID(JCas jCas){
		return JCasUtil.selectSingle(jCas, DocumentID.class).getDocumentID();
	}
	
	
	public static int getBiradNumber(Birads birads) {
		// get either value or just covered text
		String value = (birads.getValue() != null)?birads.getValue():birads.getCoveredText();
		return parseIntegerValue(value);
	}
	
	/**
	 * This function attempts to convert vaires types of input into numerical
	 * equivalent
	 */
	public static int parseIntegerValue(String text) {
		int value = 0;

		// try to parse roman numerals
		if (text.matches("[IiVvXx]+")) {
			boolean oneLess = false;
			for (int i = 0; i < text.length(); i++) {
				switch (text.charAt(i)) {
				case 'i':
				case 'I':
					value++;
					oneLess = true;
					break;
				case 'v':
				case 'V':
					value += ((oneLess) ? 3 : 5);
					oneLess = false;
					break;
				case 'x':
				case 'X':
					value += ((oneLess) ? 8 : 10);
					oneLess = false;
					break;
				}
			}

			return value;
		}
		// try to parse words
		if (text.matches("[a-zA-Z]+")) {
			if (text.equalsIgnoreCase("zero"))
				value = 0;
			else if (text.equalsIgnoreCase("one"))
				value = 1;
			else if (text.equalsIgnoreCase("two"))
				value = 2;
			else if (text.equalsIgnoreCase("three"))
				value = 3;
			else if (text.equalsIgnoreCase("four"))
				value = 4;
			else if (text.equalsIgnoreCase("five"))
				value = 5;
			else if (text.equalsIgnoreCase("six"))
				value = 6;
			else if (text.equalsIgnoreCase("seven"))
				value = 7;
			else if (text.equalsIgnoreCase("eight"))
				value = 8;
			else if (text.equalsIgnoreCase("nine"))
				value = 9;
			else if (text.equalsIgnoreCase("ten"))
				value = 10;
			else if (text.equalsIgnoreCase("eleven"))
				value = 11;
			else if (text.equalsIgnoreCase("twelve"))
				value = 12;
			else
				value = 0;

			return value;
		}

		// try to parse regular number
		try {
			value = Integer.parseInt(text.replaceAll("[^\\d]",""));
		} catch (NumberFormatException ex) {
			// ex.printStackTrace();
			return 0;
		}
		return value;
	}
	
	
	/**
	 * list of annotation tokens
	 * @param jCas
	 * @return
	 */
	public static List<Annotation> getTokenAnnotations(JCas jCas){
		List<Annotation> list = new ArrayList<Annotation>();
		
		JFSIndexRepository indexes = jCas.getJFSIndexRepository();
		FSIterator<Annotation> annotItr = indexes.getAnnotationIndex(BaseToken.type).iterator();
		while (annotItr.hasNext()) {
			Annotation annotation = (Annotation) annotItr.next();
			if (!(annotation instanceof BaseToken) & (annotation instanceof IdentifiedAnnotation) & (annotation instanceof Segment)) {
				continue;
			}
			list.add(annotation);
		}
		
		
		return list;
	}
	
	
	
	/**
	 * create token string for annotation
	 * @param annotation
	 * @return
	 */
	public static String createTokenString(Annotation annotation){
		StringBuilder tokenOutput = new StringBuilder();
		tokenOutput.append(extractTokenCoveredText(annotation));
		tokenOutput.append(" ");
		tokenOutput.append(extractSegment(annotation));
		tokenOutput.append(" ");
		tokenOutput.append(extractTokenKind(annotation));
		tokenOutput.append(" ");
		tokenOutput.append(extractRomans(annotation));
		tokenOutput.append(" ");
		// extract body side (didn't work)
		//tokenOutput.append(extractSide(annotation));
		//tokenOutput.append(" ");
		// add the birads anchor annotator
		tokenOutput.append(extractAnchors(annotation));
		tokenOutput.append(" ");
		// add the time anchor annotator
		tokenOutput.append(extractTime(annotation));
		tokenOutput.append(" ");
		tokenOutput.append(extractBiradsClassification(annotation,true));
		//tokenOutput.append(" ");
		//tokenOutput.append(extractBiradsClassification(annotation,false));
		return tokenOutput.toString();
	}
	
	

	private static String extractSide(Annotation annotation) {
		String result = "NoSide";
		final List<BodySide> list = JCasUtil.selectCovering(BodySide.class, annotation);
		if (!list.isEmpty()) {
			result = list.get(0).getSide();
		}
		return result;
	}

	private static Object extractSegment(Annotation annotation) {
		String result = "NoSegment";
		final List<Segment> segmentMentions = new ArrayList<>();
		segmentMentions.addAll(JCasUtil.selectCovering(Segment.class, annotation));
		for (int i = 0; i < segmentMentions.size(); i++) {
			// System.out.println(chunkMentions.get(i).getPreferredText());
			// Segment chunkAnnotation = chunkMentions.get(i);
			// result = chunkAnnotation.getPreferredText();
			result = segmentMentions.get(i).getPreferredText();
		}
		// if (chunkMentions.size() >= 1) {
		// Segment chunkAnnotation = chunkMentions.get(0);
		// result = chunkAnnotation.getPreferredText();
		// }
		// System.out.println(result);
		return result;
	}

	private static Object extractTokenKind(Annotation annotation) {
		return annotation.getClass().getSimpleName();
	}

	private static Object extractRomans(Annotation annotation) {
		String result = "NoRoman";
		final List<IdentifiedAnnotation> romnumMentions = new ArrayList<>();
		romnumMentions.addAll(JCasUtil.selectCovering(IdentifiedAnnotation.class, annotation));
		if (romnumMentions.size() >= 1) {
			IdentifiedAnnotation romanAnnotation = romnumMentions.get(0);
			result = romanAnnotation.getClass().getSimpleName();
		}
		return result;
	}

	// Add the BIRADS anchor extraction
	private static Object extractAnchors(Annotation annotation) {
		String result = "NoAnchor";
		final List<BiradsAnchor> anchorMentions = new ArrayList<>();
		anchorMentions.addAll(JCasUtil.selectCovering(BiradsAnchor.class, annotation));
		if (anchorMentions.size() >= 1) {
			BiradsAnchor anchorAnnotation = anchorMentions.get(0);
			result = anchorAnnotation.getClass().getSimpleName();
		}
		return result;
	}

	// Add the Time anchor extraction
	private static Object extractTime(Annotation annotation) {
		String result = "NoTime";
		final List<TimeMonth> timeMentions = new ArrayList<>();
		timeMentions.addAll(JCasUtil.selectCovering(TimeMonth.class, annotation));
		if (timeMentions.size() >= 1) {
			TimeMonth anchorAnnotation = timeMentions.get(0);
			result = anchorAnnotation.getClass().getSimpleName();
		}
		return result;
	}

	private static String extractTokenCoveredText(Annotation annotation) {
		String result = annotation.getCoveredText();
		if (result == null) {
			result = "";
		}
		if (annotation.getClass().getSimpleName().equals("NewlineToken")) {
			result = "Newline";
		}
		return result;
	}

	@SuppressWarnings("unused")
	private static Object extractBiradsBinaryClassification(Annotation annotation) {
		String result = "no";
		final List<LeftBirads> biradsMentions = new ArrayList<>();
		biradsMentions.addAll(JCasUtil.selectCovering(LeftBirads.class, annotation));
		if (biradsMentions.size() >= 1) {
			result = "yes";
		}
		return result;
	}

	private static  String extractBiradsClassification(Annotation annotation, boolean general) {

		String result = "NoBirads";

		final List<LeftBirads> biradsLeftMentions = new ArrayList<>();
		biradsLeftMentions.addAll(JCasUtil.selectCovering(LeftBirads.class, annotation));
		if (biradsLeftMentions.size() >= 1) {
			result = "LeftBirads";
		}

		final List<RightBirads> biradsRightMentions = new ArrayList<>();
		biradsRightMentions.addAll(JCasUtil.selectCovering(RightBirads.class, annotation));
		if (biradsRightMentions.size() >= 1) {
			result = "RightBirads";
		}

		final List<MultiLateralBirads> biradsMultiLateralMentions = new ArrayList<>();
		biradsMultiLateralMentions.addAll(JCasUtil.selectCovering(MultiLateralBirads.class, annotation));
		if (biradsMultiLateralMentions.size() >= 1) {
			result = "MultiLateralBirads";
		}

		final List<NonSpecificBirads> biradsNonSpecificMentions = new ArrayList<>();
		biradsNonSpecificMentions.addAll(JCasUtil.selectCovering(NonSpecificBirads.class, annotation));
		if (biradsNonSpecificMentions.size() >= 1) {
			result = "NonSpecificBirads";
		}

		final List<OverAllBirads> biradsOverAllMentions = new ArrayList<>();
		biradsOverAllMentions.addAll(JCasUtil.selectCovering(OverAllBirads.class, annotation));
		if (biradsOverAllMentions.size() >= 1) {
			result = "OverAllBirads";
		}
		
		// if in general mode, replace specific birads class with blanket category
		if(general && !"NoBirads".equals(result))
			result = "BiradsCategory";
		

		return result;
	}

	@SuppressWarnings("unused")
	private  static boolean determineNonLookup(BaseToken annotation) {
		return annotation instanceof NewlineToken || annotation instanceof PunctuationToken
				|| annotation instanceof ContractionToken || annotation instanceof SymbolToken;
	}
}
