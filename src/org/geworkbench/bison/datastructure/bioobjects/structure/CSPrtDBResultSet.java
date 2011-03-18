package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import java.io.File;

public class CSPrtDBResultSet extends CSAncillaryDataSet<DSSequence> implements DSPrtDBResultSet {
	private static final long serialVersionUID = 3534485314846172776L;
	File dataFile = null;
    public CSPrtDBResultSet(DSSequenceSet<DSSequence> parent, String label){
        super(parent, label);
    }

    public DSSequenceSet<DSSequence> getParentDataSet() {
        return (DSSequenceSet<DSSequence>) super.getParentDataSet();
    }
    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
}
