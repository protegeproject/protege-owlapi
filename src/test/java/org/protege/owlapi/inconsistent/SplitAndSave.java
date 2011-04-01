package org.protege.owlapi.inconsistent;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class SplitAndSave {
	public static final File location = new File("/home/tredmond/Shared/ontologies/real/HomoSapiens/HomoSapiens.owl");
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		System.out.println("Loading " + location);
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(location);
		OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName("org.semanticweb.HermiT.Reasoner$ReasonerFactory").newInstance();
		OntologySplitter splitter = new OntologySplitter();
		System.out.println("Splitting ontology");
		splitter.split(ontology, reasonerFactory);
		System.out.println("Saving split ontologies");
		File out = splitter.saveOntologies(location.getParentFile());
		System.out.println("Saved in " + out);
	}

}
