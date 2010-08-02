package org.protege.owlapi.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.protege.owlapi.concurrent.ChangesLock;
import org.protege.owlapi.concurrent.WriteSafeOWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;



public class ProtegeOWLOntologyManager extends OWLOntologyManagerImpl {
    private boolean useWriteSafety = false;
    private boolean useSwingThread = false;
    private List<OWLOntologyFactory> ontologyFactories = new ArrayList<OWLOntologyFactory>();

    
    public ProtegeOWLOntologyManager(OWLDataFactory factory) {
        super(factory);
    }
    
    public void setUseWriteSafety(boolean useWriteSafety) {
        this.useWriteSafety = useWriteSafety;
    }
    
    public boolean isUseWriteSafety() {
        return useWriteSafety;
    }
    
    public boolean isUseSwingThread() {
        return useSwingThread;
    }

    public void setUseSwingThread(boolean useSwingThread) {
        this.useSwingThread = useSwingThread;
    }

    /*
     * Factory stuff...
     */
    public List<OWLOntologyFactory> getOWLOntologyFactories() {
        return new ArrayList<OWLOntologyFactory>(ontologyFactories);
    }

    @Override
    public void addOntologyFactory(OWLOntologyFactory factory) {
        factory = wrapFactory(factory);
        super.addOntologyFactory(factory);
        ontologyFactories.add(0, factory);
    }
    
    @Override
    public void removeOntologyFactory(OWLOntologyFactory factory) {
        factory = wrapFactory(factory); // otherwise .equals won't work in both directions
        super.removeOntologyFactory(factory);
        ontologyFactories.remove(factory);
    }
    
    private OWLOntologyFactory wrapFactory(OWLOntologyFactory factory) {
        if (useWriteSafety && !(factory instanceof WriteSafeOWLOntologyFactory)) {
            factory = new WriteSafeOWLOntologyFactory(factory);
        }
        return factory;
    }
    
    public void clearOntologyFactories() {
        for (OWLOntologyFactory factory : new ArrayList<OWLOntologyFactory>(ontologyFactories)) {
            removeOntologyFactory(factory);
        }
    }
    
    /*
     * Change locking stuff
     */
    
    private List<OWLOntologyChange> addAxiomsSuper(OWLOntology ont, Set<? extends OWLAxiom> axioms) {
        return super.addAxioms(ont, axioms);
    }
    
    @Override
    public List<OWLOntologyChange> addAxioms(final OWLOntology ont, final Set<? extends OWLAxiom> axioms) {
        ChangeApplier run = new ChangeApplier() {

            public void run() {
                ChangesLock lock = new ChangesLock(ont);
                lock.lock();
                try {
                    setChanges(addAxiomsSuper(ont, axioms));
                }
                finally {
                    lock.unlock();
                }
            }
        };
        return runChangeApplier(run);
    }
    
    private List<OWLOntologyChange> removeAxiomsSuper(OWLOntology ont, Set<? extends OWLAxiom> axioms) {
        return super.removeAxioms(ont, axioms);
    }
    
    @Override
    public List<OWLOntologyChange> removeAxioms(final OWLOntology ont, final Set<? extends OWLAxiom> axioms) {
        ChangeApplier run = new ChangeApplier() {

            public void run() {
                ChangesLock lock = new ChangesLock(ont);
                lock.lock();
                try {
                    setChanges(removeAxiomsSuper(ont, axioms));
                }
                finally {
                    lock.unlock();
                }
            }
        };
        return runChangeApplier(run);
    }
    
    private List<OWLOntologyChange> applyChangeSuper(OWLOntologyChange change) {
        return super.applyChange(change);
    }
    
    @Override
    public List<OWLOntologyChange> applyChange(final OWLOntologyChange change) {
        ChangeApplier run = new ChangeApplier() {

            public void run() {
                ChangesLock lock = new ChangesLock(change.getOntology());
                lock.lock();
                try {
                    setChanges(applyChangeSuper(change));
                }
                finally {
                    lock.unlock();
                }
            }
        };
        return runChangeApplier(run);
    }
    
    private List<OWLOntologyChange> applyChangesSuper(List<? extends OWLOntologyChange> changes) {
        return super.applyChanges(changes);
    }
    
    public List<OWLOntologyChange> applyChanges(final List<? extends OWLOntologyChange> changes) {
        ChangeApplier run = new ChangeApplier() {

            public void run() {
                ChangesLock lock = new ChangesLock(changes);
                lock.lock();
                try {
                    setChanges(applyChangesSuper(changes));
                }
                finally {
                    lock.unlock();
                }
            }
        };
        return runChangeApplier(run);
    }
    
    private List<OWLOntologyChange> runChangeApplier(ChangeApplier run) {
        if (useSwingThread && !SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(run);
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Unexpected exception writing changes", e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Unexpected exception writing chasnges", e);
            }
        }
        else {
            run.run();
        }
        return run.getChanges();
    }
    
    private static abstract class ChangeApplier implements Runnable {
        private List<OWLOntologyChange> changes;
        
        public List<OWLOntologyChange> getChanges() {
            return changes;
        }
        
        public void setChanges(List<OWLOntologyChange> changes) {
            this.changes = changes;
        }

    }
}
