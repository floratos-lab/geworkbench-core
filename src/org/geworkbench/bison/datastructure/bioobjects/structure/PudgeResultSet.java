package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

import java.io.File;

/**
 * 
 * @author zji
 * @version $Id$
 */
public class PudgeResultSet extends CSAncillaryDataSet<DSSequence> {
    private static final long serialVersionUID = 1L;
    File dataFile = null;
    String result = null;

    @SuppressWarnings("unchecked")
	public PudgeResultSet(DSSequenceSet<? extends DSSequence> parent, String result){
        super((DSSequenceSet<DSSequence>)parent, result);
	String jobname = ""; int i=0;
	if ((i = result.indexOf("dir_name=")) > -1)
	    jobname = result.substring(i+9, result.indexOf("&", i));
	setLabel(jobname);
	setResult(result);
    }

    public DSSequenceSet<DSSequence> getParentDataSet() {
        return (DSSequenceSet<DSSequence>) super.getParentDataSet();
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
