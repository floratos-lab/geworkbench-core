package org.geworkbench.bison.model.clusters;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;

/**
 * @author John Watkinson
 * @version $Id$
 */
@SuppressWarnings("rawtypes")
public class CSHierClusterDataSet extends CSAncillaryDataSet implements DSHierClusterDataSet {
	private static final long serialVersionUID = 8176955646021386560L;

	private static Log log = LogFactory.getLog(CSHierClusterDataSet.class);

    private HierCluster[] clusters;
    private DSDataSetView parentSet;
    private HierCluster[] selectedClusters;
	private boolean selectionEnabled;

    @SuppressWarnings("unchecked")
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
    
	/**
	 * Customized serialization to avoid recursive call that may cause stack
	 * overflow.
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {

		out.writeObject(getParentDataSet());
		out.writeObject(parentSet);

		NonRecursiveClusterSet markerHierCluster = new NonRecursiveClusterSet(
				clusters[0], MarkerHierCluster.class);
		NonRecursiveClusterSet arrayHierCluster = new NonRecursiveClusterSet(
				clusters[1], MicroarrayHierCluster.class);

		out.writeObject(markerHierCluster);
		out.writeObject(arrayHierCluster);
		log.debug("finish serialization");
	}

	@SuppressWarnings({ "unchecked" })
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		setLabel("Hierarchical Clustering");
		setParent((DSDataSet) in.readObject());
		parentSet = (DSDataSetView) in.readObject();

		clusters = new HierCluster[2];

		clusters[0] = ((NonRecursiveClusterSet) in.readObject())
				.convertToHierCluster(MarkerHierCluster.class);
		clusters[1] = ((NonRecursiveClusterSet) in.readObject())
				.convertToHierCluster(MicroarrayHierCluster.class);
		log.debug("finish deserialization");
	}

}
