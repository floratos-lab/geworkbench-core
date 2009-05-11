package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import java.io.File;

public class PudgeResultSet extends CSAncillaryDataSet {
    private static final long serialVersionUID = 1L;
    File dataFile = null;
    String result = null;

    public PudgeResultSet(DSSequenceSet parent, String result){
        super(parent, result);
	String jobname = ""; int i=0;
	if ((i = result.indexOf("dir_name=")) > -1)
	    jobname = result.substring(i+9, result.indexOf("&", i));
	setLabel(jobname);
	setResult(result);
    }

    public DSSequenceSet getParentDataSet() {
        return (DSSequenceSet) super.getParentDataSet();
    }

    public String getResult() {
	return result;
    }

    public void setResult(String res) {
	result = res;
    }

    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
}
