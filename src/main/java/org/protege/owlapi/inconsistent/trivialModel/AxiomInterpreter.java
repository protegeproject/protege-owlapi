package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
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
	public static final Logger LOGGER = Logger.getLogger(AxiomInterpreter.class);
	
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
		Set<OWLIndividual> superInterpretation = axiom.getSuperClass().accept(interpreter);
		Set<OWLIndividual> subInterpretation = axiom.getSubClass().accept(interpreter);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("For the axiom " + axiom + " the trivial model super class interpretation was ");
			LOGGER.debug(superInterpretation.toString());
			LOGGER.debug(" and the sub class interpretation was ");
			LOGGER.debug(subInterpretation.toString());
		}
		return superInterpretation.containsAll(subInterpretation);
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
		OWLClassExpression[] ces = axiom.getClassExpressions().toArray(new OWLClassExpression[0]);
		for (int i = 0; i < ces.length; i++) {
			for (int j = i+1; j < ces.length; j++) {
				Set<OWLIndividual> interpretation1 = ces[i].accept(interpreter);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Trivial Model interpretation of " + ces[i] + " was " + interpretation1);
				}
				for (OWLIndividual individual : ces[j].accept(interpreter)) {
					if (interpretation1.contains(individual)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	
	public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
		Set<OWLIndividual> interpretation = axiom.getDomain().accept(interpreter);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The interpretation of " + axiom.getDomain() + " is " + interpretation);
        }
		if (model.isTopProperty(axiom.getProperty())) {
			return interpretation.equals(model.getAllIndividuals());
		}
		else {
			return interpretation.isEmpty();
		}
	}

	
	public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
		Set<OWLIndividual> interpretation = axiom.getDomain().accept(interpreter);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The interpretation of " + axiom.getDomain() + " is " + interpretation);
        }
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
		if (model.isTopProperty(axiom.getProperty())) {
            Set<OWLIndividual> interpretation = axiom.getRange().accept(interpreter);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("The interpretation of " + axiom.getRange() + " is " + interpretation);
            }
			return interpretation.equals(model.getAllIndividuals());
		}
		return true;
	}

	
	public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
		return model.isTopProperty(axiom.getProperty());
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
		Set<OWLIndividual> projectedUnion = axiom.getOWLClass().accept(interpreter);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The interpretation of " + axiom.getOWLClass() + " is " + projectedUnion);
        }
		Set<OWLIndividual> realUnion = new TreeSet<OWLIndividual>();
		for (OWLClassExpression ce : axiom.getClassExpressions()) {
            Set<OWLIndividual> interpretation = ce.accept(interpreter);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("The interpretation of " + ce + " is " + interpretation);
            }
			realUnion.addAll(interpretation);
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calculating the trivial model interpretation of " + axiom);
		}
        Set<OWLIndividual> interpretation = axiom.getClassExpression().accept(interpreter);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The interpretation of " + axiom.getClassExpression() + " is " + interpretation);
        }
		return interpretation.contains(axiom.getIndividual());
	}

	
	public Boolean visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLIndividual> individuals = null;
		for (OWLClassExpression ce : axiom.getClassExpressions()) {
            Set<OWLIndividual> interpretation = ce.accept(interpreter);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("The interpretation of " + ce + " is " + interpretation);
            }
			if (individuals == null) {
				individuals = interpretation;
			}
			else if (!individuals.equals(interpretation)) {
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
