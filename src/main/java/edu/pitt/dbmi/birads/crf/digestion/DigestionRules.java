package edu.pitt.dbmi.birads.crf.digestion;

import org.apache.commons.digester3.binder.AbstractRulesModule;

public class DigestionRules extends AbstractRulesModule {

	@Override
	protected void configure() {

		forPattern("*/data/annotations/entity").createObject()
				.ofType(Entity.class.getName()).then().setNestedProperties().then().setNext("addEntity");

	}

}
