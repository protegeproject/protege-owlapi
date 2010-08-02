package org.protege.owlapi.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

public class ChangesLock {
    private List<WriteSafeOWLOntology> ontologies;
    
    public ChangesLock(OWLOntology ontology) {
        this(Collections.singleton(ontology));
    }
    
    public ChangesLock(List<? extends OWLOntologyChange> changes) {
        this(collectChangingOntologies(changes));
    }
    
    public ChangesLock(Collection<OWLOntology> ontologies) {
        this.ontologies = new ArrayList<WriteSafeOWLOntology>();
        for (OWLOntology ontology : ontologies) {
            if (ontology instanceof WriteSafeOWLOntology) {
                this.ontologies.add((WriteSafeOWLOntology) ontology);
            }
        }
        Collections.sort(this.ontologies, new WriteSafeComparator());
    }
    
    public static Collection<OWLOntology> collectChangingOntologies(List<? extends OWLOntologyChange> changes) {
        Set<OWLOntology> ontologies = new TreeSet<OWLOntology>();
        for (OWLOntologyChange change : changes) {
            ontologies.add(change.getOntology());
        }
        return ontologies;
    }

    public void lock() {
        for (WriteSafeOWLOntology ontology : ontologies) {
            ontology.getWriteLock().lock();
        }
    }
    
    public void unlock() {
        for (int i = ontologies.size() - 1; i >= 0; i--) {
            ontologies.get(i).getWriteLock().unlock();
        }
    }
}
