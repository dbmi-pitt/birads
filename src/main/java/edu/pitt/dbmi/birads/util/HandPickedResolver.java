package edu.pitt.dbmi.birads.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class HandPickedResolver {

	private final Set<String> idFilter = new HashSet<String>();

	public static void main(String[] args) {
		try {
			HandPickedResolver resolver = new HandPickedResolver();
			resolver.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HandPickedResolver() {
		;
	}

	public void execute() throws FileNotFoundException, IOException {
		// filterAndMoveRawFilesFromList();
		filterAndMoveExpertFilesFromList();
	}

	private void filterAndMoveExpertFilesFromList()
			throws FileNotFoundException, IOException {
		
		File expertSrcDirectory = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\completed");
		File expertTgtDirectory = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\expert");
		File[] expertSrcFiles = expertSrcDirectory.listFiles();
		Pattern verificationPattern = extractNumericIdsFromFilenames();
		for (File expertSrcFile : expertSrcFiles) {
			Matcher matcher = verificationPattern.matcher(expertSrcFile
					.getName());
			if (matcher.find()) {
				System.out.println("Moving expert entry "
						+ expertSrcFile.getName());
				File expertTgtFile = new File(expertTgtDirectory,
						expertSrcFile.getName());
				IOUtils.copy(new FileInputStream(expertSrcFile),
						new FileOutputStream(expertTgtFile));
			}
		}

	}

	private Pattern extractNumericIdsFromFilenames()
			throws FileNotFoundException, IOException {
		
		File handPickedList = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\birads102115.txt");
		List<String> handPickedEntries = IOUtils.readLines(new FileInputStream(
				handPickedList));
		Pattern pattern = Pattern.compile("^report(\\d{12})\\.txt.*$");
		for (String entry : handPickedEntries) {
			Matcher matcher = pattern.matcher(entry);
			if (matcher.matches()) {
				String numberAsString = matcher.group(1);
				idFilter.add(numberAsString);
			} else {
				System.err.println("Failed to extract number from " + entry);
			}
		}
		String patternString = StringUtils.join(idFilter, "|");
		System.out.println(patternString);
		return Pattern.compile(patternString);
	}

	private void filterAndMoveRawFilesFromList() throws FileNotFoundException,
			IOException {
		File handPickedList = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\birads102115.txt");
		List<String> handPickedEntries = IOUtils.readLines(new FileInputStream(
				handPickedList));
		File rawSrcDirectory = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\allraw");
		File rawTgtDirectory = new File(
				"C:\\Users\\kjm84\\Desktop\\snapshot102115\\raw");
		for (String entry : handPickedEntries) {
			System.out.println(entry);
			String chompedEntry = StringUtils.substringBeforeLast(entry, ".");
			File srcFile = new File(rawSrcDirectory, chompedEntry);
			File tgtFile = new File(rawTgtDirectory, entry);
			IOUtils.copy(new FileInputStream(srcFile), new FileOutputStream(
					tgtFile));
		}

	}

}
