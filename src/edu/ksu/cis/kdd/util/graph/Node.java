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

import salvo.jesus.graph.VertexImpl;

import java.util.*;

/**
 * Wrapper for OpenJGraph
 *
 * @author Roby Joehanes
 */
public class Node extends VertexImpl implements HasProperty {
    protected Graph owner;
    protected Hashtable property = new Hashtable();
    protected String name;
    protected int hashCode = Integer.MIN_VALUE; // hack

    /**
     * Create an anonymous Node object
     *
     * @see java.lang.Object#Object()
     */
    public Node() {
        super();
    }

    /**
     * Create a node from a specific graph owner
     *
     * @param owner
     */
    public Node(Graph owner) {
        super();
        setOwner(owner);
    }

    /**
     * Create a node with a specific name and a graph owner
     *
     * @param owner
     * @param name
     */
    public Node(Graph owner, String name) {
        super();
        setOwner(owner);
        setName(name);
    }

    /**
     * Create node with a specific name
     *
     * @param name
     */
    public Node(String name) {
        super();
        setName(name);
    }

    /**
     * Returns the owner.
     *
     * @return Graph
     */
    public Graph getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner The owner to set
     */
    public void setOwner(Graph owner) {
        this.owner = owner;
    }

    /**
     * Get the parent nodes
     *
     * @return List
     */
    public List getParents() {
        return owner.getIncomingAdjacentVertices(this);
    }

    /**
     * Get the children nodes
     *
     * @return List
     */
    public List getChildren() {
        return owner.getOutgoingAdjacentVertices(this);
    }

    /**
     * Returns the set of node names
     *
     * @return Set
     */
    public Set getParentNames() {
        HashSet set = new HashSet();
        for (Iterator i = getParents().iterator(); i.hasNext();) {
            set.add(((Node) i.next()).getLabel());
        }
        return set;
    }

    /**
     * Returns the set of node names
     *
     * @return Set
     */
    public Set getChildrenNames() {
        HashSet set = new HashSet();
        for (Iterator i = getChildren().iterator(); i.hasNext();) {
            set.add(((Node) i.next()).getLabel());
        }
        return set;
    }

    /**
     * Get the ancestors of this node (Kleene closure)
     *
     * @return List
     */
    public Set getAncestors() {
        HashSet result = new HashSet();
        LinkedList curParents = new LinkedList();
        curParents.add(this);
        do {
            Node curNode = (Node) curParents.removeFirst();
            List l = curNode.getParents();
            if (l != null) {
                for (Iterator i = l.iterator(); i.hasNext();) {
                    Object n = i.next();
                    if (!result.contains(n)) {
                        result.add(n);
                        curParents.add(n);
                    }
                }
            }
        } while (curParents.size() > 0);
        return result;
    }

    /**
     * Get the descendants of this node (Kleene closure)
     *
     * @return List
     */
    public Set getDescendants() {
        HashSet result = new HashSet();
        LinkedList curChildren = new LinkedList();
        curChildren.add(this);
        do {
            Node curNode = (Node) curChildren.removeFirst();
            List l = curNode.getChildren();
            if (l != null) {
                for (Iterator i = l.iterator(); i.hasNext();) {
                    Object n = i.next();
                    if (!result.contains(n)) {
                        result.add(n);
                        curChildren.add(n);
                    }
                }
            }
        } while (curChildren.size() > 0);
        return result;
    }

    /**
     * Set the node name
     *
     * @param name
     */
    public void setName(String name) {
        if ((owner != null) && (this.name != null)) owner.rename(this.name, name);
        this.name = name;
        if (getLabel() == null || getLabel().equalsIgnoreCase("<Null vertex>")) setLabel(name);
    }

    /**
     * Get the node name
     *
     * @return String
     */
    public String getName() {
        return name;
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
     * Get the property value with key p.
     *
     * @param p
     * @return Object
     */
    public Object getProperty(Object p) {
        return property.get(p);
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

    /**
     * Equals
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node n = (Node) o;
        return property.equals(n.property) && getName().equals(n.getLabel());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        if (hashCode == Integer.MIN_VALUE) {
            hashCode = toString().hashCode();
        }
        return hashCode;
    }

    public String getLabel() {
        String label = super.getLabel();
        if (label == null) return getName();
        return label;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
}
