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
package edu.ksu.cis.bnj.bbn.inference.cutset;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.ExactInference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.pearl.Pearl;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.*;


public class Cutset extends ExactInference {

    BBNGraph network;
    LinkedList cutsetNodes;
    Hashtable nodeTable;
    Hashtable cutsetTable;
    Hashtable bnTable;
    Pearl pearlObject;
    Hashtable baseInfoTable;
    List topologicalOrder;

    public Cutset() {
        // for later use;
        super(null);
    }

    public Cutset(BBNGraph g) {
        super(g);
        network = g;

        CreateSplittingGraph graph = new CreateSplittingGraph(g);
        cutsetNodes = graph.go();
        nodeTable = graph.getNodetable();
        cutsetTable = graph.getCutsetTable();
        bnTable = new Hashtable();
        baseInfoTable = new Hashtable();

        //////////////////////////////////////
        //tempTest();
        /////////////////////////////
        pearlObject = new Pearl();
        topologicalOrder = network.topologicalSort();

    }

    public void tempTest() {
        cutsetTable.clear();
        cutsetNodes.clear();
        //cutsetTable.put("TbOrCa",nodeTable.get("TbOrCa"));
        //cutsetNodes.add(nodeTable.get("TbOrCa"));
        cutsetTable.put("Cancer", nodeTable.get("Cancer"));
        cutsetTable.put("Bronchitis", nodeTable.get("Bronchitis"));
        cutsetNodes.add(nodeTable.get("Cancer"));
        cutsetNodes.add(nodeTable.get("Bronchitis"));
        //System.out.println(cutsetTable.toString());
    }

    /*	protected void m() {
            HashSet cutsetDescendants = new HashSet();
            HashSet allNodes = new HashSet();
            allNodes.addAll(network.getNodes());
            for (Iterator i = cutsetNodes.iterator(); i.hasNext(); ) {
                BBNNode node = (BBNNode) i.next();
                cutsetDescendants.addAll(node.getDescendants());
            }
            allNodes.removeAll(cutsetDescendants);
        }
    */

    protected Set getUniqueInstantiation(LinkedList nodes, Hashtable curInst, HashSet set) {
        BBNNode node = (BBNNode) nodes.removeFirst();
        for (Iterator i = ((BBNDiscreteValue) node.getValues()).iterator(); i.hasNext();) {
            String value = (String) i.next();
            //////////////////////////////////
            curInst.put(node.getLabel(), value);
            if (nodes.size() == 0) {
                set.add(curInst.clone());
            } else {
                getUniqueInstantiation(nodes, curInst, set);
            }
        }
        nodes.addFirst(node);
        return set;
    }

    /**
     * Updates cutsetNodes, nodeTable == name->BBNNode,  nodesWithPredecessors == name->BBNNode
     */
    public void getNodesWithoutLoopCutsetPredecessors() {
        Iterator cutsetIterator;
        Hashtable nodesWithPredecessors = new Hashtable();
        cutsetNodes = orderCutsetNodes();
        for (cutsetIterator = cutsetNodes.iterator(); cutsetIterator.hasNext();) {
            BBNNode cutset = (BBNNode) cutsetIterator.next();
            List order = network.topologicalSort(cutset);
            Iterator listIterator = order.iterator();
            if (listIterator.hasNext())            /// since we dont need the current cutset nodes
                listIterator.next();               // we need only the nodes reachable from the cutset node
            while (listIterator.hasNext()) {
                BBNNode node = (BBNNode) listIterator.next();
                String name = node.getLabel();
                if (nodeTable.containsKey(name)) {
                    nodesWithPredecessors.put(name, nodeTable.get(name));
                    nodeTable.remove(name);
                }

            }
        }
        pearlObject.initializeForCutset(network, nodeTable, nodesWithPredecessors, topologicalOrder, cutsetTable);
        baseInfoTable = pearlObject.initNodesWithoutPredecessors();
        //System.out.println(baseInfoTable.toString());
    }

    public LinkedList orderCutsetNodes() {
        LinkedList orderedCutsetNodes = new LinkedList();
        List order = network.topologicalSort();
        Iterator listIterator = order.iterator();
        int index = 0;
        while (listIterator.hasNext()) {
            BBNNode node = (BBNNode) listIterator.next();
            if (cutsetTable.containsKey(node.getLabel())) {
                orderedCutsetNodes.add(node);
            }
        }
        //System.out.println((orderedCutsetNodes.clone()).toString());
        return orderedCutsetNodes;
    }

    public Set initAll() {
        Set result = getUniqueInstantiation(cutsetNodes, new Hashtable(), new HashSet());
        int index = 0;
        InferenceResult marginals = null;
        for (Iterator resultIterator = result.iterator(); resultIterator.hasNext();) {
            Hashtable inst = (Hashtable) resultIterator.next();

            InferenceResult i = pearlObject.initNodesWithPredecessors(inst, cutsetNodes, baseInfoTable, index);
            if (index == 0) marginals = i; else marginals.add(i);
            index++;
        }
        marginals.normalize();
        System.out.println(marginals.toString());
        return result;

    }


    public InferenceResult inferWithEvidence(Set result) {
        int index = 0;
        InferenceResult marginals = null;
        for (Iterator resultIterator = result.iterator(); resultIterator.hasNext();) {
            Hashtable inst = (Hashtable) resultIterator.next();

            InferenceResult i = pearlObject.inferWithEvidence(inst, index);
            //pearlObject.temp(infoTable);
            //bnTable.put(new Integer(index),infoTable);
            if (index == 0) marginals = i; else marginals.add(i);
            index++;
        }
        marginals.normalize();
        //System.out.println(marginals.toString());
        return marginals;
    }

    public String getName() {
        return "Cutset Conditioning for Inference in Multiply Connected Network";
    }

    public InferenceResult getMarginals() {
        InferenceResult result = new InferenceResult();

        Hashtable evTable = network.getEvidenceTable();
        getNodesWithoutLoopCutsetPredecessors();
        Set res = initAll();
        if (evTable.size() > 0) {
            network.setEvidenceNodes(evTable);
            result = inferWithEvidence(res);
        }
        return result;
    }


    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.pearl.Pearl -i=inputfile [-e=evidencefile] [-o=outputfile] [-q]");
            System.out.println("-q = quiet mode");
            return;
        }

        if (!quiet) {
            System.out.println("Cutset Conditioning");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        Cutset cs = new Cutset(g);
        InferenceResult result = cs.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for Cutset Conditioning = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }


    }
}
