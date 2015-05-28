package edu.pitt.dbmi.birads.gui;

import edu.pitt.dbmi.birads.brok.pojos.Report;

public class ReportWidget {
	
	private Report report;
	
	public ReportWidget() {
		;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
	
	public String toString() {
		return report.getAccession();
	}

}
