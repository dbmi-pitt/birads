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

public class XmiReader {

	private final String XMI_INPUT_DIR_PATH = "C:/Users/kjm84/Desktop/birads_reports/production_xmi";
	private final String FTR_OUTPUT_DIR_PATH = "C:/Users/kjm84/Desktop/birads_reports/production_ftr";

	private File ftrsDirectory = null;
	private TypeSystemDescription typeSystemDescription = null;

	private final List<JCas> jCasList = new ArrayList<JCas>();

	private int numberProcessed = 0;
	private int numberToProcess = 15;

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
		cacheXmiFiles();
		createOrReplaceFeaturesDirectory();
		deriveTokenFeaturesForXmi();

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

	private void deriveTokenFeaturesForXmi() throws IOException {
		for (JCas jCas : jCasList) {
			if (numberProcessed < numberToProcess) {
				processJCas(jCas);
				numberProcessed++;
			} else {
				break;
			}
		}

	}

	private void processJCas(JCas jCas) throws IOException {
		
		final List<String> featureLines = new ArrayList<>();
		
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
		}
		
		String documentUuid = JCasUtil.selectSingle(jCas, DocumentID.class)
				.getDocumentID();
		File document = new File(ftrsDirectory, documentUuid + ".txt");
		FileUtils.writeLines(document, featureLines);
		System.out.println("Wrote " + document.getAbsolutePath());
	}

	private Object extractBiradsClassification(Annotation annotation) {
		String result = "no";
		final List<LeftBirads> biradsMentions = new ArrayList<>();
		biradsMentions.addAll(JCasUtil.selectCovering(LeftBirads.class,
				annotation));
		if (biradsMentions.size() >= 1) {
			result = "yes";
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

	// final boolean isNonLookup = annotation instanceof NewlineToken
	// || annotation instanceof PunctuationToken
	// || annotation instanceof ContractionToken
	// || annotation instanceof SymbolToken;
	// if (isNonLookup) {
	// System.out.println("isNonLookup == true");
	// }
	// if (annotation instanceof WordToken) {
	// System.out.println(annotation.getCoveredText());
	// }

	private void cacheXmiFiles() throws UIMAException, IOException {
		File xmiDirectory = new File(XMI_INPUT_DIR_PATH);
		if (xmiDirectory.exists() && xmiDirectory.isDirectory()) {
			File[] xmiFiles = xmiDirectory.listFiles();
			for (File xmiFile : xmiFiles) {
				JCas jCas = loadXmiFileIntoCas(xmiFile);
				jCasList.add(jCas);
			}
		}

	}

	private void establishTypeSystem() {
		final File typeSystemFile = new File(
				"desc/types/biradsTypeSystemDescriptor.xml");
		final String typeSystemUri = typeSystemFile.toURI().toString();
		typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(typeSystemUri);
	}

	/**
	 * load CAS object from XMO
	 * 
	 * @param document
	 * @param typesystem
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	private JCas loadXmiFileIntoCas(File xmiFile) throws UIMAException,
			IOException {
		final JCas jCas = JCasFactory.createJCas(xmiFile.getAbsolutePath(),
				typeSystemDescription);
		return jCas;
	}

}
