package edu.pitt.dbmi.birads.brokprime;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;

public class UsedReportMarker {
	
	private BiradsDataSourceManager biradsDsm;

	public static void main(String[] args) {
		 UsedReportMarker usedReportMarker = new UsedReportMarker();
		 usedReportMarker.execute();
	}

	private void execute() {
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
		File usedReportDirectory = new File("C:\\Users\\mitchellkj\\Desktop");
		usedReportDirectory = new File(usedReportDirectory, "snapshot011116");
		File[] usedReports = usedReportDirectory.listFiles();
		for (File usedReport : usedReports) {
			String accessionNumber = extractAccessionNumberFromReportName(usedReport.getName());
			markAccessionNumberUsed(accessionNumber);
			System.out.println(accessionNumber);
		}
		destroyBiradsDataSource();
		
	}
	
	private void destroyBiradsDataSource() {
		biradsDsm.destroy();
	}
	
	private String extractAccessionNumberFromReportName(String reportName) {
		String result = "-1";
		Pattern pattern = Pattern.compile("report(\\d+)\\D.*");
		Matcher matcher = pattern.matcher(reportName);
		if (matcher.matches()) {
			result = matcher.group(1);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void markAccessionNumberUsed(String accessionNumber) {
		Criteria c = biradsDsm.getSession().createCriteria(
				Report.class);
		long accessionNumberAsLong =  Long.valueOf(accessionNumber);
		String accessionNumberAsString = accessionNumberAsLong + "";
		Criterion c1 = Restrictions.like("accession", accessionNumberAsString, MatchMode.END);
		Criterion c2 =  Restrictions.like("applicationStatus", "SUMMARIZING");
		c.add(c1);
		c.add(c2);
		List<Report> reports = (List<Report>) c.list();
		for (Report report : reports) {
			if (Long.valueOf(report.getAccession()) == accessionNumberAsLong) {
				Transaction tx = biradsDsm.getSession().beginTransaction();
				report.setApplicationStatus("USED");
				biradsDsm.getSession().saveOrUpdate(report);
				biradsDsm.getSession().flush();
				tx.commit();
			}
		}
	
	}

}
