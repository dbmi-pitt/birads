package edu.pitt.dbmi.birads.crf.digestion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Entity {

	private String id;
	private String span;
	private String type;
	private String parentsType;
	private String properties = "NA";
	private int sPos;
	private int ePos;
	private String documentSequence = "NA";
	private int matchCode = -1;
	private double maxOverLapFraction = 0.0d;
	private Entity bestMatchingEntity = null;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getSpan() {
		return span;
	}

	public void setSpan(String span) {
		this.span = span;
		if (this.span != null) {
			Pattern spanParser = Pattern.compile("(\\d+),(\\d+)");
			Matcher matcher = spanParser.matcher(this.span);
			if (matcher.matches()) {
				setsPos(Integer.valueOf(matcher.group(1)));
				setePos(Integer.valueOf(matcher.group(2)));
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		if (this.type != null) {
			this.type = this.type.replaceAll("\\s+", "_");
		}
	}

	public String getParentsType() {
		return parentsType;
	}

	public void setParentsType(String parentsType) {
		this.parentsType = parentsType;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		if (properties == null || properties.trim().length() == 0) {
			this.properties = "NA";
		}
		else {
			this.properties = properties;
		}		
	}

	public int getsPos() {
		return sPos;
	}

	public void setsPos(int sPos) {
		this.sPos = sPos;
	}

	public int getePos() {
		return ePos;
	}

	public void setePos(int ePos) {
		this.ePos = ePos;
	}
	
	public boolean onlyTypeDisagreement(Entity anotherEntity) {
		boolean condition = true;
		condition = condition && anotherEntity.getDocumentSequence().equals(getDocumentSequence());
		condition = condition && (anotherEntity.getsPos() == getsPos());
		condition = condition && (anotherEntity.getePos() == getePos());
		condition = condition && (!anotherEntity.getType().equals(getType()));	
		return condition;
	}
	
	public void registerMatch(Entity entityTwo) {
		if (getType().equals(entityTwo.getType())) {
			int tps = 0;
			int fns = 0;
			int fps = 0;	
			int sPos = Math.min(this.getsPos(), entityTwo.getsPos());
			int ePos = Math.max(this.getsPos(), entityTwo.getsPos());
			for (int pos = sPos; pos < ePos; pos++) {
				if (pos >= this.getsPos() 
						&& pos < this.getePos() 
						&& pos >= entityTwo.getsPos() 
						&& pos < entityTwo.getePos()) {
					tps++;
				}
				else if (pos >= this.getsPos() 
						&& pos < this.getePos()) {
					fns++;
				}
				else if (pos >= entityTwo.getsPos() 
						&& pos < entityTwo.getePos()) {
					fps++;
				}
				else {
				}
			}
			double p = tps / (tps + fps);
			double r = tps / (tps + fns);
			double f = (p * r) / (p + r);
			double overLapFraction = f;
			if (overLapFraction > maxOverLapFraction) {
				maxOverLapFraction = overLapFraction;
				setBestMatchingEntity(entityTwo);
			}
		}	
	}
	
	public Entity getBestMatchingEntity() {
		return bestMatchingEntity;
	}

	public void setBestMatchingEntity(Entity bestMatchingEntity) {
		this.bestMatchingEntity = bestMatchingEntity;
	}
	
	public String getDocumentSequence() {
		return documentSequence;
	}

	public void setDocumentSequence(String documentSequence) {
		this.documentSequence = documentSequence;
	}

	public int getMatchCode() {
		return matchCode;
	}

	public void setMatchCode(int matchCode) {
		this.matchCode = matchCode;
	}

	public String toKey() {
		final StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad(sPos+"", 5, "0"));
		sb.append(":");
		sb.append(StringUtils.leftPad(ePos+"", 5, "0"));
		return sb.toString();
	}

	public String toReflectionString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public String toString() {
		String formatter = "[%s,%s,%5d,%5d,%3d]";
		return String.format(formatter, getDocumentSequence(), getType(), getsPos(), getePos(), getMatchCode());
	}

	

}
