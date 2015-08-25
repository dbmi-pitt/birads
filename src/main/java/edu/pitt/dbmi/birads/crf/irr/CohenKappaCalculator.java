package edu.pitt.dbmi.birads.crf.irr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import edu.pitt.dbmi.birads.crf.digestion.Entity;
import edu.pitt.dbmi.birads.crf.digestion.ExpertDocument;

public class CohenKappaCalculator {

	private ExpertDocument documentOne;
	private ExpertDocument documentTwo;

	private final Map<String, Entity> oneOnly = new HashMap<String, Entity>();
	private final Map<String, Entity> twoOnly = new HashMap<String, Entity>();
	
	private final LinkedHashSet<String> categories = new LinkedHashSet<String>();
	private final LinkedHashMap<String, Integer> categoryIndex = new LinkedHashMap<String, Integer>();

	private final Map<String, Double> contingencyMap = new HashMap<String, Double>();
	private double[][] contingencyMatrix = null;
	private double[] rowTotals = null;
	private double[] colTotals = null;
	private double overAllTotal = 0.0d;
	private double numberOfAgreements = 0.0d;
	private double[] expectedNumOfAgreements = null;
	private double expectedNumberOfAgreements = 0.0d;
	private double kappa = 0.0d;

	public void accumulate() {
		documentOne.cacheEntities();
		documentTwo.cacheEntities();
		initializeExpertOne();
		initializeExpertTwo();
		calculateConsensus();

		oneOnly.clear();
		twoOnly.clear();
	}
	
	private void initializeExpertOne() {
		documentOne.iterate();
		while (documentOne.hasNext()) {
			Entity entity = documentOne.next();
			entity.setMatchCode(1);
			String key = entity.toKey();
			oneOnly.put(key, entity);
		}
	}

	private void initializeExpertTwo() {
		documentTwo.iterate();
		while (documentTwo.hasNext()) {
			Entity entity = documentTwo.next();
			entity.setMatchCode(2);
			String key = entity.toKey();
			twoOnly.put(key, entity);
		}
	}

	private void calculateConsensus() {
		final HashSet<String> keys = new HashSet<String>();
		keys.addAll(oneOnly.keySet());
		keys.addAll(twoOnly.keySet());
		for (String key : keys) {
			String oneKey = "Missing";
			String twoKey = "Missing";
			if (oneOnly.get(key) != null && twoOnly.get(key) != null) {
				oneKey = oneOnly.remove(key).getType();
				twoKey = twoOnly.remove(key).getType();
			}
			else if (twoOnly.get(key) == null) {
				oneKey = oneOnly.remove(key).getType();
			}
			else {
				twoKey = twoOnly.remove(key).getType();
			}			
			StringBuilder sb = new StringBuilder();
			sb.append(oneKey);
			sb.append(":");
			sb.append(twoKey);
			String contingencyKey = sb.toString();
			if (contingencyMap.get(contingencyKey) == null) {
				contingencyMap.put(contingencyKey, new Double(0.0d));
			}
			contingencyMap.put(contingencyKey, new Double(contingencyMap.get(contingencyKey).doubleValue() + 1.0d));
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
		formulateContigencyTable();

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

	private void formulateContigencyTable() {
		deriveCategories();
		buildCategoryIndex();
		buildContigencyMatrix();
		zeroMatrix(contingencyMatrix);
		populateContigencyMatrix();
	}

	private void populateContigencyMatrix() {
		for (String contingencyKey : contingencyMap.keySet()) {
			String[] keyParts = contingencyKey.split(":");
			String rowType = keyParts[0];
			String colType = keyParts[1];
			int row = categoryIndex.get(rowType);
			int col = categoryIndex.get(colType);
			contingencyMatrix[row][col] = contingencyMap.get(contingencyKey);
		}
		
	}

	private void deriveCategories() {
		if (categories.isEmpty()) {
			final TreeSet<String> categorySorter = new TreeSet<String>();
			for (String contingencyKey : contingencyMap.keySet()) {
				String[] keyParts = contingencyKey.split(":");
				categorySorter.add(keyParts[0]);
				categorySorter.add(keyParts[1]);
			}
			categories.addAll(categorySorter);
			categories.add("Missing");
		}
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
		StringBuilder sb = new StringBuilder();
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
