package edu.pitt.dbmi.birads.crf;

public class NominalClassificationResultTabulator {
	
	private String targetClass;
	
	private String currentFileName = "";
	private int currentToken = -1;
	private String goldClassification;
	private String machClassification;
	
	private double tp = 0.0d;
	private double fp = 0.0d;
	private double tn = 0.0d;
	private double fn = 0.0d;
	
	private double precision = -1.0d;
	private double recall = -1.0d;
	
	private StringBuilder errorLogger = new StringBuilder();
	
	public void tally() {
		if (goldClassification.equals(targetClass) && machClassification.equals(targetClass)) {
			tp++;
		} else if (!goldClassification.equals(targetClass) && machClassification.equals(targetClass)) {
			errorLogger.append("False Positive:");
			errorLogger.append(currentFileName + " ");
			errorLogger.append(currentToken + " ");
			errorLogger.append(goldClassification + " ");
			errorLogger.append(machClassification + "\n");
			fp++;
		} else if (!goldClassification.equals(targetClass) && !machClassification.equals(targetClass)) {
			tn++;
		} else if (goldClassification.equals(targetClass) && !machClassification.equals(targetClass)) {
			errorLogger.append("False Negative:");
			errorLogger.append(currentFileName + " ");
			errorLogger.append(currentToken + " ");
			errorLogger.append(goldClassification + " ");
			errorLogger.append(machClassification + "\n");
			fn++;
		}
	}
	
	public void calculateMicroStatistics() {
		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
	}
	
	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public double getTp() {
		return tp;
	}

	public double getFp() {
		return fp;
	}

	public double getTn() {
		return tn;
	}

	public double getFn() {
		return fn;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public StringBuilder getErrorLogger() {
		return errorLogger;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

	public int getCurrentToken() {
		return currentToken;
	}

	public void setCurrentToken(int currentToken) {
		this.currentToken = currentToken;
	}

	public void setGoldClassification(String goldClassification) {
		this.goldClassification = goldClassification;
	}

	public void setMachClassification(String machClassification) {
		this.machClassification = machClassification;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Nominal Classification Results for " + targetClass + "\n");
		sb.append("\t precision: " + precision + " recall: " + recall);
		sb.append("\ttp: " + tp);
		sb.append(" fp: " + fp);
		sb.append(" tn: " + tn);
		sb.append(" fn: " + fn);
		sb.append("\n");
		sb.append("Errors: \n");
		sb.append(errorLogger.toString());
		return sb.toString();
	}

}
