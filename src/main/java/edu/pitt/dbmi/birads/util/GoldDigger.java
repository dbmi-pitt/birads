package edu.pitt.dbmi.birads.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class GoldDigger {
	
	private int numberGoldExpected = 0;
	private int numberGoldFound = 0;
	
	private int numberRawExpected = 0;
	private int numberRawFound = 0;

	public static void main(String[] args) {
		GoldDigger digger = new GoldDigger();
		try {
			digger.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void execute() throws IOException {
	
		File analysisDirectory = new File("C:\\Users\\kjm84\\Desktop\\birads_decks\\analysis160317");
		File analysisExpDirectory = new File(analysisDirectory, "exp");
		File analysisRawDirectory = new File(analysisDirectory, "raw");
		
		FileUtils.cleanDirectory(analysisExpDirectory);
		FileUtils.cleanDirectory(analysisRawDirectory);
		
		File anaforaDirectory = new File("C:\\Users\\kjm84\\Desktop\\birads_decks\\snapshot160317");
		File goldSetFile = new File("C:\\ws\\ws-birads-1\\birads\\resources\\goldSet.txt");
		List<String> fileLines = FileUtils.readLines(goldSetFile);

		for (String fileName : fileLines) {
			numberGoldExpected++;
			numberRawExpected++;
			String fileRoot = StringUtils.substringBefore(fileName, ".");
			String goldName = fileName;
			if (goldName.matches("report\\d+\\.birads\\.sec113\\.adjudicated\\.xml")) {
				goldName = fileRoot + ".birads-Adjudication.sec113.completed.xml";
			}
			else if (goldName.matches("report\\d+\\.birads\\.Rebeccaj\\.adjudicated\\.xml")) {
				goldName = fileRoot + ".birads-Adjudication.Rebeccaj.completed.xml";
			}
			File anaforaGoldFile = new File(anaforaDirectory, goldName);
			if (anaforaGoldFile.exists() && anaforaGoldFile.isFile()) {
				File expFile = new File(analysisExpDirectory, fileRoot +  ".birads.sec113.completed.xml");
				FileUtils.copyFile(anaforaGoldFile, expFile);
				numberGoldFound++;
			}
			
			File anaforaRawFile = new File(anaforaDirectory, fileRoot);		
			if (anaforaRawFile.exists() && anaforaRawFile.isFile()) {
				File analysisRawFile = new File(analysisRawDirectory, fileRoot + ".txt");
				FileUtils.copyFile(anaforaRawFile, analysisRawFile);
				numberRawFound++;
			}
			
		}	
		if (numberGoldExpected == numberGoldFound) {
			System.out.println("Found all the golds. " + numberGoldFound + " in all.");
		}
		else {
			System.err.println("Expected " + numberGoldExpected);
			System.err.println("Got only " + numberGoldFound);
			
		}
		
		if (numberRawExpected == numberRawFound) {
			System.out.println("Found all the Raws. " + numberRawFound + " in all.");
		}
		else {
			System.err.println("Expected " + numberRawExpected);
			System.err.println("Got only " + numberRawFound);
			
		}
	}

}
