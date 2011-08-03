package org.protege.owlapi.rdf;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import org.protege.owlapi.rdf.report.MisreadAnnotationDomainAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class LoadObi {
	public static final String ROOT_DIR="src/test/resources/obi";
    public static final String LOCATION="src/test/resources/obi/branches/obi.owl";
    
    public static void main(String[] args) throws Exception {
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLOntologyIRIMapper mapper = new AutoIRIMapper(new File(ROOT_DIR), true);
    	manager.addIRIMapper(mapper);
    	OWLOntologyDocumentSource source = new FileDocumentSource(new File(LOCATION));
    	OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
    	configuration.setStrict(true);
    	System.out.println("Loading ontology");
    	long startTime = System.currentTimeMillis();
    	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source, configuration);
    	System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms");
    	Validator rdfValidator = new Validator();
    	System.out.println(rdfValidator.generateFullReport(ontology));
		String reasonerFactoryName;	
		// reasonerFactoryName = "uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory";
		reasonerFactoryName = "org.semanticweb.HermiT.Reasoner$ReasonerFactory";
		// reasonerFactoryName = "au.csiro.snorocket.owlapi3.SnorocketReasonerFactory";
		OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName(reasonerFactoryName).newInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		for (Entry<OWLOntology, List<ProblemReport>> entry : rdfValidator.analyze(ontology).entrySet()) {
			for (ProblemReport report : entry.getValue()) {
				if (report instanceof MisreadAnnotationDomainAxiom) {
					MisreadAnnotationDomainAxiom misreadAxiom = (MisreadAnnotationDomainAxiom) report;
					OWLDataFactory factory = misreadAxiom.getOWLDataFactory();
					if (!reasoner.getSuperClasses(factory.getOWLObjectSomeValuesFrom(misreadAxiom.getObjectProperty(), factory.getOWLThing()), false).getFlattened().contains(misreadAxiom.getDomainAsClass())) {
						System.out.println("Oops... " + misreadAxiom.getObjectProperty() + " domain " + misreadAxiom.getDomainAsClass() + " not entailed.");
					}
				}
			}
		}
    }
}
