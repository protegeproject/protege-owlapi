package org.protege.owlapi.concurrent;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


public class SynchronizedOWLDataFactoryImpl extends OWLDataFactoryImpl {

	private static final long serialVersionUID = 8648229272581933014L;
	private static SynchronizedOWLDataFactoryImpl instance;
       
    public static OWLDataFactory getInstance() {
        if (instance == null) {
            instance = new SynchronizedOWLDataFactoryImpl();
        }
        return instance;
    }

    public synchronized OWLClass getOWLClass(IRI iri) {
        return super.getOWLClass(iri);
    }

    public synchronized OWLObjectProperty getOWLObjectProperty(IRI iri) {
        return super.getOWLObjectProperty(iri);
    }

    public synchronized OWLDataProperty getOWLDataProperty(IRI iri) {
        return super.getOWLDataProperty(iri);
    }

    public synchronized OWLDatatype getOWLDatatype(IRI iri) {
        return super.getOWLDatatype(iri);
    }

    public synchronized OWLNamedIndividual getOWLNamedIndividual(IRI iri) {
        return super.getOWLNamedIndividual(iri);
    }

    public synchronized OWLAnnotationProperty getOWLAnnotationProperty(IRI iri) {
        return super.getOWLAnnotationProperty(iri);
    }
}
