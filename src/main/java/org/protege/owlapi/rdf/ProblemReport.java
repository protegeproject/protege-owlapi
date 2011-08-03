package org.protege.owlapi.rdf;

import org.semanticweb.owlapi.model.OWLOntology;

public interface ProblemReport {
	
	OWLOntology getOntology();

	String getDescription();
	
	String getDetailedDescription();
	
	boolean canFix();
	
	boolean configureFixInteractively();
	
	void fix();
}
