package edu.pitt.dbmi.birads.crf.irr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.pitt.dbmi.birads.crf.digestion.Entity;
import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

public class CohenKappaCalculator {

	private ExpertDocument documentOne;
	private ExpertDocument documentTwo;

	private final Map<String, Entity> oneOnly = new HashMap<String, Entity>();
	private final Map<String, Entity> twoOnly = new HashMap<String, Entity>();
	private final Map<String, Entity> consensus = new HashMap<String, Entity>();

	private final Map<String, Entity> cumulativeEntities = new HashMap<String, Entity>();
	private final LinkedHashMap<String, Entity> sortedCumulativeEntities = new LinkedHashMap<String, Entity>();
	private final LinkedHashSet<String> categories = new LinkedHashSet<String>();
	private final LinkedHashMap<String, Integer> categoryIndex = new LinkedHashMap<String, Integer>();

	private double[][] contingencyMatrix = null;
	private double[] rowTotals = null;
	private double[] colTotals = null;
	private double overAllTotal = 0.0d;
	private double numberOfAgreements = 0.0d;
	private double[] expectedNumOfAgreements = null;
	private double expectedNumberOfAgreements = 0.0d;
	private double kappa = 0.0d;

	private int oneOnlyCount = 0;
	private int twoOnlyCount = 0;
	private int consensusCount = 0;

	public void accumulate() {
		documentOne.cacheEntities();
		documentTwo.cacheEntities();
		initializeExpertOne();
		initializeExpertTwo();
		calculateConsensus();

		cumulativeEntities.putAll(oneOnly);
		cumulativeEntities.putAll(twoOnly);
		cumulativeEntities.putAll(consensus);

		oneOnlyCount += oneOnly.size();
		twoOnlyCount += twoOnly.size();
		consensusCount += consensus.size();

		oneOnly.clear();
		twoOnly.clear();
		consensus.clear();

	}

	private void calculateConsensus() {
		final HashSet<String> keys = new HashSet<String>();
		keys.addAll(oneOnly.keySet());
		keys.addAll(twoOnly.keySet());
		for (String key : keys) {
			if (oneOnly.get(key) != null && twoOnly.get(key) != null) {
				Entity consensusEntity = oneOnly.remove(key);
				consensusEntity.setMatchCode(3);
				consensus.put(key, consensusEntity);
				twoOnly.remove(key);
			}
		}
	}

	private void initializeExpertOne() {
		documentOne.iterate();
		while (documentOne.hasNext()) {
			Entity entity = documentOne.next();
			entity.setMatchCode(1);
			oneOnly.put(entity.toKey(), entity);
		}
	}

	private void initializeExpertTwo() {
		documentTwo.iterate();
		while (documentTwo.hasNext()) {
			Entity entity = documentTwo.next();
			entity.setMatchCode(2);
			twoOnly.put(entity.toKey(), entity);
		}
	}

	public void setDocumentOne(ExpertDocument documentOne) {
		this.documentOne = documentOne;
	}

	public void setDocumentTwo(ExpertDocument documentTwo) {
		this.documentTwo = documentTwo;
	}

