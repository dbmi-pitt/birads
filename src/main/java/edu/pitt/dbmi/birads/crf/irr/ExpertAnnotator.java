package edu.pitt.dbmi.birads.crf.irr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

public class ExpertAnnotator {

	private final String inputPath = "C:\\Users\\kjm84\\Desktop\\snapshot102115\\expert";

	private final List<ExpertDocument> allExpertDocs = new ArrayList<ExpertDocument>();

	private final List<ExpertDocument> expertOneDocs = new ArrayList<ExpertDocument>();
	private final List<ExpertDocument> expertTwoDocs = new ArrayList<ExpertDocument>();

	private final String expertOneUser = "Rebeccaj";
	private final String expertTwoUser = "sec113";

	private final String sequenceBoundry = "999999999999";

	private final CohenKappaCalculator cohenKappaCalculator = new CohenKappaCalculator();

	private final LinkedHashSet<String> categories = new LinkedHashSet<String>();

	@SuppressWarnings("unused")
	private final boolean isDebugging = false;

	public static void main(String[] args) {
		ExpertAnnotator expertAnnotator = new ExpertAnnotator();
		expertAnnotator.execute();
	}

	public void execute() {
		buildExpertList();
		displayExpertList(allExpertDocs);
		partitionExpertList();
		filterCategories();
		sortExpertSegregatedDocumentLists();
		cohenKappaCompare();

	}

	private void filterCategories() {
		categories.add("overall_value");
		categories.add("left_value");
		categories.add("multilateral_value");
		categories.add("nonspecific_value");
		categories.add("right_value");
		cohenKappaCalculator.setCategories(categories);
	}

	private void sortExpertSegregatedDocumentLists() {
		final Comparator<ExpertDocument> docComparator = new Comparator<ExpertDocument>() {
			@Override
			public int compare(ExpertDocument docOne, ExpertDocument docTwo) {
				return docOne.getSequence().compareTo(docTwo.getSequence());
			}
		};
		Collections.sort(expertOneDocs, docComparator);
		Collections.sort(expertTwoDocs, docComparator);
	}

	private void cohenKappaCompare() {
		Iterator<ExpertDocument> iteratorOne = expertOneDocs.iterator();
		Iterator<ExpertDocument> iteratorTwo = expertTwoDocs.iterator();
		ExpertDocument expertOneDoc = (iteratorOne.hasNext()) ? iteratorOne
				.next() : null;
		ExpertDocument expertTwoDoc = (iteratorTwo.hasNext()) ? iteratorTwo
				.next() : null;
		while (expertOneDoc != null && expertTwoDoc != null) {
			if (expertOneDoc.getPath().indexOf("Aggregate") != -1) {
				expertOneDoc = (iteratorOne.hasNext()) ? iteratorOne.next()
						: null;
			} else if (expertTwoDoc.getPath().indexOf("Aggregate") != -1) {
				expertTwoDoc = (iteratorTwo.hasNext()) ? iteratorTwo.next()
						: null;
			} else if (expertOneDoc.compareTo(expertTwoDoc) < 0) {
				expertOneDoc = (iteratorOne.hasNext()) ? iteratorOne.next()
						: null;
			} else if (expertOneDoc.compareTo(expertTwoDoc) > 0) {
				expertTwoDoc = (iteratorTwo.hasNext()) ? iteratorTwo.next()
						: null;
			} else { // sequences compare
				System.out.println("Comparing\n\t" + expertOneDoc.getPath()
						+ "\n\t" + expertTwoDoc.getPath());
				cohenKappaCalculator.setDocumentOne(expertOneDoc);
				cohenKappaCalculator.setDocumentTwo(expertTwoDoc);
				cohenKappaCalculator.accumulate();
				expertOneDoc = (iteratorOne.hasNext()) ? iteratorOne.next()
						: null;
				expertTwoDoc = (iteratorTwo.hasNext()) ? iteratorTwo.next()
						: null;
			}
		}
		cohenKappaCalculator.computeKappa();
		System.out.println(cohenKappaCalculator);
	}

	private void partitionExpertList() {
		for (ExpertDocument expertDoc : allExpertDocs) {
			if (expertDoc.getExpert().equals(expertOneUser)) {
				if (expertDoc.getSequence().compareTo(sequenceBoundry) < 0) {
					expertOneDocs.add(expertDoc);
				}

			} else if (expertDoc.getExpert().equals(expertTwoUser)) {
				if (expertDoc.getSequence().compareTo(sequenceBoundry) < 0) {
					expertTwoDocs.add(expertDoc);
				}

			}
		}
	}

	private void buildExpertList() {
		File f = new File(inputPath);
		File[] directoryFiles = f.listFiles();
		for (File directoryFile : directoryFiles) {
			Pattern pattern = Pattern.compile("^report(\\d{12})\\.birads\\.(\\w+)");
			Matcher matcher = pattern.matcher(directoryFile.getName());
			if (matcher.find()) {
				ExpertDocument doc = new ExpertDocument();
				doc.setPath(directoryFile.getAbsolutePath());
				doc.setSequence(matcher.group(1));
				doc.setExpert(matcher.group(2));
				allExpertDocs.add(doc);
			}
		}
	}

	private void displayExpertList(List<ExpertDocument> docs) {
		for (ExpertDocument doc : docs) {
			System.out.println(doc);
		}
	}

}
