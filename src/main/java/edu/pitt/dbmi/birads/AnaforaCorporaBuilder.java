package edu.pitt.dbmi.birads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brok.BiradsDataSourceManager;
import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;

public class AnaforaCorporaBuilder {

	protected static final Logger logger = Logger
			.getLogger(AnaforaCorporaBuilder.class);
	
	
	private String outputDirectoryPath = "C:\\Users\\mitchellkj\\Desktop\\birads_reports";
	private String tiesUser = null;
	private String tiesPassword = null;
	
	private String corpusName;
	private int corpusSelectionSize;

	private CaTIES_DataSourceManager tiesDsm;
	private BiradsDataSourceManager biradsDsm;

	public static void main(String[] args) {
	
		try {
			AnaforaCorporaBuilder anaforaCorporaBuilder = new AnaforaCorporaBuilder();
			anaforaCorporaBuilder.setTiesUser(args[0]);
			anaforaCorporaBuilder.setTiesPassword(args[1]);
			anaforaCorporaBuilder.setCorpusName("practice");
			anaforaCorporaBuilder.setCorpusSelectionSize(5);
			anaforaCorporaBuilder.execute();
			
			anaforaCorporaBuilder = new AnaforaCorporaBuilder();
			anaforaCorporaBuilder.setTiesUser(args[0]);
			anaforaCorporaBuilder.setTiesPassword(args[1]);
			anaforaCorporaBuilder.setCorpusName("production");
			anaforaCorporaBuilder.setCorpusSelectionSize(60);
			anaforaCorporaBuilder.execute();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void execute() throws IOException {
		establishTiesDataSource();
		establishBiradsDataSource();
		Collection<Report> unProcessedReports = getUnprocessedReportCollection(getCorpusSelectionSize());
		pullReportTextFromTies(unProcessedReports);
		buildAnaforaDirectory(getCorpusName(), unProcessedReports);
		destroyBiradsDataSource();
		destroyTiesDataSource();
	}

	private void pullReportTextFromTies(Collection<Report> unProcessedReports) {
		for (Report report : unProcessedReports) {
			String documentText = pullDocumentTextFromTies(report
					.getAccession());
			report.setBody(documentText);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Report> getUnprocessedReportCollection(int selectionSize) {
		List<Report> accumulator = new ArrayList<Report>();
		for (int idx = 1; idx < 8; idx++) {
			Criteria c = biradsDsm.getSession().createCriteria(Report.class);
			c.add(Restrictions.like("applicationStatus", "EMITTED_" + idx));
			c.setMaxResults(selectionSize);
			accumulator.addAll(c.list());
		}
		return accumulator;
	}

	private void buildAnaforaDirectory(String corpusName, Collection<Report> reports) throws IOException {
		final File outputDirectory = new File(getOutputDirectoryPath());
		final File corpusDirectory = new File(outputDirectory, corpusName);
		FileUtils.deleteDirectory(corpusDirectory);
		corpusDirectory.mkdir();
		int idx = 0;
		for (Report report : reports) {
			String docDirectoryName = "doc" + StringUtils.leftPad(idx+"", 3, "0");
			File docDirectory = new File(corpusDirectory, docDirectoryName);
			docDirectory.mkdir();
			String docFileName = docDirectoryName;
			File docFile = new File(docDirectory, docFileName);
			FileUtils.write(docFile, report.getBody(), "UTF-8");
			idx++;
		}
	}

	private String pullDocumentTextFromTies(String documentId) {
		Session session = tiesDsm.getSession();
		DocumentImpl dbDoc = getDocument(session, documentId);
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

	public String getTiesPassword() {
		return tiesPassword;
	}

	public void setTiesPassword(String tiesPassword) {
		this.tiesPassword = tiesPassword;
	}

	public String getTiesUser() {
		return tiesUser;
	}

	public void setTiesUser(String tiesUser) {
		this.tiesUser = tiesUser;
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public int getCorpusSelectionSize() {
		return corpusSelectionSize;
	}

	public void setCorpusSelectionSize(int corpusSelectionSize) {
		this.corpusSelectionSize = corpusSelectionSize;
	}

	public String getOutputDirectoryPath() {
		return outputDirectoryPath;
	}

	public void setOutputDirectoryPath(String outputDirectoryPath) {
		this.outputDirectoryPath = outputDirectoryPath;
	}

}
