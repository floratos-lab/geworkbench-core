package edu.ksu.cis.kdd.util.graph;

/*
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import salvo.jesus.graph.DirectedEdgeImpl;
import salvo.jesus.graph.Vertex;

/**
 * Wrapper for OpenJGraph
 *
 * @author Roby Joehanes
 */
public class Edge extends DirectedEdgeImpl implements Cloneable {
    public Edge(Node source, Node dest) {
        super(source, dest);
    }

    public Edge(Vertex source, Vertex dest) {
        super(source, dest);
    }

    // Note to self: toString has been defined by the superclass

    /**
     * @see salvo.jesus.graph.Graph#getSink
     */
    public Node getDestination() {
        return (Node) getSink();
    }

    /**
     * Equals
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;
        Edge n = (Edge) o;
        return getSource().equals(n.getSource()) && getDestination().equals(n.getDestination());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    public Object clone() {
        throw new RuntimeException("Never clone edges individually. See BBNGraph.clone!");
    }
}
