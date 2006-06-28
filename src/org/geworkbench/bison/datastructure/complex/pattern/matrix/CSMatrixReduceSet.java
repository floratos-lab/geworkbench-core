package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.io.File;

/**
 * @author John Watkinson
 */
public class CSMatrixReduceSet extends CSAncillaryDataSet<DSPositionSpecificAffintyMatrix> implements DSMatrixReduceSet {

    public CSMatrixReduceSet(DSDataSet parent, String label) {
        super(parent, label);
    }

    public File getDataSetFile() {
        return null;
    }

    public void setDataSetFile(File file) {
    }
}
