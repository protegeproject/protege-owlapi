package org.protege.owlapi.rdf.report;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

@SuppressWarnings("rawtypes")
public class PunnedProperties extends AbstractProblemReport {

	private Map<IRI, Collection<EntityType>> propertyPunMap;
	
	public PunnedProperties(OWLOntology ontology, Map<IRI, Collection<EntityType>> propertyPunMap) {
		super(ontology);
		this.propertyPunMap = propertyPunMap;
	}

	public String getDescription() {
		return "Ontology contains punned properties";
	}

	public String getDetailedDescription() {
		StringBuffer sb = new StringBuffer("The ontology contains the following property puns:\n");
        sb.append("\t<ul>\n");
		for (Entry<IRI, Collection<EntityType>> entry : propertyPunMap.entrySet()) {
			IRI punnedIRI = entry.getKey();
			Collection<EntityType> types = entry.getValue();
			sb.append("\t\t<li>");
			sb.append(types);
			sb.append(" ");
			sb.append(punnedIRI.toString());
			sb.append('\n');
		}
        sb.append("\t</li>\n");
		sb.append("\tThis means that certain triples can't be unambiguously parsed.");
		sb.append("\n\t<p>\n");
		sb.append("\tReferences:\n");
		sb.append("\t<ul>\n");
		sb.append("\t\t<li><a href=\"http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Typing_Constraints_of_OWL_2_DL\">");
		sb.append("Typing Constraints of OWL 2 DL</a>\n");
		sb.append("\t\t<li><a href=\"http://www.w3.org/TR/2009/REC-owl2-mapping-to-rdf-20091027/#Analyzing_Declarations\">");
		sb.append("Analyzing Declarations</a> from the RDF to structural specification.\n");
		sb.append("\t</ul>");
		return sb.toString();
	}

	public boolean canFix() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public boolean configureFixInteractively() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public void fix() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
