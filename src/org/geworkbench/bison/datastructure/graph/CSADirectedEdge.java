package org.geworkbench.bison.datastructure.graph;


/**
 * <p>Title: Bioworks</p>
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
 *          Defines an a-directed edge in which the order of the nodes does not matter. Therefore
 *          two adirected edges are defined as equal if they contain the same nodes, regardless of the order
 */
public class CSADirectedEdge <N> extends CSEdge<N> {
    public CSADirectedEdge() {
        super();
    }

    public CSADirectedEdge(N node1, N node2) {
        super(node1, node2);
    }

    public CSADirectedEdge(N node1, N node2, float mi) {
        super(node1, node2, mi);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CSEdge)) {
            return false;
        }
        CSEdge edge = (CSEdge) obj;
        if (this.getNode1().equals(edge.getNode1()) && this.getNode2().equals(edge.getNode2())) {
            return true;
        }
        if (this.getNode1().equals(edge.getNode2()) && this.getNode2().equals(edge.getNode1())) {
            return true;
        }
        return false;
    }
}
