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

	private final String inputPath = "C:\\Users\\kjm84\\Desktop\\snapshot022216";

	private final List<ExpertDocument> allExpertDocs = new ArrayList<ExpertDocument>();

	private final List<ExpertDocument> expertOneDocs = new ArrayList<ExpertDocument>();
	private final List<ExpertDocument> expertTwoDocs = new ArrayList<ExpertDocument>();

	private final String expertOneUser = "Rebeccaj";
	private final String expertTwoUser = "sec113";

	private final String sequenceBoundry = "999999999999";

	private final LinkedHashSet<String> categories = new LinkedHashSet<String>();

	private int totalAnnotations = 0;
	private int[] sumOfBiradsLevel = new int[7];
	private int[] sumOfBiradsCategory = new int[5];

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
		System.out.println("Total annotations = " + totalAnnotations);
		printVector("Sum of categories: ", sumOfBiradsCategory);
		printVector("Sum of levels: ", sumOfBiradsLevel);

	}
	
	private void printVector(String header, int[] srcVector) {
		System.out.print(header + " ");
		for (int idx = 0; idx < srcVector.length - 1; idx++) {
			System.out.print(srcVector[idx] + ", ");
		}
		System.out.println(srcVector[srcVector.length-1]);
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
			ExpertDocument expertOneDoc = iteratorOne.next();
			System.out.println("Opening " + expertOneDoc.getPath());
			File contentDirPath = new File(
					"C:\\Users\\kjm84\\Desktop\\birads_prime_160114");
			if (contentDirPath.exists() && contentDirPath.isDirectory()) {
				Pattern pattern = Pattern.compile("report\\d+");
				Matcher matcher = pattern.matcher(expertOneDoc.getPath());
				if (matcher.find()) {
					String reportName = matcher.group();
					System.out.println("Got report named " + reportName);
					File contentFile = new File(contentDirPath, reportName);
					if (contentFile.exists() && contentFile.isDirectory()) {
						contentFile = new File(contentFile, reportName);
					}	
					else {
						System.out.println(contentFile.getAbsolutePath() + " is not a directory.");
					}
					if (!contentFile.exists()) {
						continue;
					}
					String content = FileUtils.readFileToString(contentFile);
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
							String level = content.substring(entity.getsPos(),
									entity.getePos());
							if (level.matches("\\d")) {
								int levelAsInt = (new Integer(level)).intValue();
								sumOfBiradsLevel[levelAsInt]++;
							}
							else {
								System.out.println("Illegal birads level annotated ==> " + level);
							}
						}

					}

				}
			}

		}

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
