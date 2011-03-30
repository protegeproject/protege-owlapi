package org.protege.owlapi.inconsistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protege.owlapi.inconsistent.trivialModel.AxiomInterpreter;
import org.protege.owlapi.inconsistent.trivialModel.TrivialModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OntologySplitter {
	private OWLOntology consistentPart;
	private OWLOntology otherPart;
	private OWLOntology surrogateTypePart;
	private TypeCollector typeCollector;
	private Map<OWLAxiom, OWLAxiom> substituteAxiomMap = new HashMap<OWLAxiom, OWLAxiom>();
	
	public OWLOntology getConsistentPart() {
		return consistentPart;
	}
	
	public OWLOntology getOtherPart() {
		return otherPart;
	}
	
	public TypeCollector getTypeCollector() {
		return typeCollector;
	}
	
	public OWLOntology getSurrogateTypePart() {
		return surrogateTypePart;
	}
	
	public OWLAxiom getOriginalAxiom(OWLAxiom substituteAxiom) {
		return substituteAxiomMap.get(substituteAxiom);
	}
	
	public void split(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		typeCollector = new TypeCollector(ontology.getOWLOntologyManager().getOWLDataFactory());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		initializeOntologies(manager);
		assignAxioms(ontology, reasonerFactory);
	}
	
	private void initializeOntologies(OWLOntologyManager manager) throws OWLOntologyCreationException {
		IRI consistentPartIRI = Util.generateRandomIRI("ConsistentSubset");
		consistentPart = manager.createOntology(consistentPartIRI);
		otherPart = manager.createOntology(Util.generateRandomIRI("RemainingAxioms"));
		surrogateTypePart = manager.createOntology(Util.generateRandomIRI("SurrogateIndividualTypes"));
		OWLImportsDeclaration decl = manager.getOWLDataFactory().getOWLImportsDeclaration(consistentPartIRI);
		manager.applyChange(new AddImport(otherPart, decl));
		manager.applyChange(new AddImport(surrogateTypePart, decl));
	}
	
	private void assignAxioms(OWLOntology originalOntology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		AxiomInterpreter interpreter = new AxiomInterpreter(new TrivialModel(originalOntology, reasonerFactory));
		OWLOntologyManager manager = consistentPart.getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for (OWLOntology inClosure : originalOntology.getImportsClosure()) {
			for (OWLAxiom axiom : inClosure.getAxioms()) {
				if (axiom.accept(interpreter)) {
					changes.add(new AddAxiom(consistentPart, axiom));
				}
				else {
					OWLAxiom substituteAxiom = axiom.accept(typeCollector);
					if (substituteAxiom != null) {
						changes.add(new AddAxiom(surrogateTypePart, substituteAxiom));
						substituteAxiomMap.put(substituteAxiom, axiom);
					}
					changes.add(new AddAxiom(otherPart, axiom));
				}
			}
		}
		manager.applyChanges(changes);
		changes.clear();
		for (OWLAxiom axiom : typeCollector.getSingletonAxioms()) {
			changes.add(new AddAxiom(surrogateTypePart, axiom));
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

}
