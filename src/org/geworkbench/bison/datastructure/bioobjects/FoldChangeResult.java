package org.geworkbench.bison.datastructure.bioobjects;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of Fold Change analysis.
 * @author zm2165
 * @version $Id$
*/
public class FoldChangeResult extends CSAncillaryDataSet<DSMicroarray> {

	private static final long serialVersionUID = -4049899027384245969L;

	public FoldChangeResult(final DSMicroarraySet maSet, String string) {
		super(maSet, string);		
	}

}
