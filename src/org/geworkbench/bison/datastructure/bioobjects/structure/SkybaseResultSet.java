/**
 * 
 */
package org.geworkbench.bison.datastructure.bioobjects.structure;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

/**
 * @author zji
 * @version $Id$
 *
 */
public class SkybaseResultSet extends CSAncillaryDataSet<DSSequence> {
	private static final long serialVersionUID = 7101429038894550594L;
	
	private File dataFile = null;
	
    public SkybaseResultSet(DSSequenceSet<DSSequence> parent, String label){
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
