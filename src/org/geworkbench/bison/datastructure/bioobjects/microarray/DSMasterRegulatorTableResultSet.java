package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.util.Map;
import java.util.Set;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

public interface DSMasterRegulatorTableResultSet extends DSAncillaryDataSet<DSMicroarray> {

    public void setData(Object[][] data);
    public Object[][] getData();

    public void setLeadingEdge(Map<String, Set<String>> ledge);
    public Set<String> getLeadingEdge(String tf);

    public void setRegulon(Map<String, Set<String>> regulon);
    public Set<String> getRegulon(String tf);

}
