package org.protege.owlapi.concurrent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLMutableOntology;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

public class WriteSafeOWLOntologyImpl implements OWLMutableOntology, WriteSafeOWLOntology {

	private static final long serialVersionUID = -5483935495647547265L;
	private OWLMutableOntology delegate;
    private ReadLock  readLock;
    private WriteLock writeLock;


    public WriteSafeOWLOntologyImpl(OWLMutableOntology delegate) {
        this.delegate = delegate;
    }
    
    public void setLocks(ReentrantReadWriteLock locks) {
        readLock = locks.readLock();
        writeLock = locks.writeLock();
    }
    
    public ReadLock getReadLock() {
        return readLock;
    }

    public WriteLock getWriteLock() {
        return writeLock;
    }
    
    public boolean equals(Object o) {
        return delegate.equals(o);
    }
    
    public int hashCode() {
        return delegate.hashCode();
    }
    
    @Override
    public String toString() {
        return delegate.toString();
    }

    /* ************************************************
     * OWLMutableOntology interfaces.
     */
    public List<OWLOntologyChange> applyChange(OWLOntologyChange change) throws OWLOntologyChangeException {
        writeLock.lock();
        try {
            return delegate.applyChange(change);
        }
        finally {
            writeLock.unlock();
        }
    }

    public List<OWLOntologyChange> applyChanges(List<OWLOntologyChange> changes) throws OWLOntologyChangeException {
        writeLock.lock();
        try {
            return delegate.applyChanges(changes);
        }
        finally {
            writeLock.unlock();
        }
    }
    
