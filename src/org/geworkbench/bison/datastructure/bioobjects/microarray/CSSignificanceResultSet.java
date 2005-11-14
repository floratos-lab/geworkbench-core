package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.util.Icons;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

/**
 * @author John Watkinson
 */
public class CSSignificanceResultSet <T extends DSGeneMarker> extends CSAncillaryDataSet implements DSSignificanceResultSet<T> {

    private HashMap<T, Double> significance;

    public CSSignificanceResultSet(DSMicroarraySet parent, String label) {
        super(parent, label);
        significance = new HashMap<T, Double>();
    }

    public ImageIcon getIcon() {
        return Icons.SIGNIFICANCE_ICON;
    }

    public File getDataSetFile() {
        // not needed
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }

    public double getSignificance(T marker) {
        return significance.get(marker);
    }

    public void setSignificance(T marker, double value) {
        significance.put(marker, value);
    }

    public DSMicroarraySet getParentDataSet() {
        return (DSMicroarraySet) super.getParentDataSet();
    }
}
