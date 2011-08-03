package org.protege.owlapi.rdf.report;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author tredmond
 *
 */
public class MissingRequiredDeclarations extends AbstractProblemReport {
	private Set<OWLEntity> entitiesWithoutDeclarations;
	
	public MissingRequiredDeclarations(OWLOntology ontology, Set<OWLEntity> entitiesWithoutDeclarations) {
		super(ontology);
		this.entitiesWithoutDeclarations = entitiesWithoutDeclarations;
	}


	public String getDescription() {
		return "This ontology is missing the declarations needed for it to be reliably parsed as OWL 2.";
	}

	public String getDetailedDescription() {
		StringBuffer sb = new StringBuffer("The following entities are used but not declared in the ontology:\n");
        sb.append("\t<ul>\n");
		for (OWLEntity e : entitiesWithoutDeclarations) {
			sb.append("\t\t<li>");
			sb.append(e.getEntityType());
			sb.append(' ');
			sb.append(e.getIRI().toString());
			sb.append('\n');
		}
		sb.append("\t</ul>\n");
		sb.append("\tThis problem means that the parsed ontology may not reflect the intentions of the creator.\n");
		sb.append("\tOn a save the decisions of the parser will be made final but the missing declarations will\n");
		sb.append("\tbe fixed. It is recommended that you fix the ontology and reload.\n");
		sb.append("\t<p>\n");
		sb.append("\tReferences for this type of problem:\n");
        sb.append("\t<ul>\n");
        sb.append("\t\t <li> <a href=\"http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Typing_Constraints_of_OWL_2_DL\">Typing Constraints of OWL DL.</a>\n");
        sb.append("\t\t\tNote, in particular where it indicates the reason for these constraints is <i>\"These constraints are used for disambiguating the \n");
        sb.append("\t\t\ttypes of IRIs when reading ontologies from external transfer syntaxes\"</i>.\n");
        sb.append("\t\t<li> <a href=\"http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Canonical_Parsing_of_OWL_2_Ontologies\">\n");
        sb.append("\t\t\tCanonical Parsing of OWL 2 Ontologies.</a>  Note in particular, item CP 3.1 where it is essentially said that the \n");
        sb.append("\t\t\timports closure of any imported ontology must satisfy the typing constraints.\n");
        sb.append("\t\t<li> <a href=\"http://www.w3.org/TR/2009/REC-owl2-mapping-to-rdf-20091027/#Mapping_from_RDF_Graphs_to_the_Structural_Specification\">\n");
        sb.append("\t\t\tMapping from RDF Graphs to the Structural Specification</a> has numerous references to the requirement that \n");
        sb.append("\t\t\tentities are declared.\n");
        sb.append("\t</ul>\n");
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
