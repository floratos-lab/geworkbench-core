package org.geworkbench.bison.datastructure.graph;


/**
 * <p>Title: Bioworks</p>
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
public class CSEdge <N> implements DSEdge<N> {
    N node1;
    N node2;
    float value;

    public CSEdge() {
    }

    public CSEdge(N node1, N node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public CSEdge(N node1, N node2, float value) {
        this.node1 = node1;
        this.node2 = node2;
        this.value = value;
    }


    public N getNode1() {
        return node1;
    }

    public void setNode1(N node1) {
        this.node1 = node1;
    }

    public N getNode2() {
        return node2;
    }

    public void setNode2(N node2) {
        this.node2 = node2;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean contains(N node) {
        if (node.equals(node1) || node.equals(node2)) {
            return true;
        } else {
            return false;
        }
    }

    public N getTarget(N node) {
        if (node1.equals(node)) {
            return node2;
        } else {
            return node1;
        }
    }


    //Not sure if this is optimal
    public int hashCode() {
        int hashCode = (node1.hashCode() * 20000) + node2.hashCode();
        //        int hashCode = (1+node1.hashCode()) * node2.hashCode();
        return hashCode;
    }
}
