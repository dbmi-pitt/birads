package edu.pitt.dbmi.birads;

import java.io.File;
import java.io.IOException;

import org.apache.ctakes.chunker.ae.adjuster.ChunkAdjuster;
import org.apache.ctakes.contexttokenizer.ae.ContextDependentTokenizerAnnotator;
import org.apache.ctakes.core.ae.CDASegmentAnnotator;
import org.apache.ctakes.core.ae.CDASegmentAnnotator_S;
import org.apache.ctakes.core.ae.SentenceDetector;
import org.apache.ctakes.core.ae.SimpleSegmentAnnotator;
import org.apache.ctakes.core.ae.TokenizerAnnotatorPTB_S;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
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
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.birads.classifier.BiradsClassDatasetOutput;
import edu.pitt.dbmi.birads.classifier.BiradsClassModelAnnotator;
import edu.pitt.dbmi.birads.crf.ctakes.AnchorAnnotator;
import edu.pitt.dbmi.birads.crf.ctakes.BiradsExpertAnnotator;
import edu.pitt.dbmi.birads.crf.ctakes.BiradsCrfModelAnnotator;
import edu.pitt.dbmi.birads.crf.ctakes.BiradsResultOutput;
import edu.pitt.dbmi.birads.crf.ctakes.BiradsTokenOutput;
import edu.pitt.dbmi.birads.crf.ctakes.BodySideAnnotator;
import edu.pitt.dbmi.birads.crf.ctakes.TimeAnnotator;
import edu.pitt.dbmi.birads.naivebayes.LineAnnotator;

public class BiRADS_AnnotationPipeline {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		String inputDirectory= "/input/text";
		String outputDirectory= "/output";
		String expertDirectory= null;
		
