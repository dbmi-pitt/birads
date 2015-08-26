package edu.pitt.dbmi.birads.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class PassOneResolver {

	public static void main(String[] args) {
		PassOneResolver passOneResolver = new PassOneResolver();
		passOneResolver.execute(args);
	}

	private String codeBookFilePath = null;
	private String passOneFootPrintPath = null;
	
	private Map<String, String> resolutionMap = new HashMap<String, String>();

	private void execute(String[] args) {
		try {
			tryExcecute(args);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void tryExcecute(String[] args) throws IOException {
		codeBookFilePath = args[0];
		passOneFootPrintPath = args[1];
		buildResolutionMap();
		traverseDirectories();
		

	}

	private void traverseDirectories() {
		File topLevelDirectory = new File(passOneFootPrintPath);
		if (topLevelDirectory.exists() && topLevelDirectory.isDirectory()) {
			File[] files = topLevelDirectory.listFiles();
			for (File file : files) {
				traverse(file);
			}
		}
	}

	private void traverse(File entry) {
		if (entry.isDirectory()) {
			entry = renameEntry(entry);
			File[] childEntries = entry.listFiles();
			for (File childEntry : childEntries) {
				traverse(childEntry);
			}		
		}
		else if (entry.isFile()) {
			renameEntry(entry);
		}
	}

	private File renameEntry(File entry) {
		File newEntry = entry;
		String origPath = entry.getPath();
		String newPath = origPath;
		Pattern pattern = Pattern.compile("(doc\\d{3})");
		Matcher matcher = pattern.matcher(origPath);
		if (matcher.find()) {
			String docKey = matcher.group(1);
			String accessionKey = resolutionMap.get(docKey);
			if (accessionKey != null) {
				newPath = origPath.replaceAll(docKey, accessionKey);
				File origFile = new File(origPath);
				newEntry = new File(newPath);
				origFile.renameTo(newEntry);
				System.out.println("Renamed");
				System.out.println("\t" + origPath);
				System.out.println("\t" + newPath);
			}		
		}	
		return newEntry;
	}

	private void buildResolutionMap() throws IOException {
		List<String> lines = FileUtils.readLines(new File(codeBookFilePath));
		for (String line : lines) {
			if (line.matches("^\\s*item.*")) {
				System.out.println("Skipping headers...");
			}
			else {
				Pattern pattern = Pattern.compile("(\\d+)\\D+(\\d+)");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					long id = Long.valueOf(matcher.group(1));
					id -= 1;
					long accession = Long.valueOf(matcher.group(2));
					String docKey = "doc" + StringUtils.leftPad(id+"", 3, "0");
					String accessionKey = "report" + StringUtils.leftPad(accession+"", 12, "0");
					resolutionMap.put(docKey, accessionKey);
				}
			}
		}
	}

}
