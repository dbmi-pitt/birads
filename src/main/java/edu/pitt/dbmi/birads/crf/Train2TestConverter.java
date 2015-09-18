package edu.pitt.dbmi.birads.crf;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class Train2TestConverter {

	public static void main(String[] args) {
		convertTrainFiles();
	}
	
	private static void convertTrainFiles() {
		try {
			File directory = new File(
					"C:\\Users\\kjm84\\Desktop\\crfs\\CRF-train");
			File[] files = directory.listFiles();
			for (File file : files) {
				String fileAsString = FileUtils.readFileToString(file, "utf-8");
				StringBuffer output = new StringBuffer();
				String[] lines = fileAsString.split("\n");
				for (String line : lines) {
					String[] tokens = line.split("\t");
					String spaceSeparated = StringUtils.join(tokens, " ");
					output.append(spaceSeparated + "\n");
				}
				FileUtils.writeStringToFile(file, output.toString());
				System.out.println("Wrote file " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void convertTestFiles() {
		try {
			File directory = new File(
					"C:\\Users\\kjm84\\Desktop\\crfs\\CRF-test");
			File[] files = directory.listFiles();
			for (File file : files) {
				String fileAsString = FileUtils.readFileToString(file, "utf-8");
				StringBuffer output = new StringBuffer();
				String[] lines = fileAsString.split("\n");
				for (String line : lines) {
					String[] tokens = line.split("\t");
					final String[] reversedTokens = new String[tokens.length-1];
					reversedTokens[0] = tokens[1];
					reversedTokens[1] = tokens[2];
					reversedTokens[2] = tokens[0];
					String reversed = StringUtils.join(reversedTokens, " ");
					output.append(reversed + "\n");
				}
				FileUtils.writeStringToFile(file, output.toString());
				System.out.println("Wrote file " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
