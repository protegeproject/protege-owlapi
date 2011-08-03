package org.protege.owlapi.rdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.protege.owlapi.rdf.report.ImportsBrokenOntologies;
import org.protege.owlapi.rdf.report.MisreadAnnotationDomainAxiom;
import org.protege.owlapi.rdf.report.MisreadAnnotationRangeAxiom;
import org.protege.owlapi.rdf.report.MissingRequiredDeclarations;
import org.protege.owlapi.rdf.report.PunnedProperties;
import org.protege.owlapi.util.ImportsClosureComparator;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class Validator {
	private static Logger LOGGER = Logger.getLogger(Validator.class);

	public String generateFullReport(OWLOntology ontology) {
		return generateFullReport(analyze(ontology));
	}
	
	public String generateFullReport(Map<OWLOntology, List<ProblemReport>> problems) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\n<body>\n<h2>Ontology Validation Report</h2>\n<p>\n");
    	for (Entry<OWLOntology, List<ProblemReport>> entry : problems.entrySet()) {
    		OWLOntology badOntology = entry.getKey();
    		List<ProblemReport> reports = entry.getValue();
    		sb.append("<h2>");
    		sb.append(badOntology.getOntologyID().getOntologyIRI().toString());
    		sb.append("</h2>\n");
    		sb.append("This ontology has the following problems:\n<p>\n");
    		for (ProblemReport report : reports) {
    			sb.append("\t" + report.getDetailedDescription() +"\n\n");
    			sb.append("<p>\n");
    		}
    	}
    	sb.append("</body>\n</html>\n");
    	return sb.toString();
	}
	
	public Map<OWLOntology, List<ProblemReport>> analyze(OWLOntology ontology) {
		Map<OWLOntology, List<ProblemReport>> problems = new TreeMap<OWLOntology, List<ProblemReport>>();
		List<OWLOntology> toAnalyze = new ArrayList<OWLOntology>(ontology.getImportsClosure());
		Collections.sort(toAnalyze, new ImportsClosureComparator());
		for (OWLOntology ontologyBeingAnalyzed : toAnalyze) {
			if (!problems.containsKey(ontologyBeingAnalyzed)) {
				check(ontologyBeingAnalyzed, problems);
			}
		}
		return problems;
	}
	
	private void check(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {

		if (problems.containsKey(ontology)) {
			return;
		}
		if (!checkImportsClosure(ontology, problems)) {
			return;
		}
		checkForBadDomainAxiom(ontology, problems);
		checkForBadRangeAxiom(ontology, problems);
		checkContainsRequiredDeclarations(ontology, problems);
		checkForPropertyPuns(ontology, problems);
	}
	
	private boolean checkImportsClosure(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {
		List<OWLOntology> badImports = new ArrayList<OWLOntology>();
		for (OWLOntology inImportsClosure : ontology.getImportsClosure()) {
			if (problems.containsKey(inImportsClosure)) {
				badImports.add(inImportsClosure);
			}
		}
		if (!badImports.isEmpty()) {
			addProblemReport(new ImportsBrokenOntologies(ontology, badImports), problems);
		}
		return badImports.isEmpty();
	}
	
	private void checkContainsRequiredDeclarations(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		Set<OWLEntity> entitiesWithoutDeclarations = new TreeSet<OWLEntity>();
		for (OWLEntity e : ontology.getSignature()) {
			if (e instanceof OWLNamedIndividual) {
				continue;
			}
			if (e.isBuiltIn()) {
				continue;
			}
			OWLAxiom declaration = factory.getOWLDeclarationAxiom(e);
			if (!ontology.containsAxiom(declaration, true)) {
				entitiesWithoutDeclarations.add(e);
			}
		}
		if (!entitiesWithoutDeclarations.isEmpty()) {
			addProblemReport(new MissingRequiredDeclarations(ontology, entitiesWithoutDeclarations), problems);
		}
	}
	
	private void checkForBadDomainAxiom(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		for (OWLAnnotationPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
			OWLAnnotationProperty annotationProperty = axiom.getProperty();
			OWLAxiom annotationDeclaration = factory.getOWLDeclarationAxiom(annotationProperty);
			OWLAxiom classDeclaration = factory.getOWLDeclarationAxiom(factory.getOWLClass(axiom.getDomain()));
			if (!ontology.containsAxiom(annotationDeclaration, true)
					&& ontology.containsObjectPropertyInSignature(annotationProperty.getIRI(), true) 
					&& !ontology.containsAxiom(classDeclaration, true)) {
				addProblemReport(new MisreadAnnotationDomainAxiom(ontology, axiom), problems);
			}
		}
	}

	private void checkForBadRangeAxiom(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		for (OWLAnnotationPropertyRangeAxiom axiom : ontology.getAxioms(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
			OWLAnnotationProperty annotationProperty = axiom.getProperty();
			OWLAxiom annotationDeclaration = factory.getOWLDeclarationAxiom(annotationProperty);
			OWLAxiom classDeclaration = factory.getOWLDeclarationAxiom(factory.getOWLClass(axiom.getRange()));
			if (!ontology.containsAxiom(annotationDeclaration, true)
					&& ontology.containsObjectPropertyInSignature(annotationProperty.getIRI(), true) 
					&& !ontology.containsAxiom(classDeclaration, true)) {
				addProblemReport(new MisreadAnnotationRangeAxiom(ontology, axiom), problems);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void checkForPropertyPuns(OWLOntology ontology, Map<OWLOntology, List<ProblemReport>> problems) {
		Set<IRI> punnedIris = new TreeSet<IRI>();
		for (OWLAnnotationProperty p : ontology.getAnnotationPropertiesInSignature()) {
			if (ontology.containsObjectPropertyInSignature(p.getIRI())) {
				punnedIris.add(p.getIRI());
			}
			else if (ontology.containsDataPropertyInSignature(p.getIRI())) {
				punnedIris.add(p.getIRI());
			}
		}
		for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
			if (ontology.containsDataPropertyInSignature(p.getIRI())) {
				punnedIris.add(p.getIRI());
			}
		}
		if (!punnedIris.isEmpty()) {
			Map<IRI, Collection<EntityType>> propertyPunMap = new TreeMap<IRI, Collection<EntityType>>();
			for (IRI iri : punnedIris) {
				Collection<EntityType> types = new ArrayList<EntityType>();
				if (ontology.containsAnnotationPropertyInSignature(iri)) {
					types.add(EntityType.ANNOTATION_PROPERTY);
				}
				if (ontology.containsObjectPropertyInSignature(iri)) {
					types.add(EntityType.OBJECT_PROPERTY);
				}
				else if (ontology.containsDataPropertyInSignature(iri)) {
					types.add(EntityType.DATA_PROPERTY);
				}
				propertyPunMap.put(iri, types);
			}
			addProblemReport(new PunnedProperties(ontology, propertyPunMap), problems);
		}
		
	}
	
	private static void addProblemReport(ProblemReport report, Map<OWLOntology, List<ProblemReport>> problems) {
		OWLOntology ontology = report.getOntology();
		List<ProblemReport> reports = problems.get(ontology);
		if (reports == null) {
			reports = new ArrayList<ProblemReport>();
			problems.put(ontology, reports);
		}
		reports.add(report);
	}
}

