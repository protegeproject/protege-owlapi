package org.protege.owlapi.inconsistent;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

public class Heuristics {
	private OWLOntology ontology;
	private OWLReasonerFactory reasonerFactory;
	private OWLOntology consistentOntology;
	private OWLReasoner consistentOntologyReasoner;
	private OWLOntology otherOntology;
	
	
	
	public OWLOntology getOntology() {
		return ontology;
	}
	
	public OWLOntology getConsistentOntology() {
		return consistentOntology;
	}

	public OWLReasoner getConsistentOntologyReasoner() {
		return consistentOntologyReasoner;
	}

	public OWLOntology getOtherOntology() {
		return otherOntology;
	}

	public Set<OWLClass> phase01(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		this.ontology = ontology;
		this.reasonerFactory = reasonerFactory;
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		InitialSplitter splitter = new InitialSplitter();
		splitter.split(ontology, reasonerFactory);
		consistentOntology = splitter.getConsistentPart();
		otherOntology = splitter.getOtherPart();
		consistentOntologyReasoner = reasonerFactory.createReasoner(consistentOntology);
		consistentOntologyReasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		Set<OWLClass> inconsistent = new HashSet<OWLClass>(consistentOntologyReasoner.getBottomClassNode().getEntities());
		inconsistent.remove(factory.getOWLNothing());
		return inconsistent;
	}
	
	public Set<OWLAxiom> explain(OWLClass cls) {
		BlackBoxExplanation teacher = new BlackBoxExplanation(consistentOntology, reasonerFactory, consistentOntologyReasoner);
		return teacher.getExplanation(cls);
	}
	

}
