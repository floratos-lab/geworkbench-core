package org.geworkbench.bison.datastructure.biocollections.medusa;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * 
 * @author keshav
 * @version $Id$
 */
public class MedusaDataSet extends CSAncillaryDataSet<DSMicroarray> {

	private static final long serialVersionUID = -4273265470689892987L;

	private MedusaData data;

	private String filename = null;

	public MedusaDataSet(DSDataSet<DSMicroarray> parent, String label, MedusaData data,
			String filename) {
		super(parent, label);
		this.data = data;
		this.filename = filename;
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

	private String outputPath;
	public String getOutputPath() {
		return outputPath;
	}

	public void setOuputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
