package edu.pitt.dbmi.birads.crf;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.util.MalletLogger;

public class LoggingExample {

	private static Logger logger = MalletLogger
			.getLogger(CRFTrainerByLabelLikelihood.class.getName());

	static {
		try {
			logger.setLevel(Level.OFF);

			Formatter formatter = new Formatter() {

				@Override
				public String format(LogRecord arg0) {
					StringBuilder b = new StringBuilder();
					b.append(new Date());
					b.append(" ");
					b.append(arg0.getSourceClassName());
					b.append(" ");
					b.append(arg0.getSourceMethodName());
					b.append(" ");
					b.append(arg0.getLevel());
					b.append(" ");
					b.append(arg0.getMessage());
					b.append(System.getProperty("line.separator"));
					return b.toString();
				}

			};

			Handler fh = new FileHandler("test.txt");
			fh.setFormatter(formatter);
			logger.addHandler(fh);

			Handler ch = new ConsoleHandler();
			ch.setFormatter(formatter);
			logger.addHandler(ch);

			LogManager lm = LogManager.getLogManager();
			lm.addLogger(logger);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logger.info("why does my test application use the standard console logger ?\n"
				+ " I want only my console handler (Handler ch)\n "
				+ "how can i turn the standard logger to the console off. ??");
	}
}