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
	private double[] rowMarginalPercentChanceAgreement = null;
	private double[] colMarginalPercentChanceAgreement = null;
	private double expectedPercentChanceAgreement = 0.0d;
	private double kappa = 0.0d;

	public void accumulate() {
		documentOne.cacheEntities();
		documentTwo.cacheEntities();
		fuzzyResolveEntities();
		initializeExpertOne();
		initializeExpertTwo();
		calculateConsensus();

		oneOnly.clear();
		twoOnly.clear();
	}
	
	private void fuzzyResolveEntities() {
		documentOne.iterate();
		documentTwo.iterate();
		Entity entityOne = (documentOne.hasNext()) ? documentOne.next() : null;
		Entity entityTwo = (documentTwo.hasNext()) ? documentTwo.next() : null;
		while (entityOne != null && entityTwo != null) {
			if (closeProximity(entityOne, entityTwo)) {
				repositionEntity(entityOne, entityTwo);
				entityOne = (documentOne.hasNext()) ? documentOne.next() : null;
				entityTwo = (documentTwo.hasNext()) ? documentTwo.next() : null;
			}
			else if (proceeds(entityOne, entityTwo)) {
				entityOne = (documentOne.hasNext()) ? documentOne.next() : null;
			}
			else {
				entityTwo = (documentTwo.hasNext()) ? documentTwo.next() : null;
			}	
		}
	}
	
	private boolean proceeds(Entity entityOne, Entity entityTwo) {
		boolean doesProceed = false;
		if (entityOne.getsPos() < entityTwo.getsPos()) {
			doesProceed = true;
		}
		else if (entityOne.getsPos() == entityTwo.getsPos()) {
			if (entityOne.getePos() <= entityTwo.getePos()) {
				doesProceed = true;
			}
		}
		return doesProceed;
	}

	private boolean closeProximity(Entity entityOne, Entity entityTwo) {
		boolean isCloseProximity = false;
		int sDisagreement = Math.abs(entityOne.getsPos() - entityTwo.getsPos());
		int eDisagreement = Math.abs(entityOne.getePos() - entityTwo.getePos());
		int sumDisagreement = sDisagreement + eDisagreement;
		if (sumDisagreement > 0 && sumDisagreement < 4) {
			isCloseProximity = true;
		}
		return isCloseProximity;
	}
	
	private void repositionEntity(Entity entityOne, Entity entityTwo) {
		entityTwo.setsPos(entityOne.getsPos());
		entityTwo.setePos(entityOne.getePos());
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
		
		// Reference: Handbook of Inter-Rater Reliability, 4th Edition: 
		// The Definitive Guide to ...   Page. 44-47
	
		// Organize information into a contingency table
		// The way of thinking is each Annotation can be one of ten types
		// since there are nine unique possible entity designations
		// and an added possibility that one expert with disagree or "miss"
		// something the other expert annotated.  (call this Missing)
		//
		formulateContigencyTable();
		
		
		//    Convert the table to probability matrix
		//
		int numRows = contingencyMatrix.length;
		int numCols = contingencyMatrix.length;
		double N = 0.0d;
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				N += contingencyMatrix[row][col];
			}
		}
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				contingencyMatrix[row][col] /= N;
			}
		}
		
		// compute the probability of missing
		double probabilityOfMissing = 0.0d;
		for (int row = 0; row < numRows; row++) {
			probabilityOfMissing += contingencyMatrix[row][numCols-1];
		}
		for (int col = 0; col < numCols; col++) {
				probabilityOfMissing += contingencyMatrix[numRows-1][col];
		}
		
		// compute the probability of agreement
		double probabilityOfAgreement = 0.0d;
		for (int diag = 0; diag < contingencyMatrix.length; diag++) {
			probabilityOfAgreement += (contingencyMatrix[diag][diag] / (1.0d - probabilityOfMissing));
		}

		// compute row marginal probabilities for percent chance agreement
		rowMarginalPercentChanceAgreement = new double[numRows];
		for (int row = 0; row < numRows; row++) {
			rowMarginalPercentChanceAgreement[row] = 0.0d;
			for (int col = 0; col < numCols; col++) {
				rowMarginalPercentChanceAgreement[row] += contingencyMatrix[row][col];
			}
		}

		// compute column marginal probabilities for percent chance agreement
		colMarginalPercentChanceAgreement = new double[numCols];
		for (int col = 0; col < numCols; col++) {
			colMarginalPercentChanceAgreement[col] = 0.0d;
			for (int row = 0; row < numRows; row++) {
				colMarginalPercentChanceAgreement[col] += contingencyMatrix[row][col];
			}
		}
		
		// Compute the sum of the expected frequencies of agreement by chance
		expectedPercentChanceAgreement = 0.0d;
		for (int diag = 0; diag < numRows; diag++) {
			expectedPercentChanceAgreement += (rowMarginalPercentChanceAgreement[diag] * colMarginalPercentChanceAgreement[diag]);
		}
		
		// Compute Kappa
		kappa = (probabilityOfAgreement - expectedPercentChanceAgreement) /
				(1.0d - expectedPercentChanceAgreement);
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
			
			Integer row = categoryIndex.get(rowType);
			Integer col = categoryIndex.get(colType);
			if (row != null && col != null) {
				contingencyMatrix[row][col] = contingencyMap.get(contingencyKey);
			}
		}
		System.out.println(prettyFormatMatrix("ContingencyMatrix:", contingencyMatrix));
	}

	private void deriveCategories() {
		if (categories.isEmpty()) {
			final TreeSet<String> categorySorter = new TreeSet<String>();
			for (String contingencyKey : contingencyMap.keySet()) {
				String[] keyParts = contingencyKey.split(":");
				if (!keyParts[0].equals("Missing")) {
					categorySorter.add(keyParts[0]);
				}
				if (!keyParts[1].equals("Missing")) {
					categorySorter.add(keyParts[1]);
				}
			}
			categories.addAll(categorySorter);	
		}
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

	public double getKappa() {
		return kappa;
	}
	
	public void setCategories(LinkedHashSet<String> categories) {
		this.categories.addAll(categories);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\nCategories:\n");
		for (String category : categories) {
			sb.append("\t" + category + "\n");
		}
		sb.append(prettyFormatMatrix("Probability Matrix:", contingencyMatrix));
		sb.append(prettyFormatVector("Row Summations:", rowMarginalPercentChanceAgreement));
		sb.append(prettyFormatVector("Col Summations:", colMarginalPercentChanceAgreement));
		sb.append("\n\nsum of expected number of agreements is " + expectedPercentChanceAgreement);
		sb.append("\n\nkappa is " + kappa);
		
		return sb.toString();
	}

}
