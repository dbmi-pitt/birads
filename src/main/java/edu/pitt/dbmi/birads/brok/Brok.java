package edu.pitt.dbmi.birads.brok;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.pitt.dbmi.birads.util.BrokLogger;

public class Brok {

	// The working directory is whatever directory you run BROK from. This could
	// be specified to a unique place, if desired.

	private static final String fileDirectory = "C:\\Users\\mitchellkj\\Desktop\\birad_reports";

	// input and output files

	private static final String input = fileDirectory + "\\brokIn.txt";

	private static final String output = fileDirectory + "\\brokOut.csv";

	// map that uses the accession number key and the value as the report

	private static Map<String, Report> accessionToReport = new HashMap<String, Report>();

	private static Pattern digitalAccessionNumberPattern;
	private static Pattern alphaNumericAccession;
	private static Pattern patternReportEnd;
	private static Pattern patternBeginImpression;
	private static Pattern patternEndImpression;

	// These are total birads counters that are not used in final outputs.
	// They are here for historical reasons and only included in the log file.
	private static int examsWithAtLeastOneBIRADS = 0;
	private static int totalBIRADS0 = 0;
	private static int totalBIRADS1 = 0;
	private static int totalBIRADS2 = 0;
	private static int totalBIRADS3 = 0;
	private static int totalBIRADS4 = 0;
	private static int totalBIRADS5 = 0;
	private static int totalBIRADS6 = 0;
	private static int totalBogusBIRADS = 0;

	// THIS IS THE MAIN METHOD THAT ACTUALLY HAPPENS WHEN THE
	// PROGRAM IS RUN. ABOVE ARE METHODS CALLED IN THIS MAIN METHOD.

