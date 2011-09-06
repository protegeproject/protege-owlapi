package org.protege.owlapi.inference.cls;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.protege.owlapi.inference.orphan.Relation;
import org.protege.owlapi.inference.orphan.TerminalElementFinder;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

public class ClassHierarchyReasoner {
    private OWLOntologyManager owlOntologyManager;
    
    private Set<OWLOntology> ontologies;
    
    private OWLClass root;

    private NamedClassExtractor namedClassExtractor;

    private ChildClassExtractor childClassExtractor;

    private OWLOntologyChangeListener listener;

    private TerminalElementFinder<OWLClass> rootFinder;
    
    public ClassHierarchyReasoner(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
        this.owlOntologyManager = manager;
        OWLDataFactory factory = manager.getOWLDataFactory();
        root = factory.getOWLThing();
        ontologies = new HashSet<OWLOntology>();
        
        rootFinder = new TerminalElementFinder<OWLClass>(new Relation<OWLClass>() {
            public Collection<OWLClass> getR(OWLClass cls) {
                Collection<OWLClass> parents = getSuperClasses(cls, true).getFlattened();
                parents.remove(root);
                return parents;
            }
        });

        namedClassExtractor = new NamedClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        owlOntologyManager.addOntologyChangeListener(listener);
    }
    
    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLClass> possibleTerminalElements = new HashSet<OWLClass>();
        Set<OWLClass> notInOntologies = new HashSet<OWLClass>();
        
        for (OWLOntologyChange change : changes) {
            // only listen for changes on the appropriate ontologies
            if (ontologies.contains(change.getOntology())){
                if (change.isAxiomChange()) {
                    boolean remove = change instanceof RemoveAxiom;
                    OWLAxiom axiom = change.getAxiom();

                    for (OWLEntity entity : axiom.getSignature()) {
                        if (!(entity instanceof OWLClass) || entity.equals(root)) {
                            continue;
                        }
                        OWLClass cls = (OWLClass) entity;
                        if (remove && !containsReference(cls)) {
                            notInOntologies.add(cls);
                            continue;
                        }
                        else if (!remove) {
                            notInOntologies.remove(cls);
                        }
                        possibleTerminalElements.add(cls);
                    }
                }
            }
        }
        
        possibleTerminalElements.addAll(rootFinder.getTerminalElements());
        possibleTerminalElements.removeAll(notInOntologies);
        rootFinder.findTerminalElements(possibleTerminalElements);
    }
    
    private boolean containsReference(OWLClass object) {
        for (OWLOntology ont : ontologies) {
            if (ont.containsClassInSignature(object.getIRI())) {
                return true;
            }
        }
        return false;
    }
    
    public void preComputeInferences() {
        rootFinder.clear();
        for (OWLOntology ont : ontologies) {
            Set<OWLClass> ref = ont.getClassesInSignature();
            rootFinder.appendTerminalElements(ref);
        }
        rootFinder.finish();
    }
    
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        if (ce.isAnonymous()) {
            return new OWLClassNodeSet(owlOntologyManager.getOWLDataFactory().getOWLThing());
        }
        else {
            OWLClass cls = ce.asOWLClass();
            Set<OWLClass> parents = new TreeSet<OWLClass>();
            namedClassExtractor.reset();
            for (OWLClassExpression superClassExpression : cls.getSuperClasses(ontologies)) {
                superClassExpression.accept(namedClassExtractor);
            }
            for (OWLClassExpression equivalentClassExpression : cls.getEquivalentClasses(ontologies)) {
                if (equivalentClassExpression.isAnonymous()) {
                    equivalentClassExpression.accept(namedClassExtractor);
                }
            }
            parents.addAll(namedClassExtractor.getResult());
            throw new UnsupportedOperationException("How should I do this?  Is OWLReasoner the best interface?");
        }
    }

    
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    public void dispose() {
        owlOntologyManager.removeOntologyChangeListener(listener);
    }

}
