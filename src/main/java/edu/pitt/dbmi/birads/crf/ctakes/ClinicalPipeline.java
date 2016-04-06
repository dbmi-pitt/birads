package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.chunker.ae.Chunker;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.contexttokenizer.ae.ContextDependentTokenizerAnnotator;
import org.apache.ctakes.core.ae.CDASegmentAnnotator;
import org.apache.ctakes.core.ae.SentenceDetector;
import org.apache.ctakes.core.ae.SimpleSegmentAnnotator;
import org.apache.ctakes.core.ae.TokenizerAnnotatorPTB;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.lvg.ae.LvgAnnotator;
import org.apache.ctakes.postagger.POSTagger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;

public class ClinicalPipeline {
	
	private ClinicalPipeline() {
	}

	public static void main(final String... args)  {
		final ClinicalPipelineOptions options = CliFactory.parseArguments(ClinicalPipelineOptions.class, args);
		System.out.println(options.getInputDirectory());
		System.out.println(options.getExpertDirectory());
		System.out.println(options.getCdaSectionsFile());
		System.out.println(options.getOutputDirectory());
		try {
			runCancerPipeline(options.getInputDirectory(),
					options.getExpertDirectory(),
					options.getCdaSectionsFile(),
					options.getOutputDirectory());
		} catch (UIMAException | IOException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	public static void runCancerPipeline(final String inputDirectory,
			final String expertDirectory,
			final String cdaSectionFilePath,
			final String outputDirectory) throws UIMAException, IOException, SAXException {
		final TypeSystemDescription typeSystemDescription = establishTypeSystem(outputDirectory);
		final CollectionReader collectionReader = createFilesInDirectoryReader(inputDirectory);
		final AnalysisEngineDescription analysisEngineDescription = getPipelineDescription(typeSystemDescription,expertDirectory,cdaSectionFilePath);
		final AnalysisEngine xmiWriter = createXMIWriter(outputDirectory);
		runCancerPipeline(collectionReader, analysisEngineDescription,
				xmiWriter, outputDirectory);
	}

	public static void runCancerPipeline(
			final CollectionReader collectionReader,
			final AnalysisEngineDescription analysisEngineDescription,
			final AnalysisEngine outputWriter, String outputDirectory) throws UIMAException,
			IOException, SAXException {
		clearOutputDirectory(outputDirectory);
		establishTypeSystem(outputDirectory);
		writeOutAnalsisEngineDescription(analysisEngineDescription, outputDirectory);
		AnalysisEngine aae = AnalysisEngineFactory.createEngine(analysisEngineDescription);	
		SimplePipeline.runPipeline(collectionReader,
				aae,
				outputWriter);
	}
	
	private static void writeOutAnalsisEngineDescription(final AnalysisEngineDescription analysisEngineDescription, final String outputDirectory) throws SAXException, IOException {
		File outputDirectoryFile = new File(outputDirectory);
		File aaeDescFile = new File(outputDirectoryFile.getParent(), "aaeBirads.xml");
		FileOutputStream fos = new FileOutputStream(aaeDescFile);
		analysisEngineDescription.toXML(fos);
		fos.flush();
		fos.close();
	}

	private static void clearOutputDirectory(String outputDirectory) throws IOException {
		File outputDirectoryFile = new File(outputDirectory);
		if (outputDirectoryFile.exists() && outputDirectoryFile.isDirectory()) {
			FileUtils.cleanDirectory(outputDirectoryFile);
		}
	}

	private static TypeSystemDescription establishTypeSystem(String outputDirectory) throws IOException {
		final File typeSystemFile = new File("desc/types/biradsTypeSystemDescriptor.xml");
		final File copiedSystemFile =  new File(outputDirectory + "/biradsTypeSystemDescriptor.xml");
		boolean preserveFileDate = false;
		FileUtils.copyFile(typeSystemFile, copiedSystemFile, preserveFileDate);
		final String typeSystemUri = typeSystemFile.toURI().toString();
		return TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(typeSystemUri);
	}

	public static AnalysisEngineDescription getPipelineDescription(
			final TypeSystemDescription typeSystemDescription, final String expertDirectory, String cdaSectionFilePath)
			throws ResourceInitializationException, InvalidXMLException,
			IOException {
		final AggregateBuilder aggregateBuilder = new AggregateBuilder();
		aggregateBuilder.add( SimpleSegmentAnnotator.createAnnotatorDescription() );
		if (cdaSectionFilePath != null) {
			aggregateBuilder.add(createCdaSegmentDescription(typeSystemDescription, cdaSectionFilePath));
		}
		aggregateBuilder.add(SentenceDetector.createAnnotatorDescription());
		aggregateBuilder
				.add(BiradsTokenizer.createAnnotatorDescription());
		aggregateBuilder.add(LvgAnnotator.createAnnotatorDescription());
		aggregateBuilder.add(POSTagger.createAnnotatorDescription());
		aggregateBuilder.add(Chunker.createAnnotatorDescription());
		aggregateBuilder.add(ClinicalPipelineFactory
				.getStandardChunkAdjusterAnnotator());
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BiradsExpertAnnotator.class,
				typeSystemDescription, 
				BiradsExpertAnnotator.PARAM_TEXT_DIRECTORY, expertDirectory));	
		return aggregateBuilder.createAggregateDescription();
	}
	
	private static AnalysisEngineDescription createCdaSegmentDescription(TypeSystemDescription typeSystemDescription, String cdaSectionFilePath) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(CDASegmentAnnotator.class,
				typeSystemDescription,
				CDASegmentAnnotator.PARAM_SECTIONS_FILE, cdaSectionFilePath);
	}

	private static CollectionReader createFilesInDirectoryReader(
			final String inputDirectory) throws UIMAException, IOException {
		final String descriptorPath = "desc/collection_reader/FilesInDirectoryCollectionReader.xml";
		return CollectionReaderFactory
				.createReaderFromPath(descriptorPath,
						FilesInDirectoryCollectionReader.PARAM_INPUTDIR,
						inputDirectory);
	}

	private static AnalysisEngine createXMIWriter(final String outputDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(
				XmiWriterCasConsumerCtakes.class,
				XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR, outputDirectory);
	}

}
