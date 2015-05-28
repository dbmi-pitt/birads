package edu.pitt.dbmi.birads.brok;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.pitt.dbmi.birads.brok.pojos.Statistics;

public class BiradsStatisticsCalculator {

	protected static final Logger logger = Logger.getLogger(BiradsStatisticsCalculator.class);

	private BiradsDataSourceManager biradsDsm;
	
	private Statistics s = new Statistics();

	public static void main(String[] args) {
		BiradsStatisticsCalculator calculator = new BiradsStatisticsCalculator();
		calculator.execute();
	}

	public BiradsStatisticsCalculator() {
		;
	}

	public void execute() {
		try {
			establishBiradsDataSource();
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
						establishBiradsDataSource();
				}
				unProcessedReports = getUnprocessedReportCollection();
			}
			
			// Save the Statistics 
			clearBiradsData(biradsDsm);
			Transaction tx = biradsDsm.getSession().beginTransaction();
			biradsDsm.getSession().saveOrUpdate(s);
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
				s.setExamsWithAtLeastOneBIRADS(s.getExamsWithAtLeastOneBIRADS() + 1);

			if (leftBIRADS == 0)
				s.setTotalBIRADS0(s.getTotalBIRADS0() + 1);

			if (rightBIRADS == 0)
				s.setTotalBIRADS0(s.getTotalBIRADS0() + 1);

			if (leftBIRADS == 1)
				s.setTotalBIRADS1(s.getTotalBIRADS1() + 1);

			if (rightBIRADS == 1)
				s.setTotalBIRADS1(s.getTotalBIRADS1() + 1);
			
			if (leftBIRADS == 2)
				s.setTotalBIRADS2(s.getTotalBIRADS2() + 1);

			if (rightBIRADS == 2)
				s.setTotalBIRADS2(s.getTotalBIRADS2() + 1);
			
			if (leftBIRADS == 3)
				s.setTotalBIRADS3(s.getTotalBIRADS3() + 1);

			if (rightBIRADS == 3)
				s.setTotalBIRADS3(s.getTotalBIRADS3() + 1);
			
			if (leftBIRADS == 4)
				s.setTotalBIRADS4(s.getTotalBIRADS4() + 1);

			if (rightBIRADS == 4)
				s.setTotalBIRADS4(s.getTotalBIRADS4() + 1);
			
			if (leftBIRADS == 5)
				s.setTotalBIRADS5(s.getTotalBIRADS5() + 1);

			if (rightBIRADS == 5)
				s.setTotalBIRADS5(s.getTotalBIRADS5() + 1);
			
			if (leftBIRADS == 6)
				s.setTotalBIRADS6(s.getTotalBIRADS6() + 1);

			if (rightBIRADS == 6)
				s.setTotalBIRADS6(s.getTotalBIRADS6() + 1);

			if (leftBIRADS > 6)
				s.setTotalBIRADSBogus(s.getTotalBIRADSBogus() + 1);

			if (rightBIRADS > 6)
				s.setTotalBIRADSBogus(s.getTotalBIRADSBogus() + 1);

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

	private void establishBiradsDataSource() {
		biradsDsm = new BiradsDataSourceManager();
		biradsDsm.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		biradsDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		biradsDsm
				.setHibernateConnectionUrl("jdbc:mysql://localhost:3306/birads");
		biradsDsm.setHibernateShowSql("false");
		biradsDsm.setHibernateUserName("birads");
		biradsDsm.setHibernateUserPassword("birads");
	}

	private void destroyBiradsDataSource() {
		biradsDsm.destroy();
	}


}
