package org.geworkbench.bison.datastructure.graph;


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
public class CSDirectedEdge <N> extends CSEdge<N> {
    public CSDirectedEdge() {
    }

    public boolean equals(CSEdge<N> edge) {
        if (this.getNode1().equals(edge.getNode1()) && this.getNode2().equals(edge.getNode2())) {
            return true;
        }
        return false;
    }
}
