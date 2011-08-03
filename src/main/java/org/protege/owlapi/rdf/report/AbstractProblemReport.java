package org.protege.owlapi.rdf.report;

import org.protege.owlapi.rdf.ProblemReport;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

public abstract class AbstractProblemReport implements ProblemReport {
	private OWLOntology ontology;
	private OWLDataFactory factory;
	
	public AbstractProblemReport(OWLOntology ontology) {
		this.ontology = ontology;
		this.factory = ontology.getOWLOntologyManager().getOWLDataFactory();
	}

	public OWLOntology getOntology() {
		return ontology;
	}
	
	public OWLDataFactory getOWLDataFactory() {
		return factory;
	}

	@Override
	public String toString() {
		return "<" + getDescription() + ">";
	}

}
