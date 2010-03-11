package org.geworkbench.bison.datastructure.biocollections.medusa;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * 
 * @author keshav
 * @version $Id: MedusaDataSet.java,v 1.1 2007-05-15 18:27:38 keshav Exp $
 */
public class MedusaDataSet extends CSAncillaryDataSet<DSMicroarray> implements
		DSAncillaryDataSet<DSMicroarray> {
	private static final long serialVersionUID = 1L;
	private MedusaData data;

	private String filename = null;

	public MedusaDataSet(DSDataSet<DSMicroarray> parent, String label, MedusaData data,
			String filename) {
		super(parent, label);
		this.data = data;
		this.filename = filename;
	}

	public File getDataSetFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataSetFile(File file) {
		// TODO Auto-generated method stub

	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public MedusaData getData() {
		return data;
	}

	public void setData(MedusaData data) {
		this.data = data;
	}

}
