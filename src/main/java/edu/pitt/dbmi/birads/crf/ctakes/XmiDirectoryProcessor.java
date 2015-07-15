package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import com.lexicalscope.jewel.cli.CliFactory;

public class XmiDirectoryProcessor {

	private String inputDirectoryPath;
	private String outputDirectoryPath;
	private String typeSystemXmlPath;
	
	private TypeSystemDescription typeSystemDescription;

	private final TreeSet<XmiDirectoryProcessorNode> baseTokenSorter = new TreeSet<XmiDirectoryProcessorNode>();
	private final HashMap<String, XmiDirectoryProcessorNode> baseTokenFinder = new HashMap<String, XmiDirectoryProcessorNode>();

	private Map<BaseToken, Collection<Chunk>> chunkMap;
	
	public static void main(String[] args) {
		try {
			final XmiDirectoryProcessorOptions options = CliFactory
					.parseArguments(XmiDirectoryProcessorOptions.class, args);
			XmiDirectoryProcessor xmiDirectoryProcessor = new XmiDirectoryProcessor();
			xmiDirectoryProcessor.setInputDirectoryPath(options
					.getInputDirectoryPath());
			xmiDirectoryProcessor.setOutputDirectoryPath(options
					.getOutputDirectoryPath());
			xmiDirectoryProcessor.setTypeSystemXmlPath(options
					.getTypeSystemXmlPath());
			xmiDirectoryProcessor.execute();
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
	}

	private void execute() throws UIMAException, IOException {
		intializeTypeSystemDescription();
		final File xmiInputDirectory = new File(getInputDirectoryPath());
		final File[] xmiInputFiles = xmiInputDirectory.listFiles();
		for (File xmiInputFile : xmiInputFiles) {
			System.out.println("\nProcessing " + xmiInputFile.getAbsolutePath()
					+ "\n");
			processXmiFile(xmiInputFile);
		}
	}
	
	private void intializeTypeSystemDescription() {
		final File typeSystemXmlFile = new File(getTypeSystemXmlPath());
		typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(typeSystemXmlFile.toURI()
						.toString());
		
	}

	private void processXmiFile(File xmiInputFile) throws UIMAException, IOException {
		JCas jcas = JCasFactory.createJCas(xmiInputFile.getAbsolutePath(),
				typeSystemDescription);
		
		chunkMap = JCasUtil.indexCovering(jcas, BaseToken.class, Chunk.class); 
		
		Collection<BaseToken> baseTokens = JCasUtil.select(jcas,
				BaseToken.class);
		for (BaseToken baseToken : baseTokens) {
			if (isNonSpaceBaseToken(baseToken)) {
				createAndStoreBaseToken(baseToken);
			}
		}
	}

	private boolean isNonSpaceBaseToken(BaseToken baseToken) {
		return !baseToken.getCoveredText().matches("\\s+");
	}

	private void createAndStoreBaseToken(BaseToken baseToken) {
		XmiDirectoryProcessorNode xmiNode = new XmiDirectoryProcessorNode();
		xmiNode.setBegin(baseToken.getBegin());
		xmiNode.setEnd(baseToken.getEnd());
		xmiNode.setCoveredText(baseToken.getCoveredText());
		xmiNode.setNormalizedForm(baseToken.getNormalizedForm());
		xmiNode.setPartOfSpeech(baseToken.getPartOfSpeech());
		xmiNode.addValue(baseToken.getCoveredText());
		if (baseToken.getNormalizedForm() != null) {
			xmiNode.addValue(baseToken.getNormalizedForm());
		}
		if (baseToken.getPartOfSpeech() != null) {
			xmiNode.addValue(baseToken.getPartOfSpeech());
		}
		xmiNode.setChunkType(getChunkTypeForBaseToken(baseToken));
		baseTokenSorter.add(xmiNode);
		baseTokenFinder.put(xmiNode.getKey(), xmiNode);
	}
	
	private String getChunkTypeForBaseToken(BaseToken baseToken) {
		String chunkType = "NoChunk";
		Collection<Chunk> chunks = chunkMap.get(baseToken);
		if (chunks != null && chunks.size() > 0) {
			Chunk resultChunk = chunks.iterator().next();
			chunkType = resultChunk.getChunkType();
		}
		return chunkType;
	}

	public String getInputDirectoryPath() {
		return inputDirectoryPath;
	}

	public void setInputDirectoryPath(String inputDirectoryPath) {
		this.inputDirectoryPath = inputDirectoryPath;
	}

	public String getOutputDirectoryPath() {
		return outputDirectoryPath;
	}

	public void setOutputDirectoryPath(String outputDirectoryPath) {
		this.outputDirectoryPath = outputDirectoryPath;
	}

	public String getTypeSystemXmlPath() {
		return typeSystemXmlPath;
	}

	public void setTypeSystemXmlPath(String typeSystemXmlPath) {
		this.typeSystemXmlPath = typeSystemXmlPath;
	}

}
