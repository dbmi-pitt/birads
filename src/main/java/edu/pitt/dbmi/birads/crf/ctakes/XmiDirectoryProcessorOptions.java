package edu.pitt.dbmi.birads.crf.ctakes;

import com.lexicalscope.jewel.cli.Option;

public interface XmiDirectoryProcessorOptions {
	@Option(shortName = "x", description = "xmi cas files to be processed",
			defaultValue = "C:\\Users\\kjm84\\Desktop\\birads_reports\\practice_xmi")
	public String getInputDirectoryPath();

	@Option(shortName = "o",
			description = "token stream output files",
			defaultValue = "C:\\Users\\kjm84\\Desktop\\birads_reports\\practice_tokens")
	public String getOutputDirectoryPath();

	@Option(shortName = "t",
			description = "type system xml path", 
			defaultValue = "desc\\types\\TypeSystem.xml")
	public String getTypeSystemXmlPath();
}
