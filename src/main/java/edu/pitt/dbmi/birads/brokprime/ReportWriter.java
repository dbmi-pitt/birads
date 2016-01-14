package edu.pitt.dbmi.birads.brokprime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;

public class ReportWriter {

	protected static final Logger logger = Logger.getLogger(ReportWriter.class);

	public static String CONST_RANDOMIZATION_MODE_HOMOGENOUS = "homogenous";
	public static String CONST_RANDOMIZATION_MODE_HETEROGENI = "heterogenious";

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyMMdd");
	private static final String outputDirectoryPath = "X:\\work\\birads_xchange\\birads_prime_"
			+ dateFormatter.format(new Date());

	private static final String summaryPath = outputDirectoryPath
			+ "\\birads.csv";

	private CaTIES_DataSourceManager tiesDsm;
	private BiradsDataSourceManager biradsDsm;

	private String tiesUser = null;
	private String tiesPassword = null;
	
	private int goalLevel = -1;
	private int goalLimit = -1;

	private File outputDirectory;

	private String randomizationMode = CONST_RANDOMIZATION_MODE_HETEROGENI;

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
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
		
//		These 22 documents need to be replaced by 
//		18 reports with a BIRADS category 5, and
//		6 reports with a BIRADS category 4
//
		setGoalLevel(5);
		setGoalLimit(18);
		randomize();
		
		setGoalLevel(4);
		setGoalLimit(6);
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
	
	public void executeBatch() throws FileNotFoundException, IOException {
		establishTiesDataSource();
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
		randomizeBatch();
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
	
	public void openUp() {
		establishTiesDataSource();
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
	}
	
	public void closeUp() {
		destroyBiradsDataSource();
		destroyTiesDataSource();
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

	private void writeDocumentTexts(TreeSet<Report> sortedReports) {
		int reportNumber = 0;
		for (Report report : sortedReports) {
			String reportFileName = "report_" + report.getAccession() + ".txt";
			System.out.println("Report #" + reportNumber + " ==> " + reportFileName);
			reportNumber++;
			File reportFile = new File(outputDirectory, reportFileName);
			if (!reportFile.exists()) {
				try {
					reportFile.createNewFile();
				} catch (IOException e) {
					System.err.println("Error trying to create file named " + reportFile.getAbsolutePath());
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (reportFile.exists()) {
				String documentText = pullDocumentTextFromTies(report
						.getAccession());
				try {
					FileUtils.write(reportFile, documentText);
				} catch (IOException e) {
					System.err.println("Failed to create and or open file " + reportFile.getAbsolutePath());
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
		for (Report report : unProcessedReports) {
			String formattedAccession = StringUtils.leftPad(
					report.getAccession(), 12, "0");
			report.setAccession(formattedAccession);
			sortedReports.add(report);
		}
		return sortedReports;
	}

	private void randomizeBatch() {
		if (getRandomizationMode().equals(CONST_RANDOMIZATION_MODE_HETEROGENI)) {
			for (int idx = -1; idx < 7; idx++) {
				setGoalLevel(idx);
				setGoalLimit(60);
				randomize();
			}
		} else {
			setGoalLevel(-1);
			setGoalLimit(600);
			randomize();
		}
		//
	}

	private void randomize() {
		StringBuffer sb = new StringBuffer();
		sb.append("	update ");
		sb.append("		report ");
		sb.append("	set ");
		sb.append("	    application_status = 'EMITTING_<EMITT_LEVEL>' ");
		sb.append("where ");
		sb.append("   application_status = 'SUMMARIZING' and ");
		sb.append("   document_type = 'RADIOLOGY' and ");
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
		sql = sql.replaceAll("<EMITT_LEVEL>", (getGoalLevel() + 1) + "");
		sql = sql.replaceAll("<LEVEL>", getGoalLevel() + "");
		sql = sql.replaceAll("<LIMIT>", getGoalLimit() + "");
		System.out.println(sql);

		Transaction tx = biradsDsm.getSession().beginTransaction();
		SQLQuery sqlQuery = biradsDsm.getSession().createSQLQuery(sql);
		sqlQuery.executeUpdate();
		tx.commit();

	}

	@SuppressWarnings("unchecked")
	private Collection<Report> getUnprocessedReportCollection() {
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

	public String getRandomizationMode() {
		return randomizationMode;
	}

	public void setRandomizationMode(String randomizationMode) {
		this.randomizationMode = randomizationMode;
	}

	public int getGoalLevel() {
		return goalLevel;
	}

	public void setGoalLevel(int goalLevel) {
		this.goalLevel = goalLevel;
	}

	public int getGoalLimit() {
		return goalLimit;
	}

	public void setGoalLimit(int goalLimit) {
		this.goalLimit = goalLimit;
	}

}