	public static final void main(String arg[]) throws IOException {

		definePatterns();

		File file = new File(input);
		if (file.exists()) {
			accessionSectionAndCacheReport(file);
		}

		final ArrayList<String> sortedAccessionNumbers = new ArrayList<String>();
		sortedAccessionNumbers.addAll(accessionToReport.keySet());
		Collections.sort(sortedAccessionNumbers);
		
		for (String accession : sortedAccessionNumbers) {
			processingPassOne(accession);
		}

		for (String accession : sortedAccessionNumbers) {
			processingPassTwo(accession);
		}

		for (String accession : sortedAccessionNumbers) {

			Report report = accessionToReport.get(accession);

			int leftBIRADS = report.getLeftBIRADS();

			int rightBIRADS = report.getRightBIRADS();

			BrokLogger.getInstance().log("Left: " + leftBIRADS + "\tRight: " + rightBIRADS);

			if (leftBIRADS != -1 || rightBIRADS != -1)
				examsWithAtLeastOneBIRADS++;

			if (leftBIRADS == 0)
				totalBIRADS0++;

			if (rightBIRADS == 0)
				totalBIRADS0++;

			if (leftBIRADS == 1)
				totalBIRADS1++;

			if (rightBIRADS == 1)
				totalBIRADS1++;

			if (leftBIRADS == 2)
				totalBIRADS2++;

			if (rightBIRADS == 2)
				totalBIRADS2++;

			if (leftBIRADS == 3)
				totalBIRADS3++;

			if (rightBIRADS == 3)
				totalBIRADS3++;

			if (leftBIRADS == 4)
				totalBIRADS4++;

			if (rightBIRADS == 4)
				totalBIRADS4++;

			if (leftBIRADS == 5)
				totalBIRADS5++;

			if (rightBIRADS == 5)
				totalBIRADS5++;

			if (leftBIRADS == 6)
				totalBIRADS6++;

			if (rightBIRADS == 6)
				totalBIRADS6++;

			if (leftBIRADS > 6)
				totalBogusBIRADS++;

			if (rightBIRADS > 6)
				totalBogusBIRADS++;

		}

		BrokLogger.getInstance().log("Total accession numbers found: " + accessionToReport.size());

		BrokLogger.getInstance().log("Accessions with at least one BI-RADS: "
				+ examsWithAtLeastOneBIRADS);

		BrokLogger.getInstance().log("BI-RADS 0: " + totalBIRADS0);

		BrokLogger.getInstance().log("BI-RADS 1: " + totalBIRADS1);

		BrokLogger.getInstance().log("BI-RADS 2: " + totalBIRADS2);

		BrokLogger.getInstance().log("BI-RADS 3: " + totalBIRADS3);

		BrokLogger.getInstance().log("BI-RADS 4: " + totalBIRADS4);

		BrokLogger.getInstance().log("BI-RADS 5: " + totalBIRADS5);

		BrokLogger.getInstance().log("BI-RADS 6: " + totalBIRADS6);

		BrokLogger.getInstance().log("Invalid BI-RADS: " + totalBogusBIRADS);

		// "Result" gets written into the output. These are the headers of the
		// output file.

		result("Accession,Left,Right");

		// For every accession number/report, the output happens.

		for (String accession : sortedAccessionNumbers) {

			Report report = accessionToReport.get(accession);

			// leftResult and rightResult are the actual output in the output
			// file.

			// If no birads was found ("-1"), then report "none". Otherwise, the
			// found
			// number is given. The subcategory a, b, or c is also given, if it
			// was found. This is the default state
			// with none or what is found being given.

			String leftResult = (report.getLeftBIRADS() == -1 ? "none" : report
					.getLeftBIRADS())
					+ (report.getLeftSubCategory() == null ? "" : report
							.getLeftSubCategory());

			String rightResult = (report.getRightBIRADS() == -1 ? "none"
					: report.getRightBIRADS())
					+ (report.getRightSubCategory() == null ? "" : report
							.getRightSubCategory());

			// Addendum will over-ride everything.

			if (report.hasAddendum()) {

				rightResult = "addendum";

				leftResult = "addendum";

				// Otherwise. . .

			} else {

				// If there are multiple mentions of right or left birads, the
				// report is
				// flagged as such. This is based on the counter
				// (getFound(RightLeftBilateral)BIRADS above.

				if (report.getFoundRightBIRADS() > 1) {

					rightResult = "multiple right BIRADS found";

				}

				// Same for left.

				if (report.getFoundLeftBIRADS() > 1) {

					leftResult = "multiple left BIRADS found";

				}

				// Same for bilateral.

				if (report.getFoundBilateralBIRADS() > 1) {

					leftResult = "multiple bilateral BIRADS found";

					rightResult = "multiple bilateral BIRADS found";

					// If one bilateral BIRADS found, then make sure it doesn't
					// conflict
					// with any right and left BIRADS. If there is a conflict,
					// it is flagged as inconsistent.

				} else if (report.getFoundBilateralBIRADS() > 0) {

					if (report.getLeftBIRADS() != -1
							&& report.getLeftBIRADS() != report
									.getBilateralBIRADS()) {

						leftResult = "conflict with bilateral/overall/combined BIRADS";

					} else if (report.getLeftSubCategory() != null
							&& !report.getLeftSubCategory().equals(
									report.getBilateralSubCategory())) {

						leftResult = "conflict with bilateral/overall/combined BIRADS";

					} else {

						leftResult = report.getBilateralBIRADS()
								+

								(report.getBilateralSubCategory() == null ? ""
										: report.getBilateralSubCategory());

					}

					if (report.getRightBIRADS() != -1
							&& report.getRightBIRADS() != report
									.getBilateralBIRADS()) {

						rightResult = "conflict with bilateral/overall/combined BIRADS";

					} else if (report.getRightSubCategory() != null
							&& !report.getRightSubCategory().equals(
									report.getBilateralSubCategory())) {

						rightResult = "conflict with bilateral/overall/combined BIRADS";

					} else {

						rightResult = report.getBilateralBIRADS()
								+

								(report.getBilateralSubCategory() == null ? ""
										: report.getBilateralSubCategory());

					}

				}

				// this block of code uses laterality detection to set number of
				// breasts imaged

				// then this info is used below to figure out if we should
				// consider a non-reported breast
				// as not imaged or imaged, but not reported
				// HOWEVER, because almost all imaged breasts are reported, it
				// works better to set this to 1 without actually checking

				// NOW TRYING THIS BUT USING ONLY IMPRESSION TEXT

				// Summary: Laterality detection is used on the impression only
				// (as
				// indicated by the 2 in parentheses). This only triggers when
				// the input laterality is 0 (not specified
				// as input, based on exam code data). If a 1 or 2 is specified
				// with the input reports, this is not used.

				if (report.getNumberBreastsImaged() == 0) {

					int numberBreastsImagedFromReport = 0;

					int reportLateralityCode = report.getLaterality(2);

					if (reportLateralityCode == 3) {

						numberBreastsImagedFromReport = 2;

					} else if (reportLateralityCode == 2
							|| reportLateralityCode == 1) {

						numberBreastsImagedFromReport = 1;

					}

					report.setNumberBreastsImaged(numberBreastsImagedFromReport);

				}

				// report.setNumberBreastsImaged(1);

				// If we found more nonspecific birads than the number of
				// breasts
				// we think were imaged (either from our input or from
				// impression laterality detection), the report
				// is flagged.

				if (report.getFoundNonspecificBIRADS() > report
						.getNumberBreastsImaged()) {

					leftResult = "multiple BI-RADS without laterality found";

					rightResult = "multiple BI-RADS without laterality found";

				}

				// If number of breasts imaged (based on laterality input or
				// impression laterality detection) is 1, and we found 1 birads
				// and 1 none, then convert the 1 "none"
				// to "not imaged".

				if (report.getNumberBreastsImaged() == 1) {

					if (leftResult.equals("none")
							&& !rightResult.equals("none")) {

						leftResult = "not imaged";

					}

					if (!leftResult.equals("none")
							&& rightResult.equals("none")) {

						rightResult = "not imaged";

					}

				}

				// if no specific bilateral results found and we think only one
				// breast
				// was imaged

				// and we find only one side result right or left in the report,
				// set the
				// one not mentioned to not imaged

				// Summary: A nonspecific birads was found. Based on input or
				// impression laterality detection, we believe only 1 breast was
				// imaged. Impression laterality
				// detection is used to assigned the nonspecific birads to the
				// side we believe was imaged (1 is right,
				// 2 is left).

				if (report.getFoundBilateralBIRADS() == 0) {

					if (report.getNumberBreastsImaged() == 1
							&& report.getFoundRightBIRADS() == 0
							&& report.getFoundLeftBIRADS() == 0) {

						int laterality = report.getLaterality(2);

						if (laterality == 1)
							leftResult = "not imaged";

						if (laterality == 2)
							rightResult = "not imaged";

					}

				}

			}

			// This is the output: For every report you get a new log file line
			// and row in
			// the output table.

			BrokLogger.getInstance().log(report.getAccession() + "," + leftResult + "," + rightResult
					+ ",found left: " + report.getFoundLeftBIRADS()
					+ ",found right: " + report.getFoundRightBIRADS()
					+ ",found bilateral: " + report.getFoundBilateralBIRADS()
					+ ",found nonspecific: "
					+ report.getFoundNonspecificBIRADS()
					+ ",laterality detected: " + report.getLaterality(2));
			result(report.getAccession() + "," + leftResult + "," + rightResult
					+ ",found left: " + report.getFoundLeftBIRADS()
					+ ",found right: " + report.getFoundRightBIRADS()
					+ ",found bilateral: " + report.getFoundBilateralBIRADS()
					+ ",found nonspecific: "
					+ report.getFoundNonspecificBIRADS()
					+ ",laterality detected: " + report.getLaterality(2));

		}

		// test();

	} // These are the methods that right into the log file and results file.

