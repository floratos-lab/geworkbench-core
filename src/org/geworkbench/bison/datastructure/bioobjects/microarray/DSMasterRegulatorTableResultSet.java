package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

public interface DSMasterRegulatorTableResultSet extends DSAncillaryDataSet<DSMicroarray> {

    public void setData(Object[][] data);
    public Object[][] getData();
}
