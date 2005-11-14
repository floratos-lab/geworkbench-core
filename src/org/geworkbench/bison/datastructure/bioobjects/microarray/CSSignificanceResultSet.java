package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.File;

/**
 * @author John Watkinson
 */
public class CSSignificanceResultSet <T extends DSGeneMarker> extends CSAncillaryDataSet implements DSSignificanceResultSet<T> {

    public CSSignificanceResultSet(DSMicroarraySet parent, String label) {
        super(parent, label);
    }

    public File getDataSetFile() {
        // not needed
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }

    public double getSignificance(T marker) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DSMicroarraySet getParentDataSet() {
        return (DSMicroarraySet) super.getParentDataSet();
    }
}
