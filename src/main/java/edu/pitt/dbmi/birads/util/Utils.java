package edu.pitt.dbmi.birads.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Utils {
	
	public void leftPadDocumentIds(List<String> documentIds) {
		final ArrayList<String> leftPaddedDocuemntIds = new ArrayList<String>();
		int maxStringLength = findMaxStringLength(documentIds);
		for (String documentId : documentIds) {
			String paddedDocuemntId = StringUtils.leftPad(documentId,
					maxStringLength, "0");
			leftPaddedDocuemntIds.add(paddedDocuemntId);
		}
		documentIds.clear();
		documentIds.addAll(leftPaddedDocuemntIds);
	}
	
	private int findMaxStringLength(List<String> srcStrings) {
		int maxStringLength = Integer.MIN_VALUE;
		for (String srcString : srcStrings) {
			if (srcString.length() > maxStringLength) {
				maxStringLength = srcString.length();
			}
		}
		return maxStringLength;
	}

}
