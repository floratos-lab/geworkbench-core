package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

public class CSDemandResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSDemandResultSet {
	private static final long serialVersionUID = 3402703191461767022L;
	private Object[][] result = new Object[0][0];
	private Object[][] edge   = new Object[0][0];
	private Object[][] module = new Object[0][0];

	public CSDemandResultSet(DSDataSet<DSMicroarray> parent, String label,
			Object[][] result, Object[][] edge, Object[][] module) {
		super(parent, label);

		this.result = result;
		if (result != null)
			setDescription("# of DMAND results: " + result.length);
		this.edge = edge;
		this.module = module;
	}

	@Override
	public Object[][] getEdge() {
		return edge;
	}

	@Override
	public Object[][] getModule() {
		return module;
	}

	@Override
	public Object[][] getResult() {
		return result;
	}
}
