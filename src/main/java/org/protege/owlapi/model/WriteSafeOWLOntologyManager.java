package org.protege.owlapi.model;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.semanticweb.owlapi.model.OWLOntologyManager;

public interface WriteSafeOWLOntologyManager extends OWLOntologyManager {

	ReentrantReadWriteLock getReadWriteLock();
	
	ReadLock getReadLock();
	
	WriteLock getWriteLock();
	
}
