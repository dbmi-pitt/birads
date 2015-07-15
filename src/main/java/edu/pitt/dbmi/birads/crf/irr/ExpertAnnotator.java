package edu.pitt.dbmi.birads.crf.irr;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

public class ExpertAnnotator {
	
	private final String inputPath = "C:\\Users\\kjm84\\Desktop\\completed";

	private final List<ExpertDocument> allExpertDocs = new ArrayList<ExpertDocument>();
	
	private final List<ExpertDocument> expertOneDocs = new ArrayList<ExpertDocument>();
	private final List<ExpertDocument> expertTwoDocs = new ArrayList<ExpertDocument>();
	
	private final String expertOneUser = "Rebeccaj";
	private final String expertTwoUser = "sec113";
	
	private final String sequenceBoundry = "015";
	
	private final CohenKappaCalculator cohenKappaCalculator = new CohenKappaCalculator();
	
	@SuppressWarnings("unused")
	private final boolean isDebugging = false;

	public static void main(String[] args) {
		ExpertAnnotator expertAnnotator = new ExpertAnnotator();
		expertAnnotator.execute();
	}

	public void execute() {
		buildExpertList();
		partitionExpertList();
		cohenKappaCompare();

	}

	private void cohenKappaCompare() {
		Iterator<ExpertDocument> iteratorOne = expertOneDocs.iterator();
		Iterator<ExpertDocument> iteratorTwo = expertTwoDocs.iterator();
		while (iteratorOne.hasNext() && iteratorTwo.hasNext()) {
			ExpertDocument expertOneDoc = iteratorOne.next();
			ExpertDocument expertTwoDoc = iteratorTwo.next();
			cohenKappaCalculator.setDocumentOne(expertOneDoc);
			cohenKappaCalculator.setDocumentTwo(expertTwoDoc);
			cohenKappaCalculator.accumulate();
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
				
			}
			else if (expertDoc.getExpert().equals(expertTwoUser)) {
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
			Pattern pattern = Pattern.compile("^doc(\\d{3})\\.birads\\.(\\w+)");
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
	
	@SuppressWarnings("unused")
	private void displayExpertList(List<ExpertDocument> docs) {
		for (ExpertDocument doc : docs) {
			System.out.println(doc);
		}
	}

}
