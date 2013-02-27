package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

public class CSDemandResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSDemandResultSet {
	private static final long serialVersionUID = 3402703191461767022L;
	private Object[][] result = new Object[0][0];
	private Object[][] edge   = new Object[0][0];
	private Object[][] module = new Object[0][0];

	public CSDemandResultSet(DSDataSet<DSMicroarray> parent, String label) {
		super(parent, label);
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

	@Override
	public void setEdge(Object[][] edge) {
		this.edge = edge;
	}

	@Override
	public void setResult(Object[][] result) {
		this.result = result;
		if (result != null)
			setDescription("# of DMAND results: " + result.length);
	}

	@Override
	public void setModule(Object[][] module) {
		this.module = module;
	}
}
