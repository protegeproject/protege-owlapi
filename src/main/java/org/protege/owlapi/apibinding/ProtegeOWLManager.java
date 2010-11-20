package org.protege.owlapi.apibinding;/*
 * Copyright (C) 2006, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxOntologyStorer;
import org.coode.owlapi.latex.LatexOntologyStorer;
import org.coode.owlapi.obo.renderer.OBOFlatFileOntologyStorer;
import org.coode.owlapi.owlxml.renderer.OWLXMLOntologyStorer;
import org.coode.owlapi.rdf.rdfxml.RDFXMLOntologyStorer;
import org.coode.owlapi.turtle.TurtleOntologyStorer;
import org.protege.owlapi.concurrent.SynchronizedOWLDataFactoryImpl;
import org.protege.owlapi.model.ProtegeOWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;

import uk.ac.manchester.cs.owl.owlapi.EmptyInMemOWLOntologyFactory;
import uk.ac.manchester.cs.owl.owlapi.ParsableOWLOntologyFactory;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOntologyStorer;
import de.uulm.ecs.ai.owlapi.krssrenderer.KRSS2OWLSyntaxOntologyStorer;

public class ProtegeOWLManager {

    // force the static initializer
    static {
        OWLManager.getOWLDataFactory();
    }

    /**
     * Creates an OWL ontology manager that is configured with standard parsers,
     * storeres etc.
     *
     * @return The new manager.
     */
    public static ProtegeOWLOntologyManager createOWLOntologyManager() {
        return createOWLOntologyManager(getOWLDataFactory());
    }


    /**
     * Creates an OWL ontology manager that is configured with standard parsers,
     * storeres etc.
     *
     * @param dataFactory The data factory that the manager should have a reference to.
     * @return The manager.
     */
    public static ProtegeOWLOntologyManager createOWLOntologyManager(OWLDataFactory dataFactory) {
        // Create the ontology manager and add ontology factories, mappers and storers
        ProtegeOWLOntologyManager ontologyManager = new ProtegeOWLOntologyManager(dataFactory);
        ontologyManager.addOntologyStorer(new RDFXMLOntologyStorer());
        ontologyManager.addOntologyStorer(new OWLXMLOntologyStorer());
        ontologyManager.addOntologyStorer(new OWLFunctionalSyntaxOntologyStorer());
        ontologyManager.addOntologyStorer(new ManchesterOWLSyntaxOntologyStorer());
        ontologyManager.addOntologyStorer(new OBOFlatFileOntologyStorer());
        ontologyManager.addOntologyStorer(new KRSS2OWLSyntaxOntologyStorer());
        ontologyManager.addOntologyStorer(new TurtleOntologyStorer());
        ontologyManager.addOntologyStorer(new LatexOntologyStorer());

        ontologyManager.addIRIMapper(new NonMappingOntologyIRIMapper());
        
        ontologyManager.clearOntologyFactories();
        ontologyManager.addOntologyFactory(new EmptyInMemOWLOntologyFactory());
        ontologyManager.addOntologyFactory(new ParsableOWLOntologyFactory());
        
        return ontologyManager;
    }

    /**
     * Gets a global data factory that can be used to create OWL API objects.
     * @return An OWLDataFactory  that can be used for creating OWL API objects.
     */
    public static OWLDataFactory getOWLDataFactory() {
        return SynchronizedOWLDataFactoryImpl.getInstance();
    }
}
