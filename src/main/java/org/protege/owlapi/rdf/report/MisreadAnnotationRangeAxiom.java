package org.protege.owlapi.rdf.report;

import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class MisreadAnnotationRangeAxiom extends AbstractProblemReport {
	private OWLAnnotationPropertyRangeAxiom axiom;
	
	public MisreadAnnotationRangeAxiom(OWLOntology ontology, OWLAnnotationPropertyRangeAxiom axiom) {
		super(ontology);
		this.axiom = axiom;
	}

	public String getDescription() {
		return "It is possible that a missing class declaration caused an object property to be read as an annotation property";
	}

	public String getDetailedDescription() {
		StringBuffer sb = new StringBuffer("It is possible that the annotation property range axiom\n");
		sb.append("\t\t<pre>\n\t\t");
		sb.append(axiom);
		sb.append("\n\t\t</pre>\n");
		sb.append("\twas intended to be an object property range axiom. If this was the case there is a missing\n");
		sb.append("\tclass declaration for the class\n");
		sb.append("\t\t<pre>\n\t\t");
		sb.append(axiom.getRange());
		sb.append("\n\t\t</pre>\n\tThis also explains the associated annotation/object property pun.");
		return sb.toString();
	}

	public boolean canFix() {
		return true;
	}

	public boolean configureFixInteractively() {
		return true;
	}

	public void fix() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
