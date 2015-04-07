package org.protege.owlapi.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

public class WriteSafeOWLOntologyFactory implements OWLOntologyFactory {

    private static final long serialVersionUID = -6903012125420580958L;

    private OWLOntologyFactory delegate;

    private ReentrantReadWriteLock lock;

    public WriteSafeOWLOntologyFactory(OWLOntologyFactory delegate, ReentrantReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    private OWLOntology wrapOntology(OWLOntology ontology) {
        if (ontology instanceof OWLMutableOntology && !(ontology instanceof WriteSafeOWLOntology)) {
            WriteSafeOWLOntology wrappedOntology = new WriteSafeOWLOntologyImpl((OWLMutableOntology) ontology);
            wrappedOntology.setLocks(lock);
            return wrappedOntology;
        } else {
            return ontology;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Nonnull
    @Override
    public OWLOntology createOWLOntology(OWLOntologyManager owlOntologyManager, OWLOntologyID owlOntologyID, IRI iri, OWLOntologyCreationHandler owlOntologyCreationHandler) throws OWLOntologyCreationException {
        return wrapOntology(delegate.createOWLOntology(owlOntologyManager, owlOntologyID, iri, new WrappedOntologyCreationHandler(owlOntologyCreationHandler)));
    }
    
    
    /* **********************************************************************************************************
     * OWLOntologyFactory interfaces
     */

    @Nonnull
    @Override
    public OWLOntology loadOWLOntology(OWLOntologyManager owlOntologyManager, OWLOntologyDocumentSource owlOntologyDocumentSource, OWLOntologyCreationHandler owlOntologyCreationHandler, OWLOntologyLoaderConfiguration owlOntologyLoaderConfiguration) throws OWLOntologyCreationException {
        return wrapOntology(delegate.loadOWLOntology(owlOntologyManager, owlOntologyDocumentSource, new WrappedOntologyCreationHandler(owlOntologyCreationHandler), owlOntologyLoaderConfiguration));
    }

    @Override
    public boolean canCreateFromDocumentIRI(IRI iri) {
        return delegate.canCreateFromDocumentIRI(iri);
    }

    @Override
    public boolean canLoad(OWLOntologyDocumentSource owlOntologyDocumentSource) {
        return delegate.canLoad(owlOntologyDocumentSource);
    }

    private class WrappedOntologyCreationHandler implements OWLOntologyCreationHandler {

        private OWLOntologyCreationHandler delegate;

        public WrappedOntologyCreationHandler(OWLOntologyCreationHandler delegate) {
            this.delegate = delegate;
        }

        public void ontologyCreated(OWLOntology ontology) {
            delegate.ontologyCreated(wrapOntology(ontology));
        }

        @Override
        public void setOntologyFormat(OWLOntology ontology, OWLDocumentFormat format) {
            delegate.setOntologyFormat(wrapOntology(ontology), format);
        }
    }
}
