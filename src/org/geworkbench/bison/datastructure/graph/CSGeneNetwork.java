package org.geworkbench.bison.datastructure.graph;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Collection;
import java.util.Vector;


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

public class CSGeneNetwork extends SparseDoubleMatrix2D implements DSGeneNetwork, DSNamed {
    DSMicroarraySet<DSMicroarray> mArraySet;
    //private ExpressionMicroarraySet MArraySet;

    public CSGeneNetwork(int rows, int cols) {
        super(rows, cols);
    }

    public CSGeneNetwork(DoubleMatrix2D matrix) {
        super(matrix.toArray());
    }

    public DSMicroarraySet<DSMicroarray> getMArraySet() {
        return mArraySet;
    }

    public void setMArraySet(DSMicroarraySet mArraySet) {
        this.mArraySet = mArraySet;
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges(DSGeneMarker node) {
        Vector<CSADirectedEdge<DSGeneMarker>> vecEdges = new Vector<CSADirectedEdge<DSGeneMarker>>();
        int nodeId = node.getSerial();
        DoubleMatrix1D row = viewRow(nodeId);
        for (int rowCtr = 0; rowCtr < row.size(); rowCtr++) {
            if (row.get(rowCtr) != 0) {
                DSGeneMarker marker1 = mArraySet.getMarkers().get(nodeId);
                DSGeneMarker marker2 = mArraySet.getMarkers().get(rowCtr);
                CSADirectedEdge<DSGeneMarker> edge = new CSADirectedEdge<DSGeneMarker>(marker1, marker2, (float) row.get(rowCtr));
                vecEdges.add(edge);
            }
        }
        return vecEdges;
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges(DSGeneMarker[] nodes) {
        Vector<CSADirectedEdge<DSGeneMarker>> vecEdges = new Vector<CSADirectedEdge<DSGeneMarker>>();
        for (int nodeCtr = 0; nodeCtr < nodes.length; nodeCtr++) {
            vecEdges.addAll(getEdges(nodes[nodeCtr]));
        }
        return vecEdges;
    }

    public Collection<CSADirectedEdge<DSGeneMarker>> getEdges() {
        Vector<CSADirectedEdge<DSGeneMarker>> vecEdges = new Vector<CSADirectedEdge<DSGeneMarker>>();
        for (int nodeCtr = 0; nodeCtr < this.rows; nodeCtr++) {
            DoubleMatrix1D row = viewRow(nodeCtr);
            DSGeneMarker marker1 = mArraySet.getMarkers().get(nodeCtr);
            for (int rowCtr = nodeCtr + 1; rowCtr < row.size(); rowCtr++) {
                if (row.get(rowCtr) != 0) {
                    DSGeneMarker marker2 = mArraySet.getMarkers().get(rowCtr);
                    CSADirectedEdge<DSGeneMarker> edge = new CSADirectedEdge<DSGeneMarker>(marker1, marker2, (float) row.get(rowCtr));
                    vecEdges.add(edge);
                }
            }
        }
        return vecEdges;
    }

    public CSADirectedEdge<DSGeneMarker> get(DSGeneMarker node1, DSGeneMarker node2) {
        return new CSADirectedEdge<DSGeneMarker>(node1, node2, (float) this.get(node1.getSerial(), node2.getSerial()));
    }

    public int getEdgeCount() {
        return this.cardinality();
    }

    public String getLabel() {
        return mArraySet.getLabel();
    }

    public void setLabel(String label) {
        mArraySet.setLabel(label);
    }
}
