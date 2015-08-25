package edu.pitt.dbmi.birads.brok;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;



import edu.pitt.dbmi.birads.brok.pojos.Report;
import edu.pitt.dbmi.birads.util.BrokLogger;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager;
import edu.upmc.opi.caBIG.caTIES.connector.CaTIES_DataSourceManager.MODE;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentDataImpl;
import edu.upmc.opi.caBIG.caTIES.database.domain.impl.DocumentImpl;

public class ReportLoader {

	protected static final Logger logger = Logger.getLogger(ReportLoader.class);

	private CaTIES_DataSourceManager tiesDsm;
	private BiradsDataSourceManager biradsDsm;

	private String biradsUser = null;
	private String tiesPassword = null;

	public static void main(String[] args) {
		String biradsUser = args[0];
		String biradsPassword = args[1];
		ReportLoader reportLoader = new ReportLoader();
		reportLoader.setTiesUser(biradsUser);
		reportLoader.setTiesPassword(biradsPassword);
		reportLoader.execute();
	}

	public ReportLoader() {
		;
	}

	public void execute() {
		try {
			establishTiesDataSource();
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
					System.out.println("Brok run on " + numberOfReportsProcessed + " reports.");
				}
				if (numberOfReportsProcessed % 25000 == 0) {
						destroyBiradsDataSource();
						establishBiradsDataSource();
				}
				unProcessedReports = getUnprocessedReportCollection();
			}
			destroyBiradsDataSource();
			destroyTiesDataSource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void processReport(Report report) throws IOException {
		try {
			String documentText = pullDocumentTextFromTies(report.getAccession()+"");
			ReportSectionizer sectionizer = new ReportSectionizer();
			// number breasts imaged will be derived 
			// if initially set to zero
			final int defaultNumberBreasts = 0;
			Report sectionedReport = sectionizer.buildReport(report.getAccession()+"",
					defaultNumberBreasts, documentText);
			report.setNumberBreastsImaged(sectionedReport.getNumberBreastsImaged());
			report.setHasAddendum(sectionedReport.hasAddendum());
			report.setImpression(sectionedReport.getImpression());
			report.setBody(sectionedReport.getBody());
			processingPassOne(report);
			processingPassTwo(report);
			processingPassThree(report);
			// After processing we don't need to save the report text
			report.setBody("NA");
			report.setImpression("NA");
			report.setApplicationStatus("SUMMARIZING");
		}
		catch (Exception x) {
			System.err.println("Error processing report" + report.getAccession());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bos);
			x.printStackTrace(ps);			
			report.setApplicationStatus("ERROR");
			report.setErrorMessage(bos.toString());
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
		c.add(Restrictions.like("applicationStatus", "PROCESSING"));
		c.setMaxResults(1000);
		Collection<Report> reports = c.list();
		if (reports.isEmpty()) {
			return null;
		} else {
			return reports;
		}
	}

	private void processingPassOne(Report report) throws IOException {

		// For each report, we used the remove bad numbers method and
		// the assign birads method, which is the triple match. The zero after
		// assignBIRADS is just for debugging.

		report.removeBadNumbers();

		report.assignBIRADS(0);

		// If it has a left and right birads from the triple match it
		// records these as the local variable.

		int leftBIRADS = report.getLeftBIRADS();

		int rightBIRADS = report.getRightBIRADS();

		if (report.getLeftSubCategory() != null) {
			BrokLogger.getInstance().log(
					"accession: " + report.getAccession() + " subcategory: "
							+ report.getLeftSubCategory());
		}

		// If nothing was found with triple match then run
		// assignBIRADSAllowNullLateralityDescription (essentially the
		// nonspecific birads, detailed
		// above).

		if (leftBIRADS == -1 && rightBIRADS == -1
				&& report.getBilateralBIRADS() == -1) {

			BrokLogger.getInstance().log(
					"No BI-RADS found: " + report.getAccession());

			BrokLogger.getInstance().log(
					"Impression:\n" + report.getImpression());

			if (report.getImpression() == null) {
				BrokLogger.getInstance().log("Body:\n" + report.getBody());
			}

			report.assignBIRADSAllowNullLateralityDescription(1);

		}

	}
	
