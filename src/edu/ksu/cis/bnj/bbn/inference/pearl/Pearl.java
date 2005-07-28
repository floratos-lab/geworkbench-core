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
package edu.ksu.cis.bnj.bbn.inference.pearl;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.BBNValue;
import edu.ksu.cis.bnj.bbn.inference.ExactInference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.*;


/**
 * <P>Class Pearl : implements pearl algorithm to perform exact inference in polytrees
 * <P>Main methods : sendPiMessage, sendLambdaMessage, initializeRootPiValues, updateTree
 *
 * @author Siddarth Chandak
 */
public class Pearl extends ExactInference { // robbyjo's patch
    protected Hashtable infoTable;
    protected List order;
    protected Info nodeInfo[];
    protected BBNGraph graph;
    protected Hashtable evidenceNodes;
    protected Hashtable cutsetNodes; // for future use
    protected Hashtable hashOfHash[];
    protected Hashtable jointProbTable;
    protected InferenceResult marginalProbabilityTable;
    protected Hashtable ceTable;

    public Pearl() {
        // for later use;
        super(null); // robbyjo's patch
    }

    public Pearl(BBNGraph g) {
        super(g); // robbyjo's patch
        graph = g;
        order = g.topologicalSort(); // not necessary but helps to lessen complexity
        assert(order != null);
        nodeInfo = new Info[graph.getVerticesCount()];
        infoTable = new Hashtable();
        cutsetNodes = new Hashtable();

    }

    public double instantiateNode(BBNNode node, String value) {
        double prob = 1.0;
        Info cNodeInfo = (Info) infoTable.get(node.getLabel());
        //tempHashtable.put(node.getLabel(),(cNodeInfo.piValue).clone());
        //System.out.println(cNodeInfo.piMessage.toString());
        int index = cNodeInfo.getValueTable(value);
        prob = cNodeInfo.getProbabilityGivenEvidence(index);
        for (int arityIndex = 0; arityIndex < cNodeInfo.arity; arityIndex++) {
            cNodeInfo.setLambdaValue(arityIndex, 0.0);
            cNodeInfo.setPiValue(arityIndex, 0.0);
            cNodeInfo.setProbabilityGivenEvidence(arityIndex, 0.0);
        }

        cNodeInfo.setLambdaValue(index, 1.0);
        cNodeInfo.setPiValue(index, 1.0);
        cNodeInfo.setProbabilityGivenEvidence(index, 1.0);
        ///////////
        return prob;
    }


    public InferenceResult initNodesWithPredecessors(Hashtable csValues, LinkedList csNodes, Hashtable csInfoTable, int indexH) {
        infoTable = new Hashtable();
        double jointProb = 1.0;
        double updatedWeight = 1.0;
        //////////////////////////////////////////////////////////////////////
        InferenceResult updatedMarginals = null;
        //////////////////////////////////////////////////////////////////////

        for (Enumeration e = csInfoTable.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Info info = (Info) csInfoTable.get(key);
            infoTable.put(key, info.clone());
        }

        evidenceNodes.clear();
        for (Iterator i = csNodes.iterator(); i.hasNext();) {
            BBNNode cutsetNode = (BBNNode) i.next();
            String value;

            value = (String) csValues.get(cutsetNode.getLabel());
            if (value != null) {
                jointProb = jointProb * instantiateNode(cutsetNode, value);
                Info cNodeInfo = (Info) infoTable.get(cutsetNode.getLabel());
                //tempHashtable.put(cutsetNode.getLabel(),(cNodeInfo.piValue).clone());
                updatedWeight = jointProb;
            }


            /////////////////////////////////////////////////////////////////////////////////////////////////
            evidenceNodes.put(cutsetNode.getLabel(), graph.getNode(cutsetNode.getLabel()));
            //////////////////////////////////////////////////////////////////////
            List parent = cutsetNode.getParents();
            for (Iterator parentIterator = parent.iterator(); parentIterator.hasNext();) {
                BBNNode parentNode = (BBNNode) parentIterator.next();
                if (!(evidenceNodes.containsKey(parentNode.getLabel()))) {
                    sendLambdaMessage(cutsetNode, parentNode);
                }

            }

            List children = cutsetNode.getChildren();
            for (Iterator childIterator = children.iterator(); childIterator.hasNext();) {
                BBNNode childNode = (BBNNode) childIterator.next();
                if (!(evidenceNodes.containsKey(childNode.getLabel()))) {
                    sendPiMessage(cutsetNode, childNode, false);
                }


            }

        }
        jointProbTable.put(csValues, new Double(updatedWeight));
        //System.out.println(" " + jointProb);

        hashOfHash[indexH] = new Hashtable();
        for (Enumeration e = csInfoTable.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Info info = (Info) infoTable.get(key);
            hashOfHash[indexH].put(key, info.clone());
        }


        ////////////////////////////////////////////////////////////////////////
        updatedMarginals = multiplyByWeight(updatedWeight);
        ///////////////////////////////////////////////////////////////////////////

        return updatedMarginals;
    }

