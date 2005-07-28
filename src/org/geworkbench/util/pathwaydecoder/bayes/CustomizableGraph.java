package org.geworkbench.util.pathwaydecoder.bayes;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.SimAnnealSL;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.util.graph.Edge;

import java.util.Set;
import java.util.Vector;

public class CustomizableGraph extends SimAnnealSL {
    public CustomizableGraph(Data data) {
        super(data);
    }

    public void clearStructure() {
        try {
            BBNGraph graph = this.bbnGraph;
            Set edges = graph.getEdges();

            Edge[] tmp = new Edge[0];
            Edge[] arrEdges = (Edge[]) edges.toArray(tmp);
            Vector edgesToRemove = new Vector();

            for (int i = 0; i < edges.size(); i++) {
                //      Iterator it = edges.iterator();
                //      while (it.hasNext()) {
                Edge edge = arrEdges[i];
                //        Edge edge = (Edge) it.next();
                graph.removeEdge(edge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEdge(int parentIdx, int childIdx) {

        parentTable[childIdx].add(new Integer(parentIdx));

        //    BBNNode child = bbnNodes[childIdx];
        //    BBNNode parent = bbnNodes[parentIdx];
        //    try {
        //      bbnGraph.addEdge(parent, child);
        //    }
        //    catch (Exception e) {
        //      System.err.println("Error on adding parent " + parent);
        //    }
        //    parentTable[childIdx].add(new Integer(childIdx));
    }

    public void addEdge(String edge1Name, String edge2Name) {
        int childIndex = -1;
        int parentIndex = -1;

        for (int i = 0; i < bbnNodes.length; i++) {
            BBNNode node = bbnNodes[i];
            if (edge1Name.equals(node.getLabel())) {
                parentIndex = i;
            }

            if (edge2Name.equals(node.getLabel())) {
                childIndex = i;
            }

        }

        if (parentIndex == -1) {
            System.out.println("Cannot find node " + edge1Name);
        } else if (childIndex == -1) {
            System.out.println("Cannot find node " + edge2Name);
        } else {
            addEdge(parentIndex, childIndex);
        }
    }

    public void printNodes() {
        for (int i = 0; i < bbnNodes.length; i++) {
            BBNNode node = bbnNodes[i];
            System.out.println(node.getLabel());
        }
        System.out.println();
        System.out.println();
    }

    public BBNGraph getInitializedGraph() {
        initializeNodes();
        initializeNodeParents();
        initializePossibleParents();
        initializeGraph();
        return bbnGraph;
    }

    public void computeCPT() {
        super.computeCPT(bbnGraph);
    }

}
