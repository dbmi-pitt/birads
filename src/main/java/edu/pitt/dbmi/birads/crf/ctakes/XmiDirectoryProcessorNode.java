package edu.pitt.dbmi.birads.crf.ctakes;

import java.util.LinkedHashSet;

public class XmiDirectoryProcessorNode implements
		Comparable<XmiDirectoryProcessorNode> {

	private int begin;
	private int end;
	private String coveredText;
	private String normalizedForm;
	private String partOfSpeech;
	private String chunkType;
	private final LinkedHashSet<String> values = new LinkedHashSet<String>();

	@Override
	public int compareTo(XmiDirectoryProcessorNode other) {
		int retValue = 0;
		if (getBegin() < other.getBegin()) {
			retValue = -1;
		} else if (getBegin() > other.getBegin()) {
			retValue = 1;
		} else {
			if (getEnd() < other.getEnd()) {
				retValue = -1;
			} else if (getEnd() > other.getEnd()) {
				retValue = 1;
			} else {
				retValue = getCoveredText().compareTo(other.getCoveredText());
			}
		}
		return retValue;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}

	public String getNormalizedForm() {
		return normalizedForm;
	}

	public void setNormalizedForm(String normalizedForm) {
		this.normalizedForm = normalizedForm;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	
	public String getChunkType() {
		return chunkType;
	}

	public void setChunkType(String chunkType) {
		this.chunkType = chunkType;
	}

	public LinkedHashSet<String> getValues() {
		return values;
	}

	public void addValue(String value) {
		values.add(value);
	}

	public String getKey() {
		return String.format("%10d:%10d:%s", getBegin(), getEnd(),
				getCoveredText());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(" + getBegin() + "," + getEnd() + ") ");
		sb.append(getCoveredText());
		sb.append(", ");
		sb.append(getNormalizedForm());
		sb.append(", ");
		sb.append(getPartOfSpeech());
		return sb.toString();
	}

}