    public InferenceResult inferWithEvidence(Hashtable csValues, int indexH) {
        infoTable = new Hashtable();


        double piValue = 1.0;
        //////////////////////////////////////////////////////////////////////
        InferenceResult updatedMarginals = null;
        //////////////////////////////////////////////////////////////////////
        for (Enumeration e = hashOfHash[indexH].keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Info info = (Info) hashOfHash[indexH].get(key);
            infoTable.put(key, info.clone());
        }


        evidenceNodes.clear();
        Set evnodes = graph.getEvidenceNodes();
        Double jointProb = (Double) jointProbTable.get(csValues);
        double updatedWeight = jointProb.doubleValue();
        if (indexH == 0) {
            Hashtable priorsTable = new Hashtable();
            BBNNode evidenceNode = null;
            for (Iterator it = evnodes.iterator(); it.hasNext();) {
                evidenceNode = (BBNNode) it.next();
                String val = (String) evidenceNode.getEvidenceValue();
                if (cutsetNodes.containsKey(evidenceNode.getLabel())) {
                    ceTable.put(evidenceNode.getLabel(), val);
                }

                priorsTable.put(evidenceNode.getLabel(), val);

            }

            piValue = evidenceNode.query(priorsTable);
        }

        //System.out.println(csNodes.toString() + indexH + ceTable.toString());
        for (Iterator i = evnodes.iterator(); i.hasNext();) {
            BBNNode cutsetNode = (BBNNode) i.next();
            String value;

            value = (String) cutsetNode.getEvidenceValue();

            String evValue = (String) ceTable.get(cutsetNode.getLabel());
            if (evValue != null)
                if (!(evValue.equals(value))) {
                    updatedMarginals = addZeroProbability();
                    return updatedMarginals;
                }


            Info cNodeInfo = (Info) infoTable.get(cutsetNode.getLabel());
            int index = cNodeInfo.getValueTable(value);
            double evidenceVal = cNodeInfo.getProbabilityGivenEvidence(index);
            updatedWeight *= evidenceVal;
            instantiateNode(cutsetNode, value);

            //System.out.println(cutsetNode.getLabel()  + " " + value);
            //  jointProb = jointProb * instantiateNode(cutsetNode,value);

            /////////////////////////////////////////////////////////////////////////////////////////////////
            evidenceNodes.put(cutsetNode.getLabel(), graph.getNode(cutsetNode.getLabel()));
            //////////////////////////////////////////////////////////////////////
            List parent = cutsetNode.getParents();
            for (Iterator parentIterator = parent.iterator(); parentIterator.hasNext();) {
                BBNNode parentNode = (BBNNode) parentIterator.next();
                if (!(evidenceNodes.containsKey(parentNode.getLabel()))) {
                    sendLambdaMessage(cutsetNode, parentNode);
                }

            }

            List children = cutsetNode.getChildren();
            for (Iterator childIterator = children.iterator(); childIterator.hasNext();) {
                BBNNode childNode = (BBNNode) childIterator.next();
                if (!(evidenceNodes.containsKey(childNode.getLabel()))) {
                    sendPiMessage(cutsetNode, childNode, false);
                }


            }

        }

        ////////////////////////////////////////////////////////////////////////
        updatedMarginals = multiplyByWeight(updatedWeight / piValue);
        ///////////////////////////////////////////////////////////////////////////

        return updatedMarginals;

    }


