package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import java.io.File;

//public class CSPrtAnnotResultSet extends CSProteinStructure implements DSPrtAnnotResultSet {
public class CSPrtAnnotResultSet extends CSAncillaryDataSet implements DSPrtAnnotResultSet {

    File dataFile = null;
    public CSPrtAnnotResultSet(DSProteinStructure parent, String label){
        super(parent, label);
    }

    public DSProteinStructure getParentDataSet() {
        return (DSProteinStructure) super.getParentDataSet();
    }
    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
}
