package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

public class CSMasterRegulatorTableResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSMasterRegulatorTableResultSet {

	private static final long serialVersionUID = 4079428629682719155L;
	private Object[][] data = null;

	public CSMasterRegulatorTableResultSet(DSMicroarraySet parent, String label) {
		super(parent, label);
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
		if (data != null)
			setDescription("# of master regulators (MARINa): " + data.length);
	}
}
