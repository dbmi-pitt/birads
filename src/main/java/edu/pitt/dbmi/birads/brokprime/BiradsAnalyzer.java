package edu.pitt.dbmi.birads.brokprime;

import java.io.FileNotFoundException;
import java.io.IOException;

public class BiradsAnalyzer {
	
	private static boolean isSeeding = false;
	private static boolean isProcessing = false;
	private static boolean isDocLevelReporting = true;
	private static boolean isSummaryReporting = false;

	public static void main(String[] args) {
		
		/*
		 * Pull Random Sample of DocIds from TIES Lucene
		 */
		if (isSeeding) {
			ReportLuceneSeeder reportSeeder = new ReportLuceneSeeder();
			reportSeeder.execute();
		}
		
		
		/*
		 * Load and Process Document Text with Brok
		 */
		
		String tiesUser = args[0];
		String tiesPassword = args[1];
		if (isProcessing) {
			ReportLoader reportLoader = new ReportLoader();
			reportLoader.setTiesUser(tiesUser);
			reportLoader.setTiesPassword(tiesPassword);
			reportLoader.execute();
		}
	
		
		/*
		 * Write back report specific results
		 */
		if (isDocLevelReporting) {
			ReportWriter reportWriter = new ReportWriter();
			reportWriter.setRandomizationMode(ReportWriter.CONST_RANDOMIZATION_MODE_HETEROGENI);
			reportWriter.setRandomizationMode(ReportWriter.CONST_RANDOMIZATION_MODE_HOMOGENOUS);
			reportWriter.setTiesUser(tiesUser);
			reportWriter.setTiesPassword(tiesPassword);
			try {
				reportWriter.execute();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		/*
		 * Statistical Summary
		 */
		if (isSummaryReporting) {
			BiradsStatisticsCalculator calculator = new BiradsStatisticsCalculator();
			calculator.execute();
		}
		

	}

}
