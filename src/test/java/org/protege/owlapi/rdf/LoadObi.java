package org.protege.owlapi.rdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.protege.owlapi.rdf.report.MisreadAnnotationDomainAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class LoadObi {
	public static final String ROOT_DIR="src/test/resources/obi";
    public static final String LOCATION="src/test/resources/obi/branches/obi.owl";
    public static final String TEST_LOCATION="src/test/resources/obi/branches/obi-test.owl";
    
    public static final OWLObjectProperty LOCATED_IN = OWLManager.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://www.obofoundry.org/ro/ro.owl#located_in"));
    public static final OWLClass CONTINUANT = OWLManager.getOWLDataFactory().getOWLClass(IRI.create("http://www.ifomis.org/bfo/1.1/snap#Continuant"));
    
    public static void main(String[] args) throws Exception {
    	OWLOntology ontology = loadObi();
    	OWLReasoner reasoner = getReasoner(ontology);
    	// showSources(ontology);
    	checkMissingAxiomsInferred(ontology, reasoner);
    	testMissingInference(ontology, reasoner);
    	
    	ontology = extract(ontology);
    	reasoner = getReasoner(ontology);
    	testMissingInference(ontology, reasoner);
    	ontology.getOWLOntologyManager().saveOntology(ontology, new OWLFunctionalSyntaxOntologyFormat(), IRI.create(new File(TEST_LOCATION)));
    }
    
    public static OWLOntology loadObi() throws OWLOntologyCreationException {
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	manager.loadOntologyFromOntologyDocument(new File(ROOT_DIR, "branches/external.owl"));
    	manager.addIRIMapper(new SimpleIRIMapper(IRI.create("http://purl.org/obo/owl/ro_bfo_bridge1_1"), 
    			                                 IRI.create(new File(ROOT_DIR, "external/ro_bfo_bridge11.owl"))));
    	OWLOntologyIRIMapper mapper = new AutoIRIMapper(new File(ROOT_DIR), true);
    	manager.addIRIMapper(mapper);
    	OWLOntologyDocumentSource source = new FileDocumentSource(new File(LOCATION));
    	System.out.println("Loading ontology");
    	long startTime = System.currentTimeMillis();
    	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source);
    	System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms");
    	return ontology;
    }
    
    public static void showSources(OWLOntology ontology) {
    	OWLOntologyManager manager = ontology.getOWLOntologyManager();
    	for (OWLOntology inImportsClosure : ontology.getImportsClosure()) {
    		System.out.println("Ontology " + ontology + " loaded from " + manager.getOntologyDocumentIRI(inImportsClosure));
    	}
    }
    
    public static OWLReasoner getReasoner(OWLOntology ontology) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String reasonerFactoryName;	
		// reasonerFactoryName = "uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory";
		reasonerFactoryName = "org.semanticweb.HermiT.Reasoner$ReasonerFactory";
		// reasonerFactoryName = "au.csiro.snorocket.owlapi3.SnorocketReasonerFactory";
		OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName(reasonerFactoryName).newInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		return reasoner;
    }
    
    public static void checkMissingAxiomsInferred(OWLOntology ontology, OWLReasoner reasoner) {
    	Validator rdfValidator = new Validator();
    	Set<OWLAxiom> alreadyReported = new TreeSet<OWLAxiom>();
		for (Entry<OWLOntology, List<ProblemReport>> entry : rdfValidator.analyze(ontology).entrySet()) {
			List<ProblemReport> reports = entry.getValue();
			for (ProblemReport report : reports) {
				if (report instanceof MisreadAnnotationDomainAxiom) {
					MisreadAnnotationDomainAxiom misreadAxiom = (MisreadAnnotationDomainAxiom) report;
					OWLAxiom expectedInferred = misreadAxiom.getObjectPropertyDomainAxiom();
					if (alreadyReported.contains(expectedInferred)) {
						continue;
					}
					alreadyReported.add(misreadAxiom.getObjectPropertyDomainAxiom());
					
					if (!reasoner.isEntailed(expectedInferred)) {
						System.out.println(expectedInferred + "is not entailed");
					}
				}
			}
		}
    }
    
    public static OWLOntology extract(OWLOntology ontology) throws OWLOntologyCreationException {
    	OWLOntologyManager altManager = OWLManager.createOWLOntologyManager();
    	OWLOntology extracted = altManager.createOntology(IRI.create("http://extracted.owl"));
    	OWLAnnotationProperty label = altManager.getOWLDataFactory().getRDFSLabel();
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	for (OWLOntology inImportsClosure : ontology.getImportsClosure()) {
    		for (OWLAxiom axiom : inImportsClosure.getAxioms()) {
    			if (axiom.isLogicalAxiom()) {
    				changes.add(new AddAxiom(extracted, axiom));
    			}
//    			else if (axiom instanceof OWLAnnotationAssertionAxiom &&
//    						((OWLAnnotationAssertionAxiom) axiom).getProperty().equals(label)) {
//    				changes.add(new AddAxiom(extracted, axiom));
//    			}
    		}
    	}
    	altManager.applyChanges(changes);
    	return extracted;
    	
    }
    
    public static void testMissingInference(OWLOntology ontology, OWLReasoner reasoner) {
    	System.out.println("----------------testMissingInference----------------");
    	OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
    	OWLAxiom domainAxiom = factory.getOWLObjectPropertyDomainAxiom(LOCATED_IN, CONTINUANT);
    	System.out.println(domainAxiom.toString() + " is entailed? " + reasoner.isEntailed(domainAxiom));
    	OWLClassExpression someValuesFrom = factory.getOWLObjectSomeValuesFrom(LOCATED_IN, factory.getOWLThing());
    	System.out.println("Super classes of " + someValuesFrom + " includes continuant? " 
    			+ reasoner.getSuperClasses(someValuesFrom, true).getFlattened().contains(CONTINUANT));
    	System.out.println("Equivalent classes of " + someValuesFrom + " includes continuant? " 
    			+ reasoner.getEquivalentClasses(someValuesFrom).getEntities().contains(CONTINUANT));
    }
}
