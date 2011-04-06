package org.protege.owlapi.inconsistent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protege.owlapi.inconsistent.trivialModel.AxiomInterpreter;
import org.protege.owlapi.inconsistent.trivialModel.TrivialModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OntologySplitter {
	private OWLOntology originalOntology;
	private OWLOntology consistentPart;
	private OWLOntology otherPart;
	private OWLOntology surrogateTypePart;
	private OWLOntology hotSpotPart;
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
	
	public OWLOntology getHotSpotPart() {
		return hotSpotPart;
	}
	
	public OWLAxiom getOriginalAxiom(OWLAxiom substituteAxiom) {
		return substituteAxiomMap.get(substituteAxiom);
	}
	
	public void split(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		originalOntology = ontology;
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
		hotSpotPart = manager.createOntology(Util.generateRandomIRI("Hotspots"));
		OWLImportsDeclaration decl = manager.getOWLDataFactory().getOWLImportsDeclaration(consistentPartIRI);
		manager.applyChange(new AddImport(otherPart, decl));
		manager.applyChange(new AddImport(surrogateTypePart, decl));
		manager.applyChange(new AddImport(hotSpotPart, decl));
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
					else {
						changes.add(new AddAxiom(hotSpotPart, axiom));
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
	
	
	public File saveOntologies(File parent) throws OWLOntologyStorageException {
		OWLOntologyManager manager = consistentPart.getOWLOntologyManager();
		File dir;
		for (int i = 0; true; i++) {
			dir = new File(parent, "explanation-" + i);
			if (!dir.exists()) {
				break;
			}
		}
		dir.mkdir();
		OWLXMLOntologyFormat format = new OWLXMLOntologyFormat();
		OWLOntologyFormat originalFormat = originalOntology.getOWLOntologyManager().getOntologyFormat(originalOntology);
		if (originalFormat.isPrefixOWLOntologyFormat()) {
			format.copyPrefixesFrom(originalFormat.asPrefixOWLOntologyFormat());
		}
		manager.saveOntology(consistentPart, format, new FileDocumentTarget(new File(dir, "ConsistentSubset.owl")));
		manager.saveOntology(surrogateTypePart, format, new FileDocumentTarget(new File(dir, "SurrogateTypes.owl")));
		manager.saveOntology(otherPart, format, new FileDocumentTarget(new File(dir, "RemainingPart.owl")));		
		return dir;
	}

}
