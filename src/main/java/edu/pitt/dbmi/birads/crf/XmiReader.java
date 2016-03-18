package edu.pitt.dbmi.birads.crf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.syntax.ContractionToken;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.PunctuationToken;
import org.apache.ctakes.typesystem.type.syntax.SymbolToken;
import org.apache.ctakes.typesystem.type.syntax.WordToken;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.pitt.dbmi.birads.typesystem.type.LeftBirads;
import edu.pitt.dbmi.birads.typesystem.type.MultiLateralBirads;
import edu.pitt.dbmi.birads.typesystem.type.NonSpecificBirads;
import edu.pitt.dbmi.birads.typesystem.type.OverAllBirads;
import edu.pitt.dbmi.birads.typesystem.type.RightBirads;

public class XmiReader {

	private final String XMI_INPUT_DIR_PATH = "C:/Users/kjm84/Desktop/birads_decks/analysis160317/xmi";
	private final String FTR_OUTPUT_DIR_PATH = "C:/Users/kjm84/Desktop/birads_decks/analysis160317/ftr";

	private File ftrsDirectory = null;
	private TypeSystemDescription typeSystemDescription = null;

	public static void main(String[] args) {
		XmiReader xmiReader = new XmiReader();
		xmiReader.execute();
	}

	private void execute() {
		try {
			tryExecute();
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
	}

	private void tryExecute() throws UIMAException, IOException {
		establishTypeSystem();
		createOrReplaceFeaturesDirectory();
		processXmiFiles();
	}
	
	private void processXmiFiles() throws UIMAException, IOException {
		File xmiDirectory = new File(XMI_INPUT_DIR_PATH);
		if (xmiDirectory.exists() && xmiDirectory.isDirectory()) {
			File[] xmiFiles = xmiDirectory.listFiles();
			for (File xmiFile : xmiFiles) {
				JCas jCas = loadXmiFileIntoCas(xmiFile);
				processJCas(jCas);
			}
		}
	}
	
	private void processJCas(JCas jCas) throws IOException {
		
		final List<String> featureLines = new ArrayList<>();
		
		boolean hasBirads = false;
	
		JFSIndexRepository indexes = jCas.getJFSIndexRepository();
		FSIterator<Annotation> annotItr = indexes.getAnnotationIndex(
				BaseToken.type).iterator();
		while (annotItr.hasNext()) {
			Annotation annotation = (Annotation) annotItr.next();
			if (!(annotation instanceof BaseToken)) {
				continue;
			}
			StringBuilder tokenOutput = new StringBuilder();
			tokenOutput.append(extractTokenCoveredText(annotation));
			tokenOutput.append(" ");
			tokenOutput.append(extractTokenCanonicalForm(annotation));
			tokenOutput.append(" ");
			tokenOutput.append(extractTokenPartOfSpeech(annotation));
			tokenOutput.append(" ");
			tokenOutput.append(extractTokenShallowParse(annotation));
			tokenOutput.append(" ");
			tokenOutput.append(extractTokenKind(annotation));
			tokenOutput.append(" ");
			tokenOutput.append(extractBiradsClassification(annotation));
			featureLines.add(tokenOutput.toString());
			
			if (!featureLines.get(featureLines.size()-1).endsWith("NoBirads")) {
				System.out.println(featureLines.get(featureLines.size()-1));
				hasBirads = true;
			}
		}
		
		if (hasBirads) {
			String documentUuid = JCasUtil.selectSingle(jCas, DocumentID.class)
					.getDocumentID();
			documentUuid = documentUuid.replaceAll("\\.txt$", "");
			File document = new File(ftrsDirectory, documentUuid + ".txt");
			FileUtils.writeLines(document, featureLines);
			System.out.println("Wrote " + document.getAbsolutePath());
		}
		
	}

	@SuppressWarnings("unused")
	private Object extractBiradsBinaryClassification(Annotation annotation) {
		String result = "no";
		final List<LeftBirads> biradsMentions = new ArrayList<>();
		biradsMentions.addAll(JCasUtil.selectCovering(LeftBirads.class,
				annotation));
		if (biradsMentions.size() >= 1) {
			result = "yes";
		}
		return result;
	}
	
	private Object extractBiradsClassification(Annotation annotation) {
		
		String result = "NoBirads";
	
		final List<LeftBirads> biradsLeftMentions = new ArrayList<>();
		biradsLeftMentions.addAll(JCasUtil.selectCovering(LeftBirads.class,
				annotation));
		if (biradsLeftMentions.size() >= 1) {
			result = "LeftBirads";
		}
		
		final List<RightBirads> biradsRightMentions = new ArrayList<>();
		biradsRightMentions.addAll(JCasUtil.selectCovering(RightBirads.class,
				annotation));
		if (biradsRightMentions.size() >= 1) {
			result = "RightBirads";
		}
		
		final List<MultiLateralBirads> biradsMultiLateralMentions = new ArrayList<>();
		biradsMultiLateralMentions.addAll(JCasUtil.selectCovering(MultiLateralBirads.class,
				annotation));
		if (biradsMultiLateralMentions.size() >= 1) {
			result = "MultiLateralBirads";
		}
		
		final List<NonSpecificBirads> biradsNonSpecificMentions = new ArrayList<>();
		biradsNonSpecificMentions.addAll(JCasUtil.selectCovering(NonSpecificBirads.class,
				annotation));
		if (biradsNonSpecificMentions.size() >= 1) {
			result = "NonSpecificBirads";
		}
		
		final List<OverAllBirads> biradsOverAllMentions = new ArrayList<>();
		biradsOverAllMentions.addAll(JCasUtil.selectCovering(OverAllBirads.class,
				annotation));
		if (biradsOverAllMentions.size() >= 1) {
			result = "OverAllBirads";
		}
		
		return result;
	}

	private Object extractTokenShallowParse(Annotation annotation) {
		String result = "NoChunk";
		final List<Chunk> chunkMentions = new ArrayList<>();
		chunkMentions.addAll(JCasUtil.selectCovering(Chunk.class, annotation));
		if (chunkMentions.size() >= 1) {
			Chunk chunkAnnotation = chunkMentions.get(0);
			result = chunkAnnotation.getChunkType();
		}
		return result;
	}

	private Object extractTokenKind(Annotation annotation) {
		return annotation.getClass().getSimpleName();
	}

	private String extractTokenPartOfSpeech(Annotation annotation) {
		String result = "NoPos";
		try {
			Class<?> cls = annotation.getClass();
			final Class<?>[] emptySignature = {};
			final Object[] emptyParameters = {};
			Method getPartOfSpeechMethod = cls.getMethod("getPartOfSpeech",
					emptySignature);
			result = (String) getPartOfSpeechMethod.invoke(annotation,
					emptyParameters);
			if (result == null) {
				result = "";
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return result;
	}

	private Object extractTokenCanonicalForm(Annotation annotation) {
		String result = "NoCanonicalForm";
		if (annotation instanceof WordToken) {
			if (((WordToken) annotation).getCanonicalForm() != null) {
				result = ((WordToken) annotation).getCanonicalForm();
			}
		}
		return result;
	}

	private String extractTokenCoveredText(Annotation annotation) {
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
	private boolean determineNonLookup(BaseToken annotation) {
		return annotation instanceof NewlineToken
				|| annotation instanceof PunctuationToken
				|| annotation instanceof ContractionToken
				|| annotation instanceof SymbolToken;
	}

	private void establishTypeSystem() {
		final File typeSystemFile = new File(
				"desc/types/biradsTypeSystemDescriptor.xml");
		final String typeSystemUri = typeSystemFile.toURI().toString();
		typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(typeSystemUri);
	}
	
	private void createOrReplaceFeaturesDirectory() {
		try {
			ftrsDirectory = new File(FTR_OUTPUT_DIR_PATH);
			if (!ftrsDirectory.exists()) {
				ftrsDirectory.mkdir();
			}
			FileUtils.cleanDirectory(ftrsDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JCas loadXmiFileIntoCas(File xmiFile) throws UIMAException,
			IOException {
		final JCas jCas = JCasFactory.createJCas(xmiFile.getAbsolutePath(),
				typeSystemDescription);
		return jCas;
	}

}
