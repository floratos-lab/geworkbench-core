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
 * @version $Id: MarkUsResultDataSet.java,v 1.2 2009-02-17 21:59:39 jiz Exp $
 */
public class MarkUsResultDataSet<T> extends CSAncillaryDataSet<DSBioObject> {
	private static final long serialVersionUID = -5868552586583422711L;
	
	String result = null;
    File dataSetFile = null;

    public MarkUsResultDataSet(DSDataSet<DSBioObject> parent, String label) {
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
