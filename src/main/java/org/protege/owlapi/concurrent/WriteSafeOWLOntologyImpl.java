package org.protege.owlapi.concurrent;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
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
import org.semanticweb.owlapi.model.OWLDocumentFormat;
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
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
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
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLPrimitive;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Search;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;

public class WriteSafeOWLOntologyImpl implements OWLMutableOntology, WriteSafeOWLOntology {

	private static final long serialVersionUID = -5483935495647547265L;
	private OWLMutableOntology delegate;
    private ReadLock  readLock;
    private WriteLock writeLock;


    public WriteSafeOWLOntologyImpl(OWLMutableOntology delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void setLocks(ReentrantReadWriteLock locks) {
        readLock = locks.readLock();
        writeLock = locks.writeLock();
    }
    
    @Override
    public ReadLock getReadLock() {
        return readLock;
    }

    @Override
    public WriteLock getWriteLock() {
        return writeLock;
    }
    
    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }
    
    @Override
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
    @Override
    public ChangeApplied applyChange(OWLOntologyChange change)
            throws OWLOntologyChangeException {
        writeLock.lock();
        try {
            return delegate.applyChange(change);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<OWLOntologyChange> applyChanges(List<? extends OWLOntologyChange> changes) throws OWLOntologyChangeException {
        writeLock.lock();
        try {
            return delegate.applyChanges(changes);
        }
        finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public void accept(OWLObjectVisitor visitor) {
        delegate.accept(visitor);
    }

    @Override
    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return delegate.accept(visitor);
    }

    @Override
    public int compareTo(OWLObject o) {
        readLock.lock();
        try {
            return delegate.compareTo(o);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI, Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsAxiom(OWLAxiom axiom) {
        readLock.lock();
        try {
            return delegate.containsAxiom(axiom);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsClassInSignature(IRI owlClassIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsClassInSignature(owlClassIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsDataPropertyInSignature(owlDataPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsDatatypeInSignature(IRI owlDatatypeIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsDatatypeInSignature(owlDatatypeIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsEntityInSignature(IRI entityIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(entityIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsEntityInSignature(OWLEntity owlEntity,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(owlEntity, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsEntityInSignature(OWLEntity owlEntity) {
        readLock.lock();
        try {
            return delegate.containsEntityInSignature(owlEntity);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsIndividualInSignature(IRI owlIndividualIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsIndividualInSignature(owlIndividualIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsObjectPropertyInSignature(owlObjectPropertyIRI, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject entity) {
        readLock.lock();
        try {
            return delegate.getAnnotationAssertionAxioms(entity);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAnnotationPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAnnotationPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
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
    
    @Override
    public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getAsymmetricObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public int getAxiomCount() {
        readLock.lock();
        try {
            return delegate.getAxiomCount();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxiomCount(axiomType, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
        readLock.lock();
        try {
            return delegate.getAxiomCount(axiomType);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAxiom> getAxioms() {
        readLock.lock();
        try {
            return delegate.getAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(axiomType, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
        readLock.lock();
        try {
            return delegate.getAxioms(axiomType);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property) {
        readLock.lock();
        try {
            return delegate.getAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAxiom> getAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty prop) {
        readLock.lock();
        try {
            return delegate.getAxioms(prop);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype) {
        readLock.lock();
        try {
            return delegate.getAxioms(datatype);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression prop) {
        readLock.lock();
        try {
            return delegate.getAxioms(prop);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClassExpression ce) {
        readLock.lock();
        try {
            return delegate.getClassAssertionAxioms(ce);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getClassAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClass> getClassesInSignature() {
        readLock.lock();
        try {
            return delegate.getClassesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClass> getClassesInSignature(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getClassesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature() {
        readLock.lock();
        try {
            return delegate.getDataPropertiesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature(
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getDataPropertiesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getDataPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDataPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDataPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(OWLDataProperty subProperty) {
        readLock.lock();
        try {
            return delegate.getDataSubPropertyAxiomsForSubProperty(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(OWLDataPropertyExpression superProperty) {
        readLock.lock();
        try {
            return delegate.getDataSubPropertyAxiomsForSuperProperty(superProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(OWLDatatype datatype) {
        readLock.lock();
        try {
            return delegate.getDatatypeDefinitions(datatype);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatype> getDatatypesInSignature() {
        readLock.lock();
        try {
            return delegate.getDatatypesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatype> getDatatypesInSignature(
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getDatatypesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity subject) {
        readLock.lock();
        try {
            return delegate.getDeclarationAxioms(subject);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getDifferentIndividualAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLOntology> getDirectImports() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getDirectImports();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<IRI> getDirectImportsDocuments() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getDirectImportsDocuments();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getDisjointClassesAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getDisjointDataPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getDisjointObjectPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass owlClass) {
        readLock.lock();
        try {
            return delegate.getDisjointUnionAxioms(owlClass);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEntity> getEntitiesInSignature(IRI iri,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getEntitiesInSignature(iri, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEntity> getEntitiesInSignature(IRI iri) {
        readLock.lock();
        try {
            return delegate.getEntitiesInSignature(iri);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getEquivalentClassesAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(OWLDataProperty property) {
        readLock.lock();
        try {
            return delegate.getEquivalentDataPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getEquivalentObjectPropertiesAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(OWLDataPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getFunctionalDataPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getFunctionalObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAxiom> getGeneralClassAxioms() {
        readLock.lock();
        try {
            return delegate.getGeneralClassAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLHasKeyAxiom> getHasKeyAxioms(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getHasKeyAxioms(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLOntology> getImports() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getImports();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLOntology> getImportsClosure() throws UnknownOWLOntologyException {
        readLock.lock();
        try {
            return delegate.getImportsClosure();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLImportsDeclaration> getImportsDeclarations() {
        readLock.lock();
        try {
            return delegate.getImportsDeclarations();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature() {
        readLock.lock();
        try {
            return delegate.getIndividualsInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature(
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getIndividualsInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getInverseFunctionalObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getInverseObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getIrreflexiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public int getLogicalAxiomCount() {
        readLock.lock();
        try {
            return delegate.getLogicalAxiomCount();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms() {
        readLock.lock();
        try {
            return delegate.getLogicalAxioms();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getNegativeDataPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getNegativeObjectPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassExpression> getNestedClassExpressions() {
        readLock.lock();
        try {
            return delegate.getNestedClassExpressions();
        }
        finally {
            readLock.unlock();
        }
    }
    
    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
        readLock.lock();
        try {
            return delegate.getObjectPropertiesInSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature(
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getObjectPropertiesInSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyAssertionAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyDomainAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getObjectPropertyRangeAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(OWLObjectPropertyExpression subProperty) {
        readLock.lock();
        try {
            return delegate.getObjectSubPropertyAxiomsForSubProperty(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(OWLObjectPropertyExpression superProperty) {
        readLock.lock();
        try {
            return delegate.getObjectSubPropertyAxiomsForSuperProperty(superProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public OWLOntologyID getOntologyID() {
        readLock.lock();
        try {
            return delegate.getOntologyID();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public OWLOntologyManager getOWLOntologyManager() {
        readLock.lock();
        try {
            return delegate.getOWLOntologyManager();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(
            Imports imports) {
        readLock.lock();
        try {
            return delegate.getReferencedAnonymousIndividuals(imports);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getReflexiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(OWLIndividual individual) {
        readLock.lock();
        try {
            return delegate.getSameIndividualAxioms(individual);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEntity> getSignature() {
        readLock.lock();
        try {
            return delegate.getSignature();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLEntity> getSignature(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getSignature(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(OWLAnnotationProperty subProperty) {
        readLock.lock();
        try {
            return delegate.getSubAnnotationPropertyOfAxioms(subProperty);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getSubClassAxiomsForSubClass(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(OWLClass cls) {
        readLock.lock();
        try {
            return delegate.getSubClassAxiomsForSuperClass(cls);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getSymmetricObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
        readLock.lock();
        try {
            return delegate.getTransitiveObjectPropertyAxioms(property);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isAnonymous() {
        readLock.lock();
        try {
            return delegate.isAnonymous();
        }
        finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean isBottomEntity() {
        readLock.lock();
        try {
            return delegate.isBottomEntity();
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean
            isDeclared(OWLEntity owlEntity, Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.isDeclared(owlEntity, includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isDeclared(OWLEntity owlEntity) {
        readLock.lock();
        try {
            return delegate.isDeclared(owlEntity);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return delegate.isEmpty();
        }
        finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean isTopEntity() {
        readLock.lock();
        try {
            return delegate.isTopEntity();
        }
        finally {
            readLock.unlock();
        }
    }
    
    @Override
    public Set<OWLAxiom> getABoxAxioms(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getABoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }
    
    @Override
    public Set<OWLAxiom> getRBoxAxioms(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getRBoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }    

    @Override
    public Set<OWLAxiom> getTBoxAxioms(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getTBoxAxioms(includeImportsClosure);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public void accept(OWLNamedObjectVisitor visitor) {
        readLock.lock();
        try {
            delegate.accept(visitor);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <O> O accept(OWLNamedObjectVisitorEx<O> visitor) {
        readLock.lock();
        try {
            return delegate.accept(visitor);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setOWLOntologyManager(OWLOntologyManager saveOntology) {
        writeLock.lock();
        try {
            delegate.setOWLOntologyManager(saveOntology);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology() throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(IRI arg0) throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(arg0);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OutputStream outputStream)
            throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(outputStream);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OWLDocumentFormat ontologyFormat)
            throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(ontologyFormat);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OWLDocumentFormat ontologyFormat, IRI documentIRI)
            throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(ontologyFormat, documentIRI);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OWLDocumentFormat ontologyFormat,
            OutputStream outputStream) throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(ontologyFormat, outputStream);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OWLOntologyDocumentTarget documentTarget)
            throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(documentTarget);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void saveOntology(OWLDocumentFormat ontologyFormat,
            OWLOntologyDocumentTarget documentTarget)
            throws OWLOntologyStorageException {
        writeLock.lock();
        try {
            delegate.saveOntology(ontologyFormat, documentTarget);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<OWLAxiom> getAxioms(boolean b) {
        readLock.lock();
        try {
            return delegate.getAxioms(b);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAxiom> getAxioms(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getAxiomCount(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxiomCount(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getLogicalAxioms(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getLogicalAxiomCount(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getLogicalAxiomCount(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsAxiom(OWLAxiom axiom, Imports includeImportsClosure,
            Search ignoreAnnotations) {
        readLock.lock();
        try {
            return delegate.containsAxiom(axiom, includeImportsClosure,
                    ignoreAnnotations);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxiomsIgnoreAnnotations(axiom,
                    includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLPrimitive owlEntity,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getReferencingAxioms(owlEntity,
                    includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAxiom> getAxioms(OWLClass cls,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(cls, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyAxiom>
            getAxioms(OWLObjectPropertyExpression property,
                    Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(individual, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(datatype, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLClassAxiom> getAxioms(OWLClass cls,
            boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(cls, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLObjectPropertyAxiom>
            getAxioms(OWLObjectPropertyExpression property,
                    boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property,
            boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual,
            boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(individual, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property,
            boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(property, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype,
            boolean includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getAxioms(datatype, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate
                    .getAnnotationPropertiesInSignature(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<IRI> getPunnedIRIs(Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.getPunnedIRIs(includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsReference(OWLEntity entity,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.containsReference(entity, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(Class<T> type,
            OWLObject entity, Imports includeImports, Search forSubPosition) {
        readLock.lock();
        try {
            return delegate.getAxioms(type, entity, includeImports,
                    forSubPosition);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> Collection<T> filterAxioms(
            OWLAxiomSearchFilter filter, Object key,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.filterAxioms(filter, key, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(OWLAxiomSearchFilter filter, Object key,
            Imports includeImportsClosure) {
        readLock.lock();
        try {
            return delegate.contains(filter, key, includeImportsClosure);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(Class<T> type,
            Class<? extends OWLObject> explicitClass, OWLObject entity,
            Imports includeImports, Search forSubPosition) {
        readLock.lock();
        try {
            return delegate.getAxioms(type, explicitClass, entity,
                    includeImports, forSubPosition);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public ChangeApplied addAxiom(OWLAxiom axiom) {
        writeLock.lock();
        try {
            return delegate.addAxiom(axiom);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<OWLOntologyChange> addAxioms(Set<? extends OWLAxiom> axioms) {
        writeLock.lock();
        try {
            return delegate.addAxioms(axioms);
        } finally {
            writeLock.unlock();
        }
    } 
    
}
