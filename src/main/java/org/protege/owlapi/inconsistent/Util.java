package org.protege.owlapi.inconsistent;

import java.util.UUID;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

public class Util {
	public final static String RANDOM_URN_PREFIX = "urn:protege:";
	
	private Util() { }
	
	public static IRI generateRandomIRI(String fragment) {
		return IRI.create(RANDOM_URN_PREFIX + UUID.randomUUID().toString() + "#" + fragment);
	}	
	
	public static IRI generateRandomIRI() {
		return IRI.create(RANDOM_URN_PREFIX + UUID.randomUUID().toString());
	}
	
	public static <X extends OWLEntity> X generateRandomEntity(OWLDataFactory factory, EntityType<X> entityType) {
		return factory.getOWLEntity(entityType, generateRandomIRI());
	}
}
