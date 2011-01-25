package org.geworkbench.bison.datastructure.biocollections.views;

import java.io.Serializable;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * 
 * Dataset view only from package org.geworkbench.bison.model.clusters.
 * 
 * Copyright: Copyright (c) 2003 -2004
 * 
 * Company: Columbia University
 * 
 * Provides a view in to a {@link DSDataSet} and its associated {@link DSPanel
 * DSPanels}. The view can be optionally limited to exposing only those elements
 * in active panels.
 * 
 * @author Adam Margolin
 * @version $Id$
 */

public interface DSDataSetView<Q extends DSBioObject> extends Serializable {

	/**
	 * Get the <code>DSDataSet</code> object underlying this is view
	 * 
	 * @return The reference <code>DSDataSet</code> object.
	 */
	public DSDataSet<Q> getDataSet();

}
