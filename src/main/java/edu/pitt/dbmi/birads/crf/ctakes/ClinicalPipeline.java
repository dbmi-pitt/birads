package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
import java.io.IOException;

import org.apache.ctakes.chunker.ae.Chunker;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.contexttokenizer.ae.ContextDependentTokenizerAnnotator;
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

import com.lexicalscope.jewel.cli.CliFactory;

public class ClinicalPipeline {
	
	private ClinicalPipeline() {
	}

	public static void main(final String... args) throws UIMAException,
			IOException {
		final ClinicalPipelineOptions options = CliFactory.parseArguments(ClinicalPipelineOptions.class, args);
		System.out.println(options.getInputDirectory());
		System.out.println(options.getExpertDirectory());
		System.out.println(options.getOutputDirectory());
		runCancerPipeline(options.getInputDirectory(),
				options.getExpertDirectory(),
				options.getOutputDirectory());
	}
	
	public static void runCancerPipeline(final String inputDirectory,
			final String expertDirectory,
			final String outputDirectory) throws UIMAException, IOException {
		final TypeSystemDescription typeSystemDescription = establishTypeSystem();
		final CollectionReader collectionReader = createFilesInDirectoryReader(inputDirectory);
		final AnalysisEngineDescription analysisEngineDescription = getPipelineDescription(typeSystemDescription,expertDirectory);
		final AnalysisEngine xmiWriter = createXMIWriter(outputDirectory);
		runCancerPipeline(collectionReader, analysisEngineDescription,
				xmiWriter);
	}

	public static void runCancerPipeline(
			final CollectionReader collectionReader,
			final AnalysisEngineDescription analysisEngineDescription,
			final AnalysisEngine outputWriter) throws UIMAException,
			IOException {
		establishTypeSystem();	
		AnalysisEngine aae = AnalysisEngineFactory.createEngine(analysisEngineDescription);	
		SimplePipeline.runPipeline(collectionReader,
				aae,
				outputWriter);
	}
	
	private static TypeSystemDescription establishTypeSystem() {
		final File typeSystemFile = new File("desc/types/biradsTypeSystemDescriptor.xml");
		final String typeSystemUri = typeSystemFile.toURI().toString();
		return TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(typeSystemUri);
	}

	public static AnalysisEngineDescription getPipelineDescription(
			TypeSystemDescription typeSystemDescription, final String expertDirectory)
			throws ResourceInitializationException, InvalidXMLException,
			IOException {
		final AggregateBuilder aggregateBuilder = new AggregateBuilder();
		aggregateBuilder.add( SimpleSegmentAnnotator.createAnnotatorDescription() );
		aggregateBuilder.add(SentenceDetector.createAnnotatorDescription());
		aggregateBuilder
				.add(TokenizerAnnotatorPTB.createAnnotatorDescription());
		aggregateBuilder.add(LvgAnnotator.createAnnotatorDescription());
		aggregateBuilder.add(ContextDependentTokenizerAnnotator
				.createAnnotatorDescription());
		aggregateBuilder.add(POSTagger.createAnnotatorDescription());
		aggregateBuilder.add(Chunker.createAnnotatorDescription());
		aggregateBuilder.add(ClinicalPipelineFactory
				.getStandardChunkAdjusterAnnotator());
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BiradsExpertAnnotator.class,
				typeSystemDescription, 
				BiradsExpertAnnotator.PARAM_TEXT_DIRECTORY, expertDirectory));	
		return aggregateBuilder.createAggregateDescription();
	}

	private static CollectionReader createFilesInDirectoryReader(
			final String inputDirectory) throws UIMAException, IOException {
		final String descriptorPath = "../ctakes/ctakes-core/desc/collection_reader/FilesInDirectoryCollectionReader.xml";
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
