package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

public class CSMasterRegulatorTableResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSMasterRegulatorTableResultSet {

	private static final long serialVersionUID = 4079428629682719155L;
	private Object[][] data = null;

	public CSMasterRegulatorTableResultSet(DSMicroarraySet<DSMicroarray> parent, String label) {
		super(parent, label);
	}

	public File getDataSetFile() {
		return null;
	}

	public void setDataSetFile(File file) {	
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}
}
