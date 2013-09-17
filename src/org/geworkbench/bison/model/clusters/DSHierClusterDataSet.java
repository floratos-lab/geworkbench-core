package org.geworkbench.bison.model.clusters;

/**
 * @author John Watkinson
 */
public interface DSHierClusterDataSet extends DSClusterDataSet {

    public HierCluster getCluster(int index);
    public HierCluster getSelectedCluster(int index);
    public void setSelectedClusters(HierCluster[] sc);
    public boolean getSelectionEnabled();
    public void setSelectionEnabled(boolean selected);
}
