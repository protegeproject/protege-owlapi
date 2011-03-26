package org.protege.owlapi.inconsistent;

import java.util.ArrayList;
import java.util.List;

import org.protege.owlapi.inconsistent.trivialModel.AxiomInterpreter;
import org.protege.owlapi.inconsistent.trivialModel.TrivialModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class InitialSplitter {
	private OWLOntology consistentPart;
	private OWLOntology otherPart;
	
	public void split(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		AxiomInterpreter interpreter = new AxiomInterpreter(new TrivialModel(ontology, reasonerFactory));
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		consistentPart = manager.createOntology();
		otherPart = manager.createOntology();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for (OWLOntology inClosure : ontology.getImportsClosure()) {
			for (OWLAxiom axiom : inClosure.getAxioms()) {
				try {
					if (axiom.accept(interpreter)) {
						changes.add(new AddAxiom(consistentPart, axiom));
					}
					else {
						changes.add(new AddAxiom(otherPart, axiom));
					}
				}
				catch (UnsupportedOperationException e) {
					// top object or data property in a restriction
					changes.add(new AddAxiom(otherPart, axiom));
				}
			}
		}
		manager.applyChanges(changes);
		changes.clear();
		for (OWLEntity e : otherPart.getSignature()) {
			if (!consistentPart.containsEntityInSignature(e)) {
				changes.add(new AddAxiom(consistentPart, factory.getOWLDeclarationAxiom(e)));
			}
		}
		manager.applyChanges(changes);
	}
	
	public OWLOntology getConsistentPart() {
		return consistentPart;
	}
	
	public OWLOntology getOtherPart() {
		return otherPart;
	}

}
