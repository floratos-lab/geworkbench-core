/*
 * Created on Feb 12, 2003
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

import edu.ksu.cis.kdd.util.TableSet;

import java.util.*;

/**
 * A class to compute the strongly connected components. A straight forward
 * implementation from Cormen's textbook.
 *
 * @author Roby Joehanes
 */
public class StronglyConnectedComponents {

    protected TableSet adjacencyListTable;
    protected LinkedList reversePostOrderList;
    protected HashSet seenAlready;  // set implies no nodes having the same name.
    protected TableSet gTransform;
    protected Hashtable clusterTable; // table of node -> scc cluster

    private static StronglyConnectedComponents instance = new StronglyConnectedComponents();

    private StronglyConnectedComponents() {
    }

    /**
     *
     */
    public StronglyConnectedComponents(TableSet adjList) {
        adjacencyListTable = (TableSet) adjList.clone();
    }

    public StronglyConnectedComponents(Set[] adjList) {
        assert(adjList != null);
        int max = adjList.length;
        adjacencyListTable = new TableSet();
        for (int i = 0; i < max; i++) {
            adjacencyListTable.putAll(new Integer(i), adjList[i]);
        }
    }

    public StronglyConnectedComponents(Graph graph) {
        adjacencyListTable = graph.getAdjacencyList();
    }

    /**
     * Returns the set of sets of strongly connected components
     *
     * @return Set
     */
    public Set getComponents() {
        seenAlready = new HashSet();
        reversePostOrderList = new LinkedList();
        gTransform = new TableSet();
        clusterTable = new Hashtable();

        for (Iterator i = adjacencyListTable.keySet().iterator(); i.hasNext();) {
            Object nextNode = i.next();
            if (!seenAlready.contains(nextNode)) {
                dfsSortByPostScore(nextNode);
            }
        }

        seenAlready = new HashSet();
        HashSet componentSet = new HashSet();

        for (Iterator i = reversePostOrderList.iterator(); i.hasNext();) {
            Object nextNode = i.next();
            if (!seenAlready.contains(nextNode)) {
                Set comp = dfsGetStronglyConnectedGraph(nextNode, new HashSet());
                componentSet.add(comp);
            }
        }

        return componentSet;
    }

    /**
     * Deep first search computing post order score and the graph transform
     *
     * @param curNode
     */
    protected void dfsSortByPostScore(Object curNode) {
        seenAlready.add(curNode);
        Set nextNodes = adjacencyListTable.get(curNode);
        if (nextNodes != null) {
            for (Iterator i = nextNodes.iterator(); i.hasNext();) {
                Object nextNode = i.next();
                if (!seenAlready.contains(nextNode)) {
                    dfsSortByPostScore(nextNode);
                }
            }
            for (Iterator i = nextNodes.iterator(); i.hasNext();) {
                Object nextNode = i.next();
                gTransform.put(nextNode, curNode);
            }
        }

        reversePostOrderList.addFirst(curNode);
    }

    protected Set dfsGetStronglyConnectedGraph(Object curNode, HashSet set) {
        seenAlready.add(curNode);
        Set nextNodes = gTransform.get(curNode);
        if (nextNodes != null) {
            for (Iterator i = nextNodes.iterator(); i.hasNext();) {
                Object nextNode = i.next();
                if (!seenAlready.contains(nextNode)) {
                    dfsGetStronglyConnectedGraph(nextNode, set);
                }
            }
        }
        set.add(curNode);
        clusterTable.put(curNode, set);

        return set;
    }

    public static Set getComponents(TableSet adjList) {
        instance.adjacencyListTable = adjList;
        return instance.getComponents();
    }

    public static Set getComponents(Graph graph) {
        return getComponents(graph.getAdjacencyList());
    }

    /**
     * Get the table of node -> scc cluster
     *
     * @return Hashtable
     */
    public Hashtable getClusterTable() {
        return clusterTable;
    }

    /**
     * A driver to test this strongly connected components
     *
     * @param args
     */
    public static void main(String[] args) {
        // This is the example in Cormen's text book
        TableSet t = new TableSet();
        t.put("a", "b");
        t.putAll("b", new Object[]{"c", "e", "f"});
        t.putAll("c", new Object[]{"d", "g"});
        t.putAll("d", new Object[]{"c", "h"});
        t.putAll("e", new Object[]{"a", "f"});
        t.put("f", "g");
        t.putAll("g", new Object[]{"f", "h"});
        t.put("h", "h");

        // Should output something like: [[a,b,e],[c,d],[f,g],[h]]
        // (order is not important)
        System.out.println(StronglyConnectedComponents.getComponents(t).toString());

        System.out.println(instance.clusterTable.toString());
    }

}
