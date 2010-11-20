package org.protege.owlapi.concurrent;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLMutableOntology;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class WriteSafeOWLOntologyFactory implements OWLOntologyFactory {
    private OWLOntologyFactory delegate;
    
    public WriteSafeOWLOntologyFactory(OWLOntologyFactory delegate) {
        this.delegate = delegate;
    }
    
    private class WrappedOntologyCreationHandler implements OWLOntologyCreationHandler {
        private OWLOntologyCreationHandler delegate;
        
        public WrappedOntologyCreationHandler(OWLOntologyCreationHandler delegate) {
            this.delegate = delegate;
        }

        public void ontologyCreated(OWLOntology ontology) {
            delegate.ontologyCreated(wrapOntology(ontology));
        }

        public void setOntologyFormat(OWLOntology ontology, OWLOntologyFormat format) {
            delegate.setOntologyFormat(wrapOntology(ontology), format);
        }       
    }
    
    
    private OWLOntology wrapOntology(OWLOntology ontology) {
        if (ontology instanceof OWLMutableOntology &&  !(ontology instanceof WriteSafeOWLOntology)) {
            return new WriteSafeOWLOntologyImpl((OWLMutableOntology) ontology);
        }
        else {
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
    
    
    /* **********************************************************************************************************
     * OWLOntologyFactory interfaces
     */
    
    public boolean canCreateFromDocumentIRI(IRI documentIRI) {
        return delegate.canCreateFromDocumentIRI(documentIRI);
    }

    public boolean canLoad(OWLOntologyDocumentSource documentSource) {
        return delegate.canLoad(documentSource);
    }

    public OWLOntology createOWLOntology(OWLOntologyID ontologyID, IRI documentIRI, OWLOntologyCreationHandler handler) throws OWLOntologyCreationException {
        return wrapOntology(delegate.createOWLOntology(ontologyID, documentIRI, new WrappedOntologyCreationHandler(handler)));
    }

    public OWLOntology loadOWLOntology(OWLOntologyDocumentSource documentSource, OWLOntologyCreationHandler handler) throws OWLOntologyCreationException {
        return wrapOntology(delegate.loadOWLOntology(documentSource, new WrappedOntologyCreationHandler(handler)));
    }

    public void setOWLOntologyManager(OWLOntologyManager owlOntologyManager) {
        delegate.setOWLOntologyManager(owlOntologyManager);
    }

    
}
