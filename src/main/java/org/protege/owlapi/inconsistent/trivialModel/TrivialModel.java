package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.protege.owlapi.inconsistent.Util;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
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
	
	public boolean isTopClass(OWLClass c) {
		return c.equals(factory.getOWLThing());
	}
	
	public boolean isTopProperty(OWLObjectPropertyExpression pe) {
		return pe.getNamedProperty().equals(factory.getOWLTopObjectProperty());
	}

	public boolean isTopProperty(OWLDataPropertyExpression pe) {
		return pe.equals(factory.getOWLTopDataProperty());
	}
	
	public boolean isTopDataRange(OWLDataRange range) {
		return !reasoner.isSatisfiable(factory.getOWLObjectComplementOf(factory.getOWLDataAllValuesFrom(dp, range)));
	}

	public boolean isConsistent(OWLDataRange range) {
		return reasoner.isSatisfiable(factory.getOWLDataSomeValuesFrom(dp, range));
	}

	public boolean hasAtLeast(OWLDataRange range, int cardinality) {
		return reasoner.isSatisfiable(factory.getOWLDataMinCardinality(cardinality, dp, range));
	}
	
	public boolean hasNoMoreThan(OWLDataRange range, int cardinality) {
		return reasoner.isSatisfiable(factory.getOWLDataMaxCardinality(cardinality, dp, range));
	}
	
	public boolean hasExactly(OWLDataRange range, int cardinality) {
		return reasoner.isSatisfiable(factory.getOWLDataExactCardinality(cardinality, dp, range));
	}
}
