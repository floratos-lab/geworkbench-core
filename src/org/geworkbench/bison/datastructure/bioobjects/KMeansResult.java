package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private int clusterBy=0;
	private ArrayList<List<String[]>> resultList = null;
	private DSMicroarraySet<DSMicroarray> maSet;
	
	public KMeansResult(final DSMicroarraySet<DSMicroarray> maSet, String string, int clusterBy, String resultText,
			ArrayList<List<String[]>> resultList) {
		super(maSet, string);
		this.clusterBy=clusterBy;
		this.resultText=resultText;
		this.resultList=resultList;
		this.maSet=maSet;
	}

	public DSMicroarraySet<DSMicroarray> getMaSet(){
		return maSet;
	}
	public String getResultText(){
		return resultText;
	}
	
	public int getClusterBy(){
		return clusterBy;
	}
	
	public ArrayList<List<String[]>> getResultList(){
		return resultList;
	}
	
	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}	
}
