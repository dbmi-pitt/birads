package edu.pitt.dbmi.birads.brokprime;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;
import edu.pitt.dbmi.birads.util.BrokLogger;

public class ReportSectionizer {

	private Pattern patternBeginImpression;
	private Pattern patternEndImpression;

	public ReportSectionizer() {
		definePatterns();
	}

	public Report buildReport(String documentId,
			int numberBreastsImaged, String documentText)
			throws FileNotFoundException {

		// When a new report is entered, a new report will be created and
		// the accession number will be entered into it, along with the
		// number of breasts imaged and the start of
		// the body of the report is entered into the variable to build
		// it up.

		Report report = new Report();
		report.setAccession(documentId);
		report.setNumberBreastsImaged(numberBreastsImaged);
		
		String lastLine = null;
		String line;
		boolean inImpression = false;

		Scanner inScanner = new Scanner(documentText);
		while (inScanner.hasNext()) {

			line = inScanner.nextLine();
			line = line.toLowerCase();

			// skip duplicated lines (error on addended reports). If the
			// line it is reading is identical to the previous, then it
			// ignore it.
			if (lastLine != null && lastLine.equals(line)) {
				continue;
			}

			lastLine = line;

			// This looks for the text, "end of addendum" to identify
			// reports with an addendum and flags it as such.
			if (report != null && line.contains("end of addendum")) {
				report.setHasAddendum(true);
			}

			// The above regular expression patterns (compiled above) are
			// used in the matchers below for efficiency.
			Matcher matcherBeginImpression = patternBeginImpression
					.matcher(line);
			Matcher matcherEndImpression = patternEndImpression.matcher(line);

			if (matcherBeginImpression.matches()) {
				BrokLogger.getInstance().log("matched beginning impression");
				// If the beginning of the impression is found, that
				// line and all subsequent lines until the end of an impression,
				// or report end
				//
				// report, are assigned to the impression. Otherwise, the
				// line is assigned to the body, including the
				// line that contains an accession number (start of the
				// report).
				report.setImpression(line + "\n");
				inImpression = true;

			} else if (matcherEndImpression.matches()) {
				inImpression = false;
			} else if (line != null) {
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
					report.setBody(report.getBody() + line + "\n");
				}
			}
		}
		inScanner.close();

		return report;
	}

	private void definePatterns() {

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

		// This looks for the beginning of the impression, by looking
		// for the
		// start of a line, follow by the word "impression:" followed by
		// optionally anything.

		patternBeginImpression = Pattern.compile("^impression:.*");

		// The same is done for finding the end of the impression.

		patternEndImpression = Pattern.compile("^end of impression:.*");
	}

}
