package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import java.io.*;

/**
 * A class to contain returned result from MarkUs analysis.
 * This is necessary for the Dispatcher framework (used in PollingThread).
 * 
 * @author zji
 * @version $Id$
 */
public class SkyLineResultDataSet extends CSAncillaryDataSet<DSBioObject> {
	private static final long serialVersionUID = -630087506848526523L;
	
	String result = null;
    File dataSetFile = null;

    public SkyLineResultDataSet(DSDataSet<DSBioObject> parent, String label) {
        super(parent, label);
    }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public File getDataSetFile() {
		return dataSetFile;
	}

	public void setDataSetFile(File file) {
		this.dataSetFile = file;
	}



}
