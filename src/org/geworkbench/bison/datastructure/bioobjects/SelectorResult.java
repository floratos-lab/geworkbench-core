package org.geworkbench.bison.datastructure.bioobjects;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of Fold Change analysis.
 * @author zm2165
 * @version $Id$
*/
public class SelectorResult extends CSAncillaryDataSet<DSMicroarray> {

	private static final long serialVersionUID = -4049899027384245969L;
	private String message = "";

	public SelectorResult(final DSMicroarraySet maSet, String label) {
		super(maSet, label);
	}
	
	public SelectorResult(final DSMicroarraySet maSet, String label, String text){
		super(maSet, label);
		message = text;
	}
	
	public String getText(){
		return message;
	}

}
