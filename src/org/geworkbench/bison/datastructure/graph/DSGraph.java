package org.geworkbench.bison.datastructure.graph;

import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Collection;


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

public interface DSGraph <N, E extends DSEdge<N>> extends DSNamed {
    public Collection<E> getEdges(N node);

    public Collection<E> getEdges(N[] nodes);

    public Collection<E> getEdges();

    public E get(N node1, N node2);

    public int getEdgeCount();
}
