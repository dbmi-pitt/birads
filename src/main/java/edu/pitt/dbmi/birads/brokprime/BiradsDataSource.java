package edu.pitt.dbmi.birads.brokprime;

public class BiradsDataSource {
	
	private static BiradsDataSource singleton = null;
	private BiradsDataSourceManager biradsDsm;
	
	public static BiradsDataSource getInstance() {
		if (singleton == null) {
			singleton = new BiradsDataSource();
		}
		return singleton;
	}
	
	private BiradsDataSource()  {
	}
	
	public BiradsDataSourceManager getBiradsDataSourceManager() {
		biradsDsm = new BiradsDataSourceManager();
		biradsDsm.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		biradsDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		biradsDsm
				.setHibernateConnectionUrl("jdbc:mysql://localhost:3306/birads_prime");
		biradsDsm.setHibernateHbm2ddlAuto("update");
		biradsDsm.setHibernateShowSql("false");
		biradsDsm.setHibernateUserName("birads");
		biradsDsm.setHibernateUserPassword("birads");
		return biradsDsm;
	}

}
