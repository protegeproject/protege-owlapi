package org.protege.owlapi.rdf.report;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLOntology;

public class ImportsBrokenOntologies extends AbstractProblemReport {
	private OWLOntology ontology;
	private Collection<OWLOntology> badImports;
	
	public ImportsBrokenOntologies(OWLOntology ontology, Collection<OWLOntology> badImports) {
		super(ontology);
		this.badImports = badImports;
	}
	
	public String getDescription() {
		return "This ontology imports ontologies that have rdf serialization problems";
	}

	public String getDetailedDescription() {
		StringBuffer sb = new StringBuffer("This ontology indirectly imports the following ontologies that have rdf serialization problems:\n");
		sb.append("\t<ul>\n");
		for (OWLOntology badOntology : badImports) {
			sb.append("\t\t<li>" + badOntology.getOntologyID().getOntologyIRI().toString());
			sb.append('\n');
		}
		sb.append("\t</ul>\n");
		return sb.toString();
	}

	public boolean canFix() {
		return false;
	}

	public boolean configureFixInteractively() {
		return false;
	}

	public void fix() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