    public void initializeForCutset(BBNGraph g, Hashtable nodesWithoutPredecessors, Hashtable nodesWithPredecessors, List topologicalOrder, Hashtable cutsetTable) {
        graph = g;
        order = topologicalOrder;
        nodeInfo = new Info[graph.getVerticesCount()];
        infoTable = new Hashtable();
        cutsetNodes = new Hashtable(cutsetTable.size());
        evidenceNodes = new Hashtable();
        hashOfHash = new Hashtable[500];
        jointProbTable = new Hashtable();
        //tempHashtable = new Hashtable(cutsetTable.size());
        //visitedTable = new Hashtable(topologicalOrder.size());
        ceTable = new Hashtable();

        marginalProbabilityTable = new InferenceResult();

        evidenceNodes = nodesWithPredecessors;
        for (Enumeration e = cutsetTable.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            cutsetNodes.put(key, cutsetTable.get(key));
        }

    }

    public Hashtable initNodesWithoutPredecessors() {

        setNodeInfoValues();
        initializeRootPiValues();
        Hashtable newInfo = new Hashtable();
        for (Enumeration e = infoTable.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Info info = (Info) infoTable.get(key);
            newInfo.put(key, info.clone());
        }

        return newInfo;
    }


    public void finishedInstantiation(Hashtable csValues) {
        jointProbTable.remove(csValues);
    }

    public Hashtable getJointProbabilityTable() {
        return jointProbTable;
    }

    public Info getNodeInfo(BBNNode v) {
        return (Info) infoTable.get(v.getLabel());
    }


    public int getNumberOfChildren(BBNNode currNode) {
        List childList = currNode.getChildren();
        if (childList != null)
            return childList.size();
        else
            return 0;
    }

    public int getNumberOfParents(BBNNode currNode) {
        List parentList = currNode.getParents();
        if (parentList != null)
            return parentList.size();
        else
            return 0;
    }

    public int checkForArity(BBNNode currNode) {
        BBNValue value = currNode.getValues();

        if (value instanceof BBNDiscreteValue) {
            BBNDiscreteValue dval = (BBNDiscreteValue) value;
            return dval.getArity();
        } else {
            System.out.println("Inference possible only for discrete nodes for Now");
            // Actually we need to exit from program if this is the case
            return -1;
        }
    }

    public Vector getParentsArity(BBNNode currNode) {
        Vector parentsArity = new Vector();
        List parentList = currNode.getParents();

        for (Iterator li = parentList.iterator(); li.hasNext();) {
            BBNNode parent = (BBNNode) li.next();
            BBNValue value = parent.getValues();

            if (value instanceof BBNDiscreteValue) {
                BBNDiscreteValue dval = (BBNDiscreteValue) value;
                if (dval.getArity() > 0) parentsArity.add(new Integer(dval.getArity()));
            } else {
                System.out.println("Inference possible only for discrete nodes for Now");
                // Actually we need to exit from program if this is the case
                return null;
            }
        }
        return parentsArity;
    }

    public void setCurrNodeValueTable(BBNNode currNode, Info nInfo) {
        BBNValue value = currNode.getValues();
        BBNDiscreteValue currDVal = (BBNDiscreteValue) value;
        int valueIndex = 0;

        for (Iterator it = currDVal.iterator(); it.hasNext();) {
            Object val = it.next();
            nInfo.setValueTable(val.toString(), valueIndex);
            valueIndex++;
        }
    }

    public void setCurrNodeParentIndex(BBNNode currNode, Info nInfo) {
        int parentindex = 0;
        List parentList = currNode.getParents();
        BBNNode parent;

        for (Iterator li = parentList.iterator(); li.hasNext();) {
            parent = (BBNNode) li.next();
            nInfo.setParentIndex(parent, parentindex);
            parentindex++;
        }
    }

    public void setCurrNodeChildIndex(BBNNode currNode, Info nInfo) {
        int childindex = 0;
        List childList = currNode.getChildren();
        BBNNode child;

        for (Iterator li = childList.iterator(); li.hasNext();) {
            child = (BBNNode) li.next();
            nInfo.setChildIndex(child, childindex);
            childindex++;
        }
    }

