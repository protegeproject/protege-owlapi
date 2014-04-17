package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public class ClassExpressionInterpreter implements OWLClassExpressionVisitorEx<Set<OWLIndividual>> {
	private TrivialModel model;
	
	public ClassExpressionInterpreter(TrivialModel model) {
		this.model = model;
	}

	
	/* ****************************************************
	 * Interfaces
	 */

	
	public Set<OWLIndividual> visit(OWLClass ce) {
		if (model.isTopClass(ce)) {
			return model.getAllIndividuals();
		}
		else {
			return Collections.emptySet();
		}
	}

	
	public Set<OWLIndividual> visit(OWLObjectIntersectionOf ce) {
		Set<OWLIndividual> interpretation = null;
		for (OWLClassExpression conjunct : ce.getOperands()) {
			if (interpretation == null) {
				interpretation = new TreeSet<OWLIndividual>(conjunct.accept(this));
			}
			else {
				interpretation.removeAll(conjunct.accept(this));
			}
		}
		if (interpretation == null) {
			interpretation = model.getAllIndividuals();
		}
		return interpretation;
	}

	
	public Set<OWLIndividual> visit(OWLObjectUnionOf ce) {
		Set<OWLIndividual> interpretation = new TreeSet<OWLIndividual>();
		for (OWLClassExpression conjunct : ce.getOperands()) {
			interpretation.addAll(conjunct.accept(this));
		}
		return interpretation;
	}

	
	public Set<OWLIndividual> visit(OWLObjectComplementOf ce) {
		Set<OWLIndividual> interpretation = new TreeSet<OWLIndividual>(model.getAllIndividuals());
		interpretation.removeAll(ce.getOperand().accept(this));
		return interpretation;
	}

	
	public Set<OWLIndividual> visit(OWLObjectSomeValuesFrom ce) {
		if (model.isTopProperty(ce.getProperty())) {
			Set<OWLIndividual> interpretation = ce.getFiller().accept(this);
			if (interpretation.isEmpty()) {
				return Collections.emptySet();
			}
			else {
				return model.getAllIndividuals();
			}
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectAllValuesFrom ce) {
		if (model.isTopProperty(ce.getProperty())) {
			Set<OWLIndividual> interpretation = ce.getFiller().accept(this);
			if (interpretation.equals(model.getAllIndividuals())) {
				return model.getAllIndividuals();
			}
			else {
				return Collections.emptySet();
			}
		}
		return model.getAllIndividuals();
	}

	
	public Set<OWLIndividual> visit(OWLObjectHasValue ce) {
		if (model.isTopProperty(ce.getProperty())) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectMinCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && ce.getFiller().accept(this).size() >= ce.getCardinality()) {
			return model.getAllIndividuals();
		}
		else if (ce.getCardinality() == 0) {
			return model.getAllIndividuals();
		}
		else {
			return Collections.emptySet();
		}
	}

	
	public Set<OWLIndividual> visit(OWLObjectExactCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && ce.getCardinality() == ce.getFiller().accept(this).size()) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectMaxCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && ce.getFiller().accept(this).size() > ce.getCardinality()) {
			return Collections.emptySet();
		}
		return model.getAllIndividuals();
	}

	
	public Set<OWLIndividual> visit(OWLObjectHasSelf ce) {
		if (model.isTopProperty(ce.getProperty())) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectOneOf ce) {
		return new TreeSet<OWLIndividual>(ce.getIndividuals());
	}

	
	public Set<OWLIndividual> visit(OWLDataSomeValuesFrom ce) {
		if (model.isTopProperty(ce.getProperty()) && model.isConsistent(ce.getFiller())) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataAllValuesFrom ce) {
		if (model.isTopProperty(ce.getProperty()) && model.isTopDataRange(ce.getFiller())) {
			return model.getAllIndividuals();
		}
		else if (model.isTopProperty(ce.getProperty())) {
			return Collections.emptySet();
		}
		else {
			return model.getAllIndividuals();
		}
	}

	
	public Set<OWLIndividual> visit(OWLDataHasValue ce) {
		if (model.isTopProperty(ce.getProperty())) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataMinCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && model.hasAtLeast(ce.getFiller(), ce.getCardinality())) {
			return model.getAllIndividuals();
		}
		else if (!model.isTopProperty(ce.getProperty()) && ce.getCardinality() == 0){
			return model.getAllIndividuals();
		}
		else {
			return Collections.emptySet();
		}
	}

	
	public Set<OWLIndividual> visit(OWLDataExactCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && model.hasExactly(ce.getFiller(), ce.getCardinality())) {
			return model.getAllIndividuals();
		}
		else if (!model.isTopProperty(ce.getProperty()) && ce.getCardinality() == 0){
			return model.getAllIndividuals();
		}
		else {
			return Collections.emptySet();
		}
	}

	
	public Set<OWLIndividual> visit(OWLDataMaxCardinality ce) {
		if (model.isTopProperty(ce.getProperty()) && model.hasNoMoreThan(ce.getFiller(), ce.getCardinality())) {
			return model.getAllIndividuals();
		}
		else {
			return model.getAllIndividuals();
		}
	}
}