		if(args.length == 2) {
			inputDirectory= args[0];
			outputDirectory= args[1];
		}else if(args.length == 4 && "-train".equals(args[0])){
			expertDirectory = args[1];
			inputDirectory= args[2];
			outputDirectory= args[3];
		}else{
			if(!new File(inputDirectory).exists()){
				System.err.println("Usage: java -jar BiRADS_Extractor.jar <input directory> <output directory>");
				System.err.println("Usage: java -jar BiRADS_Extractor.jar -train <annotation dir> <input dir> <output dir>");
				return ;
			}
		}

		
		// run to execute a model
		if(expertDirectory == null) {
			runModelPipeline(inputDirectory, outputDirectory);
		}else {
			runTrainPipeline(inputDirectory, expertDirectory, outputDirectory);
		}
		System.out.println("\ndone");
	}

	
	
	/**
	 * run train pipeline
	 * @param inputDirectory
	 * @param expertDirectory
	 * @param tokenDirectory
	 * @param outputDirectory
	 * @throws UIMAException
	 * @throws IOException
	 * @throws SAXException
	 */
	
	public static void runTrainPipeline(String inputDirectory,	String expertDirectory, String outputDirectory) throws UIMAException, IOException, SAXException {
		
		//final TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(); //establishTypeSystem(outputDirectory);
		final CollectionReader collectionReader = CollectionReaderFactory.createReader(FilesInDirectoryCollectionReader.class,FilesInDirectoryCollectionReader.PARAM_INPUTDIR, inputDirectory); 
		final AnalysisEngine pipeline = AnalysisEngineFactory.createEngine(getPipelineDescription(expertDirectory,null,null));
		
		// output tokens
		final AnalysisEngine tokenOutput = AnalysisEngineFactory.createEngine(BiradsTokenOutput.class,BiradsTokenOutput.PARAM_OUTPUTDIR, outputDirectory+File.separator+"tokens");
		final AnalysisEngine dataOutput = AnalysisEngineFactory.createEngine(BiradsClassDatasetOutput.class,BiradsClassDatasetOutput.PARAM_OUTPUTDIR, outputDirectory+File.separator+"spreadsheet");
		final AnalysisEngine xmiWriter = AnalysisEngineFactory.createEngine(XmiWriterCasConsumerCtakes.class,XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR, outputDirectory+File.separator+"xmi");
		final AnalysisEngine resultOutput = AnalysisEngineFactory.createEngine(BiradsResultOutput.class,BiradsResultOutput.PARAM_OUTPUTDIR, outputDirectory);
		
		// run the pipeline to train a model
		SimplePipeline.runPipeline(collectionReader,pipeline,xmiWriter,tokenOutput,dataOutput,resultOutput);
	}
	
	/**
	 * run train pipeline
	 * @param inputDirectory
	 * @param expertDirectory
	 * @param tokenDirectory
	 * @param outputDirectory
	 * @throws UIMAException
	 * @throws IOException
	 * @throws SAXException
	 */
	
	public static void runModelPipeline(String inputDirectory, String outputDirectory) throws UIMAException, IOException, SAXException {
		
		//final TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(); //establishTypeSystem(outputDirectory);
		final CollectionReader collectionReader = CollectionReaderFactory.createReader(FilesInDirectoryCollectionReader.class,FilesInDirectoryCollectionReader.PARAM_INPUTDIR, inputDirectory); 
		
		final AnalysisEngine pipeline = AnalysisEngineFactory.createEngine(getPipelineDescription(null,null,null));
		
		//final AnalysisEngine xmiWriter = AnalysisEngineFactory.createEngine(XmiWriterCasConsumerCtakes.class,XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR, outputDirectory+File.separator+"xmi");
		final AnalysisEngine resultOutput = AnalysisEngineFactory.createEngine(BiradsResultOutput.class,BiradsResultOutput.PARAM_OUTPUTDIR, outputDirectory);
		//final AnalysisEngine dataOutput = AnalysisEngineFactory.createEngine(BiradsClassDatasetOutput.class,BiradsClassDatasetOutput.PARAM_OUTPUTDIR, outputDirectory+File.separator+"spreadsheet");
		
		// run the pipeline to train a model
		SimplePipeline.runPipeline(collectionReader,pipeline,resultOutput);
	}
	
	
	
	
	/**
	 * create a pipeline to run CRF
	 * @param typeSystemDescription
	 * @param expertDirectory
	 * @param cdaSectionFilePath
	 * @return
	 * @throws ResourceInitializationException
	 * @throws InvalidXMLException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription getPipelineDescription(final String expertDirectory,final String modelFile, final String classModel) 	
			throws ResourceInitializationException, InvalidXMLException, IOException {
		
		final AggregateBuilder aggregateBuilder = new AggregateBuilder();
		
		aggregateBuilder.add( SimpleSegmentAnnotator.createAnnotatorDescription() );
		//aggregate the BiradsAnchorAnnotator
		aggregateBuilder.add(AnchorAnnotator.createAnnotatorDescription());
		//aggregate the TimeAnchorAnnottator
		aggregateBuilder.add(TimeAnnotator.createAnnotatorDescription());
		aggregateBuilder.add(SentenceDetector.createAnnotatorDescription());
		aggregateBuilder.add(TokenizerAnnotatorPTB_S.createAnnotatorDescription());
		aggregateBuilder.add(ContextDependentTokenizerAnnotator.createAnnotatorDescription());
		aggregateBuilder.add(LineAnnotator.createAnnotatorDescription());
		
		// option section annotations
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CDASegmentAnnotator_S.class));
		//,CDASegmentAnnotator_S.PARAM_SECTIONS_FILE, "/home/tseytlin/Data/BiRADS/ccda_sections.txt"
		
		aggregateBuilder.add(getStandardChunkAdjusterAnnotator());
		
		// add body side annotator
		aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BodySideAnnotator.class));
		
		
		// optional expert annotation (if we need to train a model)
		if(expertDirectory != null){
			aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BiradsExpertAnnotator.class,BiradsExpertAnnotator.PARAM_TEXT_DIRECTORY, expertDirectory));
		}else {
			// now if we have a model
			aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BiradsCrfModelAnnotator.class,BiradsCrfModelAnnotator.PARAM_MODEL_FILE, modelFile));
			aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(BiradsClassModelAnnotator.class,BiradsClassModelAnnotator.PARAM_MODEL_FILE,classModel));
		}
		
		return aggregateBuilder.createAggregateDescription();
	}
	
	public static AnalysisEngineDescription getStandardChunkAdjusterAnnotator() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(ChunkAdjuster.createAnnotatorDescription(new String[] { "NP", "NP" }, 1), new String[0]);
		builder.add(ChunkAdjuster.createAnnotatorDescription(new String[] { "NP", "PP", "NP" }, 2),	new String[0]);
		return builder.createAggregateDescription();
	}
}
