package edu.pitt.dbmi.birads.brokprime;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;
import edu.pitt.dbmi.birads.brokprime.pojos.Statistics;


public class BiradsStatisticsCalculator {

	protected static final Logger logger = Logger.getLogger(BiradsStatisticsCalculator.class);

	private BiradsDataSourceManager biradsDsm;
	
	private Statistics statisticsObj = new Statistics();

	public static void main(String[] args) {
		BiradsStatisticsCalculator calculator = new BiradsStatisticsCalculator();
		calculator.execute();
	}

	public BiradsStatisticsCalculator() {
		;
	}

	public void execute() {
		try {
			biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
			int numberOfReportsProcessed = 0;
		    Collection<Report> unProcessedReports = getUnprocessedReportCollection();
			while (unProcessedReports != null) {
				for (Report report : unProcessedReports) {
					processReport(report);	
				}
				batchSaveReports(unProcessedReports);			
				numberOfReportsProcessed += unProcessedReports.size();
				if (numberOfReportsProcessed % 1000 == 0) {
					System.out.println("Calculated stats on " + numberOfReportsProcessed + " reports.");
				}
				if (numberOfReportsProcessed % 25000 == 0) {
						destroyBiradsDataSource();
						biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
				}
				unProcessedReports = getUnprocessedReportCollection();
			}
			
			// Save the Statistics 
			clearBiradsData(biradsDsm);
			Transaction tx = biradsDsm.getSession().beginTransaction();
			biradsDsm.getSession().saveOrUpdate(statisticsObj);
			biradsDsm.getSession().flush();
			tx.commit();
			
			
			destroyBiradsDataSource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clearBiradsData(BiradsDataSourceManager biradsDsm) {
		String queryString = "delete FROM STATISTICS";
		SQLQuery q = biradsDsm.getSession().createSQLQuery(queryString);
		q.executeUpdate();
	}
	
	private void processReport(Report report) throws IOException {
		try {
			int leftBIRADS = report.getLeftBIRADS();

			int rightBIRADS = report.getRightBIRADS();

			if (leftBIRADS != -1 || rightBIRADS != -1)
				statisticsObj.setExamsWithAtLeastOneBIRADS(statisticsObj.getExamsWithAtLeastOneBIRADS() + 1);

			if (leftBIRADS == 0)
				statisticsObj.setTotalBIRADS0(statisticsObj.getTotalBIRADS0() + 1);

			if (rightBIRADS == 0)
				statisticsObj.setTotalBIRADS0(statisticsObj.getTotalBIRADS0() + 1);

			if (leftBIRADS == 1)
				statisticsObj.setTotalBIRADS1(statisticsObj.getTotalBIRADS1() + 1);

			if (rightBIRADS == 1)
				statisticsObj.setTotalBIRADS1(statisticsObj.getTotalBIRADS1() + 1);
			
			if (leftBIRADS == 2)
				statisticsObj.setTotalBIRADS2(statisticsObj.getTotalBIRADS2() + 1);

			if (rightBIRADS == 2)
				statisticsObj.setTotalBIRADS2(statisticsObj.getTotalBIRADS2() + 1);
			
			if (leftBIRADS == 3)
				statisticsObj.setTotalBIRADS3(statisticsObj.getTotalBIRADS3() + 1);

			if (rightBIRADS == 3)
				statisticsObj.setTotalBIRADS3(statisticsObj.getTotalBIRADS3() + 1);
			
			if (leftBIRADS == 4)
				statisticsObj.setTotalBIRADS4(statisticsObj.getTotalBIRADS4() + 1);

			if (rightBIRADS == 4)
				statisticsObj.setTotalBIRADS4(statisticsObj.getTotalBIRADS4() + 1);
			
			if (leftBIRADS == 5)
				statisticsObj.setTotalBIRADS5(statisticsObj.getTotalBIRADS5() + 1);

			if (rightBIRADS == 5)
				statisticsObj.setTotalBIRADS5(statisticsObj.getTotalBIRADS5() + 1);
			
			if (leftBIRADS == 6)
				statisticsObj.setTotalBIRADS6(statisticsObj.getTotalBIRADS6() + 1);

			if (rightBIRADS == 6)
				statisticsObj.setTotalBIRADS6(statisticsObj.getTotalBIRADS6() + 1);

			if (leftBIRADS > 6)
				statisticsObj.setTotalBIRADSBogus(statisticsObj.getTotalBIRADSBogus() + 1);

			if (rightBIRADS > 6)
				statisticsObj.setTotalBIRADSBogus(statisticsObj.getTotalBIRADSBogus() + 1);

			report.setApplicationStatus("IDLING");
		}
		catch (Exception x) {
			System.err.println("Error s report" + report.getAccession());
		}
	}
	
	private void batchSaveReports(Collection<Report> reports) {
		Session biradsSession = biradsDsm.getSession();
		Transaction tx = biradsSession.beginTransaction();
		for (Report report : reports) {
			biradsSession.saveOrUpdate(report);
		}
		biradsSession.flush();
		tx.commit();	
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Report> getUnprocessedReportCollection() {
		Criteria c = biradsDsm.getSession().createCriteria(Report.class);
		c.add(Restrictions.like("applicationStatus", "SUMMARIZING"));
		c.setMaxResults(1000);
		Collection<Report> reports = c.list();
		if (reports.isEmpty()) {
			return null;
		} else {
			return reports;
		}
	}

	private void destroyBiradsDataSource() {
		biradsDsm.destroy();
	}


}
