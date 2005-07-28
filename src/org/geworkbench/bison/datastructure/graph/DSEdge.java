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
public interface DSEdge <N> {
    public N getNode1();

    public void setNode1(N node1);

    public N getNode2();

    public void setNode2(N node2);

    public float getValue();

    public void setValue(float value);

    public boolean contains(N node);

    public N getTarget(N node);
}
