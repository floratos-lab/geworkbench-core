package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version $Id$
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
    public AdjacencyMatrixEvent(AdjacencyMatrix am, String m, Action action) {
        super(null);
        adjm = am;
        message = m;
        this.action = action;
    }

    public AdjacencyMatrix getAdjacencyMatrix() {
        return adjm;
    }

    public String getMessage() {
        return message;
    }

    public Action getAction() {
        return action;
    }

}
