package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class BiradsResultOutput extends JCasAnnotator_ImplBase {
	private static final String RESULT = "RESULT.tsv";
	private static final String I = "\t";
	
	private Logger logger = Logger.getLogger(getClass().getName());

	public static final String PARAM_OUTPUTDIR = "OutputDirectory";
	@ConfigurationParameter(name = PARAM_OUTPUTDIR, description = "Output directory to BiRADS output", mandatory = true)
	private File outpuDirectory = null;
	private File outputSpreadsheet;
	
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);
		outpuDirectory = new File(""+uimaContext.getConfigParameterValue(PARAM_OUTPUTDIR));
		
		// delete previous spreadsheet
		outputSpreadsheet = new File(outpuDirectory,RESULT);
		if(outputSpreadsheet.exists())
			outputSpreadsheet.delete();
		try {
			writeHeaders(outputSpreadsheet);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	private void writeHeaders(File file) throws IOException {
		String model = "ties.model.";
		String header = "ID"+I+"BIRADS Category"+I+"BIRADS Type"+I+model+"report_offset\n";
		FileUtils.writeStringToFile(file,header);
	}

	/**
	 * process cas
	 */
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// get data from the cas as a token stream
		String data = BiradsUtils.getBiRADS_Results(jCas);

		// save document
		String documentUuid = JCasUtil.selectSingle(jCas, DocumentID.class).getDocumentID();
		documentUuid = documentUuid.replaceAll("\\.txt$", "");
		File document = new File(outpuDirectory, documentUuid + ".txt");
		try {
			FileUtils.writeStringToFile(document, data);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		logger.info("Saved result to " + document.getAbsolutePath());

		// append to spreadsheet
		String spreadsheet_data = BiradsUtils.getBiRADS_As_Speadsheet(jCas,I);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputSpreadsheet,true));
			writer.write(spreadsheet_data);
			writer.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}
	

}