	private static void processingPassOne(String accession) throws IOException {

		// For loop that says go through every accession number found in the
		// input file. Each of those accession numbers has one
		// report associated with it.

		Report report = accessionToReport.get(accession);

		// For each report, we used the above remove bad numbers method and
		// the assign birads method, which is the triple match. The zero after
		// assignBIRADS is just for debugging.

		report.removeBadNumbers();

		report.assignBIRADS(0);

		// If it has a left and right birads from the triple match it
		// records these as the local variable.

		int leftBIRADS = report.getLeftBIRADS();

		int rightBIRADS = report.getRightBIRADS();

		if (report.getLeftSubCategory() != null)
			BrokLogger.getInstance().log("accession: " + accession + " subcategory: "
					+ report.getLeftSubCategory());

		// If nothing was found with triple match then run
		// assignBIRADSAllowNullLateralityDescription (essentially the
		// nonspecific birads, detailed
		// above).

		if (leftBIRADS == -1 && rightBIRADS == -1
				&& report.getBilateralBIRADS() == -1) {

			BrokLogger.getInstance().log("No BI-RADS found: " + report.getAccession());

			BrokLogger.getInstance().log("Impression:\n" + report.getImpression());

			if (report.getImpression() == null)
				BrokLogger.getInstance().log("Body:\n" + report.getBody());

			report.assignBIRADSAllowNullLateralityDescription(1);

		}

	}

	
	private static void processingPassTwo(String accession) throws IOException {
		// Go back to the accession numbers and if we still did not find a
		// bi-rads, then run assignBIRADSAllowNullBIRADSDescription,
		// which is a different method, detailed above.

		Report report = accessionToReport.get(accession);
		int leftBIRADS = report.getLeftBIRADS();
		int rightBIRADS = report.getRightBIRADS();

		// disabled this because was findings more specific ones plus less
		// specific ones and causing multiple found problems

		// ((report.getLaterality()==3 && report.getBilateralBIRADS()==-1 &&
		// (leftBIRADS==-1 || rightBIRADS==-1 ))||(leftBIRADS==-1 &&
		// rightBIRADS==-1 &&
		// report.getBilateralBIRADS()==-1))

		// This commented out section was trying to use laterality info to
		// improve birads detection, but we were not successful.

		if ((leftBIRADS == -1 && rightBIRADS == -1 && report
				.getBilateralBIRADS() == -1)) {
			BrokLogger.getInstance().log("No BI-RADS found: " + report.getAccession());
			BrokLogger.getInstance().log("Impression:\n" + report.getImpression());
			if (report.getImpression() == null)
				BrokLogger.getInstance().log("Body:\n" + report.getBody());
			report.assignBIRADSAllowNullBIRADSDescription(1);
		}

	}


