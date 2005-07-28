package org.geworkbench.engine.model.clusters;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generalization of <code>DefaultHierCluster</code> that contains a
 * {@link org.geworkbench.engine.model.microarray.DSMarker} that contains a reference to a
 * Genetic Marker defined by {@link org.geworkbench.engine.model.microarray.MarkerValue}. Clusters
 * of this type are used to create a Marker Dendrogram representing Genetic
 * Marker Clustering as obtained from the Hierachical Clustering Analysis
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MarkerHierCluster extends DefaultHierCluster {
    /**
     * Stores the marker associated with this cluster. For hierarchical
     * clusters, only leaf nodes have a non-null value in this field.
     */
    protected DSGeneMarker mInfo = null;

    /**
     * Sets the <code>DSMarker</code> associated with this node
     *
     * @param mi <code>DSMarker</code> associated with this node
     */
    public void setMarkerInfo(DSGeneMarker mi) {
        mInfo = mi;
    }

    /**
     * Gets the <code>DSMarker</code> associated with this node
     *
     * @return <code>DSMarker</code> associated with this node
     */
    public DSGeneMarker getMarkerInfo() {
        return mInfo;
    }

}
