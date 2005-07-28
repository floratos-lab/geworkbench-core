package org.systemsbiology.chem;

/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import org.systemsbiology.util.DataNotFoundException;

import java.io.PrintWriter;

/**
 * Defines a class that can write a markup language description of a
 * {@link Model} containing a set of chemical {@link Reaction} objects
 * and the the initial populations of the chemical {@link Species}.
 *
 * @author Stephen Ramsey
 * @see Model
 * @see Species
 */
public interface IModelExporter {
    public void export(Model pModel, PrintWriter pOutputWriter) throws IllegalArgumentException, DataNotFoundException, IllegalStateException, UnsupportedOperationException, ModelExporterException;

    public String getFileRegex();
}
