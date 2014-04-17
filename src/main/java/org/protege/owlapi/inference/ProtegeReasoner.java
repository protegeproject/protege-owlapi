package org.protege.owlapi.inference;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.protege.owlapi.inference.cls.ClassHierarchyReasoner;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.util.Version;

/**
 * This class is intended to capture some very simple inferences used by Prot&#x00E9g&#x00E9 and other
 * tools.  Be warned, this is not a true OWLReasoner; it differs from a true OWLReasoner in that
 * <ul>
 * <li>it is incomplete - it only performs certain inferences.</li>
 * <li>it works with an arbitrary set of ontologies that are not necessarily the 
 *     imports closure of some single ontology.</li>
 * </ul>
 * <p/>
 * This reasoner is an incremental reasoner and will perform the following inferences:
 * <ul>
 * <li> the cheesey pizza inference where it is deduced that a cheesey pizza is a pizza because it is 
 *      equivalent to "Pizza &#x2227 &#x2203 hasTopping. CheeseTopping".</li>
 * <li> orphan finding for the class hierarchy.</li>
 * <li> transitive closure processing for the class and property hierarchies.</li>
 * <li> inverse property processing for object property values and for domains and ranges</li>
 * <li> transitive closure processing for the set of all individuals in a class.
 * </ul>
 * <p/>
 * A goal of this reasoner is that there will be a one-time linear time overhead of any particular inference
 * type and after this price is paid the cost of continuing the inference is constant time.
 */
public class ProtegeReasoner implements OWLReasoner {
    private OWLOntologyManager owlOntologyManager;
    
    private Set<OWLOntology> ontologies;
    
    private ClassHierarchyReasoner classHierarchyReasoner;
    
    
    public ProtegeReasoner(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
        owlOntologyManager = manager;
        ontologies = new TreeSet<OWLOntology>(ontologies);
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
            switch (type) {
            case CLASS_HIERARCHY:
                if (classHierarchyReasoner == null) {
                    classHierarchyReasoner = new ClassHierarchyReasoner(owlOntologyManager, ontologies);
                }
                break;
            case CLASS_ASSERTIONS:
            case DATA_PROPERTY_ASSERTIONS:
            case DATA_PROPERTY_HIERARCHY:
            case DIFFERENT_INDIVIDUALS:
            case DISJOINT_CLASSES:
            case OBJECT_PROPERTY_ASSERTIONS:
            case OBJECT_PROPERTY_HIERARCHY:
            case SAME_INDIVIDUAL:
            	//$CASES-OMITTED$
            }
        }
    }


    public void dispose() {
        if (classHierarchyReasoner != null) {
            classHierarchyReasoner.dispose();
            classHierarchyReasoner = null;
        }
    }

    
    public void interrupt() {

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
        precomputeInferences(InferenceType.CLASS_HIERARCHY);
        return classHierarchyReasoner.getSuperClasses(ce, direct);
    }

    
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) {
        precomputeInferences(InferenceType.CLASS_HIERARCHY);
        return classHierarchyReasoner.getSubClasses(ce, direct);
    }

    
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        precomputeInferences(InferenceType.CLASS_HIERARCHY);
        return classHierarchyReasoner.getEquivalentClasses(ce);
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
