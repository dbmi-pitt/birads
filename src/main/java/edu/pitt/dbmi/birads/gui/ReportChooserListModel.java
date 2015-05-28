package edu.pitt.dbmi.birads.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brok.BiradsDataSourceManager;
import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;

public class ReportChooserListModel implements ListModel<ReportWidget> {	
	
	private BiradsDataSourceManager biradsDsm;
	private CaTIES_DataSourceManager tiesDsm;
	private String tiesUsername;
	private String tiesPassword;
	
	private final ArrayList<ReportWidget> reports = new ArrayList<ReportWidget>();
	private final ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

	public ReportChooserListModel() {
		;
	}
	
	public void initialize() {
		establishBiradsDataSource();
		establishTiesDataSource();
		final Collection<Report> dbReports = pullReportsFromDatabase();
		for (Report dbReport : dbReports) {
			ReportWidget reportWidget = new ReportWidget();
			reportWidget.setReport(dbReport);
			String body = pullDocumentTextFromTies(reportWidget.getReport().getAccession());
			reportWidget.getReport().setBody(body);
			reports.add(reportWidget);
		}
	}
	
	private String pullDocumentTextFromTies(String accession) {
		Session session = tiesDsm.getSession();
		DocumentImpl dbDoc = getDocument(session, accession);
		DocumentDataImpl dbDocData = dbDoc.getDocumentData();
		String dbDocText = dbDocData.getDocumentText();
		session.evict(dbDocData);
		session.evict(dbDoc);
		return dbDocText;
	}
	
	private DocumentImpl getDocument(Session session, String documentId) {
		String queryString = "SELECT d FROM DocumentImpl d"
				+ " WHERE d.id = ? ";
		Query q = session.createQuery(queryString);
		q.setLong(0, new Long(documentId));
		return (DocumentImpl) q.uniqueResult();
	}

	
	@SuppressWarnings("unchecked")
	private Collection<Report> pullReportsFromDatabase() {
		Criteria c = biradsDsm.getSession().createCriteria(Report.class);
		c.add(Restrictions.like("applicationStatus", "EMITTING_%"));
		c.addOrder(Order.asc("accession"));
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
	
	private void establishTiesDataSource() {
		tiesDsm = new CaTIES_DataSourceManager();
		tiesDsm.setCurrentMode(MODE.CLIENT);
		tiesDsm.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		tiesDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		tiesDsm.setHibernateConnectionUrl("jdbc:mysql://ties-db.isd.upmc.edu:3306/ties_public");
		tiesDsm.setHibernateUserName(getTiesUsername());
		tiesDsm.setHibernateUserPassword(getTiesPassword());

	}
	


	public int getSize() {
		return reports.size();
	}
	
	public ReportWidget getElementAt(int index) {
		return reports.get(index);
	}


	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
		
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public String getTiesUsername() {
		return tiesUsername;
	}

	public void setTiesUsername(String tiesUsername) {
		this.tiesUsername = tiesUsername;
	}

	public String getTiesPassword() {
		return tiesPassword;
	}

	public void setTiesPassword(String tiesPassword) {
		this.tiesPassword = tiesPassword;
	}	
	

}
