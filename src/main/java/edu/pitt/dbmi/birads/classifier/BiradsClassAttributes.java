package edu.pitt.dbmi.birads.classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


import weka.core.Attribute;
import weka.core.Instances;

/**
 * represents birads class attributes
 * @author tseytlin
 *
 */
public class BiradsClassAttributes {
	private Set<String> words;
	private Map<String,Attribute> attributes; 
	private ArrayList<Attribute> list;
	
	public BiradsClassAttributes(Set<String> words){
		this.words = words;
	}
	
	public BiradsClassAttributes(InputStream is) throws IOException{
		this(null,is);
	}
	public BiradsClassAttributes(Instances inst, InputStream is) throws IOException{
		words = new LinkedHashSet<String>();
		// load word file
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		for(String l=r.readLine();l != null; l=r.readLine()){
			if(l.length() > 0 && !l.startsWith("#"))
				words.add(l.trim());
		}
		r.close();
		
		// load attributes from instances
		if(inst != null){
			attributes = new LinkedHashMap<String,Attribute>();
			for(int i=0;i<inst.numAttributes();i++){
				attributes.put(inst.attribute(i).name(),inst.attribute(i));
			}
		}
	}
	
	
	public Attribute getAttribute(String a){
		return getAttributes().get(a);
	}
	
	public ArrayList<Attribute> getAttributeList(){
		if(list == null){
			list = new ArrayList<Attribute>();
			for(String key: getAttributes().keySet()){
				list.add(getAttributes().get(key));
			}
		}
		return list;
	}
	public Set<String> getAttributeNames(){
		return getAttributes().keySet();
	}
	
	public Set<String> getWordList(){
		return words;
	}
	
	public int getAttributeCount(){
		return getAttributes().size();
	}
	
	
	/**
	 * get attributes for this 
	 * @return
	 */
	private Map<String,Attribute> getAttributes() {
		if(attributes == null){
			
			// BiradsNumber - actual birads category, can be  0,1,2,3,4,5,6
			// BiradsCounter - which birads mention is it?
			// isLeftFROMStudyTypeORFooter - if <desc> == 0 => "?", else if (<desc>.contans("LEFT") || <side>.contains("LEFT")) => 1, else 0	
			// isRightFROMStudyTypeORFooter	- if <desc> == 0 => "?", else if (<desc>.contans("RIGHT") || <side>.contains("RIGHt")) => 1, else 0
			// isBilateralFROMStudyTypeORFooter	 -if<desc> == 0 => "?", else if !<desc>.contains("BILATERAL") => 0, else if (<desc>.contains("BILATERAL") && !<desc>.contains("UNILATERAL")) => 1, else if (<desc>.contains("BILATERAL") && 
			//										<desc>.contains("UNILATERAL") && !<desc>.contians("LEFT") && !<desc>.contians("RIGHT") && !<isLeftFROMStudyTypeORFooter> == 1, && !isRightFROMStudyTypeORFooter == 1) => 0.5, else => 0
			// isUnilateralFROMStudyTypeORFooter - if<desc> == 0 => "?", else if !<desc>.contains("UNILATERAL") => 0, else if ( <desc>.contains("UNILATERAL" or "LEFTL, or"RIGHT") && !<desc>.contains("BILATERAL") || <side>.equals("LEFT RIGHT ") 
			//									|| (<StudyTypeFromName>.equals("MAMMO") && <footer_txt>.lowerCase.contains("routine"))) => 1, else if (<desc>.contains("BILATERAL") && ( <desc.contains."UNILATERAL" or "LEFTL, or "RIGHT") 
			//									|| <isLeftFROMStudyTypeORFooter> == 1 || isRightFROMStudyTypeORFooter == 1) => 0.5, else => 0	
			// StudyTypeFromName	- if <desc>.contains ("TOMOSYNTHESIS" or "MAMM") => "MAMMO", else if <desc>.contains("US " or "ULTRASOUND") => "US", else if <desc>.contains("MRI" or "MAGNETIC") => "MRI" else "?" (US, MAMMO, MRI, ?)
			// TotalNumBirad	    - max(<BiradsCounter> per report)
			// IfOneThenIsLastBiradsRank	- if <TotalNumBirad> == 1 => "Last", else if <BiradsCounter> == 1 => "First", else if  <BiradsCounter> <  <TotalNumBirad> => "Middle", else "Last"
			
			// Bag Of Words for a given BiRADS line, 
			
			// BiradsCategory - this is a birads category (LeftBirads, RightBirads, MultiLateralBirads, OverAllBirads, NonSpecificBirads)
			attributes = new LinkedHashMap<String,Attribute>();
			
			// add bag of words
			for(String word: words){
				attributes.put(word, new Attribute(word));
			}
			
			for(String type: Arrays.asList("isLeftFROMStudyTypeORFooter","isRightFROMStudyTypeORFooter","isBilateralFROMStudyTypeORFooter","isUnilateralFROMStudyTypeORFooter","BiradsNumber","BiradsCounter","TotalNumBirad")){
				attributes.put(type, new Attribute(type));
			}
			attributes.put("StudyTypeFromName", new Attribute("StudyTypeFromName",Arrays.asList("MAMMO","MRI","US")));
			attributes.put("IfOneThenIsLastBiradsRank", new Attribute("IfOneThenIsLastBiradsRank",Arrays.asList("First","Last","Middle")));
			//attributes.put("NearestSide", new Attribute("NearestSide",Arrays.asList("Left","Right","Bilateral","Overall")));
			
			for(String type: Arrays.asList("LeftCount","RightCount","BilateralCount")){
				attributes.put(type, new Attribute(type));
			}
			
			// this is a class
			attributes.put("BiradsCategory", new Attribute("BiradsCategory",Arrays.asList("LeftBirads","RightBirads","MultiLateralBirads","OverAllBirads","NonSpecificBirads")));
			
		}
		return attributes;
	}
}
