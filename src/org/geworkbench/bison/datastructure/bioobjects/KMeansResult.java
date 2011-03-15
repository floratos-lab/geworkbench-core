package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of K-Means Clustering analysis.
 * @author zm2165
 * @version $Id$
*/
public class KMeansResult extends CSAncillaryDataSet<DSMicroarray> {

	private static final long serialVersionUID = -5747912398049332993L;
	private String resultText;

	public KMeansResult(final DSMicroarraySet<DSMicroarray> maSet, String string, String resultText) {
		super(maSet, string);
		this.resultText=resultText;
	}

	public String getResultText(){
		return resultText;
	}
	
	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}	
}
