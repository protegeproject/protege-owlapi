package org.protege.owlapi.inference;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.owlapi.inference.orphan.Relation;
import org.protege.owlapi.inference.orphan.TerminalElementFinder;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.util.Version;

/**
 * This class is intended to capture some very simple inferences used by Prot&#x00E9g&#x00E9 and other
 * tools.  In particular, this reasoner does the cheesey pizza inference and will find individuals that 
 * are not members of any particular class and infer that they are members of owl:Thing.
 * 
 */
public class ProtegeQuasiReasoner implements OWLQuasiReasoner {
    private OWLOntologyManager owlOntologyManager;
    
    private Set<OWLOntology> ontologies;
    
    private OWLClass root;

    private ParentClassExtractor parentClassExtractor;

    private ChildClassExtractor childClassExtractor;

    private OWLOntologyChangeListener listener;

    private TerminalElementFinder<OWLClass> rootFinder;
    
    
    public ProtegeQuasiReasoner(OWLOntologyManager manager) {
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


        parentClassExtractor = new ParentClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        parentClassExtractor = new ParentClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        owlOntologyManager.addOntologyChangeListener(listener);
    }
    
    
    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLClass> oldTerminalElements = new HashSet<OWLClass>(rootFinder.getTerminalElements());
        Set<OWLClass> changedClasses = new HashSet<OWLClass>();
        changedClasses.add(root);
        for (OWLOntologyChange change : changes) {
            // only listen for changes on the appropriate ontologies
            if (ontologies.contains(change.getOntology())){
                if (change.isAxiomChange()) {
                    updateImplicitRoots(change);
                    for (OWLEntity entity : ((OWLAxiomChange) change).getEntities()) {
                        if (entity instanceof OWLClass && !entity.equals(root)) {
                            changedClasses.add((OWLClass) entity);
                        }
                    }
                }
            }
        }
    }
        
    private void updateImplicitRoots(OWLOntologyChange change) {
        boolean remove = change instanceof RemoveAxiom;
        OWLAxiom axiom = change.getAxiom();
        Set<OWLClass> possibleTerminalElements = new HashSet<OWLClass>();
        Set<OWLClass> notInOntologies = new HashSet<OWLClass>();
        for (OWLEntity entity : axiom.getSignature()) {
            if (!(entity instanceof OWLClass) || entity.equals(root)) {
                continue;
            }
            OWLClass cls = (OWLClass) entity;
            if (remove && !containsReference(cls)) {
                notInOntologies.add(cls);
                continue;
            }
            possibleTerminalElements.add(cls);
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
    
    
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
        precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }


    public boolean isPrecomputed(InferenceType inferenceType) {
        return true;
    }
    
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return Collections.singleton(InferenceType.CLASS_HIERARCHY);
    }
    
    public void precomputeInferences(InferenceType... inferenceTypes) throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
        for  (InferenceType type : inferenceTypes) {
            if (type == InferenceType.CLASS_HIERARCHY) {
                rootFinder.clear();
                for (OWLOntology ont : ontologies) {
                    Set<OWLClass> ref = ont.getClassesInSignature();
                    rootFinder.appendTerminalElements(ref);
                }
                rootFinder.finish();
            }
        }
    }


    public void dispose() {
        owlOntologyManager.removeOntologyChangeListener(listener);
    }

    
    public void interrupt() {
        // TODO Auto-generated method stub
        
    }


    public void flush() {
        // TODO Auto-generated method stub
        
    }

    
    public BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }

    
    public Node<OWLClass> getBottomClassNode() {
        return OWLClassNode.getBottomNode();
    }

    
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return OWLDataPropertyNode.getBottomNode();
    }

    
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return OWLObjectPropertyNode.getBottomNode();
    }

    
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public FreshEntityPolicy getFreshEntityPolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind, OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Set<OWLAxiom> getPendingAxiomAdditions() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Set<OWLAxiom> getPendingAxiomRemovals() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public List<OWLOntologyChange> getPendingChanges() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public String getReasonerName() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Version getReasonerVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public OWLOntology getRootOntology() {
        throw new UnsupportedOperationException("This is a quasi reasoner");
    }

    
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public long getTimeOut() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public Node<OWLClass> getTopClassNode() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
        return true;
    }

    
    public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException {
        return false;
    }

    
    public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException {
        return false;
    }

    
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return false;
    }

    
    public boolean isSatisfiable(OWLClassExpression classExpression) throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
        return true;
    }

}
