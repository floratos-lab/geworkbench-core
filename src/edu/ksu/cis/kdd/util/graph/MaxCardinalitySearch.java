/*
 * Created on Mar 5, 2003
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
 * Perform maximal cardinality search. Actually a part of Clique tree construction, but
 * refactored out because other modules may use this ordering as well.
 *
 * @author Roby Joehanes
 */
public class MaxCardinalitySearch {

    TableSet adjList; // processed adjacency list (node -> parents)
    TableSet origList; // the original adjacency list
    Object[] reverseOrdering;  // == alpha^-1[i]
    Hashtable nodeOrdering;    // == alpha(v)

    public MaxCardinalitySearch(Graph g) {
        this(g.getReverseAdjacencyList());
    }

    /**
     * Clique tree construction. Follows directly from Neapolitan's book.
     *
     * @param adjList
     */
    public MaxCardinalitySearch(TableSet adjList) {
        super();
        origList = adjList;
        this.adjList = (TableSet) adjList.clone();
        moralization();
        toUndirectedGraph();
        maxCardinalitySearch();
    }

    /**
     * "Moralize" the graph. For every two parents of a particular node,
     * add an undirected edge to it.
     */
    protected void moralization() {
        TableSet addedEdges = new TableSet();
        for (Enumeration e = adjList.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object[] nodes = adjList.get(key).toArray();
            int max = nodes.length;
            for (int i = 0; i < max; i++) {
                if (nodes[i] == key) continue;
                for (int j = 0; j < max; j++) {
                    if (i == j) continue;
                    addedEdges.put(nodes[i], nodes[j]);
                    addedEdges.put(nodes[j], nodes[i]);
                }
            }
        }

        for (Enumeration e = addedEdges.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Set parentSet = addedEdges.get(key);
            if (parentSet == null) continue;
            adjList.putAll(key, parentSet);
        }
    }

    /**
     * Converting the adjacency list into undirected edges. It simply
     * add the reverse edge.
     */
    protected void toUndirectedGraph() {
        for (Enumeration e = adjList.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Set set = adjList.get(key);
            for (Iterator i = set.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o == key) throw new RuntimeException("Error: Self loop at node " + o);
                adjList.put(o, key);
            }
        }
    }

    /**
     * <P>Maximum Cardinality Search. See Tarjan and Yannakakis.
     * <P>For the pseudocode, see Neapolitan, R., Probabilistic Reasoning In Expert Systems,
     * Theory and Algorithm, 1990, John Wiley and Sons, page 110.
     */
    protected void maxCardinalitySearch() {
        int nodeSize = adjList.size();   // == n
        HashSet[] sets = new HashSet[nodeSize + 1];  // == set[i]
        reverseOrdering = new Object[nodeSize + 1];  // == alpha^-1[i]
        nodeOrdering = new Hashtable();            // == alpha(v)
        Hashtable sizeTable = new Hashtable();     // == size(v)

        for (int i = 0; i <= nodeSize; i++) {
            sets[i] = new HashSet();
        }

        for (Iterator i = adjList.keySet().iterator(); i.hasNext();) {
            Object node = i.next();
            sizeTable.put(node, new Integer(0));
            sets[0].add(node);
        }

        for (int i = 1, j = 0; i <= nodeSize; i++) {
            Object chosen = null;
            int maxParent = -1;
            // Choose the node with most connection
            for (Iterator k = sets[j].iterator(); k.hasNext();) {
                Object node = k.next();
                Set parents = adjList.get(node);
                int parentSize = 0;
                if (parents != null) parentSize = parents.size();
                if (parentSize > maxParent) {
                    maxParent = parentSize;
                    chosen = node;
                }
            }
            assert (chosen != null);
            sets[j].remove(chosen);
            nodeOrdering.put(chosen, new Integer(i));
            reverseOrdering[i] = chosen;
            sizeTable.put(chosen, new Integer(-1));

            Set parents = adjList.get(chosen);
            if (parents != null) {
                for (Iterator k = parents.iterator(); k.hasNext();) {
                    Object parent = k.next();
                    if (k == chosen) continue;
                    int parentSize = ((Integer) sizeTable.get(parent)).intValue();
                    if (parentSize < 0) continue;
                    sets[parentSize].remove(parent);
                    parentSize++;
                    sizeTable.put(parent, new Integer(parentSize));
                    sets[parentSize].add(parent);
                }
            }

            j++;
            while (j >= 0 && sets[j].size() == 0) j--;
        }
    }

    /**
     * @return List
     */
    public List getOrder() {
        LinkedList order = new LinkedList();
        for (int i = 1; i < reverseOrdering.length; i++) {
            order.add(reverseOrdering[i]);
        }

        return order;
    }

    public static void main(String[] args) {
        // This example is of Asia network.
        TableSet adjList = new TableSet();
        adjList.put("TB", "V");
        adjList.putAll("TC", new Object[]{"TB", "C"});
        adjList.put("X", "TC");
        adjList.put("C", "S");
        adjList.put("B", "S");
        adjList.putAll("D", new Object[]{"TC", "B"});

        MaxCardinalitySearch order = new MaxCardinalitySearch(adjList);
        System.out.println(order.toString());
    }
}
