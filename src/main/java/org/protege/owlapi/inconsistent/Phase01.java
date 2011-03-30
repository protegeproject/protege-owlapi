package org.protege.owlapi.inconsistent;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

public class Phase01 {
	private OWLOntology ontology;
	private OWLReasonerFactory reasonerFactory;
	private OntologySplitter splitter;
	private OWLOntology consistentOntology;
	private OWLOntology otherPartOntology;
	private OWLOntology surrogateTypeOntology;
	private OWLReasoner reasoner;
	private Set<OWLClass> inconsistentClasses;
	private Set<OWLIndividual> inconsistentIndividuals;
	
	public OWLOntology getOntology() {
		return ontology;
	}
	
	public OWLOntology getConsistentOntology() {
		return consistentOntology;
	}
	
	public OWLOntology getOtherPartOntology() {
		return otherPartOntology;
	}
	
	public OWLOntology getSurrogateTypeOntology() {
		return surrogateTypeOntology;
	}
	
	public Set<OWLClass> getInconsistentClasses() {
		return inconsistentClasses;
	}
	
	public Set<OWLIndividual> getInconsistentIndividuals() {
		return inconsistentIndividuals;
	}

	public OWLReasoner getReasoner() {
		return reasoner;
	}
	
	

	public void phase01(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		this.ontology = ontology;
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		this.reasonerFactory = reasonerFactory;
		splitter = new OntologySplitter();
		splitter.split(ontology, reasonerFactory);
		consistentOntology = splitter.getConsistentPart();
		otherPartOntology = splitter.getOtherPart();
		surrogateTypeOntology = splitter.getSurrogateTypePart();
		reasoner = reasonerFactory.createReasoner(surrogateTypeOntology);
		
		inconsistentClasses = new HashSet<OWLClass>();
		inconsistentIndividuals = new HashSet<OWLIndividual>();
		for (OWLClass c : reasoner.getBottomClassNode().getEntities()) {
			if (c.equals(factory.getOWLNothing())) {
				continue;
			}
			else {
				OWLIndividual i = splitter.getTypeCollector().getTypedIndividual(c);
				if (i != null) {
					inconsistentIndividuals.add(i);
				}
				else {
					inconsistentClasses.add(c);
				}
			}
		}
	}
	
	public Set<OWLAxiom> explain(OWLClass cls) {
		BlackBoxExplanation teacher = new BlackBoxExplanation(consistentOntology, reasonerFactory, reasoner);
		return teacher.getExplanation(cls);
	}
	
	public Set<OWLAxiom> explain(OWLIndividual i) {
		BlackBoxExplanation teacher = new BlackBoxExplanation(surrogateTypeOntology, reasonerFactory, reasoner);
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLClass type = splitter.getTypeCollector().getSurrogateType(i);
		for (OWLAxiom axiom : teacher.getExplanation(type)) {
			OWLAxiom originalAxiom = splitter.getOriginalAxiom(axiom);
			if (originalAxiom != null) {
				axioms.add(originalAxiom);
			}
			else if (!splitter.getTypeCollector().getSingletonAxioms().contains(axiom)){
				axioms.add(axiom);
			}
		}
		return axioms;
	}
	

}
