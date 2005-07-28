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

import edu.ksu.cis.kdd.util.TableSet;

import java.util.*;

/**
 * @author Roby Joehanes
 */
public class CliqueTree extends Graph {

    protected TableSet adjList; // processed adjacency list (node -> parents)
    protected TableSet origList; // the original adjacency list
    protected Object[] reverseOrdering;  // == alpha^-1[i]
    protected Hashtable nodeOrdering;    // == alpha(v)
    protected LinkedList cliques;  // List of cliques, ordered

    /**
     *
     */
    public CliqueTree() {
        super();
    }

    public CliqueTree(Graph g) {
        this(g.getReverseAdjacencyList());
    }

    public CliqueTree(Set[] adjList) {
        int max = adjList.length;
        origList = new TableSet();
        for (int i = 0; i < max; i++) {
            if (adjList[i] != null) {
                origList.putAll(new Integer(i), adjList[i]);
            }
        }
        this.adjList = (TableSet) origList.clone();
        buildCliqueTree();
    }

    /**
     * Clique tree construction. Follows directly from Neapolitan's book.
     *
     * @param adjList
     */
    public CliqueTree(TableSet adjList) {
        super();
        origList = adjList;
        this.adjList = (TableSet) adjList.clone();
        buildCliqueTree();
    }

    public void buildCliqueTree() {
        MaxCardinalitySearch search = new MaxCardinalitySearch(adjList);
        adjList = search.adjList;
        reverseOrdering = search.reverseOrdering;
        nodeOrdering = search.nodeOrdering;
        triangulation();
        findCliques();
        buildEdges();
    }

    /**
     * <P>Triangulation.
     * <P>For the pseudocode, see Neapolitan, R., Probabilistic Reasoning In Expert Systems,
     * Theory and Algorithm, 1990, John Wiley and Sons, page 117.
     */
    protected void triangulation() {
        int nodeSize = adjList.size();  // == n
        Hashtable vertexTable = new Hashtable(); // == f(v)
        Hashtable indexTable = new Hashtable();  // == index(v)
        TableSet addedEdges = new TableSet();    // == F(alpha)

        for (int i = nodeSize; i > 0; i--) {
            Object node = reverseOrdering[i];
            vertexTable.put(node, node);
            indexTable.put(node, new Integer(i));

            Set parents = adjList.get(node);
            if (parents != null) {
                for (Iterator j = parents.iterator(); j.hasNext();) {
                    Object parent = j.next();
                    int order = ((Integer) nodeOrdering.get(parent)).intValue();
                    if (order <= i) continue;

                    while (((Integer) indexTable.get(parent)).intValue() > i) {
                        indexTable.put(parent, new Integer(i));
                        addedEdges.put(node, parent);
                        addedEdges.put(parent, node); // Make it undirected
                        parent = vertexTable.get(parent);
                    }
                    if (vertexTable.get(parent) == parent) {
                        vertexTable.put(parent, node);
                    }
                }
            }
        }

        // Compute E U F(alpha)
        for (Enumeration e = addedEdges.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Set parentSet = addedEdges.get(key);
            if (parentSet == null) continue;
            adjList.putAll(key, parentSet);
        }

    }

    /**
     * <P>Find cliques. It simply follows the reverse order of the cardinality
     * ordering. Then search on its parents that has ordering number less than
     * itself. After that, we must check whether the clique is already contained
     * in the previous search. If not, then we can safely add the clique.
     */
    protected void findCliques() {
        cliques = new LinkedList();
        int nodeSize = adjList.size();
        for (int i = nodeSize; i > 0; i--) {
            Object node = reverseOrdering[i];
            Set parents = adjList.get(node);
            HashSet clique = new HashSet();
            clique.add(node);
            if (parents != null) {
                for (Iterator j = parents.iterator(); j.hasNext();) {
                    Object parent = j.next();
                    int order = ((Integer) nodeOrdering.get(parent)).intValue();
                    if (order <= i) clique.add(parent);
                }
            }
            cliques.add(clique);
        }

        // Rescan for containment 
        HashSet[] cliqueSet = (HashSet[]) cliques.toArray(new HashSet[0]);
        int max = cliqueSet.length;
        cliques.clear(); // empty the clique list
        for (int i = 0; i < max; i++) {
            boolean isContained = false;
            for (int j = 0; j < max; j++) {
                if (i != j && cliqueSet[j].containsAll(cliqueSet[i])) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                Clique clique = new Clique(this, cliqueSet[i]);
                addNode(clique);
                cliques.addFirst(clique);
            }
        }

        //System.out.println(nodeOrdering.toString()); 
        //System.out.println(cliques.toString());
    }

    /**
     * <P>Compute S for each clique (i.e. the separator set). We don't need to
     * compute R, because it is inferred. Here, we also build edges
     */
    protected void buildEdges() {
        // Ignore if there are less than 2 cliques
        if (cliques.size() < 2) { // corrected bug fix. Ref: http://groups.yahoo.com/group/bndev/message/130 -- RJ
            Clique clq = (Clique) cliques.get(0);
            clq.addBaseNodes(clq.getNodeSet());
            return;
        }

        Clique[] cliqueSet = (Clique[]) cliques.toArray(new Clique[0]);
        int max = cliqueSet.length;

        HashSet union = new HashSet();
        union.addAll(cliqueSet[0].getNodeSet()); // union = Clq_0 U ... U Clq_i-1
        Hashtable parentTable = new Hashtable();

        for (int i = 1; i < max; i++) {
            HashSet s = new HashSet();
            Set nodeSet = cliqueSet[i].getNodeSet();
            s.addAll(nodeSet);
            s.retainAll(union);  // S = Clq_i \cup union
            cliqueSet[i].setS(s);
            union.addAll(nodeSet);

            for (int j = i - 1; j >= 0; j--) {
                if (cliqueSet[j].containsAll(s)) {
                    parentTable.put(cliqueSet[i], cliqueSet[j]);
                }
            }
        }

        for (Enumeration e = parentTable.keys(); e.hasMoreElements();) {
            Clique child = (Clique) e.nextElement();
            Clique parent = (Clique) parentTable.get(child);
            addEdge(parent, child);
        }

        // Find clique assignment
        // table that maps a node to its clique
        TableSet clique2node = new TableSet();

        // We use adj list to iterate over the node names because
        // in the original adjacency list some nodes may not appear 
        // because it is a root node
        for (Iterator j = adjList.keySet().iterator(); j.hasNext();) {
            Object node = j.next();
            // Of course, to detect its parent, we have to use the original
            // adjacency list
            Set parents = origList.get(node);

            HashSet nodeUparents = new HashSet();
            nodeUparents.add(node);
            if (parents != null) nodeUparents.addAll(parents);

            for (int i = 0; i < max; i++) {
                if (cliqueSet[i].containsAll(nodeUparents)) {
                    clique2node.put(cliqueSet[i], node);
                    break;
                }
            }
        }

        for (Iterator i = clique2node.keySet().iterator(); i.hasNext();) {
            Clique clique = (Clique) i.next();
            Set baseNodes = (Set) clique2node.get(clique);
            clique.addBaseNodes(baseNodes);
        }
    }

    public List getOrderedCliques() {
        return (List) cliques.clone();
    }

    /**
     * For debugging only
     */
    protected void printAdjList() {
        for (Enumeration e = adjList.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Set parentSet = adjList.get(key);
            System.out.println(key + " = " + parentSet);
        }
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

        CliqueTree tree = new CliqueTree(adjList);
        System.out.println(tree.toString());
    }
}
