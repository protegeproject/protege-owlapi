package org.protege.owlapi.inconsistent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public class TypeCollector implements OWLAxiomVisitorEx<OWLAxiom> {
	private OWLDataFactory factory;
	private Map<OWLIndividual, OWLClass> surrogateTypeMap = new HashMap<OWLIndividual, OWLClass>();
	private Map<OWLClass, OWLIndividual> typedIndividualMap = new HashMap<OWLClass, OWLIndividual>();
	private Set<OWLAxiom> singletonAxioms = new HashSet<OWLAxiom>();
		
	public TypeCollector(OWLDataFactory factory) {
		this.factory = factory;
	}
	
	public void reset() {
		surrogateTypeMap.clear();
	}

	public OWLClass getSurrogateType(OWLIndividual i) {
		OWLClass surrogate = surrogateTypeMap.get(i);
		if (surrogate == null) {
			surrogate = Util.generateRandomEntity(factory, EntityType.CLASS);
			surrogateTypeMap.put(i, surrogate);
			typedIndividualMap.put(surrogate, i);
			singletonAxioms.add(factory.getOWLSubClassOfAxiom(surrogate, factory.getOWLObjectOneOf(i)));
		}
		return surrogate;
	}
	
	public OWLIndividual getTypedIndividual(OWLClass c) {
		return typedIndividualMap.get(c);
	}
	
	public Set<OWLAxiom> getSingletonAxioms() {
		return singletonAxioms;
	}

	/* **********************************************************************
	 * Visitor methods
	 */
	
	
	public OWLAxiom visit(OWLClassAssertionAxiom axiom) {
		return factory.getOWLSubClassOfAxiom(getSurrogateType(axiom.getIndividual()), axiom.getClassExpression());
	}

	
	public OWLAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
		OWLIndividual subject = axiom.getSubject();
		OWLObjectPropertyExpression property = axiom.getProperty();
		OWLIndividual object = axiom.getObject();
		OWLClassExpression restriction = factory.getOWLObjectHasValue(property, object);
		OWLClass surrogateType = getSurrogateType(subject);
		OWLAxiom substituteAxiom = factory.getOWLSubClassOfAxiom(surrogateType, restriction);
		return substituteAxiom;
	}

	public OWLAxiom visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		OWLIndividual subject = axiom.getSubject();
		OWLObjectPropertyExpression property = axiom.getProperty();
		OWLIndividual object = axiom.getObject();
		OWLClassExpression notObject = factory.getOWLObjectComplementOf(factory.getOWLObjectOneOf(object));
		OWLClassExpression restriction = factory.getOWLObjectAllValuesFrom(property, notObject);
		OWLClass surrogateType = getSurrogateType(subject);
		OWLAxiom substituteAxiom = factory.getOWLSubClassOfAxiom(surrogateType, restriction);
		return substituteAxiom;
	}
	
	
	public OWLAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
		OWLIndividual subject = axiom.getSubject();
		OWLDataPropertyExpression property = axiom.getProperty();
		OWLLiteral object = axiom.getObject();
		OWLClassExpression restriction = factory.getOWLDataHasValue(property, object);
		OWLClass surrogateType = getSurrogateType(subject);
		OWLAxiom substituteAxiom = factory.getOWLSubClassOfAxiom(surrogateType, restriction);
		return substituteAxiom;
	}

	
	public OWLAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		OWLIndividual subject = axiom.getSubject();
		OWLDataPropertyExpression property = axiom.getProperty();
		OWLLiteral object = axiom.getObject();
		OWLClassExpression restriction = factory.getOWLDataHasValue(property, object);
		OWLClassExpression notRestriction = factory.getOWLObjectComplementOf(restriction);
		OWLClass surrogateType = getSurrogateType(subject);
		OWLAxiom substituteAxiom = factory.getOWLSubClassOfAxiom(surrogateType, notRestriction);
		return substituteAxiom;
	}

	
	public OWLAxiom visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDisjointClassesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDataPropertyDomainAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLObjectPropertyDomainAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLAnnotationAssertionAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDatatypeDefinitionAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDeclarationAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDifferentIndividualsAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDataPropertyRangeAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDisjointDataPropertiesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDisjointObjectPropertiesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLDisjointUnionAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLEquivalentDataPropertiesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLFunctionalDataPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLHasKeyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLInverseObjectPropertiesAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLObjectPropertyRangeAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSameIndividualAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSubDataPropertyOfAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSubObjectPropertyOfAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSubPropertyChainOfAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return null;
	}

	
	public OWLAxiom visit(SWRLRule rule) {
		return null;
	}
	

}
