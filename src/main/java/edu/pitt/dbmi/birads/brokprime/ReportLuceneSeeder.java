package edu.pitt.dbmi.birads.brokprime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.pitt.dbmi.birads.brokprime.pojos.Report;
import edu.upmc.opi.caBIG.caTIES.server.dispatcher.search.CaTIES_HitsFacadeScoreSorted;
import edu.upmc.opi.caBIG.caTIES.server.index.CaTIES_CombinedLuceneAnalyzer;

public class ReportLuceneSeeder {
	
	protected static final Logger logger = Logger.getLogger(ReportLuceneSeeder.class);

	private int numberOfDocumentsToProcess = Integer.MAX_VALUE;

	private String indexDirectoryName = "z:\\ties\\indices\\PRODINDEX";
	
	private BiradsDataSourceManager biradsDsm;

	private FSDirectory fsDirectory = null;


	public static void main(String[] args) {
		ReportLuceneSeeder reportLoader = new ReportLuceneSeeder();
		reportLoader.execute();

	}
	
	public void execute() {
		System.out.println("Initiated preprocessing docIds...");
		biradsDsm = BiradsDataSource.getInstance().getBiradsDataSourceManager();
		clearBiradsData();
		pullDocumentIdsFromIndex();		
		destroyBiradsDataSource();
		System.out.println("Finished preprocessing docIds...");
	}
	
	private void clearBiradsData() {
		String queryString = "delete FROM Report";
		SQLQuery q = biradsDsm.getSession().createSQLQuery(queryString);
		q.executeUpdate();
	}

	private void pullDocumentIdsFromIndex() {
		try {
			fsDirectory = FSDirectory.open(new File(indexDirectoryName));
			logger.info("Opened the index at " + indexDirectoryName);

			DirectoryReader reader = DirectoryReader.open(fsDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			logger.info("Succeeded in opening the searcher with FSDirectory ==> "
					+ fsDirectory.getDirectory().getAbsolutePath());

			CaTIES_CombinedLuceneAnalyzer perFieldAnalyzer = openPerFieldAnalyzer();
			QueryParser queryParser = new QueryParser(Version.LUCENE_45,
					"documentText", perFieldAnalyzer);

			CaTIES_HitsFacadeScoreSorted hits = new CaTIES_HitsFacadeScoreSorted();
			hits.setQueryParser(queryParser);
			hits.setSearcher(searcher);
			hits.setLuceneQueryObject("AC1511314");
			hits.setCacheSize(1000);
			hits.searchInitially();

			int payLoadSize = Math.min(numberOfDocumentsToProcess,
					hits.getTotalHits());
			
			final ArrayList<Report> reports = new ArrayList<Report>();
			for (int idx = 0; idx < payLoadSize; idx++) {
				Document luceneDoc = hits.getHitAt(idx);
				String documentId = luceneDoc.get("documentId");
				Report report = new Report();
				report.setAccession(documentId);
				report.setApplicationStatus("PROCESSING");
				reports.add(report);
				if (reports.size() == 1000) {
					System.out.println("Loaded " + idx + " docIds from lucene.");
					batchSaveReports(reports);
					reports.clear();
				}
			}

			fsDirectory.close();

		} catch (Exception x) {
			x.printStackTrace();
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

	protected CaTIES_CombinedLuceneAnalyzer openPerFieldAnalyzer() {
		CaTIES_CombinedLuceneAnalyzer perFieldAnalyzer = new CaTIES_CombinedLuceneAnalyzer();
		try {
			perFieldAnalyzer.addKeyWordFieldName("patientId");
			perFieldAnalyzer.addKeyWordFieldName("documentId");
			perFieldAnalyzer.addKeyWordFieldName("documentType");
			perFieldAnalyzer.addKeyWordFieldName("gender");
			perFieldAnalyzer.addKeyWordFieldName("race");
			perFieldAnalyzer.addKeyWordFieldName("ethnicity");
			perFieldAnalyzer.addWhiteSpaceFieldName("document_text_report_tag");
		} catch (Exception x) {
			x.printStackTrace();
		}
		return perFieldAnalyzer;
	}

	private void destroyBiradsDataSource() {
		biradsDsm.destroy();
	}

}
