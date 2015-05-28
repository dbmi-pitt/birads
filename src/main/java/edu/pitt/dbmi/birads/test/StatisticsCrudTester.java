package edu.pitt.dbmi.birads.test;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import edu.pitt.dbmi.birads.brok.BiradsDataSourceManager;
import edu.pitt.dbmi.birads.brok.pojos.Statistics;


public class StatisticsCrudTester {

	public static void main(String[] args) {
		StatisticsCrudTester crudTester = new StatisticsCrudTester();
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
		Statistics statistics = new Statistics();
		statistics.setExamsWithAtLeastOneBIRADS(25);
		statistics.setTotalBIRADS0(50);
		statistics.setTotalBIRADS1(100);
		statistics.setTotalBIRADS2(150);
		statistics.setTotalBIRADS3(200);
		statistics.setTotalBIRADS4(250);
		statistics.setTotalBIRADS5(300);
		statistics.setTotalBIRADS6(350);
		statistics.setTotalBIRADSBogus(400);
	
		Transaction tx = biradsDsm.getSession().beginTransaction();
		biradsDsm.getSession().saveOrUpdate(statistics);
		biradsDsm.getSession().flush();
		tx.commit();
		
		String queryString = "SELECT s FROM Statistics s";
		Query q = biradsDsm.getSession().createQuery(queryString);
		Statistics result = (Statistics) q.uniqueResult();
		System.out.println(result);

	}
	
	private void clearBiradsData(BiradsDataSourceManager biradsDsm) {
		String queryString = "delete FROM STATISTICS";
		SQLQuery q = biradsDsm.getSession().createSQLQuery(queryString);
		q.executeUpdate();
	}

}
