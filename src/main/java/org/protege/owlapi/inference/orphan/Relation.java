package org.protege.owlapi.inference.orphan;

import java.util.Collection;

/**
 * This interface simply represents a relation
 *     R: X -> X.
 * 
 * @author tredmond
 *
 */

public interface Relation<X> {
    
    Collection<X> getR(X x);
}
