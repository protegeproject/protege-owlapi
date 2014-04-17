package org.protege.owlapi.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLMutableOntology;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class WriteSafeOWLOntologyFactory implements OWLOntologyFactory {

	private static final long serialVersionUID = -6903012125420580958L;
	private OWLOntologyFactory delegate;
    private ReentrantReadWriteLock lock;
    
    public WriteSafeOWLOntologyFactory(OWLOntologyFactory delegate, ReentrantReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
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
            WriteSafeOWLOntology wrappedOntology = new WriteSafeOWLOntologyImpl((OWLMutableOntology) ontology);
            wrappedOntology.setLocks(lock);
            return wrappedOntology;
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

    public OWLOntologyManager getOWLOntologyManager() {
    	return delegate.getOWLOntologyManager();
    }
    
    /* new OWL api interface -- will be restored with the latest owl api. */
    public OWLOntology loadOWLOntology(OWLOntologyDocumentSource documentSource,
    		                           OWLOntologyCreationHandler handler,
    		                           OWLOntologyLoaderConfiguration configuration)
						throws OWLOntologyCreationException {
	   return wrapOntology(delegate.loadOWLOntology(documentSource, 
			                                        new WrappedOntologyCreationHandler(handler),
			                                        configuration));
    }

    
}
