package org.protege.owlapi.inconsistent.trivialModel;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/*
 * TODO - need to fix the top object and data property cases
 */
public class ClassExpressionInterpreter implements OWLClassExpressionVisitorEx<Set<OWLIndividual>> {
	private TrivialModel model;
	private OWLDataFactory factory;
	
	public ClassExpressionInterpreter(TrivialModel model) {
		this.model = model;
		factory = model.getOntology().getOWLOntologyManager().getOWLDataFactory();
	}
	
	private boolean isTopProperty(OWLObjectPropertyExpression pe) {
		return pe.getNamedProperty().equals(factory.getOWLTopObjectProperty());
	}
	
	private boolean isTopProperty(OWLDataPropertyExpression pe) {
		return pe.equals(factory.getOWLTopDataProperty());
	}
	
	/*
	 * It simplifies things greatly to ignore the top object and data property when they appear
	 * in restrictions.   What real ontology uses these properties in this way anyway?
	 * 
	 * This can be put back with little trouble especially if we use a reasoner to answer the stupid
	 * questions about the data types.
	 */
	
	private void checkProperty(OWLObjectPropertyExpression p) {
		if (p.getNamedProperty().equals(factory.getOWLTopObjectProperty())) {
			throw new UnsupportedOperationException("top object property is not handled in this context");
		}
	}
	
	private void checkProperty(OWLDataPropertyExpression p) {
		if (p.equals(factory.getOWLTopDataProperty())) {
			throw new UnsupportedOperationException("top data property is not handled in this context");
		}
	}

	
	/* ****************************************************
	 * Interfaces
	 */

	
	public Set<OWLIndividual> visit(OWLClass ce) {
		if (ce.equals(factory.getOWLThing())) {
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
			interpretation = Collections.emptySet();
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
		interpretation.removeAll(ce.accept(this));
		return interpretation;
	}

	
	public Set<OWLIndividual> visit(OWLObjectSomeValuesFrom ce) {
		if (ce.getProperty().equals(factory.getOWLTopObjectProperty())) {
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
		if (ce.getProperty().equals(factory.getOWLTopObjectProperty())) {
			Set<OWLIndividual> interpretation = ce.getFiller().accept(this);
			if (interpretation.equals(model.getAllIndividuals())) {
				return model.getAllIndividuals();
			}
			else {
				return Collections.emptySet();
			}
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectHasValue ce) {
		if (ce.getProperty().equals(factory.getOWLTopObjectProperty())) {
			return model.getAllIndividuals();
		}
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectMinCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectExactCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectMaxCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectHasSelf ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLObjectOneOf ce) {
		return new TreeSet<OWLIndividual>(ce.getIndividuals());
	}

	
	public Set<OWLIndividual> visit(OWLDataSomeValuesFrom ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataAllValuesFrom ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataHasValue ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataMinCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataExactCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}

	
	public Set<OWLIndividual> visit(OWLDataMaxCardinality ce) {
		checkProperty(ce.getProperty());
		return Collections.emptySet();
	}
}
