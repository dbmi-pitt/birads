package edu.pitt.dbmi.birads.test;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import edu.pitt.dbmi.birads.brok.BiradsDataSourceManager;
import edu.pitt.dbmi.birads.brok.pojos.Report;

public class ReportCrudTester {

	public static void main(String[] args) {
		ReportCrudTester crudTester = new ReportCrudTester();
		crudTester.execute();
	}

	private void execute() {
		BiradsDataSourceManager biradsDsm = new BiradsDataSourceManager();
		biradsDsm.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		biradsDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		biradsDsm
				.setHibernateConnectionUrl("jdbc:mysql://localhost:3306/birads");
		biradsDsm.setHibernateUserName("birads");
		biradsDsm.setHibernateUserPassword("birads");
		biradsDsm.setHibernateHbm2ddlAuto("update");
		
		clearBiradsData(biradsDsm);
		
		// Save a Report
		Report report = new Report();
		report.setAccession("0000101");
		report.setApplicationStatus("PROCESSING");
		report.setNumberBreastsImaged(2);
		report.setLeftResult("a left result string");
		report.setRightResult("a right result string");
		report.setBody("body text");
		report.setImpression("impression text");
		report.setLeftBIRADS(-1);
		report.setRightBIRADS(-1);
		report.setBilateralBIRADS(-1);
		report.setNonspecificBIRADS(-1);
		report.setLeftSubCategory("leftSub");
		report.setRightSubCategory("rightSub");
		report.setBilateralSubCategory("bilateralSub");
		report.setNonspecificSubCategory("nonspecificSub");
		report.setFoundLeftBIRADS(-1);
		report.setFoundRightBIRADS(-1);
		report.setFoundBilateralBIRADS(-1);
		report.setFoundNonspecificBIRADS(-1);
		report.setHasAddendum(true);
	
		Transaction tx = biradsDsm.getSession().beginTransaction();
		biradsDsm.getSession().saveOrUpdate(report);
		biradsDsm.getSession().flush();
		tx.commit();
		
		String queryString = "SELECT r FROM Report r"
				+ " WHERE r.accession = ? ";
		Query q = biradsDsm.getSession().createQuery(queryString);
		q.setString(0,"0000101");
		Report result = (Report) q.uniqueResult();
		System.out.println(result);

	}
	
	private void clearBiradsData(BiradsDataSourceManager biradsDsm) {
		String queryString = "delete FROM REPORT";
		SQLQuery q = biradsDsm.getSession().createSQLQuery(queryString);
		q.executeUpdate();
	}

}
