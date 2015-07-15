package edu.pitt.dbmi.birads.crf.ctakes;

import com.lexicalscope.jewel.cli.Option;

public interface ClinicalPipelineOptions {
	
	@Option(shortName = "i", description = "specify the path to the directory containing the clinical notes to be processed")
	public String getInputDirectory();
	
	@Option(shortName = "x", description = "specify the path to the directory containing the expert annotations")
	public String getExpertDirectory();

	@Option(shortName = "o", description = "specify the path to the directory where the output xmi files are to be saved")
	public String getOutputDirectory();
}