    public void accept(OWLObjectVisitor visitor) {
        delegate.accept(visitor);
    }

    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return delegate.accept(visitor);
    }

    public int compareTo(OWLObject o) {
        readLock.lock();
        try {
            return delegate.compareTo(o);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI) {
        readLock.lock();
        try {
            return delegate.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAxiom(OWLAxiom axiom, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsAxiom(axiom, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAxiom(OWLAxiom axiom) {
        readLock.lock();
        try {
            return delegate.containsAxiom(axiom);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsAxiomIgnoreAnnotations(axiom, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom) {
        readLock.lock();
        try {
            return delegate.containsAxiomIgnoreAnnotations(axiom);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsClassInSignature(IRI owlClassIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsClassInSignature(owlClassIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsClassInSignature(IRI owlClassIRI) {
        readLock.lock();
        try {
            return delegate.containsClassInSignature(owlClassIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsDataPropertyInSignature(owlDataPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI) {
        readLock.lock();
        try {
            return delegate.containsDataPropertyInSignature(owlDataPropertyIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsDatatypeInSignature(IRI owlDatatypeIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsDatatypeInSignature(owlDatatypeIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsDatatypeInSignature(IRI owlDatatypeIRI) {
        readLock.lock();
        try {
            return delegate.containsDatatypeInSignature(owlDatatypeIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsEntityInSignature(IRI entityIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(entityIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsEntityInSignature(IRI entityIRI) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(entityIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsEntityInSignature(OWLEntity owlEntity, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(owlEntity, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsEntityInSignature(OWLEntity owlEntity) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(owlEntity);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsIndividualInSignature(IRI owlIndividualIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsIndividualInSignature(owlIndividualIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsIndividualInSignature(IRI owlIndividualIRI) {
        readLock.lock();
        try {
            return delegate.containsIndividualInSignature(owlIndividualIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsObjectPropertyInSignature(owlObjectPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI) {
        readLock.lock();
        try {
            return delegate.containsObjectPropertyInSignature(owlObjectPropertyIRI);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject entity) {
        readLock.lock();
        try {
            return delegate.getAnnotationAssertionAxioms(entity);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
        readLock.lock();
        try {
            return delegate.getAnnotationPropertiesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAnnotationPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAnnotationPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotation> getAnnotations() {
        readLock.lock();
        try {
            return delegate.getAnnotations();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
        readLock.lock();
        try {
            return delegate.getAnonymousIndividuals();
        }
        finally {
            readLock.unlock();
        }
    }
    
    public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getAsymmetricObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public int getAxiomCount() {
        readLock.lock();
        try {
            return delegate.getAxiomCount();
        }
        finally {
            readLock.unlock();
        }
    }

    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxiomCount(axiomType, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
        readLock.lock();
        try {
            return delegate.getAxiomCount(axiomType);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getAxioms() {
        readLock.lock();
        try {
            return delegate.getAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(axiomType, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
        readLock.lock();
        try {
            return delegate.getAxioms(axiomType);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClassAxiom> getAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty prop) {
        readLock.lock();
        try {
            return delegate.getAxioms(prop);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype) {
        readLock.lock();
        try {
            return delegate.getAxioms(datatype);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression prop) {
        readLock.lock();
        try {
            return delegate.getAxioms(prop);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxiomsIgnoreAnnotations(axiom, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom) {
        readLock.lock();
        try {
            return delegate.getAxiomsIgnoreAnnotations(axiom);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClassExpression ce) {
        readLock.lock();
        try {
            return delegate.getClassAssertionAxioms(ce);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getClassAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClass> getClassesInSignature() {
        readLock.lock();
        try {
            return delegate.getClassesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClass> getClassesInSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getClassesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataProperty> getDataPropertiesInSignature() {
        readLock.lock();
        try {
            return delegate.getDataPropertiesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataProperty> getDataPropertiesInSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getDataPropertiesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getDataPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDataPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDataPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(OWLDataProperty subProperty) {
        readLock.lock();
        try {
            return delegate.getDataSubPropertyAxiomsForSubProperty(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(OWLDataPropertyExpression superProperty) {
        readLock.lock();
        try {
            return delegate.getDataSubPropertyAxiomsForSuperProperty(superProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(OWLDatatype datatype) {
        readLock.lock();
        try {
            return delegate.getDatatypeDefinitions(datatype);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDatatype> getDatatypesInSignature() {
        readLock.lock();
        try {
            return delegate.getDatatypesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDatatype> getDatatypesInSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getDatatypesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity subject) {
        readLock.lock();
        try {
            return delegate.getDeclarationAxioms(subject);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getDifferentIndividualAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLOntology> getDirectImports() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getDirectImports();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<IRI> getDirectImportsDocuments() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getDirectImportsDocuments();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getDisjointClassesAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDisjointDataPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getDisjointObjectPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass owlClass) {
        readLock.lock();
        try {
            return delegate.getDisjointUnionAxioms(owlClass);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEntity> getEntitiesInSignature(IRI iri, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getEntitiesInSignature(iri, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEntity> getEntitiesInSignature(IRI iri) {
        readLock.lock();
        try {
            return delegate.getEntitiesInSignature(iri);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getEquivalentClassesAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getEquivalentDataPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getEquivalentObjectPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(OWLDataPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getFunctionalDataPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getFunctionalObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClassAxiom> getGeneralClassAxioms() {
        readLock.lock();
        try {
            return delegate.getGeneralClassAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLHasKeyAxiom> getHasKeyAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getHasKeyAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLOntology> getImports() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getImports();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLOntology> getImportsClosure() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getImportsClosure();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLImportsDeclaration> getImportsDeclarations() {
        readLock.lock();
        try {
            return delegate.getImportsDeclarations();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLNamedIndividual> getIndividualsInSignature() {
        readLock.lock();
        try {
            return delegate.getIndividualsInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLNamedIndividual> getIndividualsInSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getIndividualsInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getInverseFunctionalObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getInverseObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getIrreflexiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public int getLogicalAxiomCount() {
        readLock.lock();
        try {
            return delegate.getLogicalAxiomCount();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLLogicalAxiom> getLogicalAxioms() {
        readLock.lock();
        try {
            return delegate.getLogicalAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getNegativeDataPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getNegativeObjectPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLClassExpression> getNestedClassExpressions() {
        readLock.lock();
        try {
            return delegate.getNestedClassExpressions();
        }
        finally {
            readLock.unlock();
        }
    }
    
    public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
        readLock.lock();
        try {
            return delegate.getObjectPropertiesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLObjectProperty> getObjectPropertiesInSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getObjectPropertiesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(OWLObjectPropertyExpression subProperty) {
        readLock.lock();
        try {
            return delegate.getObjectSubPropertyAxiomsForSubProperty(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(OWLObjectPropertyExpression superProperty) {
        readLock.lock();
        try {
            return delegate.getObjectSubPropertyAxiomsForSuperProperty(superProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    public OWLOntologyID getOntologyID() {
        readLock.lock();
        try {
            return delegate.getOntologyID();
        }
        finally {
            readLock.unlock();
        }
    }

    public OWLOntologyManager getOWLOntologyManager() {
        readLock.lock();
        try {
            return delegate.getOWLOntologyManager();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals() {
        readLock.lock();
        try {
            return delegate.getReferencedAnonymousIndividuals();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getReferencingAxioms(OWLAnonymousIndividual individual) {
        readLock.lock();
        try {
            return delegate.getReferencingAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getReferencingAxioms(owlEntity, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) {
        readLock.lock();
        try {
            return delegate.getReferencingAxioms(owlEntity);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getReflexiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getSameIndividualAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEntity> getSignature() {
        readLock.lock();
        try {
            return delegate.getSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLEntity> getSignature(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(OWLAnnotationProperty subProperty) {
        readLock.lock();
        try {
            return delegate.getSubAnnotationPropertyOfAxioms(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getSubClassAxiomsForSubClass(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getSubClassAxiomsForSuperClass(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getSymmetricObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getTransitiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean isAnonymous() {
        readLock.lock();
        try {
            return delegate.isAnonymous();
        }
        finally {
            readLock.unlock();
        }
    }
    
    public boolean isBottomEntity() {
        readLock.lock();
        try {
            return delegate.isBottomEntity();
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean isDeclared(OWLEntity owlEntity, boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.isDeclared(owlEntity, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean isDeclared(OWLEntity owlEntity) {
        readLock.lock();
        try {
            return delegate.isDeclared(owlEntity);
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return delegate.isEmpty();
        }
        finally {
            readLock.unlock();
        }
    }
    
    public boolean isTopEntity() {
        readLock.lock();
        try {
            return delegate.isTopEntity();
        }
        finally {
            readLock.unlock();
        }
    }
    
    public Set<OWLAxiom> getABoxAxioms(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getABoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }
    
    public Set<OWLAxiom> getRBoxAxioms(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getRBoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }    

    public Set<OWLAxiom> getTBoxAxioms(boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getTBoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    } 
    
}
