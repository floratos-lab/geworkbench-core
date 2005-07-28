package org.geworkbench.bison.datastructure.graph;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */

public class GeneNetworkView implements DSGeneNetwork {
    /**
     * Contains the activated panels.
     */
    protected DSPanel<? extends DSGeneMarker> nodePanel = null;
    Set<DSGeneMarker> panelObjects = null;
    protected CSGeneNetwork refNetwork = null;
    //    protected GeneNetwork networkView = null;
    protected SparseDoubleMatrix2D networkView = null;
    protected boolean usePanels = true;
    int[] geneIndices = null;

    public GeneNetworkView(CSGeneNetwork refNetwork) {
        this.refNetwork = refNetwork;
    }

    public void setNodePanel(DSPanel<? extends DSGeneMarker> nodePanel) {
        this.nodePanel = nodePanel;
        this.panelObjects = new HashSet(nodePanel);
        if (usePanels) {
            rebuildGraph();
        }
    }

    void rebuildGraph() {
        IntArrayList selectedIndices = new IntArrayList(panelObjects.size());
        for (DSGeneMarker node : panelObjects) {
            selectedIndices.add(node.getSerial());
        }
        //        networkView = new GeneNetwork(refNetwork.viewSelection(selectedIndices.elements(), selectedIndices.elements()));
        //        networkView.setMArraySet(refNetwork.getMArraySet());

        networkView = new CSGeneNetwork(refNetwork.viewSelection(selectedIndices.elements(), selectedIndices.elements()));

        geneIndices = selectedIndices.elements();
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges(DSGeneMarker node) {
        //        return networkView.getEdges(node);
        return null;
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges(DSGeneMarker[] nodes) {
        //        return networkView.getEdges(nodes);
        return null;
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges() {
        //        return networkView.getEdges();
        Vector<CSADirectedEdge<DSGeneMarker>> vecEdges = new Vector<CSADirectedEdge<DSGeneMarker>>();

        for (int nodeCtr = 0; nodeCtr < networkView.rows(); nodeCtr++) {
            DoubleMatrix1D row = networkView.viewRow(nodeCtr);
            DSGeneMarker marker1 = refNetwork.getMArraySet().getMarkers().get(geneIndices[nodeCtr]);
            for (int rowCtr = nodeCtr + 1; rowCtr < row.size(); rowCtr++) {
                if (row.get(rowCtr) != 0) {
                    DSGeneMarker marker2 = refNetwork.getMArraySet().getMarkers().get(geneIndices[rowCtr]);
                    CSADirectedEdge<DSGeneMarker> edge = new CSADirectedEdge<DSGeneMarker>(marker1, marker2, (float) row.get(rowCtr));
                    vecEdges.add(edge);
                }
            }
        }
        return vecEdges;

    }

    public CSADirectedEdge<DSGeneMarker> get(DSGeneMarker node1, DSGeneMarker node2) {
        //        return networkView.get(node1, node2);
        return null;
    }

    public int getEdgeCount() {
        //        return networkView.getEdgeCount();
        return networkView.cardinality();
    }

    public void setLabel(String label) {
        if (refNetwork != null) {
            refNetwork.setLabel(label);
        }
    }

    public String getLabel() {
        if (refNetwork != null) {
            return refNetwork.getLabel();
        } else {
            return "Empty Network";
        }
    }
}
