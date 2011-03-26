package org.protege.owlapi.inconsistent;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
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
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
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

public class TypeCollector implements OWLAxiomVisitor {
	private Set<OWLClassExpression> types = new HashSet<OWLClassExpression>();
	private OWLDataFactory factory;
	private OWLIndividual i;
	
	public TypeCollector(OWLDataFactory factory, OWLIndividual i) {
		this.factory = factory;
		this.i = i;
	}
	
	public void reset() {
		types.clear();
	}

	
	public void visit(OWLAnnotationAssertionAxiom axiom) {

	}

	
	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDeclarationAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSubClassOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDisjointClassesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDataPropertyDomainAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDifferentIndividualsAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDisjointUnionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDataPropertyRangeAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLClassAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLEquivalentClassesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSameIndividualAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLHasKeyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(OWLDatatypeDefinitionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	
	public void visit(SWRLRule rule) {
		// TODO Auto-generated method stub

	}

}
