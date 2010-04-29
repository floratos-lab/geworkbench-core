package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.util.HashMap;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public class AdjacencyMatrixEvent extends Event {

    public enum Action {
        LOADED,
        RECEIVE,
        DRAW_NETWORK,
        DRAW_NETWORK_AND_INTERACTION,
        FINISH,
        DRAW_NETWORK_ON_MICROARRAY,
        DRAW_GENEWAYS_NETWORK,
        DRAW_GENEWAYS_COMPLETE_NETWORK,
        CANCEL
    };

    private Action action;

    private AdjacencyMatrix adjm = null;

    private String message = null;

    private int networkFocus = 0;

    private int depth = 1;

    private double threshold = 0d;

    // to be used when user selects to draw the whole geneways network
    private HashMap genewaysGeneidNameMap = null;

    /**
     * Constructs an <code>AdjacencyMatrixEvent</code>
     *
     * @param am AdjacencyMatrix Adjacency Matrix contained in the
     *           <code>Event</code>
     * @param m  String message
     * @param nf int Accession number of the gene corresponding to the center
     *           of the network to be drawn
     * @param d  int depth of the network to be drawn
     * @param t  double mutual information threshold
     */
    public AdjacencyMatrixEvent(AdjacencyMatrix am, String m, int nf, int d, double t, Action action) {
        super(null);
        adjm = am;
        message = m;
        networkFocus = nf;
        depth = d;
        threshold = t;
        this.action = action;
        this.genewaysGeneidNameMap = null;
    }

    /**
     * Constructs an <code>AdjacencyMatrixEvent</code>
     *
     * @param am AdjacencyMatrix Adjacency Matrix contained in the
     *           <code>Event</code>
     * @param genewaysGeneidNameMap the mapping of geneId and Names
     *           of proteins in geneways interaction
     * @param m  String message
     * @param nf int Accession number of the gene corresponding to the center
     *           of the network to be drawn
     * @param d  int depth of the network to be drawn
     * @param t  double mutual information threshold
     */
    public AdjacencyMatrixEvent(AdjacencyMatrix am, HashMap genewaysGeneidNameMap, String m, int nf, int d, double t, Action action) {
        super(null);
        adjm = am;
        message = m;
        networkFocus = nf;
        depth = d;
        threshold = t;
        this.action = action;
        this.genewaysGeneidNameMap = genewaysGeneidNameMap;
    }

    public AdjacencyMatrix getAdjacencyMatrix() {
        return adjm;
    }

    public String getMessage() {
        return message;
    }

    public int getNetworkFocus() {
        return networkFocus;
    }

    public int getDisplayDepth() {
        return depth;
    }

    public double getThreshold() {
        return threshold;
    }

    public Action getAction() {
        return action;
    }

    public HashMap getGenewaysGeneidNameMap() {
        return genewaysGeneidNameMap;
    }
}
