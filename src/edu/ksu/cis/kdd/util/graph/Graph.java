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

import edu.ksu.cis.kdd.util.TableSet;
import salvo.jesus.graph.DirectedAcyclicGraphImpl;
import salvo.jesus.graph.Vertex;

import java.util.*;

/**
 * Wrapper for OpenJGraph
 *
 * @author Roby Joehanes
 */
public class Graph extends DirectedAcyclicGraphImpl implements HasProperty {
    protected String name = "Untitled"; //$NON-NLS-1$
    protected Hashtable nodeTable = new Hashtable();
    protected Hashtable property = new Hashtable();

    /**
     * Adding vertex override to accomodate retrieval under certain name.
     *
     * @see salvo.jesus.graph.Graph#add(Vertex)
     */
    public void add(Vertex v) {
        try {
            super.add(v);
            if (v instanceof Node)
                ((Node) v).setOwner(this);
            nodeTable.put(v.getLabel(), v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An alias to add(Vertex)
     *
     * @param v
     */
    public void addNode(Vertex v) {
        add(v);
    }

    /**
     * Add multiple nodes at once. A shortcut
     *
     * @param c
     */
    public void addNodes(Collection c) {
        for (Iterator i = c.iterator(); i.hasNext();) {
            add((Vertex) i.next());
        }
    }

    /**
     * Tell the user not to use this addEdge(Vertex, Vertex)
     *
     * @see salvo.jesus.graph.Graph#addEdge(Vertex, Vertex)
     */
    public salvo.jesus.graph.Edge addEdge(Vertex v1, Vertex v2) {
        try {
            Edge edge = new Edge((Node) v1, (Node) v2);
            addEdge(edge);
            return edge;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see salvo.jesus.graph.GraphImpl#containsVertex(Vertex)
     */
    public boolean containsNode(Node v) {
        return containsVertex(v);
    }

    /**
     * Remove a node
     *
     * @see salvo.jesus.graph.Graph#remove(Vertex)
     */
    public void remove(Vertex v) {
        try {
            super.remove(v);
            if (v instanceof Node)
                ((Node) v).setOwner(null);
            nodeTable.remove(v.getLabel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a node. Same as <tt>remove</tt>
     *
     * @param v
     */
    public void removeNode(Vertex v) {
        remove(v);
    }

    public void removeEdge(Vertex v1, Vertex v2) {
        try {
            removeEdge(new Edge(v1, v2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get node called <tt>name</tt>.
     *
     * @param name
     * @return Node
     */
    public Node getNode(String name) {
        return (Node) nodeTable.get(name);
    }

    /**
     * @see salvo.jesus.graph.Graph#getVertexSet()
     */
    public Set getNodes() {
        return getVertexSet();
    }

    public List getNodeList() {
        LinkedList list = new LinkedList();
        list.addAll(getVertexSet());
        return list;
    }

    /**
     * Returns the set of node names
     *
     * @return Set
     */
    public Set getNodeNames() {
        HashSet set = new HashSet();
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            set.add(((Node) i.next()).getLabel());
        }
        return set;
    }

    /**
     * Return the set of edges. Just an alias of getEdgeSet().
     *
     * @return Set
     */
    public Set getEdges() {
        return getEdgeSet();
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Equals
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Graph)) return false;
        Graph n = (Graph) o;
        return nodeTable.equals(n.nodeTable) && getName().equals(n.getName());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        Set nodes = getNodes();

        buf.append("Graph = " + getName() + ln);
        buf.append("Nodes = " + nodes + ln);
        buf.append("Edges = " + getEdgeSet() + ln);

        return buf.toString();
    }

    /**
     * Returns the property.
     *
     * @return Hashtable
     */
    public Hashtable getProperty() {
        return property;
    }

    /**
     * Sets the property.
     *
     * @param property The property to set
     */
    public void setProperty(Hashtable property) {
        this.property = property;
    }

    /**
     * Get the property value with key p.
     *
     * @param p
     * @return Object
     */
    public Object getProperty(Object p) {
        return property.get(p);
    }

    /**
     * Put the property p with value v
     *
     * @param p
     * @param v
     */
    public void putProperty(Object p, Object v) {
        property.put(p, v);
    }

    /**
     * Remove the property with key p.
     *
     * @param p
     */
    public void removeProperty(Object p) {
        property.remove(p);
    }

    void rename(String oldName, String newName) {
        Vertex oldNode = getNode(oldName);
        Vertex newNode = getNode(newName);
        if (newNode == oldNode) return; // Renaming to itself
        if (newNode != null) throw new RuntimeException("Duplicate name after renaming to " + newName);
        nodeTable.remove(oldName);
        nodeTable.put(newName, oldNode);
    }

    /**
     * Construct the adjacency list table for a particular graph.
     * It is a table of Node -> Children
     */
    public TableSet getAdjacencyList() {
        TableSet adjacencyListTable = new TableSet();
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            Node n = (Node) i.next();
            List l = n.getChildren();
            if (l == null) l = new LinkedList();
            adjacencyListTable.putAll(n, l);
        }
        return adjacencyListTable;
    }

    /**
     * Construct the adjacency list table for a particular graph.
     * It is a table of Node -> Parents
     */
    public TableSet getReverseAdjacencyList() {
        TableSet adjacencyListTable = new TableSet();
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            Node n = (Node) i.next();
            List l = n.getParents();
            if (l == null) l = new LinkedList();
            adjacencyListTable.putAll(n, l);
        }
        return adjacencyListTable;
    }

    public int getError(Graph goldGraph) {
        assert (goldGraph != null);
        Set nodes = getNodes();
        Set goldNodes = goldGraph.getNodes();

        if (nodes.size() != goldNodes.size() || !goldNodes.containsAll(nodes))
            throw new RuntimeException("Nodes don't match");

        Set edges = new HashSet();
        edges.addAll(getEdges());

        Set edges2 = new HashSet();
        edges2.addAll(goldGraph.getEdges());

        edges.removeAll(goldGraph.getEdges());
        edges2.removeAll(getEdges());
        edges.addAll(edges2);

        // Here edges contains all edges that differs between this graph and goldGraph
        Edge[] edgeArray = (Edge[]) edges.toArray(new Edge[0]);
        int max = edgeArray.length;
        for (int i = 0; i < max; i++) {
            Edge edge = edgeArray[i];
            if (edges.contains(new Edge(edge.getDestination(), edge.getSource())))
                edges.remove(edge);
        }

        return edges.size();
    }
}

