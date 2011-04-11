package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.SOMCluster;

/**
 * Result of K-Means Clustering analysis.
 * @author zm2165
 * @version $Id$
*/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class KMeansResult extends CSAncillaryDataSet {

	private static final long serialVersionUID = -5747912398049332993L;	
	private int clusterBy=0;
	private ArrayList<List<String[]>> resultList = null;
	private DSMicroarraySet<DSMicroarray> maSet;
	
	public KMeansResult(final DSMicroarraySet<DSMicroarray> maSet, String name,
			SOMCluster[][] graphResults, 
			int clusterBy, ArrayList<List<String[]>> resultList) {
		super(maSet, name);
		this.clusterBy=clusterBy;		
		this.resultList=resultList;
		this.maSet=maSet;
	}

	public DSMicroarraySet<DSMicroarray> getMaSet(){
		return maSet;
	}	
	
	public int getClusterBy(){
		return clusterBy;
	}
	
	public ArrayList<List<String[]>> getResultList(){
		return resultList;
	}
	
	public File getDataSetFile() {
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}	
}
