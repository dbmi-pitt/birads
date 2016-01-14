package edu.pitt.dbmi.birads.brokprime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentTypeImpl;

public class DocumentTypeFixer {

	protected static final Logger logger = Logger.getLogger(DocumentTypeFixer.class);

	private CaTIES_DataSourceManager tiesDsm;
	private BiradsDataSourceManager biradsDsm;

	private String tiesUser = null;
	private String tiesPassword = null;

	public static void main(String[] args) {
		String tiesUser = args[0];
		String tiesPassword = args[1];
		DocumentTypeFixer documentTypeFixer = new DocumentTypeFixer();
		documentTypeFixer.setTiesUser(tiesUser);
		documentTypeFixer.setTiesPassword(tiesPassword);
		try {
			documentTypeFixer.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws FileNotFoundException, IOException {
		establishTiesDataSource();
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
		modifyDocumentTypes();
		destroyBiradsDataSource();
		destroyTiesDataSource();
	}

	@SuppressWarnings("unchecked")
	private void modifyDocumentTypes() {
		Criteria c = biradsDsm.getSession().createCriteria(Report.class);
		c.add(Restrictions.like("documentType", "UNDEFINED"));
		c.addOrder(Order.asc("accession"));
		c.setMaxResults(10000);
		int numberProcessed = 0;
		while (true) {
			List<Report> reports = c.list();
			if (reports.isEmpty()) {
				break;
			} else {
				for (Report report : reports) {
					String documentTypeString = pullDocumentTypeFromTies(report.getAccession());
					if (documentTypeString.equals("PATHOLOGY")) {
						System.out.println("Updating to Pathology");
					}
					report.setDocumentType(documentTypeString);
					biradsDsm.getSession().saveOrUpdate(report);
				}
				Transaction tx = biradsDsm.getSession().beginTransaction();
				biradsDsm.getSession().flush();
				tx.commit();
				numberProcessed += reports.size();
				System.out.println("Processed " + numberProcessed);
			}
		}
	}

	private void establishTiesDataSource() {
		tiesDsm = new CaTIES_DataSourceManager();
		tiesDsm.setCurrentMode(MODE.CLIENT);
		tiesDsm.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		tiesDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		tiesDsm.setHibernateConnectionUrl("jdbc:mysql://ties-db.isd.upmc.edu:3306/ties_public");
		tiesDsm.setHibernateUserName(getTiesUser());
		tiesDsm.setHibernateUserPassword(getTiesPassword());

	}

	private void destroyTiesDataSource() {
		tiesDsm.destroy();
	}

	private String pullDocumentTypeFromTies(String documentId) {
		String result = "000";
		try {
			Session session = tiesDsm.getSession();
			DocumentImpl dbDoc = getDocument(session, documentId);
			if (dbDoc != null) {
				DocumentTypeImpl dbDocType = dbDoc.getDocumentType();
				result = dbDocType.getName();
				session.evict( dbDocType);
				session.evict(dbDoc);
			}
			else {
				result = "MISSING_TIES_ID";
			}		
		} catch (Exception x) {
			System.out.println("Failed to read " + documentId);
			x.printStackTrace();
		}	
		return  result;
	}

	private DocumentImpl getDocument(Session session, String documentId) {
		String queryString = "SELECT d FROM DocumentImpl d" + " WHERE d.id = ? ";
		Query q = session.createQuery(queryString);
		q.setLong(0, Long.valueOf(documentId));
		return (DocumentImpl) q.uniqueResult();
	}

	private void destroyBiradsDataSource() {
		biradsDsm.destroy();
	}

	public String getTiesUser() {
		return tiesUser;
	}

	public void setTiesUser(String tiesUser) {
		this.tiesUser = tiesUser;
	}

	public String getTiesPassword() {
		return tiesPassword;
	}

	public void setTiesPassword(String tiesPassword) {
		this.tiesPassword = tiesPassword;
	}

}
