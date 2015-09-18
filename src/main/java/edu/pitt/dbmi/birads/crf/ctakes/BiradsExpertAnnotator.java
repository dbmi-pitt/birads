package edu.pitt.dbmi.birads.crf.ctakes;

import java.io.File;
import java.net.URI;

import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.birads.crf.digestion.Entity;
import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;
import edu.pitt.dbmi.birads.typesystem.type.Birads;
import edu.pitt.dbmi.birads.typesystem.type.LeftBirads;
import edu.pitt.dbmi.birads.typesystem.type.MultiLateralBirads;
import edu.pitt.dbmi.birads.typesystem.type.NonSpecificBirads;
import edu.pitt.dbmi.birads.typesystem.type.OverAllBirads;
import edu.pitt.dbmi.birads.typesystem.type.RightBirads;

public class BiradsExpertAnnotator extends JCasAnnotator_ImplBase {

	// LOG4J logger based on class name
	private Logger logger = Logger.getLogger(getClass().getName());

	public static final String PARAM_TEXT_DIRECTORY = "ExpertDirectory";
	@ConfigurationParameter(name = PARAM_TEXT_DIRECTORY, mandatory = true, description = "directory containing the text files (if DocumentIDs are just filenames); "
			+ "defaults to assuming that DocumentIDs are full file paths")
	private File expertDirectory = null;

	@Override
	public void initialize(UimaContext uimaContext)
			throws ResourceInitializationException {
		super.initialize(uimaContext);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		logger.info(" process(JCas)");

		// String documentId = DocumentIDAnnotationUtil.getDocumentID(jCas);

		URI expertURI = getTextURI(jCas);
		if (expertURI != null) {
			ExpertDocument expertDocument = new ExpertDocument();
			expertDocument.setPath(expertURI.getPath());
			expertDocument.cacheEntities();
			expertDocument.iterate();
			while (expertDocument.hasNext()) {
				Entity entity = expertDocument.next();
				createBirads(jCas, entity.getsPos(), entity.getePos(),
						entity.getType());

			}
		}
	}

	/**
	 * Get the URI that the text in this class was loaded from
	 */
	protected URI getTextURI(JCas jCas) throws AnalysisEngineProcessException {
		String expertPath = JCasUtil.selectSingle(jCas, DocumentID.class)
				.getDocumentID();
		if (expertDirectory != null) {
			expertPath = expertDirectory + File.separator + expertPath
					+ ".birads.Rebeccaj.completed.xml";
		}
		File tmpFile = new File(expertPath);
		URI answer = null;
		if (tmpFile.exists() && tmpFile.isFile()) {
			System.out.println("Found file named " + tmpFile);
			answer = tmpFile.toURI();
		}
		return answer;
	}

	public Annotation createBirads(JCas jCas, int start, int end,
			String biradsType) {
		Birads biradsAnnotation = null;
		switch (biradsType) {
		case "left_value":
			System.out.println("creating LeftBirads");
			biradsAnnotation = new LeftBirads(jCas);
			biradsAnnotation.setBegin(start);
			biradsAnnotation.setEnd(end);
			break;
		case "right_value":
			System.out.println("creating RightBirads");
			biradsAnnotation = new RightBirads(jCas);
			biradsAnnotation.setBegin(start);
			biradsAnnotation.setEnd(end);
			break;
		case "multilateral_value":
			System.out.println("creating MultiLateralBirads");
			biradsAnnotation = new MultiLateralBirads(jCas);	
			biradsAnnotation.setBegin(start);
			biradsAnnotation.setEnd(end);
			break;
		case "nonspecific_value":
			System.out.println("creating NonSpecificBirads");
			biradsAnnotation = new NonSpecificBirads(jCas);
			biradsAnnotation.setBegin(start);
			biradsAnnotation.setEnd(end);
			break;
		case "overall_value":
			System.out.println("creating OverAllBirads");
			biradsAnnotation = new OverAllBirads(jCas);
			biradsAnnotation.setBegin(start);
			biradsAnnotation.setEnd(end);
			break;
		default:
			break;

		}
		if (biradsAnnotation != null) {
			biradsAnnotation.addToIndexes();
		}
		return biradsAnnotation;
	}

	
}
