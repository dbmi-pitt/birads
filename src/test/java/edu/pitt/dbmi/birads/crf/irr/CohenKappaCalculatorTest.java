package edu.pitt.dbmi.birads.crf.irr;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

public class CohenKappaCalculatorTest {

	private String value = "x";

	private static final String[][] entries = {
			{ "Derangement", "Derangement", "22" },
			{ "Derangement", "Dysfunctional", "10" },
			{ "Derangement", "Postural", "2" },
			{ "Dysfunctional", "Derangement", "6" },
			{ "Dysfunctional", "Dysfunctional", "27" },
			{ "Dysfunctional", "Postural", "11" },
			{ "Postural", "Derangement", "2" },
			{ "Postural", "Dysfunctional", "5" },
			{ "Postural", "Postural", "17" } };

	private static final String[][] entriesMissing = { { "DER", "DER", "22" },
			{ "DER", "DYS", "10" }, { "DER", "POS", "2" }, { "DER", "X", "3" },
			{ "DYS", "DER", "6" }, { "DYS", "DYS", "27" },
			{ "DYS", "POS", "11" }, { "DYS", "X", "2" }, { "POS", "DER", "2" },
			{ "POS", "DYS", "5" }, { "POS", "POS", "17" }, { "POS", "X", "3" },
			{ "X", "DER", "3" }, { "X", "DYS", "1" }, { "X", "POS", "6" } };

	@Before
	public void setUp() throws Exception {

	}

	
	@After
	public void tearDown() throws Exception {
		System.out.println("In tearDown");
	}

	@Test
	public final void testKappa() throws Exception {
		
		final CohenKappaCalculator cohenKappaCalculator = new CohenKappaCalculator();

		String expertOneName = "Rebeccaj";
		String expertTwoName = "sec113";
		String expertOneXml = buildXmlForExpert(entries, expertOneName, 0);
		System.out.println(expertOneXml);

		String expertTwoXml = buildXmlForExpert(entries, expertTwoName, 1);
		System.out.println(expertTwoXml);

		File desktop = new File("C:\\Users\\mitchellkj\\Desktop");
		File irrTestsDirectory = FileUtils.createTempDir(desktop, "irr_tests");
		File expertOneFile = FileUtils.createTempFile(expertOneName, ".xml",
				irrTestsDirectory);
		File expertTwoFile = FileUtils.createTempFile(expertTwoName, ".xml",
				irrTestsDirectory);

		FileWriter expertOneWriter = new FileWriter(expertOneFile);
		IOUtils.write(expertOneXml, expertOneWriter);
		expertOneWriter.flush();
		expertOneWriter.close();

		FileWriter expertTwoWriter = new FileWriter(expertTwoFile);
		IOUtils.write(expertTwoXml, expertTwoWriter);
		expertTwoWriter.flush();
		expertTwoWriter.close();

		ExpertDocument expertOneDoc = new ExpertDocument();
		expertOneDoc.setExpert(expertOneName);
		expertOneDoc.setPath(expertOneFile.getAbsolutePath());
		expertOneDoc.setSequence("0");

		ExpertDocument expertTwoDoc = new ExpertDocument();
		expertTwoDoc.setExpert(expertTwoName);
		expertTwoDoc.setPath(expertTwoFile.getAbsolutePath());
		expertTwoDoc.setSequence("0");

		cohenKappaCalculator.setDocumentOne(expertOneDoc);
		cohenKappaCalculator.setDocumentTwo(expertTwoDoc);
		cohenKappaCalculator.accumulate();

		cohenKappaCalculator.computeKappa();
		String cohenResultsAsString = cohenKappaCalculator.toString();
		System.out.println(cohenResultsAsString);
	}

	@Test
	public final void testKappaWithMissing() throws Exception {
		final CohenKappaCalculator cohenKappaCalculator = new CohenKappaCalculator();

		String expertOneName = "Rebeccaj";
		String expertTwoName = "sec113";
		String expertOneXml = buildXmlForExpert(entriesMissing, expertOneName, 0);
		System.out.println(expertOneXml);

		String expertTwoXml = buildXmlForExpert(entriesMissing, expertTwoName, 1);
		System.out.println(expertTwoXml);

		File desktop = new File("C:\\Users\\mitchellkj\\Desktop");
		File irrTestsDirectory = FileUtils.createTempDir(desktop, "irr_tests");
		File expertOneFile = FileUtils.createTempFile(expertOneName, ".xml",
				irrTestsDirectory);
		File expertTwoFile = FileUtils.createTempFile(expertTwoName, ".xml",
				irrTestsDirectory);

		FileWriter expertOneWriter = new FileWriter(expertOneFile);
		IOUtils.write(expertOneXml, expertOneWriter);
		expertOneWriter.flush();
		expertOneWriter.close();

		FileWriter expertTwoWriter = new FileWriter(expertTwoFile);
		IOUtils.write(expertTwoXml, expertTwoWriter);
		expertTwoWriter.flush();
		expertTwoWriter.close();

		ExpertDocument expertOneDoc = new ExpertDocument();
		expertOneDoc.setExpert(expertOneName);
		expertOneDoc.setPath(expertOneFile.getAbsolutePath());
		expertOneDoc.setSequence("0");

		ExpertDocument expertTwoDoc = new ExpertDocument();
		expertTwoDoc.setExpert(expertTwoName);
		expertTwoDoc.setPath(expertTwoFile.getAbsolutePath());
		expertTwoDoc.setSequence("0");

		cohenKappaCalculator.setDocumentOne(expertOneDoc);
		cohenKappaCalculator.setDocumentTwo(expertTwoDoc);
		cohenKappaCalculator.accumulate();

		cohenKappaCalculator.computeKappa();
		String cohenResultsAsString = cohenKappaCalculator.toString();
		System.out.println(cohenResultsAsString);
	}

	private String buildXmlForExpert(String[][] entries, String expertName, int expertIndex) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("\n");
		sb.append("<data>\n");
		sb.append("\t<annotations>\n");
		int sPos = 1;
		int ePos = sPos + 10;
		for (String[] entry : entries) {
			String expertAnnot = entry[expertIndex];
			int numberOfAnnotations = Integer.valueOf(entry[2]).intValue();
			for (int annotationNumber = 0; annotationNumber < numberOfAnnotations; annotationNumber++) {
				sb.append("\t\t<entity>\n");
				sb.append("\t\t\t<id>" + (annotationNumber + 1) + "@doc"
						+ StringUtils.leftPad(annotationNumber + "", 3, "0")
						+ "@" + expertName + "</id>\n");
				sb.append("\t\t\t<span>" + sPos + "," + ePos + "</span>\n");
				sb.append("\t\t\t<type>" + expertAnnot + "</type>\n");
				sb.append("\t\t\t<parentsType>" + "birads" + "</parentsType>\n");
				sb.append("\t\t\t<properties>\n");
				sb.append("\t\t\t</properties>\n");
				sb.append("\t\t</entity>\n");
				sPos = ePos + 1;
				ePos = sPos + 10;
			}
		}
		sb.append("\t</annotations>\n");
		sb.append("</data>\n");
		return sb.toString();
	}


}
