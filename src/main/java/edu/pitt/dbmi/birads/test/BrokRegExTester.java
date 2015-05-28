package edu.pitt.dbmi.birads.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.birads.util.BrokLogger;

public class BrokRegExTester {
	
	public static void main(String[] args) {
		BrokRegExTester tester = new BrokRegExTester();
		tester.execute();
	}

	private void execute() {
		
		String text = "foo 3.23x3 x 5.3 cm";

		// Pattern p =
		Pattern.compile(
				".*\\b(left|right|overall|combined|bilateral)\\b.*\\b(birads|bi-rads|category)\\b.*\\b([0-6]{1})([abc]?)\\b.*",
				Pattern.DOTALL);

		// Pattern p2 =
		Pattern.compile(
				".{0,20}\\b(left|right|bilateral|right\\s+and\\s+left|left\\s+and\\s+right)\\b.{0,10}?\\b(ultrasound|us|sonogram|sonography|mri|magnetic resonance imaging|mammogram|mammography|radiograph|radiography)\\b.*",
				Pattern.DOTALL);

		Pattern p3 = Pattern
				.compile("\\b([0-9]+(\\.[0-9]+)?\\s*x\\s*)+([0-9]+(\\.[0-9]+)?)?");

		Matcher m = p3.matcher(text);
		BrokLogger.getInstance().log(text + " match? " + m.matches());
		System.out.println(text + " match? " + m.matches());

		if (m.matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				BrokLogger.getInstance().log("Match " + i + ": " + m.group(i));
				System.out.println("Match " + i + ": " + m.group(i));
			}
		}

		String out = text.replaceAll(
				"\\b([0-9]+(\\.[0-9]+)?\\s*x\\s*)+([0-9]+(\\.[0-9]+)?)?", "");

		System.out.println(out);
		
	}

}
