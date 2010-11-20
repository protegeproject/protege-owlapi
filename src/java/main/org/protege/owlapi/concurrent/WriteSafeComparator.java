package org.protege.owlapi.concurrent;

import java.util.Comparator;

public class WriteSafeComparator implements Comparator<WriteSafeOWLOntology> {

    public int compare(WriteSafeOWLOntology o1, WriteSafeOWLOntology o2) {
        return o1.getLockId() - o2.getLockId();
    }

}
