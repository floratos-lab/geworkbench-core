package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.apache.commons.collections15.map.ListOrderedMap;

import java.io.File;

/**
 * @author John Watkinson
 */
public class CSMatrixReduceSet extends CSAncillaryDataSet<DSPositionSpecificAffintyMatrix> implements DSMatrixReduceSet {

    private ListOrderedMap<String, String> sequences;

    public CSMatrixReduceSet(DSDataSet parent, String label) {
        super(parent, label);
    }

    public File getDataSetFile() {
        return null;
    }

    public void setDataSetFile(File file) {
    }

    public ListOrderedMap<String, String> getSequences() {
        return sequences;
    }

    public void setSequences(ListOrderedMap<String, String> sequences) {
        this.sequences = sequences;
    }
}
