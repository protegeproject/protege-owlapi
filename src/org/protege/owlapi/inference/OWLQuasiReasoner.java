package org.protege.owlapi.inference;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * This interface extends the OWL API OWLReasoner interface but indicates that the implementor does not quite 
 * meet the specifications of the OWLReasoner interface for the following reasons:
 * <ol>
 * <li> it may be incomplete.</li>
 * <li> it works with a collection of ontologies rather the imports closure of a single ontology.</li>
 * </ol>
 * @author tredmond
 *
 */
public interface OWLQuasiReasoner extends OWLReasoner {

    void setOntologies(Set<OWLOntology> ontologies);
}
