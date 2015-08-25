package edu.pitt.dbmi.birads.brok;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brok.pojos.Quarantine;
import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;

public class ReportWriter {

	protected static final Logger logger = Logger.getLogger(ReportLoader.class);

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyMMdd");
	private static final String outputDirectoryPath = "X:\\work\\birads_xchange\\birads_"
			+ dateFormatter.format(new Date());

	private File quarantineFile = new File(
			"C:\\Users\\mitchellkj\\Desktop\\quarantines.txt");

	private static final String summaryPath = outputDirectoryPath
			+ "\\birads.csv";

	private CaTIES_DataSourceManager tiesDsm;
	private BiradsDataSourceManager biradsDsm;
	private String tiesUser = null;
	private String tiesPassword = null;

	private File outputDirectory;

	private final List<Long> quarantinedReports = new ArrayList<Long>();

	public static void main(String[] args) {
		String tiesUser = args[0];
		String tiesPassword = args[1];
		ReportWriter reportWriter = new ReportWriter();
		reportWriter.setTiesUser(tiesUser);
		reportWriter.setTiesPassword(tiesPassword);
		try {
			reportWriter.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws FileNotFoundException, IOException {
		establishTiesDataSource();
		establishBiradsDataSource();
		// quarantineUsingScrollableResults();
		// quarantineToDatabase();
		// quarantineApplicationState();
		randomize();
		Collection<Report> unProcessedReports = getUnprocessedReportCollection();
		TreeSet<Report> sortedReports = sortReports(unProcessedReports);
		establishOutputDirectory();
		writeCsvSummaryFile(sortedReports);
		writeDocumentTexts(sortedReports);
		System.out.println("Done Processing " + unProcessedReports.size()
				+ " reports.");
		destroyBiradsDataSource();
		destroyTiesDataSource();
	}

	private void quarantineToDatabase() {
		try {
			final List<String> lines = FileUtils.readLines(quarantineFile);
			int totalProcessed = 0;
			for (String line : lines) {
				if (line.matches("\\d+")) {
					Long accession = Long.parseLong(line);
					Quarantine quarantine = new Quarantine();
					quarantine.setAccession(accession);
					biradsDsm.getSession().saveOrUpdate(
							quarantine);
					if (totalProcessed % 100 == 0) {
						System.out.println("Saving accession " + accession
								+ " after processing " + totalProcessed);
						Transaction tx = biradsDsm.getSession()
								.beginTransaction();
						biradsDsm.getSession().flush();
						biradsDsm.getSession().clear();
						tx.commit();
					}
					totalProcessed++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void quarantineApplicationState() {
		String sql = "update report r, quarantine q set r.application_status = 'QUARANTINING' where r.accession = q.accession";
		SQLQuery sqlQuery = biradsDsm.getSession()
				.createSQLQuery(sql);
		Transaction tx = biradsDsm.getSession()
				.beginTransaction();
		sqlQuery.executeUpdate();
		tx.commit();
	}

	private void quarantineUsingScrollableResults() {

		Query biradsQuery = biradsDsm
				.getSession()
				.createQuery(
						"from Report where application_status = :application_status order by id asc");
		biradsQuery.setString("application_status", "SUMMARIZING");
		biradsQuery.setReadOnly(true);
		// MIN_VALUE gives hint to JDBC driver to stream results
		biradsQuery.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults biradsResults = biradsQuery
				.scroll(ScrollMode.FORWARD_ONLY);

		// iterate over results
		int totalNumberProcessed = 0;
		boolean hasNextBirads = biradsResults.next();
		while (hasNextBirads) {
			Report biradsReport = (Report) biradsResults.get(0);
			biradsDsm.getSession().evict(biradsReport);
			resolveReportToTiesDocument(biradsReport.getAccession());
			if (quarantinedReports.size() == 100) {
				System.out
						.println("Quarantine queue full after having looked at "
								+ totalNumberProcessed + " records.");
				updateApplicationStatusQuarantined();
			}
			hasNextBirads = biradsResults.next();
			totalNumberProcessed++;
			if (totalNumberProcessed % 100 == 0) {
				System.out.println("total " + totalNumberProcessed);
			}
		}
		biradsResults.close();

		if (!quarantinedReports.isEmpty()) {
			System.out
					.println("Empty remaining Quarantine queue after having looked at "
							+ totalNumberProcessed + " records.");
			updateApplicationStatusQuarantined();
		}

	}

	private void updateApplicationStatusQuarantined() {
		for (Long quarantinedReportAccession : quarantinedReports) {
			try {
				boolean isAppending = true;
				FileUtils.write(quarantineFile, quarantinedReportAccession
						+ "\n", isAppending);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		quarantinedReports.clear();
	}

	private Report fetchReportByAccession(Long accession) {
		Query biradsQuery = biradsDsm
				.getSession()
				.createQuery(
						"from Report where accession = :accession and application_status = :application_status");
		biradsQuery.setLong("accession", accession);
		biradsQuery.setString("application_status", "SUMMARIZING");
		Report report = (Report) biradsQuery.uniqueResult();
		return report;
	}

	private void resolveReportToTiesDocument(long reportAccession) {
		DocumentImpl doc = getDocument(tiesDsm.getSession(), reportAccession);
		if (doc == null) {
			quarantinedReports.add(reportAccession);
			System.out.println("Report " + reportAccession + " is quaratined.");
		}
	}

	private void establishOutputDirectory() {
		outputDirectory = new File(outputDirectoryPath);
		System.out.println("Making directory " + outputDirectory);
		outputDirectory.mkdir();
	}

	private void writeCsvSummaryFile(TreeSet<Report> sortedReports)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		for (Report report : sortedReports) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(report.toStringProduction());
		}
		sb.append("\n");
		File summaryFile = new File(summaryPath);
		if (summaryFile.exists()) {
			summaryFile.delete();
		}
		summaryFile.createNewFile();
		FileUtils.write(summaryFile, sb.toString());

	}

	private void writeDocumentTexts(TreeSet<Report> sortedReports)
			throws FileNotFoundException, IOException {
		for (Report report : sortedReports) {
			String reportFileName = "report_" + report.getAccession() + ".txt";
			System.out.println("Writing file " + reportFileName);
			File reportFile = new File(outputDirectory, reportFileName);
			if (reportFile.createNewFile()) {
				String documentText = pullDocumentTextFromTies(report
						.getAccession());
				if (documentText != null) {
					FileUtils.write(reportFile, documentText);
				}
				else {
					System.err.println("Failed to get text for " + report.getAccession());
				}
				
			}
		}
	}

	private TreeSet<Report> sortReports(Collection<Report> unProcessedReports) {
		final TreeSet<Report> sortedReports = new TreeSet<Report>(
				new Comparator<Report>() {
					public int compare(Report o1, Report o2) {
						return o1.getAccession().compareTo(o2.getAccession());
					}
				});
		sortedReports.addAll(unProcessedReports);
		return sortedReports;
	}

	private void randomize() {
		for (int idx = -1; idx < 7; idx++) {
			randomize(idx, 26);
		}
	}

	private void randomize(int level, int emissionPartitionSize) {
		StringBuffer sb = new StringBuffer();
		sb.append("	update ");
		sb.append("		report ");
		sb.append("	set ");
		sb.append("	    application_status = 'EMITTING_PASS2_<EMITT_LEVEL>' ");
		sb.append("where ");
		sb.append("   application_status = 'SUMMARIZING' and ");
		sb.append("   (left_birads = <LEVEL> and ");
		sb.append("       right_birads <= <LEVEL> and  ");
		sb.append("       bilateral_birads <= <LEVEL> and ");
		sb.append("       nonspecific_birads <= <LEVEL>) or ");
		sb.append("   (right_birads = <LEVEL> and ");
		sb.append("       left_birads <= <LEVEL> and  ");
		sb.append("       bilateral_birads <= <LEVEL> and ");
		sb.append("       nonspecific_birads <= <LEVEL>) or ");
		sb.append("   (bilateral_birads = <LEVEL> and ");
		sb.append("       left_birads <= <LEVEL> and  ");
		sb.append("       right_birads <= <LEVEL> and  ");
		sb.append("       nonspecific_birads <= <LEVEL>) or ");
		sb.append("   (nonspecific_birads = <LEVEL> and ");
		sb.append("       left_birads <= <LEVEL> and  ");
		sb.append("       right_birads <= <LEVEL> and  ");
		sb.append("       bilateral_birads <= <LEVEL>) ");
		sb.append("order by  ");
		sb.append("    RAND() ");
		sb.append("limit ");
		sb.append("    <LIMIT> ");
		String sql = sb.toString();
		sql = sql.replaceAll("<EMITT_LEVEL>", (level + 1) + "");
		sql = sql.replaceAll("<LEVEL>", level + "");
		sql = sql.replaceAll("<LIMIT>", emissionPartitionSize + "");
		System.out.println(sql);

		Transaction tx = biradsDsm.getSession()
				.beginTransaction();
		SQLQuery sqlQuery = biradsDsm.getSession()
				.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		tx.commit();

	}

	@SuppressWarnings("unchecked")
	private Collection<Report> getUnprocessedReportCollection() {
		Criteria c = biradsDsm.getSession().createCriteria(
				Report.class);
		c.add(Restrictions.like("applicationStatus", "EMITTING_PASS2_%"));
		Collection<Report> reports = c.list();
		if (reports.isEmpty()) {
			return null;
		} else {
			return reports;
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

	private void establishBiradsDataSource() {
		biradsDsm = new BiradsDataSourceManager();
		biradsDsm
				.setHibernateDialect("org.hibernate.dialect.MySQLDialect");
		biradsDsm.setHibernateDriver("com.mysql.jdbc.Driver");
		biradsDsm
				.setHibernateConnectionUrl("jdbc:mysql://localhost:3306/birads");
		biradsDsm.setHibernateShowSql("false");
		biradsDsm.setHibernateUserName("birads");
		biradsDsm.setHibernateUserPassword("birads");
	}

	private String pullDocumentTextFromTies(Long documentId) {
		String dbDocText = null;
		Session session = tiesDsm.getSession();
		DocumentImpl dbDoc = getDocument(session, documentId);
		if (dbDoc != null) {
			DocumentDataImpl dbDocData = dbDoc.getDocumentData();
			dbDocText = dbDocData.getDocumentText();
			session.evict(dbDocData);
			session.evict(dbDoc);
		}		
		return dbDocText;
	}

	private DocumentImpl getDocument(Session session, Long documentId) {
		String queryString = "SELECT d FROM DocumentImpl d"
				+ " WHERE d.id = ? ";
		Query q = session.createQuery(queryString);
		q.setLong(0, documentId);
		DocumentImpl doc = (DocumentImpl) q.uniqueResult();
		if (doc != null) {
			session.evict(doc);
		}
		return doc;
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
