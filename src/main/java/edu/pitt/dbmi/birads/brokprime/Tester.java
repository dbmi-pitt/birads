package edu.pitt.dbmi.birads.brokprime;


import java.util.regex.*;

public class Tester {
	
	private static final String[] text = {
		"impression:",
		"1. questionable small nodule in the lateral aspect the left breast.",
		"diagnostic mammogram with spot compression view recommended for",
		"further evaluation.",
		"2. bi-rads category zero, need additional imaging evaluation.",
		"dictated by:    **name[m zzz] gates ",
		"signed by:  **name[m zzz] gates signed on: ",
		"**date[dec 29 2013] at  pm",
		"",
		"[mars exam_type]",
		"mammogram screening bilateral with cad biscrbi",
		"[mars report_subtype]",
		"mammo",
		"[mars dx]",
		"screening/mammo"
	};
	
    public static void main(String[] args) {
    	StringBuilder sb = new StringBuilder();
    	for (String textLine : text) {
    		sb.append(textLine + "\n");
    	}
        String s = sb.toString();
        Pattern pBIRADSc = Pattern
				.compile(
						".*\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,10}?\\b(birads|bi-rads|bi rads|category)\\b.*",
						Pattern.DOTALL);
       
        Matcher m = pBIRADSc.matcher(s);
        if (m.matches()) {
        	System.out.println("group 1: " + m.group(1));
        	System.out.println("group 2: " + m.group(2));
        	System.out.println("group 3: " + m.group(3));
        }
        
    } 
}