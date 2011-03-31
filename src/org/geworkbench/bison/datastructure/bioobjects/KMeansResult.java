package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.CSSOMClusterDataSet;
import org.geworkbench.bison.model.clusters.SOMCluster;

/**
 * Result of K-Means Clustering analysis.
 * @author zm2165
 * @version $Id$
*/
public class KMeansResult extends CSSOMClusterDataSet {

	private static final long serialVersionUID = -5747912398049332993L;
	private String resultText;
	private int clusterBy=0;
	private ArrayList<List<String[]>> resultList = null;
	private DSMicroarraySet<DSMicroarray> maSet;
	private SOMCluster[][] graphResults;
	
	
	public KMeansResult(final DSMicroarraySet<DSMicroarray> maSet, String name, 
			DSDataSetView<?> dataSetView, SOMCluster[][] graphResults, 
				int clusterBy, ArrayList<List<String[]>> resultList) {
		
		super(graphResults, name, dataSetView);
		this.graphResults=graphResults;
		this.clusterBy=clusterBy;		
		this.resultList=resultList;
		this.maSet=maSet;		
	}
	
	public void CSSOMClusterDataSet(){
		
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
	public SOMCluster[][] getGraphResults(){
		return graphResults;
	}
	
	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}	
}
