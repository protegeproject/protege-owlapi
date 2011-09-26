package org.protege.owlapi.concurrent;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.configurables.ThreadSafeOWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This class is very fragile but it illustrates the motivation for having a ProtegeOWLOntologyManager instead of using the
 * existing OWL api concurrency support.  It shows that adding an axiom to the OWL api (with concurrency turned on) is not an
 * atomic operation.  It is only one style of problem that can come up but it may be that there are other issues as well.  In this example
 * some read calls are made while a write is in progress.  The first read call shows that an axiom has already been added to the 
 * ontology but the second read call shows that the axiom is not present.
 * <p/>
 * This example was run on the OWL api out of svn a revision 1803 but may not work with 
 * future versions of the OWL api.  It shows a race condition and the only way to reliably demonstrate this is using a debugger.
 * The test method below has some instructions on how to set up the break points to demonstrate the issue.
 * <p/>
 * One point is that the OWL api implementation is considerably more complex than the implementation here so it is difficult to 
 * determine what other issues might arise.
 * <p/>
 * For the lazy sets may cases are ruled out because the internals implementation makes an initMap call to ensure that the map 
 * is initialized.  The initMap call takes the lazyIndexLock write lock and appears to block if something interesting
 * is in progress on the writer side.  More checking needed.
 * 
 * @author tredmond
 *
 */
public class OWLApiThreadSafetyIssue {
    public static String NS = "http://protege.org/ontologies/ThreadSafe.owl";
    public static final OWLClass C = ThreadSafeOWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#C"));
    public static final OWLNamedIndividual I = ThreadSafeOWLManager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(NS + "#i"));
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        OWLOntologyManager manager = ThreadSafeOWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        final OWLOntology ontology = manager.createOntology(IRI.create(NS));
        test(ontology, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                test(ontology, true);
            }
        }).start();
        manager.applyChange(new AddAxiom(ontology, factory.getOWLClassAssertionAxiom(C, I)));
    }

    // if things happen just right then the second run through this call displays the following:
    // Stopping at a break point
    // Class assertion axioms involving <http://protege.org/ontologies/ThreadSafe.owl#C> = [ClassAssertion(<http://protege.org/ontologies/ThreadSafe.owl#C> <http://protege.org/ontologies/ThreadSafe.owl#i>)]
    // Class assertion axioms involving <http://protege.org/ontologies/ThreadSafe.owl#i> = []
    public static void test(OWLOntology ontology, boolean display) {
        if (display) {
            // set the first break point here and set the second breakpoint in OWLOntologyImpl.handleAxiomAdded
            // When both break points are achieved loop through the handleAxiomAdded until the first entity (the
            // class C has been added to the referenced classes but the individual has not.
            // then let this breakpoint go through to the end
            // the result is that
            //    1. the first getReferencingAxioms call returns a result indicating that the axiom has already been added.
            //    2. the second getReferencingAxioms call returns a result indicating that the axiom has not already been added.
            // This experiment is fragile.
            System.out.println("Stopping at a break point");
        }
        Set<OWLAxiom> axioms1 = ontology.getReferencingAxioms(C);
        Set<OWLAxiom> axioms2 = ontology.getReferencingAxioms(I);
        ontology.getClassAssertionAxioms(I);
        ontology.getClassAssertionAxioms(C);
        if (display) {
            System.out.println("Class assertion axioms involving " + C + " = " + axioms1);
            System.out.println("Class assertion axioms involving " + I + " = " + axioms2);
        }
    }
    
}
