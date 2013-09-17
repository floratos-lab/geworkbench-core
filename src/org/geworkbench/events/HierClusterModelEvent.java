package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.DSHierClusterDataSet;
import org.geworkbench.bison.model.clusters.MarkerHierCluster;
import org.geworkbench.bison.model.clusters.MicroarrayHierCluster;

import java.util.EventObject;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Models a JAVA event that would be thrown from a geaw application component
 * when the data used in the <code>HierClusterViewWidget</code> get modified.
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class HierClusterModelEvent extends EventObject {
	private static final long serialVersionUID = -3433240912097398524L;
	/**
     * The underlying micorarray set used in the hierarchical clustering
     * analysis.
     */
    private DSMicroarraySetView<DSGeneMarker, DSMicroarray> mASet;
    /**
     * The top-level marker cluster produced by the hierarchical clustering
     * analysis.
     */
    private MarkerHierCluster markerCluster;
    /**
     * The top-level microarray cluster produced by the hierarchical clustering
     * analysis.
     */
    private MicroarrayHierCluster arrayCluster;

    private MarkerHierCluster selectedMarkerCluster;
    private MicroarrayHierCluster selectedArrayCluster;
    private boolean selectionEnabled;
    private DSHierClusterDataSet hierclusterSet;

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>HierClusterModelEvent</code>
     */
    public HierClusterModelEvent(Object source) {
        super(source);
    }

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>HierClusterModelEvent</code>
     * @param mas    <code>MicroarraySet</code> used for
     *               Hierarchical Clustering Analysis
     * @param mrkhc  <code>MarkerHierCluster</code> node resulting from
     *               Hierarchical Clustering Analysis representing Genetic Marker clusters
     * @param mrahc  <code>MicroarrayHierCluster</code> node resulting from
     *               Hierarchical Clustering Analysis representing Microarray clusters
     */
    @SuppressWarnings("unchecked")
	public HierClusterModelEvent(Object source, DSHierClusterDataSet clusterSet) {
        super(source);
    	mASet = (DSMicroarraySetView<DSGeneMarker, DSMicroarray>)clusterSet.getDataSetView();
    	markerCluster = (MarkerHierCluster)clusterSet.getCluster(0);
    	arrayCluster = (MicroarrayHierCluster)clusterSet.getCluster(1);
    	selectedMarkerCluster = (MarkerHierCluster)clusterSet.getSelectedCluster(0);
    	selectedArrayCluster = (MicroarrayHierCluster)clusterSet.getSelectedCluster(1);
    	selectionEnabled = clusterSet.getSelectionEnabled();
    	hierclusterSet = clusterSet;
    }

    /**
     * Gets the <code>MicroarraySet</code> used for Hierarchical Clustering
     * Analysis
     *
     * @return <code>MicroarraySet</code> used for Hierarchical Clustering
     *         Analysis
     */
    public DSMicroarraySetView<DSGeneMarker, DSMicroarray> getMicroarraySet() {
        return mASet;
    }

    /**
     * Gets the <code>MarkerHierCluster</code> node resulting from
     * Hierarchical Clustering Analysis representing Genetic Marker clusters
     *
     * @return <code>MarkerHierCluster</code>
     */
    public MarkerHierCluster getMarkerCluster() {
        return markerCluster;
    }

    /**
     * Gets the <code>MicroarrayHierCluster</code> node resulting from
     * Hierarchical Clustering Analysis representing Microarray clusters
     *
     * @return <code>MicroarrayHierCluster</code>
     */
    public MicroarrayHierCluster getMicroarrayCluster() {
        return arrayCluster;
    }

    public MarkerHierCluster getSelectedMarkerCluster() {
        return selectedMarkerCluster;
    }

    public MicroarrayHierCluster getSelectedMicroarrayCluster() {
        return selectedArrayCluster;
    }
    public boolean getSelectionEnabled() {
    	return selectionEnabled;
    }
    public DSHierClusterDataSet getClusterSet()
    {
    	return hierclusterSet;
    }
}
