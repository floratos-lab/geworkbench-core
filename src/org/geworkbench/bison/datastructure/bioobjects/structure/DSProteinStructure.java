package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import java.util.HashMap;

/**
 * User: mhall
 * Date: Mar 13, 2006
 * Time: 11:43:13 AM
 */
public interface DSProteinStructure extends DSAncillaryDataSet {
    public HashMap<String, Integer> getChains();
}
