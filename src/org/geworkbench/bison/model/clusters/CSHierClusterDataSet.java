package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;

import java.io.File;

/**
 * @author John Watkinson
 */
public class CSHierClusterDataSet extends CSAncillaryDataSet implements DSHierClusterDataSet {

    private HierCluster[] clusters;
    private DSDataSetView parentSet;
    private HierCluster[] selectedClusters;
	private boolean selectionEnabled;

    public CSHierClusterDataSet(HierCluster[] clusters, HierCluster[] sc, boolean selected, String name, DSDataSetView dataSetView) {
        super(dataSetView.getDataSet(), name);
        this.clusters = clusters;
        this.parentSet = dataSetView;
        this.selectedClusters = sc;
        this.selectionEnabled = selected;
    }

    public void writeToFile(String fileName) {
        // No-op
    }

    public DSDataSetView getDataSetView() {
        return parentSet;
    }

    public HierCluster getCluster(int index) {
        return clusters[index];
    }
   
    public HierCluster getSelectedCluster(int index) {
    	if (selectedClusters == null || index >= selectedClusters.length)
    		return null;
        return selectedClusters[index];
    }

    public void setSelectedClusters(HierCluster[] sc) {
        selectedClusters = sc;
    }
    
    public boolean getSelectionEnabled() {
        return selectionEnabled;
    }    
    public void setSelectionEnabled(boolean selected) {
        selectionEnabled = selected;
    } 

    public int getNumberOfClusters() {
        return clusters.length;
    }

    public File getDataSetFile() {
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }
}
