package edu.pitt.dbmi.birads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.pitt.dbmi.birads.crf.digestion.Entity;
import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

//Kevin,
//I hope you are having a great day. I was wondering if you could you me help getting
//the number of the BIRADS annotations? I think I'll need those for my proposal.
//The total number of BIRADS annotations. The number of BIRADS per category(0,1,2,3, etc)
//And the number of BIRADS per laterality (overall , right , left, etc).
//

public class StatisticsGenerator {

	private final String rawPath = "C:\\Users\\kjm84\\Desktop\\birads_decks\\analysis160317\\raw";
	private final String expertPath = "C:\\Users\\kjm84\\Desktop\\birads_decks\\analysis160317\\exp";

	private final List<ExpertDocument> allExpertDocs = new ArrayList<ExpertDocument>();

	private final List<ExpertDocument> expertOneDocs = new ArrayList<ExpertDocument>();
	private final List<ExpertDocument> expertTwoDocs = new ArrayList<ExpertDocument>();

	private final String expertOneUser = "Rebeccaj";
	private final String expertTwoUser = "sec113";

	private final String sequenceBoundry = "999999999999";

	private final LinkedHashSet<String> categories = new LinkedHashSet<String>();

	private File rawDirectory;

	private int totalAnnotations = 0;
	private String[] biradsCategory = {"Overall", "Left", "Right", "Multilateral", "Nonspecific"};
	private int[] sumOfBiradsCategory = new int[biradsCategory.length];
	private int[] sumOfBiradsLevel = new int[7];
	

	@SuppressWarnings("unused")
	private final boolean isDebugging = false;

	public static void main(String[] args) {
		StatisticsGenerator expertAnnotator = new StatisticsGenerator();
		try {
			expertAnnotator.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws IOException {
		buildExpertList();
		displayExpertList(allExpertDocs);
		partitionExpertList();
		filterCategories();
		sortExpertSegregatedDocumentLists();
		gatherStatistics();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Total annotations = " + totalAnnotations);
		sb.append("\nCategory break down:");
		for (int idx = 0; idx < biradsCategory.length; idx++) {
			sb.append("\n\tNumber of " + biradsCategory[idx] + " birads = " + sumOfBiradsCategory[idx]);
		}
		sb.append("\nLevel break down:");
		for (int idx = 0; idx < sumOfBiradsLevel.length; idx++) {
			sb.append("\n\tNumber of " + idx + " level birads = " + sumOfBiradsLevel[idx]);
		}
		System.out.println(sb.toString());
		
		File statisticsDirectory = new File(rawPath);
		statisticsDirectory = statisticsDirectory.getParentFile();
		File statisticsFile = new File(statisticsDirectory, "statistics.txt");
		FileUtils.write(statisticsFile, sb.toString());

	}

	@SuppressWarnings("unused")
	private void printVector(String header, int[] srcVector) {
		System.out.print(header + " ");
		for (int idx = 0; idx < srcVector.length - 1; idx++) {
			System.out.print(srcVector[idx] + ", ");
		}
		System.out.println(srcVector[srcVector.length - 1]);
	}

	private void filterCategories() {
		categories.add("overall_value");
		categories.add("left_value");
		categories.add("multilateral_value");
		categories.add("nonspecific_value");
		categories.add("right_value");
	}

	private void sortExpertSegregatedDocumentLists() {
		final Comparator<ExpertDocument> docComparator = new Comparator<ExpertDocument>() {
			@Override
			public int compare(ExpertDocument docOne, ExpertDocument docTwo) {
				return docOne.getSequence().compareTo(docTwo.getSequence());
			}
		};
		expertOneDocs.addAll(expertTwoDocs);
		Collections.sort(expertOneDocs, docComparator);
	}

	private void gatherStatistics() throws IOException {
		Iterator<ExpertDocument> iteratorOne = expertOneDocs.iterator();
		while (iteratorOne.hasNext()) {
			processExpertDocument(iteratorOne.next());
		}
	}

	private void processExpertDocument(ExpertDocument expertOneDoc)
			throws IOException {
		rawDirectory = new File(rawPath);
		String reportName = expertOneDoc.getReportName();
		File rawFile = new File(rawDirectory, reportName + ".txt");
		String content = FileUtils.readFileToString(rawFile);
		processExpertDocumentWithContent(expertOneDoc, content);
	}

	private void processExpertDocumentWithContent(ExpertDocument expertOneDoc,
			String content) {
		expertOneDoc.cacheEntities();
		expertOneDoc.iterate();
		while (expertOneDoc.hasNext()) {
			Entity entity = expertOneDoc.next();
			totalAnnotations++;
			boolean isLevelComputable = false;
			if (entity.getType().equals("overall_value")) {
				sumOfBiradsCategory[0]++;
				isLevelComputable = true;
			}
			if (entity.getType().equals("left_value")) {
				System.out.println(entity);
				sumOfBiradsCategory[1]++;
				isLevelComputable = true;
			}
			if (entity.getType().equals("right_value")) {
				sumOfBiradsCategory[2]++;
				isLevelComputable = true;
			}
			if (entity.getType().equals("multilateral_value")) {
				sumOfBiradsCategory[3]++;
				isLevelComputable = true;
			}
			if (entity.getType().equals("nonspecific_value")) {
				sumOfBiradsCategory[4]++;
				isLevelComputable = true;
			}
			if (isLevelComputable) {
				int levelAsInt = -1;
				String contentBeneathLevel = content.substring(
						entity.getsPos(), entity.getePos());
				levelAsInt = numericMatcher(contentBeneathLevel);
				levelAsInt = (levelAsInt < 0) ? romanNumeralMatcher(contentBeneathLevel)
						: levelAsInt;
				if (levelAsInt >= 0) {
					sumOfBiradsLevel[levelAsInt]++;
				} else {
					System.out.println("Illegal birads level annotated ==> "
							+ contentBeneathLevel);
				}
			}
		}
	}

	private int romanNumeralMatcher(String content) {
		int result = -1;
		Pattern pattern = Pattern.compile("\\b(i|ii|iii|iv|v|vi)\\b",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			switch (matcher.group(1).toLowerCase()) {
			case "i":
				result = 1;
				break;
			case "ii":
				result = 2;
				break;
			case "iii":
				result = 3;
				break;
			case "iv":
				result = 4;
				break;
			case "v":
				result = 5;
				break;
			case "vi":
				result = 6;
				break;
			default:
				result = -1;
			}
		}
		return result;
	}

	private int numericMatcher(String content) {
		int result = -1;
		Pattern pattern = Pattern.compile("(\\d).*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			result = new Integer(matcher.group(1));
		}
		return result;
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
		File f = new File(expertPath);
		File[] directoryFiles = f.listFiles();
		for (File directoryFile : directoryFiles) {
			Pattern pattern = Pattern
					.compile("^report(\\d{12})\\.birads\\.(\\w+)");
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
