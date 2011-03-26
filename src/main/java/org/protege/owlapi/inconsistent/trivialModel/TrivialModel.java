package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.protege.owlapi.inconsistent.Util;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * This class represents all the interesting (?) information about a 
 * interpretation that has a set of anonymous and named individuals 
 * but all class, object and data properties are empty.
 * 
 * @author tredmond
 *
 */
public class TrivialModel {
	private OWLOntology ontology;
	private Set<OWLIndividual> allIndividuals;
	private OWLDataFactory factory;
	private OWLReasoner reasoner;
	private OWLDataProperty dp;
	
	public TrivialModel(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		this.ontology = ontology;
		initializeIndividuals();
		initializeDataReasoner(reasonerFactory);
	}
	
	private void initializeIndividuals() {
		allIndividuals = new HashSet<OWLIndividual>();
		allIndividuals.addAll(ontology.getIndividualsInSignature(true));
		for (OWLOntology inClosure : ontology.getImportsClosure()) {
			allIndividuals.addAll(inClosure.getReferencedAnonymousIndividuals());
		}
		if (allIndividuals.isEmpty()) {
			OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			allIndividuals.add(Util.generateRandomEntity(factory, EntityType.NAMED_INDIVIDUAL));
		}
	}
	
	private void initializeDataReasoner(OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology();
		dp = Util.generateRandomEntity(factory, EntityType.DATA_PROPERTY);
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(dp));
		reasoner = reasonerFactory.createReasoner(ontology);
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}

	public Set<OWLIndividual> getAllIndividuals() {
		return Collections.unmodifiableSet(allIndividuals);
	}
	
	public boolean isConsistent(OWLDataRange range) {
		return reasoner.isSatisfiable(factory.getOWLDataSomeValuesFrom(dp, range));
	}

	public boolean isTop(OWLDataRange range) {
		return !reasoner.isSatisfiable(factory.getOWLObjectComplementOf(factory.getOWLDataAllValuesFrom(dp, range)));
	}
}
