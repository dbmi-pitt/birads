package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
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


/**
 * This annotator extract a sequence of tokens from CAS to be consumed by Mallet
 * CRF
 */
public class BiradsTokenOutput extends JCasAnnotator_ImplBase {
	private Logger logger = Logger.getLogger(getClass().getName());

	public static final String PARAM_OUTPUTDIR = "OutputDirectory";
	@ConfigurationParameter(name = PARAM_OUTPUTDIR, description = "Output directory to token sequence files", mandatory = true)
	private File outpuDirectory = null;
	
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);
		outpuDirectory = new File(""+uimaContext.getConfigParameterValue(PARAM_OUTPUTDIR));
	}
	
	/**
	 * process cas
	 */
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// get data from the cas as a token stream
		List<String> data = BiradsUtils.getTokenStream(BiradsUtils.getTokenAnnotations(jCas));

		// save document
		String documentUuid = JCasUtil.selectSingle(jCas, DocumentID.class).getDocumentID();
		documentUuid = documentUuid.replaceAll("\\.txt$", "");
		File document = new File(outpuDirectory, documentUuid + ".txt");
		try {
			FileUtils.writeLines(document, data);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		logger.info("Saved tokens to " + document.getAbsolutePath());

	}
	

}
