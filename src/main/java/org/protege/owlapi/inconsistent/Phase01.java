package org.protege.owlapi.inconsistent;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

public class Phase01 {
	public static final Logger LOGGER = Logger.getLogger(Phase01.class);
	
	private boolean reasonerActive = false;
	private boolean isCancelled = false;
	
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
	
	public OWLOntology getHotspots() {
		return splitter.getHotSpotPart();
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
	
	

	public boolean run(OWLOntology ontology, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException {
		synchronized (this) {
			if (isCancelled) {
				return false;
			}
		}
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
		synchronized (this) {
			if (isCancelled) {
				return false;
			}
			reasonerActive = true;
		}
		Set<OWLClass> entities;
		try {
			entities = reasoner.getBottomClassNode().getEntities();
		}
		catch (ReasonerInterruptedException interrupt) {
			LOGGER.info("Reasoning process was interrupted - aborting explanation.");
			return false;
		}
		synchronized (this) {
			reasonerActive = false;
		}
		for (OWLClass c : entities) {
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
		return true;
	}
	
	public void cancel() {
		synchronized (this) {
			isCancelled = true;
			if (reasonerActive) {
				reasoner.interrupt();
			}
		}
	}

	public void reset() {
		isCancelled = false;
		reasonerActive = false;
	}
	
	public void dispose() {
		if (reasoner != null) {
			reasoner.dispose();
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

	public File saveOntologies(File parent) throws OWLOntologyStorageException {
		return splitter.saveOntologies(parent);
	}

}
