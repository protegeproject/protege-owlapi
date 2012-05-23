package org.protege.owlapi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.swing.SwingUtilities;

import org.protege.owlapi.concurrent.WriteSafeOWLOntologyFactory;
import org.protege.owlapi.util.SaveResultsRunnable;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;



public class ProtegeOWLOntologyManager extends OWLOntologyManagerImpl implements WriteSafeOWLOntologyManager {
	private static final long serialVersionUID = -6371104970223669912L;
	private boolean useWriteSafety = false;
    private boolean useSwingThread = false;
    private List<OWLOntologyFactory> ontologyFactories = new ArrayList<OWLOntologyFactory>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    
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
    
    public ReentrantReadWriteLock getReadWriteLock() {
        return lock;
    }
    
    public ReadLock getReadLock() {
        return lock.readLock();
    }

    public WriteLock getWriteLock() {
        return lock.writeLock();
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
            factory = new WriteSafeOWLOntologyFactory(factory, lock);
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
        return callWithWriteLockUnchecked(new Callable<List<OWLOntologyChange>>() {
                    
                public List<OWLOntologyChange> call() {
                    return addAxiomsSuper(ont, axioms);
                }
            });
    }
    
    private List<OWLOntologyChange> removeAxiomsSuper(OWLOntology ont, Set<? extends OWLAxiom> axioms) {
        return super.removeAxioms(ont, axioms);
    }
    
    @Override
    public List<OWLOntologyChange> removeAxioms(final OWLOntology ont, final Set<? extends OWLAxiom> axioms) {
        return callWithWriteLockUnchecked(new Callable<List<OWLOntologyChange>>() {
                    
                public List<OWLOntologyChange> call() {
                    return removeAxiomsSuper(ont, axioms);
                }
            });
    }
    
    private List<OWLOntologyChange> applyChangeSuper(OWLOntologyChange change) {
        return super.applyChange(change);
    }
    
    @Override
    public List<OWLOntologyChange> applyChange(final OWLOntologyChange change) {
        return callWithWriteLockUnchecked(new Callable<List<OWLOntologyChange>>() {
                    
                public List<OWLOntologyChange> call() {
                    return applyChangeSuper(change);
                }
            });
    }
    
    private List<OWLOntologyChange> applyChangesSuper(List<? extends OWLOntologyChange> changes) {
        return super.applyChanges(changes);
    }
    
    public List<OWLOntologyChange> applyChanges(final List<? extends OWLOntologyChange> changes) {
        return callWithWriteLockUnchecked(new Callable<List<OWLOntologyChange>>() {
                    
                public List<OWLOntologyChange> call() {
                    return applyChangesSuper(changes);
                }
            });
    }
    
    public <X> X callWithWriteLock(final Callable<X> call) throws Exception {
        if (useSwingThread && !SwingUtilities.isEventDispatchThread()) {
            final SaveResultsRunnable<X> run = new SaveResultsRunnable<X>(call);
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    lock.writeLock().lock();
                    try {
                        run.run();
                    }
                    finally {
                        lock.writeLock().unlock();
                    }
                }
            });
            if (run.getException() != null) {
                throw run.getException();
            }
            return run.getResult();
        }
        else {
            lock.writeLock().lock();
            try {
                return call.call();
            }
            finally {
                lock.writeLock().unlock();
            }
        }
    }

    public <X> X callWithWriteLockUnchecked(final Callable<X> call) {
        try {
            return callWithWriteLock(call);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
