/*
 * Created on 16 Apr 2003
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
package edu.ksu.cis.bnj.bbn.inference.cutset;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.pearl.Pearl;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.*;

/**
 * @author Siddarth Chandak
 */
public class BoundedCutset {

    BBNGraph network;
    LinkedList cutsetNodes;
    Hashtable nodeTable;
    Hashtable cutsetTable;
    Hashtable bnTable;
    Pearl pearlObject;
    Hashtable baseInfoTable;
    List topologicalOrder;
    Hashtable oldIndexTable;
    ///////////////////////////
    AISBCC bridge;
    //////////////////////////
    protected double cutoffPercentage = 0.5;
    protected int maxInstantiationIndex = 0;

    protected Object lock = new Object();

    public BoundedCutset() {
    }

    public BoundedCutset(BBNGraph g) {
        network = g;

        CreateSplittingGraph graph = new CreateSplittingGraph(g);
        cutsetNodes = graph.go();
        nodeTable = graph.getNodetable();
        cutsetTable = graph.getCutsetTable();
        bnTable = new Hashtable();
        baseInfoTable = new Hashtable();
        oldIndexTable = new Hashtable();
        //network.removeEdge()
        //////////////////////////////////////
        //tempTest();
        bridge = new AISBCC(g);
        /////////////////////////////
        pearlObject = new Pearl();
        topologicalOrder = network.topologicalSort();
        System.out.println("We r out of  here ");

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

    /*  protected void m() {
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
        int cutsetNodeIndex = 0;
        int index = 0;
        InferenceResult marginals = null;
        for (Iterator resultIterator = result.iterator(); resultIterator.hasNext();) {
            Hashtable inst = (Hashtable) resultIterator.next();

            InferenceResult i = pearlObject.initNodesWithPredecessors(inst, cutsetNodes, baseInfoTable, index);
            //pearlObject.temp(infoTable);
            //bnTable.put(new Integer(index),infoTable);
            if (index == 0) marginals = i; else marginals.add(i);
            oldIndexTable.put(inst, new Integer(index));
            index++;
        }
        marginals.normalize();
        System.out.println(marginals.toString());
        return result;

    }

    public InferenceResult inferWithEvidence(Set result) {
        int index = 0;
        Hashtable jpd = pearlObject.getJointProbabilityTable();
        TreeSet sortedJPD = new TreeSet();

        for (Enumeration e = jpd.keys(); e.hasMoreElements();) {
            Hashtable inst = (Hashtable) e.nextElement();
            double weight = ((Double) jpd.get(inst)).doubleValue();
            sortedJPD.add(new JointProbEntry(inst, weight));
        }

        int iteration = sortedJPD.size();
        //iteration = 1;
        InferenceResult marginals = null;
        while (iteration > 10) {
            JointProbEntry e = (JointProbEntry) sortedJPD.last();
            sortedJPD.remove(e);
            Hashtable inst = (Hashtable) e.key;
            Integer oldIndex = (Integer) oldIndexTable.get(inst);
            InferenceResult i = pearlObject.inferWithEvidence(inst, oldIndex.intValue());
            if (index == 0) marginals = i; else marginals.add(i);
            //System.out.println(jpd.toString());
            pearlObject.finishedInstantiation(inst);

            //Sort --------here to get the next instantiation;
            index++;
            iteration--;
        }
        marginals.normalize();
        return marginals;
    }

    public InferenceResult sortAndInferUsingAIS() {
        Vector cutsetNodeNames = new Vector();
        TreeSet sortedJPD = new TreeSet();
        for (Iterator i = cutsetNodes.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            cutsetNodeNames.add(node.getLabel());
        }
        Hashtable jpd = pearlObject.getJointProbabilityTable();
        Hashtable aisJPD = bridge.getAISresult(cutsetNodeNames, 10000);
        for (Enumeration e = aisJPD.keys(); e.hasMoreElements();) {
            Hashtable inst = (Hashtable) e.nextElement();
            double weight = ((Double) aisJPD.get(inst)).doubleValue();
            sortedJPD.add(new JointProbEntry(inst, weight));
        }
        int iteration = sortedJPD.size();
        InferenceResult marginals = null;
        int index = 0;
        while (iteration > 10) {
            JointProbEntry e = (JointProbEntry) sortedJPD.last();
            sortedJPD.remove(e);
            Hashtable inst = (Hashtable) e.key;
            Integer oldIndex = (Integer) oldIndexTable.get(inst);
            InferenceResult i = pearlObject.inferWithEvidence(inst, oldIndex.intValue());
            if (index == 0) marginals = i; else marginals.add(i);
            //System.out.println(jpd.toString());
            pearlObject.finishedInstantiation(inst);

            //Sort --------here to get the next instantiation;
            index++;
            iteration--;
        }
        marginals.normalize();
        return marginals;

    }

    public InferenceResult getMarginals() {
        InferenceResult result = new InferenceResult();
        Hashtable evTable = network.getEvidenceTable();
        getNodesWithoutLoopCutsetPredecessors();
        Set res = initAll();
        if (evTable.size() > 0) {
            network.setEvidenceNodes(evTable);
            //result = inferWithEvidence(res);
            result = sortAndInferUsingAIS();
        }
        return result;
    }

    public Object getLock() {
        return lock;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        boolean quiet = params.getBool("-q");
        boolean runAIS = params.getBool("-a");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.pearl.Pearl -i=inputfile [-e=evidencefile] [-o=outputfile] [-q] [-a]");
            System.out.println("-q = quiet mode");
            return;
        }

        if (!quiet) {
            System.out.println("Bounded Cutset Conditioning");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        BoundedCutset bcs = new BoundedCutset(g);

        //AISBCC bridge = new AISBCC(g);

        bcs.cutoffPercentage = 0.5;

        InferenceResult result = bcs.getMarginals();

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
