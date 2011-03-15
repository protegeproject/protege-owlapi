package org.protege.owlapi.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.semanticweb.owlapi.model.OWLMutableOntology;

public interface WriteSafeOWLOntology extends OWLMutableOntology {
    void setLocks(ReentrantReadWriteLock locks);
    ReadLock getReadLock();
    WriteLock getWriteLock();
}
