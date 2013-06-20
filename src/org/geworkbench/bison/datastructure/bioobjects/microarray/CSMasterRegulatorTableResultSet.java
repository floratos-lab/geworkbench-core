package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

public class CSMasterRegulatorTableResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSMasterRegulatorTableResultSet {

	private static final long serialVersionUID = 4079428629682719155L;
	private Object[][] data = null;
	private Map<String, Set<String>> leadingEdge = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> regulon = new HashMap<String, Set<String>>();

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
	
	public void setLeadingEdge(Map<String, Set<String>> ledge){
		leadingEdge = ledge;
	}
	
	public Set<String> getLeadingEdge(String tf){
		return leadingEdge.get(tf);
	}
	
	public void setRegulon(Map<String, Set<String>> target){
		regulon = target;
	}
	
	public Set<String> getRegulon(String tf){
		return regulon.get(tf);
	}
}