	private static void definePatterns() {
		// This looks for landmarks in the input file to identify
		// accession number, provided laterality number, and then the report
		// text.
		// The accession number is identified
		// with a an optional space or tab, followed by a mandatory
		// accession number (any length number),
		// followed by a mandatory space or tab, followed by the
		// integers 0, 1 or 2, followed by a
		// mandatory space or tab, followed by a report text starting
		// with a double quote. This identifies the
		// start of a new report. To accept alphanumeric accession
		// numbers, change
		// "[ \t]*([0-9]+)[ \t]+([0-2])[ \t]+\"(.*)"
		// to
		// "[ \t]*(\\w+)[ \t]+([0-2])[ \t]+\"(.*)"

		digitalAccessionNumberPattern = Pattern
				.compile("[ \t]*([0-9]+)[ \t]+([0-2])[ \t]+\"(.*)");
		alphaNumericAccession = Pattern
				.compile("[ \t]*([\\w-]+)[ \t]+([0-2])[ \t]+\"(.*)");

		// This looks for the end of the report, but may not be used.

		patternReportEnd = Pattern.compile("^\"$");

		// This looks for the beginning of the impression, by looking
		// for the
		// start of a line, follow by the word "impression:" followed by
		// optionally anything.

		patternBeginImpression = Pattern.compile("^impression:.*");

		// The same is done for finding the end of the impression.

		patternEndImpression = Pattern.compile("^end of impression:.*");
	}

