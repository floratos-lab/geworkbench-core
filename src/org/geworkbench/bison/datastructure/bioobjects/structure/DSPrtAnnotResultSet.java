package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

//public interface DSPrtAnnotResultSet extends DSProteinStructure {
public interface DSPrtAnnotResultSet extends DSAncillaryDataSet {

    public DSProteinStructure getParentDataSet();

}
