package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

public class CSTTestResultSet <T extends DSGeneMarker> extends CSSignificanceResultSet<T> implements DSTTestResultSet<T>{

	private static final long serialVersionUID = 1L;

	public CSTTestResultSet(DSMicroarraySet<DSMicroarray> parent, String label,
			String[] caseLabels, String[] controlLabels, double alpha) {
		super(parent, label, caseLabels, controlLabels, alpha);
		// TODO Auto-generated constructor stub
	}
	
}
