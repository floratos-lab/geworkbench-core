package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import java.io.File;

public class CSPrtDBResultSet extends CSAncillaryDataSet implements DSPrtDBResultSet {

    File dataFile = null;
    public CSPrtDBResultSet(DSSequenceSet parent, String label){
        super(parent, label);
    }

    public DSSequenceSet getParentDataSet() {
        return (DSSequenceSet) super.getParentDataSet();
    }
    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
}
