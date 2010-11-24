package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of IDEA analysis.
 * @author zji
 * @version $Id$
*/
public class IdeaResult extends CSAncillaryDataSet<DSMicroarray> {
	private static final long serialVersionUID = 1728642489420856774L;

	public Object[][] output1_loc = null;
	public Object[][] output1_goc = null;
	public Object[][] output2 = null;

	public IdeaResult(DSMicroarraySet<DSMicroarray> maSet, String string,
			Object[][] output1_loc, Object[][] output1_goc, Object[][] output2) {
		super(maSet, string);
		
		this.output1_loc = output1_loc;
		this.output1_goc = output1_goc;
		this.output2 = output2;
	}

	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}
}
