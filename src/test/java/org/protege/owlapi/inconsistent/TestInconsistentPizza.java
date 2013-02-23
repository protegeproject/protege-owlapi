package org.protege.owlapi.inconsistent;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.testng.annotations.Test;

public class TestInconsistentPizza {
	public static final String PIZZA_NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
	
	@Test
	public void testPizza01() throws OWLOntologyCreationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology pizza = manager.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza01.owl"));
        Class<?> hermitClass = Class.forName("uk.ac.manchester.cs.jfact.JFactFactory");
        OWLReasonerFactory hermitFactory = (OWLReasonerFactory) hermitClass.newInstance();
        Phase01 heuristic = new Phase01();
        heuristic.run(pizza, hermitFactory);
        OWLClass pizzaClass = factory.getOWLClass(IRI.create(PIZZA_NS + "#Pizza"));
        assertTrue(heuristic.getInconsistentClasses().contains(pizzaClass));
        checkExplanationForPizza01(heuristic.explain(pizzaClass), factory);
        OWLNamedIndividual myPizza = factory.getOWLNamedIndividual(IRI.create(PIZZA_NS + "#myPizza"));
        assertTrue(heuristic.getInconsistentIndividuals().contains(myPizza));
        checkExplanationForPizza01(heuristic.explain(myPizza), factory);
	}
	
	private void checkExplanationForPizza01(Set<OWLAxiom> axioms, OWLDataFactory factory) {
		OWLClass vegetarianToppingClass = factory.getOWLClass(IRI.create(PIZZA_NS + "#VegetarianTopping"));
		for (OWLAxiom axiom : axioms) {
			if (axiom instanceof OWLEquivalentClassesAxiom &&
					((OWLEquivalentClassesAxiom) axiom).getClassExpressions().contains(vegetarianToppingClass)) {
				return; // don't fail
			}
		}
		fail("Should find the definition for " + vegetarianToppingClass);
	}
}
