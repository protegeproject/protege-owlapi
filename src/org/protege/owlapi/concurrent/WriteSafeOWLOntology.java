package org.protege.owlapi.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.semanticweb.owlapi.model.OWLMutableOntology;

public interface WriteSafeOWLOntology extends OWLMutableOntology {
    int getLockId();
    ReadLock getReadLock();
    WriteLock getWriteLock();
}
