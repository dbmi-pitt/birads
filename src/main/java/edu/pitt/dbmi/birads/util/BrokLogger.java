package edu.pitt.dbmi.birads.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BrokLogger {
	
	private static final String logFilePath = "C:\\Users\\mitchellkj\\Desktop\\birad_reports\\brok.log";
	
	private static final BrokLogger brokLogger = new BrokLogger();

	private BrokLogger() {
		;
	}
	
	public static BrokLogger getInstance() {
		return brokLogger;
	}
	
	public void log(String text) {
		File file = new File(logFilePath);
		try {
			boolean isAppending = true;
			FileWriter writer = new FileWriter(file, isAppending);
			writer.write(text + System.getProperty("line.separator"));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
