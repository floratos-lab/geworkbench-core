package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;

public interface DSPrtDBResultSet extends DSAncillaryDataSet {

    public DSSequenceSet getParentDataSet();

}