package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of IDEA analysis.
 * @author zji
 * @version $Id$
*/
public class IdeaResult extends CSAncillaryDataSet<DSMicroarray> {
	private static final long serialVersionUID = 1728642489420856774L;

	public IdeaResult(DSDataSet<DSMicroarray> parent, String label, double[][] a) {
		super(parent, label);
	}

	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}
}
