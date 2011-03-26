package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
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


public class AxiomInterpreter implements OWLAxiomVisitorEx<Boolean> {
	private ClassExpressionInterpreter interpreter;
	private TrivialModel model;
	private OWLDataFactory factory;
	
	public AxiomInterpreter(TrivialModel model) {
		this.model = model;
		interpreter = new ClassExpressionInterpreter(model);
		factory = model.getOntology().getOWLOntologyManager().getOWLDataFactory();
	}

	
	public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLSubClassOfAxiom axiom) {
		return axiom.getSuperClass().accept(interpreter).containsAll(axiom.getSubClass().accept(interpreter));
	}

	
	public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return !model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		return !model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLDisjointClassesAxiom axiom) {
		OWLClassExpression[] ces = axiom.getClassExpressions().toArray(new OWLClassExpression[0]);
		for (int i = 0; i < ces.length; i++) {
			for (int j = i+1; j < ces.length; j++) {
				Set<OWLIndividual> interpretation = ces[i].accept(interpreter);
				for (OWLIndividual individual : ces[j].accept(interpreter)) {
					if (interpretation.contains(individual)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	
	public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
		Set<OWLIndividual> interpretation = axiom.getDomain().accept(interpreter);
		if (model.isTopProperty(axiom.getProperty())) {
			return interpretation.equals(model.getAllIndividuals());
		}
		else {
			return interpretation.isEmpty();
		}
	}

	
	public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
		Set<OWLIndividual> interpretation = axiom.getDomain().accept(interpreter);
		if (model.isTopProperty(axiom.getProperty())) {
			return interpretation.equals(model.getAllIndividuals());
		}
		else {
			return interpretation.isEmpty();
		}
	}

	
	public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		boolean hasBottomProperty = false;
		boolean hasTopProperty = false;
		for (OWLObjectPropertyExpression p : axiom.getProperties()) {
			if (model.isTopProperty(p)) {
				hasTopProperty = true;
			}
			else {
				hasBottomProperty = true;
			}
		}
		return !(hasTopProperty && hasBottomProperty);
	}

	
	public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return !model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
		int topPropertiesCount = 0;
		for (OWLDataPropertyExpression p : axiom.getProperties()) {
			if (model.isTopProperty(p)) {
				topPropertiesCount++;
			}
		}
		return topPropertiesCount <=1;
	}

	
	public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
		int topPropertiesCount = 0;
		for (OWLObjectPropertyExpression p : axiom.getProperties()) {
			if (model.isTopProperty(p)) {
				topPropertiesCount++;
			}
		}
		return topPropertiesCount <=1;
	}

	
	public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
		if (model.isTopProperty(axiom.getProperty())) {
			return axiom.getRange().accept(interpreter).equals(model.getAllIndividuals());
		}
		return true;
	}

	
	public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
		return !model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
		if (model.isTopProperty(axiom.getProperty())) {
			return model.getAllIndividuals().size() == 1;
		}
		return true;
	}

	
	public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
		return !(model.isTopProperty(axiom.getSubProperty()) && !model.isTopProperty(axiom.getSuperProperty()));
	}

	
	public Boolean visit(OWLDisjointUnionAxiom axiom) {
		Set<OWLIndividual> projectedUnion = axiom.getOWLClass().accept(interpreter);
		Set<OWLIndividual> realUnion = new TreeSet<OWLIndividual>();
		for (OWLClassExpression ce : axiom.getClassExpressions()) {
			realUnion.addAll(ce.accept(interpreter));
		}
		return projectedUnion.equals(realUnion);
	}

	
	public Boolean visit(OWLDeclarationAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
		if (model.isTopProperty(axiom.getProperty())) {
			return model.isTopDataRange(axiom.getRange());
		}
		return true;
	}

	
	public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
		return !model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
		boolean hasBottomProperty = false;
		boolean hasTopProperty = false;
		for (OWLDataPropertyExpression p :axiom.getProperties()) {
			if (model.isTopProperty(p)) {
				hasTopProperty = true;
			}
			else {
				hasBottomProperty = true;
			}
		}
		return hasBottomProperty ? !hasTopProperty : true;
	}

	
	public Boolean visit(OWLClassAssertionAxiom axiom) {
		return axiom.getClassExpression().accept(interpreter).contains(axiom.getIndividual());
	}

	
	public Boolean visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLIndividual> individuals = null;
		for (OWLClassExpression ce : axiom.getClassExpressions()) {
			if (individuals == null) {
				individuals = ce.accept(interpreter);
			}
			else if (!individuals.equals(ce.accept(interpreter))) {
				return false;
			}
		}
		return true;
	}

	
	public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
		return model.isTopProperty(axiom.getProperty());
	}

	
	public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
		return !(model.isTopProperty(axiom.getSubProperty()) && !model.isTopProperty(axiom.getSuperProperty()));
	}

	
	public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return true;
	}

	
	public Boolean visit(OWLSameIndividualAxiom axiom) {
		return false;
	}

	
	public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
		if (model.isTopProperty(axiom.getSuperProperty())) {
			return true;
		}
		for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
			if (!model.isTopProperty(p)) {
				return true;
			}
		}
		return false;
	}

	
	public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
		return model.isTopProperty(axiom.getFirstProperty()) ? model.isTopProperty(axiom.getSecondProperty())
														     : !model.isTopProperty(axiom.getSecondProperty());
	}

	
	public Boolean visit(OWLHasKeyAxiom axiom) {
		return model.getAllIndividuals().size() < 2;
	}

	
	public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
		return true;
	}

	
	public Boolean visit(SWRLRule rule) {
		return true;
	}

	
}
