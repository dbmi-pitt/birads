package edu.pitt.dbmi.birads.crf.digestion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.xml.sax.SAXException;

public class ExpertDocument {

	private String path;
	private String expert;
	private String sequence;

	private DigesterLoader digestionLoader;
	private Digester digester;
	private DigestionRules digestionRules;
	private InputStream inputStream;

	private final List<Entity> entities = new ArrayList<Entity>();
	
	private Iterator<Entity> iterator;

	public void cacheEntities() {
		try {
			tryCacheEntities();
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	public void displayEntities() {
		for (Entity entity : entities) {
			System.out.println(entity);
		}
	}

	private void tryCacheEntities() throws IOException, SAXException {
		digestionRules = new DigestionRules();
		digestionLoader = DigesterLoader.newLoader(digestionRules);
		digester = digestionLoader.newDigester();

		entities.clear();
		digester.push(this);
		inputStream = new FileInputStream(path);
		digester.parse(inputStream);
		inputStream.close();
		Collections.sort(entities, new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				int result = o1.getsPos() - o2.getePos();
				result = (result == 0) ? o1.getePos() - o2.getePos() : result;
				return result;
			}});
			
	}

	public void addEntity(Entity entity) {
		entity.setDocumentSequence(sequence);
		entities.add(entity);
	}
	
	public void iterate() {
		iterator = entities.iterator();
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	public Entity next() {
		return iterator.next();
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert) {
		this.expert = expert;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
