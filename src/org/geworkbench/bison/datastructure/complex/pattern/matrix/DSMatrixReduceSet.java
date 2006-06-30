package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.apache.commons.collections15.map.ListOrderedMap;

import javax.swing.*;

/**
 * @author John Watkinson
 */
public interface DSMatrixReduceSet extends DSAncillaryDataSet<DSPositionSpecificAffintyMatrix> {

    ListOrderedMap<String, String> getSequences();

    void setSequences(ListOrderedMap<String, String> sequences);
    
}