	private static void accessionSectionAndCacheReport(File file)
			throws FileNotFoundException {
		String line;
		boolean inImpression = false;
		Report report = null;
		String lastLine = null;
		Scanner inFile = new Scanner(file);
		while (inFile.hasNext()) {

			line = inFile.nextLine();
			line = line.toLowerCase();

			if (lastLine != null && lastLine.equals(line))
				continue;
			// skip duplicated lines (error on addended reports). If the
			// line it is reading is identical to the previous, then it
			// ignore it.

			lastLine = line;

			// This looks for the text, "end of addendum" to identify
			// reports with an addendum and flags it as such.
			if (report != null && line.contains("end of addendum"))
				report.setHasAddendum(true);

			// The above regular expression patterns (compiled above) are
			// used
			// in the matchers below for efficiency.

			Matcher matcherAccession = digitalAccessionNumberPattern.matcher(line);
//			Matcher matcherAccession = alphaNumericAccession.matcher(line);
			Matcher matcherReportEnd = patternReportEnd.matcher(line);
			Matcher matcherBeginImpression = patternBeginImpression
					.matcher(line);
			Matcher matcherEndImpression = patternEndImpression.matcher(line);

			// When a new report is found, a new report will be created and
			// the accession number will be entered into it, along with the
			// number of breasts imaged and the start of
			// the body of the report is entered into the variable to build
			// it up.

			if (matcherAccession.find()) {
				report = processAccessionMatch(matcherAccession, report);
				inImpression = false;
			} else if (report != null && matcherBeginImpression.matches()) {
				// If we did not find an accession number, did we find the
				// beginning of an impression? Booleans keep track of where we
				// are in
				// the report. If the beginning of the impression is found, that
				// line
				// and all subsequent lines until the end of an impression, or a
				// new
				// report, are assigned to the impression. Otherwise, the
				// line is assigned to the body, including the
				// line that contains an accession number (start of the
				// report).
				report.setImpression(line + "\n");
				inImpression = true;
				BrokLogger.getInstance().log("matched beginning impression");
			} else if (report != null && matcherEndImpression.matches()) {
				inImpression = false;
			} else if (report != null && line != null) {
				// If none of the above stuff triggered, this default block
				// will happen.
				if (inImpression) {
					if (report.getImpression() == null) {
						report.setImpression("");
					}
					report.setImpression(report.getImpression() + line + "\n");
				} else {
					if (report.getBody() == null) {
						report.setBody("");
					}
					report.setBody(report
							.getBody() + line + "\n");
				}
			}
		}
		inFile.close();
	}

	private static Report processAccessionMatch(Matcher mAccession, Report report) {
		report = accessionToReport.get(mAccession.group(1)) == null ? new Report()
				: accessionToReport.get(mAccession.group(1));
		report.setAccession(new Long(mAccession.group(1)));
		report.setNumberBreastsImaged(Integer.parseInt(mAccession.group(2)));
		report.setBody(mAccession.group(3) + "\n");
		accessionToReport.put(report.getAccession()+"", report);
		BrokLogger.getInstance().log("Cached report for accession: " + report.getAccession());
		return report;
	}



	public static void result(String text) {

		File file = new File(output);

		try {

			FileWriter writer = new FileWriter(file, true);

			System.out.print(text + System.getProperty("line.separator"));

			writer.write(text + System.getProperty("line.separator"));

			writer.flush();

			writer.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