	public void computeKappa() {
		
		// Reference http://psych.unl.edu/psycrs/handcomp/hckappa.PDF
		
		// Step 1
		// Organize information into a contingency table
		// The way of thinking is each Annotation can be one of ten types
		// since there are nine unique possible entity designations
		// and an added possibility that one expert with disagree or "miss"
		// something the other expert annotated.  (call this Missing)
		//
		
		sortCumulativeEntities();
		formulateContigencyTable();
		processAgreements();
		processCategoricalDisagreements();
		processMissingOrPoorlyAlignedAnnotations();

		int numRows = contingencyMatrix.length;
		int numCols = contingencyMatrix.length;

		// Step 2
		// compute the row totals (sum across values in the same row)
		// and column totals (sum across values on the same column)
		// of the observed frequencies.

		// compute row summations
		rowTotals = new double[numRows];
		for (int row = 0; row < contingencyMatrix.length; row++) {
			rowTotals[row] = 0.0d;
			for (int col = 0; col < numCols; col++) {
				rowTotals[row] += contingencyMatrix[row][col];
			}
		}

		// compute column summations
		colTotals = new double[numCols];
		for (int col = 0; col < numCols; col++) {
			colTotals[col] = 0.0d;
			for (int row = 0; row < numRows; row++) {
				colTotals[col] += contingencyMatrix[row][col];
			}
		}

		// Step 3
		// compute the over all total
		//
		overAllTotal = 0.0d;
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				overAllTotal += contingencyMatrix[row][col];
			}
		}

		// Step 4
		//
		// Compute the total number of agreements by summing the values in
		// the diagonal cells of the table
		//
		numberOfAgreements = 0.0d;
		for (int diag = 0; diag < numRows; diag++) {
			numberOfAgreements += contingencyMatrix[diag][diag];
		}

		// Step 5
		// Compute the expected frequency for the number of
		// agreements the would have been expected by chance for each coding
		// category.
		//
		expectedNumOfAgreements = new double[numRows];
		for (int diag = 0; diag < numRows; diag++) {
			expectedNumOfAgreements[diag] = (rowTotals[diag] * colTotals[diag])
					/ overAllTotal;
		}
		
		// Step 6
		// Compute the sum of the expected frequencies of agreement by chance
		expectedNumberOfAgreements = 0.0d;
		for (int row = 0; row < numRows; row++) {
			expectedNumberOfAgreements += expectedNumOfAgreements[row];
		}
		
		// Step 7 
		// Compute Kappa
		kappa = (numberOfAgreements - expectedNumberOfAgreements) /
				(overAllTotal - expectedNumberOfAgreements);
	}

	private void processMissingOrPoorlyAlignedAnnotations() {
		// Non paired disagreements are thought to be "Missing"
		Iterator<Entity> entityIterator = sortedCumulativeEntities.values()
				.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			if (entity.getMatchCode() < 3) {
				int idx = categoryIndex.get(entity.getType());
				int jdx = categoryIndex.get("Missing");
				if (entity.getMatchCode() == 1) {
					contingencyMatrix[idx][jdx] += 1.0d;
				} else {
					contingencyMatrix[jdx][idx] += 1.0d;
				}
			}
		}
	}

	private void processCategoricalDisagreements() {
		// Do disagreement or off-diagonals of the contingency matrix
		Iterator<Entity> trailingIterator = sortedCumulativeEntities.values()
				.iterator();
		Iterator<Entity> leadingIterator = sortedCumulativeEntities.values()
				.iterator();
		leadingIterator.next();
		while (leadingIterator.hasNext()) {
			Entity trailingEntity = trailingIterator.next();
			Entity leadingEntity = leadingIterator.next();
			if (leadingEntity.onlyTypeDisagreement(trailingEntity)) {
				int idx = categoryIndex.get(trailingEntity.getType());
				int jdx = categoryIndex.get(leadingEntity.getType());
				if (trailingEntity.getMatchCode() == 1
						&& leadingEntity.getMatchCode() == 2) {
					contingencyMatrix[idx][jdx] += 1.0d;
				} else if (trailingEntity.getMatchCode() == 2
						&& leadingEntity.getMatchCode() == 1) {
					contingencyMatrix[jdx][idx] += 1.0d;
				}
				trailingEntity.setMatchCode(4);
				leadingEntity.setMatchCode(4);
			}
		}

	}

	private void processAgreements() {
		// Do agreements or diagonals of the contingency matrix
		Iterator<Entity> entityIterator = sortedCumulativeEntities.values()
				.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			if (entity.getMatchCode() == 3) {
				int idx = categoryIndex.get(entity.getType());
				contingencyMatrix[idx][idx] += 1.0d;
				entity.setMatchCode(4);
			}
		}
	}

	private void sortCumulativeEntities() {
		final List<String> sortedKeys = new ArrayList<String>();
		sortedKeys.addAll(cumulativeEntities.keySet());
		Collections.sort(sortedKeys);
		for (String key : sortedKeys) {
			sortedCumulativeEntities.put(key, cumulativeEntities.get(key));
		}
	}

	private void formulateContigencyTable() {
		deriveCategories();
		buildCategoryIndex();
		buildContigencyMatrix();
		zeroMatrix(contingencyMatrix);
	}

	private void deriveCategories() {
		final TreeSet<String> categorySorter = new TreeSet<String>();
		for (Entity entity : cumulativeEntities.values()) {
			categorySorter.add(entity.getType());
		}
		categories.addAll(categorySorter);
		categories.add("Missing");
	}

	private void buildCategoryIndex() {
		int categoryNumber = 0;
		for (String category : categories) {
			categoryIndex.put(category, categoryNumber++);
		}

	}

	private void buildContigencyMatrix() {
		contingencyMatrix = new double[categories.size()][];
		for (int idx = 0; idx < categories.size(); idx++) {
			contingencyMatrix[idx] = new double[categories.size()];
		}
	}

	private void zeroMatrix(double[][] m) {
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				contingencyMatrix[i][j] = 0.0d;
			}
		}
	}

	public String prettyFormatMatrix(String title, double[][] matrix) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n" + title + "\n\n");
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				sb.append(String.format("%5.2f", matrix[row][col]) + ", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public String prettyFormatVector(String title, double[] vec) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n" + title + "\n\n");
		for (int col = 0; col < vec.length; col++) {
			sb.append(String.format("%5.2f", vec[col]) + ", ");
		}
		sb.append("\n");
		return sb.toString();
	}

	public String toString() {
		String formattingString = "";
		formattingString += "oneOnly: %5d%n";
		formattingString += "twoOnly: %5d%n";
		formattingString += "consensus: %5d%n";
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(formattingString, oneOnlyCount, twoOnlyCount,
				consensusCount));
		final List<String> sortedKeys = new ArrayList<String>();
		sortedKeys.addAll(cumulativeEntities.keySet());
		Collections.sort(sortedKeys);
		for (String key : sortedKeys) {
			sb.append(cumulativeEntities.get(key).toString() + "\n");
		}
		sb.append("\n\nCategories:\n");
		for (String category : categories) {
			sb.append("\t" + category + "\n");
		}
		sb.append(prettyFormatMatrix("ContingencyMatrix:", contingencyMatrix));
		sb.append(prettyFormatVector("Row Summations:", rowTotals));
		sb.append(prettyFormatVector("Col Summations:", colTotals));
		sb.append("\n\nover all total is " + overAllTotal);
		sb.append("\n\nsum of agreements is " + numberOfAgreements);
		sb.append(prettyFormatVector("Expected number of agreements:",
				expectedNumOfAgreements));
		sb.append("\n\nsum of expected number of agreements is " + expectedNumberOfAgreements);
		sb.append("\n\nkappa is " + kappa);
		
		return sb.toString();
	}

}
