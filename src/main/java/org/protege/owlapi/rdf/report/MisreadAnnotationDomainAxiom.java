package org.protege.owlapi.rdf.report;

import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class MisreadAnnotationDomainAxiom extends AbstractProblemReport {
	private OWLAnnotationPropertyDomainAxiom axiom;
	
	public MisreadAnnotationDomainAxiom(OWLOntology ontology, OWLAnnotationPropertyDomainAxiom axiom) {
		super(ontology);
		this.axiom = axiom;
	}

	public String getDescription() {
		return "It is possible that a missing class declaration caused an object property to be read as an annotation property";
	}

	public String getDetailedDescription() {
		StringBuffer sb = new StringBuffer("\nIt is possible that the annotation property domain axiom\n");
		sb.append("\t\t<pre>\n\t\t");
		sb.append(axiom.toString().replace("<", "&lt;").replace(">", "&gt;"));
		sb.append("\n\t\t</pre>\n");
		sb.append("\twas intended to be an object property domain axiom. If this was the case there is a missing\n");
		sb.append("\tclass declaration for the class\n");
		sb.append("\t\t<pre>\n\t\t");
		sb.append(axiom.getDomain());
		sb.append("\n\t\t</pre>\n\tThis also explains the associated annotation/object property pun.");
		return sb.toString();
	}
	
	public OWLAnnotationPropertyDomainAxiom getAxiom() {
		return axiom;
	}
	
	public OWLObjectPropertyDomainAxiom getObjectPropertyDomainAxiom() {
		return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getObjectProperty(), getDomainAsClass());
	}
	
	public OWLObjectProperty getObjectProperty() {
		return getOWLDataFactory().getOWLObjectProperty(axiom.getProperty().getIRI());
	}
	
	public OWLClass getDomainAsClass() {
		return getOWLDataFactory().getOWLClass(axiom.getDomain());
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