    void setRootPiValues(BBNNode currNode, Info nInfo, int numberofparents) {
        if (numberofparents == 0) {
            int ind = 0;
            BBNValue value = currNode.getValues();
            BBNDiscreteValue currDVal = (BBNDiscreteValue) value;

            for (Iterator it = currDVal.iterator(); it.hasNext();) {
                Object val = it.next();
                Hashtable priorsTable = new Hashtable();
                priorsTable.put(currNode.getLabel(), val.toString());
                double prior = currNode.query(priorsTable);
                nInfo.addPiValue(ind, prior);
                nInfo.addProbabilityGivenEvidence(ind, prior);
                //System.out.println("index " + ind + "prior " + prior);
                ind++;
            }
        }
    }

    public InferenceResult addZeroProbability() {
        BBNNode currNode;

        InferenceResult result = new InferenceResult();
        for (Iterator i = order.iterator(); i.hasNext();) {
            currNode = (BBNNode) i.next();
            Info currInfo = getNodeInfo(currNode);
            String nodeName = currNode.getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) currNode.getValues();

            Hashtable tbl = new Hashtable();
            for (Iterator j = currInfo.probabilityGivenEvidence.iterator(), k = dval.iterator(); j.hasNext();) {
                j.next();
                tbl.put(k.next().toString(), new Double(0.0));
            }
            result.put(nodeName, tbl);
        }
        return result;
    }

    public InferenceResult addProbability() {
        BBNNode currNode;

        InferenceResult result = new InferenceResult();
        for (Iterator i = order.iterator(); i.hasNext();) {
            currNode = (BBNNode) i.next();
            Info currInfo = getNodeInfo(currNode);
            String nodeName = currNode.getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) currNode.getValues();

            Hashtable tbl = new Hashtable();
            for (Iterator j = currInfo.probabilityGivenEvidence.iterator(), k = dval.iterator(); j.hasNext();) {
                tbl.put(k.next().toString(), j.next());
            }
            result.put(nodeName, tbl);
        }
        return result;
    }

    public void setNodeInfoValues() {
        //initialization function
        int numberofparents = 0, numberofchildren = 0, arity = 0, index = 0;
        BBNNode currNode;
        Vector parentsArity;

        for (Iterator orderIterator = order.iterator(); orderIterator.hasNext();) {
            currNode = (BBNNode) orderIterator.next();
            numberofchildren = getNumberOfChildren(currNode);
            numberofparents = getNumberOfParents(currNode);
            arity = checkForArity(currNode);
            parentsArity = getParentsArity(currNode);
            nodeInfo[index] = new Info(arity, parentsArity, numberofparents, numberofchildren);

            setCurrNodeValueTable(currNode, nodeInfo[index]);
            setCurrNodeParentIndex(currNode, nodeInfo[index]);
            setCurrNodeChildIndex(currNode, nodeInfo[index]);
            setRootPiValues(currNode, nodeInfo[index], numberofparents);

            infoTable.put(currNode.getLabel(), nodeInfo[index]);
            //visitedTable.put(currNode.getLabel(),new Boolean(false));
            index++;
        }
    }


    void initializeRootPiValues() {
        BBNNode currNode, child;
        BBNValue values;
        List rootNodes, childNodes;
        Iterator i, j;
        Hashtable priorsTable = new Hashtable();
        rootNodes = graph.getRoot();

        for (Iterator li = rootNodes.iterator(); li.hasNext();) {
            currNode = (BBNNode) li.next();
            childNodes = currNode.getChildren();
            for (j = childNodes.iterator(); j.hasNext();) {
                child = (BBNNode) j.next();
                sendPiMessage(currNode, child, true);
            }
        }
    }


    public InferenceResult multiplyByWeight(double weight) {
        BBNNode currNode;
        InferenceResult result = new InferenceResult();
        for (Iterator i = order.iterator(); i.hasNext();) {
            currNode = (BBNNode) i.next();
            //if(!(evidenceNodes.contains(currNode)))
            //{
            Info currInfo = getNodeInfo(currNode);
            String nodeName = currNode.getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) currNode.getValues();

            Hashtable tbl = new Hashtable();
            for (Iterator j = currInfo.probabilityGivenEvidence.iterator(), k = dval.iterator(); j.hasNext();) {
                Double marginal = (Double) j.next();
                //System.out.println("marginal " + marginal);
                double weightXmarginal = marginal.doubleValue() * weight;
                //System.out.println("weightXmarginal " + weightXmarginal);
                tbl.put(k.next().toString(), new Double(weightXmarginal));
            }
            result.put(nodeName, tbl);
            //}
        }
        return result;
    }


    void calculatePiMessage(BBNNode parent, BBNNode child) {
        Info parentInfo = (Info) infoTable.get(parent.getLabel());
        List childNodes = parent.getChildren();
        int childindex = parentInfo.getChildIndex(child);

        double lambdaProduct = 1.0;
        double totalValue = 1.0;

        for (int i = 0; i < parentInfo.arity; i++) {
            for (Iterator childNodesIterator = childNodes.iterator(); childNodesIterator.hasNext();) {
                BBNNode currChild = (BBNNode) childNodesIterator.next();

                if (!(currChild.equals(child))) {
                    Info currChildInfo = (Info) infoTable.get(currChild.getLabel());
                    int parentindex = currChildInfo.getParentIndex(parent);
                    //System.out.println("parentindex" + parentindex);
                    lambdaProduct *= currChildInfo.getLambdaMessage(parentindex, i);
                }
            }
            totalValue = parentInfo.getPiValue(i) * lambdaProduct;
            lambdaProduct = 1.0;
            parentInfo.setPiMessage(childindex, i, totalValue);
        }
    }

    void updatePiValue(BBNNode parent, BBNNode child, boolean init) {
        Info childInfo = (Info) infoTable.get(child.getLabel());
        List parentNodes = child.getParents();
        LinkedList nodes = new LinkedList();
        nodes.addAll(parentNodes);

        Set result = getUniqueInstantiation(nodes, new Hashtable(), new HashSet());
        double probability = 1.0, piProduct = 1.0;
        double summationOverAllParents = 0.0;
        double totalProbabilityGivenEvidence = 0.0;
        BBNValue childValues = child.getValues();
        BBNDiscreteValue discreteChildVal = (BBNDiscreteValue) childValues;
        int childArityIndex = 0;

        for (Iterator discreteChildValIterator = discreteChildVal.iterator(); discreteChildValIterator.hasNext();) { //--for each value of child
            Object val = discreteChildValIterator.next();
            //System.out.println("result size " + result.size());

            for (Iterator i = result.iterator(); i.hasNext();) { //-- for the summation
                Hashtable inst = (Hashtable) i.next();
                inst.put(child.getLabel(), val.toString());
                probability = child.getCPF().query(inst);
                for (Iterator k = parentNodes.iterator(); k.hasNext();) { //-- for the product of Pi messages of the parents to the child
                    BBNNode currParent = (BBNNode) k.next();
                    String value = (String) inst.get(currParent.getLabel());
                    Info currParentInfo = (Info) infoTable.get(currParent.getLabel());
                    int arityIndex = currParentInfo.getValueTable(value);
                    int currchildindex = currParentInfo.getChildIndex(child);
                    piProduct *= currParentInfo.getPiMessage(currchildindex, arityIndex);
                }
                summationOverAllParents += probability * piProduct;
                piProduct = 1.0;
            }

            childInfo.setPiValue(childArityIndex, summationOverAllParents);
            double probValue = summationOverAllParents * childInfo.getLambdaValue(childArityIndex);

            childInfo.setProbabilityGivenEvidence(childArityIndex, probValue);
            totalProbabilityGivenEvidence += probValue;
            summationOverAllParents = 0.0;
            childArityIndex++;
        }

        int cArityIndex = 0;
        for (Iterator discreteChildValIterator = discreteChildVal.iterator(); discreteChildValIterator.hasNext();) {
            Object val = discreteChildValIterator.next();
            double tempProbability = childInfo.getProbabilityGivenEvidence(cArityIndex) / totalProbabilityGivenEvidence;
            childInfo.setProbabilityGivenEvidence(cArityIndex, tempProbability);
            cArityIndex++;
        }
    }

    void sendPiMessage(BBNNode parent, BBNNode child, boolean init) {
        calculatePiMessage(parent, child);
        if (!(evidenceNodes.containsKey(child.getLabel()))) {

            updatePiValue(parent, child, init);
            propagatePiMessage(child, init);
        }

        if (init == false) {
            List allParents = child.getParents();
            for (Iterator allParentIterator = allParents.iterator(); allParentIterator.hasNext();) {
                BBNNode currParent = (BBNNode) allParentIterator.next();
                if (!(currParent.equals(parent))) {
                    if (!(evidenceNodes.containsKey(currParent.getLabel()))) {
                        if (!(cutsetNodes.containsKey(child.getLabel()))) {
                            sendLambdaMessage(child, currParent);
                        }
                    }
                }
            }
        }
    }

    void sendLambdaMessage(BBNNode child, BBNNode parent) {


        Info childInfo = (Info) infoTable.get(child.getLabel());
        Info parentInfo = (Info) infoTable.get(parent.getLabel());
        BBNValue parentValues = parent.getValues();
        BBNValue childValues = child.getValues();
        BBNDiscreteValue parentVal = (BBNDiscreteValue) parentValues;
        BBNDiscreteValue childVal = (BBNDiscreteValue) childValues;

        int parentIndex = childInfo.getParentIndex(parent);
        List parentNodes = child.getParents();
        LinkedList nodes = new LinkedList();

        for (Iterator j = parentNodes.iterator(); j.hasNext();) {
            BBNNode currParent = (BBNNode) j.next();
            if (!(currParent.equals(parent))) {
                nodes.add(currParent);
                //System.out.println("parent added " + currParent.getLabel());
            }
        }

        Set result;
        if (!nodes.isEmpty())
            result = getUniqueInstantiation(nodes, new Hashtable(), new HashSet());
        else {
            result = null;
        }
        List childNodes = parent.getChildren();

        double probability = 0, piProduct = 1.0;
        double summationOverParentInstantiations = 0;
        double summationOverChildValues = 0;
        int parentArityIndex = 0;
        double totalProbabilityGivenevidence = 0.0;

        for (Iterator parentIterator = parentVal.iterator(); parentIterator.hasNext();) {
            Object parentValue = parentIterator.next();
            int childIndex = 0;

            for (Iterator childIterator = childVal.iterator(); childIterator.hasNext();) {
                Object childValue = childIterator.next();
                if (result != null) {
                    for (Iterator uniqueInstantiationIterator = result.iterator(); uniqueInstantiationIterator.hasNext();) { //-- for the summation
                        Hashtable inst = (Hashtable) uniqueInstantiationIterator.next();
                        inst.put(parent.getLabel(), parentValue.toString());
                        inst.put(child.getLabel(), childValue.toString());
                        probability = child.getCPF().query(inst);
                        //System.out.println(inst.toString() + " probability" + probability);

                        for (Iterator parentNodesIterator = nodes.iterator(); parentNodesIterator.hasNext();) { //-- for the product of Pi messages of the parents to the child
                            BBNNode currParent = (BBNNode) parentNodesIterator.next();
                            String value = (String) inst.get(currParent.getLabel());
                            Info currParentInfo = (Info) infoTable.get(currParent.getLabel());
                            int arityIndex = currParentInfo.getValueTable(value);
                            int currchildindex = currParentInfo.getChildIndex(child);
                            piProduct *= currParentInfo.getPiMessage(currchildindex, arityIndex);

                        }

                        summationOverParentInstantiations += probability * piProduct;
                        piProduct = 1.0;
                        //System.out.println("summationOverParentInstantiations" + summationOverParentInstantiations);
                    }
                } else {
                    Hashtable inst = new Hashtable();
                    inst.put(parent.getLabel(), parentValue.toString());
                    inst.put(child.getLabel(), childValue.toString());
                    probability = child.getCPF().query(inst);
                    summationOverParentInstantiations = probability;
                }

                summationOverChildValues += childInfo.getLambdaValue(childIndex) * summationOverParentInstantiations;
                summationOverParentInstantiations = 0;
                childIndex++;
            }

            //System.out.println("summationOverChildValues" + summationOverChildValues);
            childInfo.setLambdaMessage(parentIndex, parentArityIndex, summationOverChildValues);
            summationOverChildValues = 0;

            double lambdaProduct = calculateLambdaValue(childNodes, parentArityIndex, parent);

            parentInfo.setLambdaValue(parentArityIndex, lambdaProduct);
            double tempProduct = lambdaProduct * parentInfo.getPiValue(parentArityIndex);
            parentInfo.setProbabilityGivenEvidence(parentArityIndex, tempProduct);
            totalProbabilityGivenevidence += tempProduct;
            parentArityIndex++;
        }

        int pArityIndex = 0;
        for (Iterator parentIterator = parentVal.iterator(); parentIterator.hasNext();) {
            Object parentValue = parentIterator.next();
            double temp = parentInfo.getProbabilityGivenEvidence(pArityIndex) / totalProbabilityGivenevidence;
            parentInfo.setProbabilityGivenEvidence(pArityIndex, temp);
            pArityIndex++;
        }


        List grandParents = parent.getParents();

        for (Iterator grandParentIterator = grandParents.iterator(); grandParentIterator.hasNext();) {
            BBNNode grandParent = (BBNNode) grandParentIterator.next();

            if (!(evidenceNodes.containsKey(grandParent.getLabel()))) {
                if (!(cutsetNodes.containsKey(parent.getLabel()))) {
                    sendLambdaMessage(parent, grandParent);
                }
            }
        }

        List siblings = parent.getChildren();
        for (Iterator siblingIterator = siblings.iterator(); siblingIterator.hasNext();) {
            BBNNode sibling = (BBNNode) siblingIterator.next();
            if (!(sibling.equals(child))) {
                if (!(cutsetNodes.containsKey(parent.getLabel()))) {
                    sendPiMessage(parent, sibling, false);
                }
            }
        }
    }

    void propagatePiMessage(BBNNode node, boolean init) {
        List children = node.getChildren();
        for (Iterator childIterator = children.iterator(); childIterator.hasNext();) {
            BBNNode child = (BBNNode) childIterator.next();
            if (!(cutsetNodes.containsKey(node.getLabel()))) {
                sendPiMessage(node, child, init);
            }
        }
    }

    double calculateLambdaValue(List childNodes, int parentArityIndex, BBNNode parent) {
        double lambdaProduct = 1.0;

        for (Iterator childNodeIterator = childNodes.iterator(); childNodeIterator.hasNext();) {
            BBNNode currChild = (BBNNode) childNodeIterator.next();
            Info currChildInfo = (Info) infoTable.get(currChild.getLabel());
            int currIndex = currChildInfo.getParentIndex(parent);
            lambdaProduct *= currChildInfo.getLambdaMessage(currIndex, parentArityIndex);
        }
        return lambdaProduct;
    }

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

    // robbyjo's patch for standard compliance
    public String getName() {
        return "Pearl's Algorithm for Singly Connected Network";
    }

    // robbyjo's patch for standard compliance
    public InferenceResult getMarginals() {
        InferenceResult result = new InferenceResult();
        BBNNode currNode;
        Hashtable evTable = graph.getEvidenceTable();
        graph.clearEvidenceNodes();

        setNodeInfoValues();
        initializeRootPiValues();
        if (evTable.size() > 0) graph.setEvidenceNodes(evTable);
        //updateTree(null,-1);

        for (Iterator i = order.iterator(); i.hasNext();) {
            currNode = (BBNNode) i.next();
            Info currInfo = getNodeInfo(currNode);
            String nodeName = currNode.getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) currNode.getValues();

            Hashtable tbl = new Hashtable();
            for (Iterator j = currInfo.probabilityGivenEvidence.iterator(), k = dval.iterator(); j.hasNext();) {
                tbl.put(k.next().toString(), j.next());
            }
            result.put(nodeName, tbl);
        }
        return result;
    }

    // robbyjo's patch for standard compliance
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
            System.out.println("Pearl's Algorithm");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        Pearl pearl = new Pearl(g);
        InferenceResult result = pearl.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for Pearl = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }

        /*
        BBNGraph g = BBNGraph.load(args[0]);
        Pearl p = new Pearl(g);
        p.setNodeInfoValues();
        p.initializeRootPiValues();
        if (args.length > 1) g.loadEvidence(args[1]);
        p.updateTree();
        p.printNodeInfo();
        */
    }
}



