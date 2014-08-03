package org.protege.owlapi.model;

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
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;



public class ProtegeOWLOntologyManager extends OWLOntologyManagerImpl implements WriteSafeOWLOntologyManager {
	private static final long serialVersionUID = -6371104970223669912L;
	private boolean useWriteSafety = false;
    private boolean useSwingThread = false;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public OWLOntologyFactory wrapFactory(OWLOntologyFactory factory) {
        if (useWriteSafety && !(factory instanceof WriteSafeOWLOntologyFactory)) {
            return new WriteSafeOWLOntologyFactory(factory, lock);
        }
        return factory;
    }
    
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
    
    @Override
    public ReentrantReadWriteLock getReadWriteLock() {
        return lock;
    }
    
    @Override
    public ReadLock getReadLock() {
        return lock.readLock();
    }

    @Override
    public WriteLock getWriteLock() {
        return lock.writeLock();
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
                    
                @Override
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
                    
                @Override
                public List<OWLOntologyChange> call() {
                    return removeAxiomsSuper(ont, axioms);
                }
            });
    }
    
    private ChangeApplied applyChangeSuper(OWLOntologyChange change) {
        return super.applyChange(change);
    }
    
    @Override
    public ChangeApplied applyChange(final OWLOntologyChange change) {
        return callWithWriteLockUnchecked(new Callable<ChangeApplied>() {
                    
            @Override
            public ChangeApplied call() {
                    return applyChangeSuper(change);
                }
            });
    }
    
    private List<OWLOntologyChange> applyChangesSuper(List<? extends OWLOntologyChange> changes) {
        return super.applyChanges(changes);
    }
    
    @Override
    public List<OWLOntologyChange> applyChanges(final List<? extends OWLOntologyChange> changes) {
        return callWithWriteLockUnchecked(new Callable<List<OWLOntologyChange>>() {
                    
                @Override
                public List<OWLOntologyChange> call() {
                    return applyChangesSuper(changes);
                }
            });
    }
    
    public <X> X callWithWriteLock(final Callable<X> call) throws Exception {
        if (useSwingThread && !SwingUtilities.isEventDispatchThread()) {
            final SaveResultsRunnable<X> run = new SaveResultsRunnable<X>(call);
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
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