	private void processingPassTwo(Report report) throws IOException {
		int leftBIRADS = report.getLeftBIRADS();
		int rightBIRADS = report.getRightBIRADS();
		if ((leftBIRADS == -1 && rightBIRADS == -1 && report
				.getBilateralBIRADS() == -1)) {
			BrokLogger.getInstance().log("No BI-RADS found: " + report.getAccession());
			BrokLogger.getInstance().log("Impression:\n" + report.getImpression());
			if (report.getImpression() == null) {
				BrokLogger.getInstance().log("Body:\n" + report.getBody());
			}
			report.assignBIRADSAllowNullBIRADSDescription(1);
		}
	}

	
	private void processingPassThree(Report report) {
		
		// leftResult and rightResult are the actual output in the output
		// file.

		// If no birads was found ("-1"), then report "none". Otherwise, the
		// found
		// number is given. The subcategory a, b, or c is also given, if it
		// was found. This is the default state
		// with none or what is found being given.

		String leftResult = (report.getLeftBIRADS() == -1 ? "none" : report
				.getLeftBIRADS())
				+ (report.getLeftSubCategory() == null ? "" : report
						.getLeftSubCategory());

		String rightResult = (report.getRightBIRADS() == -1 ? "none"
				: report.getRightBIRADS())
				+ (report.getRightSubCategory() == null ? "" : report
						.getRightSubCategory());

		// Addendum will over-ride everything.

		if (report.hasAddendum()) {

			rightResult = "addendum";

			leftResult = "addendum";

			// Otherwise. . .

		} else {

			// If there are multiple mentions of right or left birads, the
			// report is
			// flagged as such. This is based on the counter
			// (getFound(RightLeftBilateral)BIRADS above.

			if (report.getFoundRightBIRADS() > 1) {

				rightResult = "multiple right BIRADS found";

			}

			// Same for left.

			if (report.getFoundLeftBIRADS() > 1) {

				leftResult = "multiple left BIRADS found";

			}

			// Same for bilateral.

			if (report.getFoundBilateralBIRADS() > 1) {

				leftResult = "multiple bilateral BIRADS found";

				rightResult = "multiple bilateral BIRADS found";

				// If one bilateral BIRADS found, then make sure it doesn't
				// conflict
				// with any right and left BIRADS. If there is a conflict,
				// it is flagged as inconsistent.

			} else if (report.getFoundBilateralBIRADS() > 0) {

				if (report.getLeftBIRADS() != -1
						&& report.getLeftBIRADS() != report
								.getBilateralBIRADS()) {

					leftResult = "conflict with bilateral/overall/combined BIRADS";

				} else if (report.getLeftSubCategory() != null
						&& !report.getLeftSubCategory().equals(
								report.getBilateralSubCategory())) {

					leftResult = "conflict with bilateral/overall/combined BIRADS";

				} else {

					leftResult = report.getBilateralBIRADS()
							+

							(report.getBilateralSubCategory() == null ? ""
									: report.getBilateralSubCategory());

				}

				if (report.getRightBIRADS() != -1
						&& report.getRightBIRADS() != report
								.getBilateralBIRADS()) {

					rightResult = "conflict with bilateral/overall/combined BIRADS";

				} else if (report.getRightSubCategory() != null
						&& !report.getRightSubCategory().equals(
								report.getBilateralSubCategory())) {

					rightResult = "conflict with bilateral/overall/combined BIRADS";

				} else {

					rightResult = report.getBilateralBIRADS()
							+

							(report.getBilateralSubCategory() == null ? ""
									: report.getBilateralSubCategory());

				}

			}

			// this block of code uses laterality detection to set number of
			// breasts imaged

			// then this info is used below to figure out if we should
			// consider a non-reported breast
			// as not imaged or imaged, but not reported
			// HOWEVER, because almost all imaged breasts are reported, it
			// works better to set this to 1 without actually checking

			// NOW TRYING THIS BUT USING ONLY IMPRESSION TEXT

			// Summary: Laterality detection is used on the impression only
			// (as
			// indicated by the 2 in parentheses). This only triggers when
			// the input laterality is 0 (not specified
			// as input, based on exam code data). If a 1 or 2 is specified
			// with the input reports, this is not used.

			if (report.getNumberBreastsImaged() == 0) {

				int numberBreastsImagedFromReport = 0;

				int reportLateralityCode = report.getLaterality(2);

				if (reportLateralityCode == 3) {

					numberBreastsImagedFromReport = 2;

				} else if (reportLateralityCode == 2
						|| reportLateralityCode == 1) {

					numberBreastsImagedFromReport = 1;

				}

				report.setNumberBreastsImaged(numberBreastsImagedFromReport);

			}

			// If we found more nonspecific birads than the number of
			// breasts
			// we think were imaged (either from our input or from
			// impression laterality detection), the report
			// is flagged.

			if (report.getFoundNonspecificBIRADS() > report
					.getNumberBreastsImaged()) {

				leftResult = "multiple BI-RADS without laterality found";

				rightResult = "multiple BI-RADS without laterality found";

			}

			// If number of breasts imaged (based on laterality input or
			// impression laterality detection) is 1, and we found 1 birads
			// and 1 none, then convert the 1 "none"
			// to "not imaged".

			if (report.getNumberBreastsImaged() == 1) {

				if (leftResult.equals("none")
						&& !rightResult.equals("none")) {

					leftResult = "not imaged";

				}

				if (!leftResult.equals("none")
						&& rightResult.equals("none")) {

					rightResult = "not imaged";

				}

			}

			// if no specific bilateral results found and we think only one
			// breast
			// was imaged

			// and we find only one side result right or left in the report,
			// set the
			// one not mentioned to not imaged

			// Summary: A nonspecific birads was found. Based on input or
			// impression laterality detection, we believe only 1 breast was
			// imaged. Impression laterality
			// detection is used to assigned the nonspecific birads to the
			// side we believe was imaged (1 is right,
			// 2 is left).

			if (report.getFoundBilateralBIRADS() == 0) {

				if (report.getNumberBreastsImaged() == 1
						&& report.getFoundRightBIRADS() == 0
						&& report.getFoundLeftBIRADS() == 0) {

					int laterality = report.getLaterality(2);

					if (laterality == 1)
						leftResult = "not imaged";

					if (laterality == 2)
						rightResult = "not imaged";

				}

			}
					
			report.setLeftResult(leftResult);
			report.setRightResult(rightResult);

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
		return biradsUser;
	}

	public void setTiesUser(String tiesUser) {
		this.biradsUser = tiesUser;
	}

}
