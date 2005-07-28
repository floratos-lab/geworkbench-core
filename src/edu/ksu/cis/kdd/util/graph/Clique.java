/*
 * Created on Feb 17, 2003
 *
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
package edu.ksu.cis.kdd.util.graph;

import java.util.*;

/**
 * @author Roby Joehanes
 */
public class Clique extends Node {

    protected HashSet set = new HashSet();
    protected HashSet s = new HashSet();  // Clique's S
    protected HashSet r = new HashSet();
    protected HashSet baseNodes = new HashSet(); // The base nodes (i.e. the nodes that assigned to this clique)

    /**
     * @param owner
     */
    public Clique(Graph owner) {
        super(owner);
    }

    /**
     * @param owner
     */
    public Clique(Graph owner, Collection c) {
        super(owner);
        addAll(c);
    }

    /**
     *
     */
    public Clique() {
        super();
    }


    public String getName() {
        return set.toString();
    }

    public String getLabel() {
        return set.toString();
    }

    /**
     * @return HashSet
     */
    public Set getR() {
        return r;
    }

    /**
     * @return HashSet
     */
    public Set getS() {
        return s;
    }

    /**
     * Sets the s.
     *
     * @param s The S to set
     */
    public void setS(HashSet s) {
        this.s = s;
        r = new HashSet();
        r.addAll(set);
        r.removeAll(s);
    }

    /**
     * @return HashSet
     */
    public Set getNodeSet() {
        return Collections.unmodifiableSet(set); // original node set must not be touched!
    }

    public void addBaseNode(Node n) {
        baseNodes.add(n);
    }

    public void addBaseNodes(Collection n) {
        baseNodes.addAll(n);
    }

    public Set getBaseNodes() {
        //if (baseNodes.size() == 0) return getNodeSet(); // wrong bug fix. Sorry for that -- RJ
        return baseNodes;
    }

    public Set intersect(Clique c) {
        return intersect(c.set);
    }

    public Set intersect(Set c) {
        HashSet set = new HashSet();
        set.addAll(this.set);
        set.retainAll(c);
        return set;
    }

    public Set difference(Clique c) {
        return difference(c.set);
    }

    public Set difference(Set c) {
        HashSet set = new HashSet();
        set.addAll(this.set);
        set.removeAll(c);
        return set;
    }

    // From this point below is basically the same mundane operations provided by HashSet

    /**
     * Remove a node from this clique
     *
     * @param n The node to be removed
     */
    public void remove(Node n) {
        set.remove(n);
    }

    /**
     * Add a node to this clique.
     *
     * @param n The node to remove
     */
    public void add(Node n) {
        set.add(n);
    }

    /**
     * Add all nodes contained in the collection <tt>c</tt>.
     *
     * @param c
     */
    public void addAll(Collection c) {
        set.addAll(c);
    }

    /**
     * The number of nodes the clique has
     *
     * @return int
     */
    public int size() {
        return set.size();
    }

    /**
     * Whether or not the node in this clique
     *
     * @param n
     * @return boolean
     */
    public boolean contains(Node n) {
        return set.contains(n);
    }

    /**
     * Whether or not all of the nodes in the collection <tt>c</tt> are in this
     * clique
     *
     * @param c
     * @return boolean
     */
    public boolean containsAll(Collection c) {
        return set.containsAll(c);
    }

    /**
     * Giving standard iterator of the clique
     *
     * @return Iterator
     */
    public Iterator iterator() {
        return set.iterator();
    }

    /**
     * Whether or not the clique is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Give the nodes in an array
     *
     * @return BBNNode[]
     */
    public Node[] toArray() {
        return (Node[]) set.toArray(new Node[0]);
    }

    /**
     * Empties the clique
     */
    public void clear() {
        set.clear();
    }

    /**
     * Standard Hash Code. It's really hashset's code
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return set.hashCode();
    }

    /**
     * Standard equality test: Two cliques are equal if their node set
     * are equal.
     */
    public boolean equals(Object o) {
        return o instanceof Clique && ((Clique) o).set.equals(set);
    }
}
