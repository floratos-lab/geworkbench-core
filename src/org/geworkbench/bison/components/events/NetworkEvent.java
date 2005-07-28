package org.geworkbench.bison.components.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.graph.DSGraph;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class NetworkEvent {

    public static final int CREATE_NETWORK = 0;
    public static final int MODIFY_NETWORK = 1;

    private DSGraph graph = null;

    private String message = null;

    private DSItemList<DSGeneMarker> arrMarkers = null;

    private int depth = -1;

    private double threshold = 0.0;

    private int tag;

    /**
     * Constructs an <code>AdjacencyMatrixEvent</code>
     *
     * @param source EventSource
     * @param am     AdjacencyMatrix Adjacency Matrix contained in the
     *               <code>Event</code>
     * @param m      String message
     * @param nf     int Accession number of the gene corresponding to the center
     *               of the network to be drawn
     * @param d      int depth of the network to be drawn
     * @param t      double mutual information threshold
     */
    public NetworkEvent(DSGraph graph, DSItemList<DSGeneMarker> markerList, int depth, double threshold) {
        this.graph = graph;
        this.arrMarkers = markerList;
        this.depth = depth;
        this.threshold = threshold;
    }

    public NetworkEvent(DSGraph graph) {
        this.graph = graph;
    }

    public NetworkEvent(DSGraph graph, double threshold) {
        this.graph = graph;
        this.threshold = threshold;
    }


    public DSGraph getGraph() {
        return graph;
    }

    public String getMessage() {
        return message;
    }

    public int getDisplayDepth() {
        return depth;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public DSItemList<DSGeneMarker> getMarkers() {
        return arrMarkers;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

}
