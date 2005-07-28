package edu.ksu.cis.bnj.bbn.inference.elimbel;

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

import edu.ksu.cis.bnj.bbn.*;
import edu.ksu.cis.bnj.bbn.inference.ExactInference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.graph.MaxCardinalitySearch;

import java.util.*;

/**
 * Elim-Bel Implementation.
 *
 * @author Roby Joehanes
 */
public class ElimBel extends ExactInference {

    protected List order = null;

    protected BBNCPF[] lambda;
    protected BBNNode[] nodes;
    protected Set[] lambdaParameters;
    protected Set[] lambdaTable;
    protected List[] baseNodes;
    protected Hashtable indexCache;
    protected int nodeCount;
    protected InferenceResult result;

    public ElimBel() {
    }

    /**
     * Constructor for ElimBel.
     *
     * @param g
     */
    public ElimBel(BBNGraph g) {
        super(g);
    }

    public String getName() {
        return "Variable Elimination";
    }

    /**
     * @return List
     */
    public List getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order The order to set
     */
    public void setOrder(List order) {
        this.order = order;
    }

    /**
     * Manual topological sort...
     * OpenJGraph already provide one
     */
    protected List topologicalSort() {
        LinkedList order = new LinkedList();
        HashSet knownNodes = new HashSet();
        LinkedList nextNodes = new LinkedList();

        for (Iterator i = graph.getNodes().iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            if (node.getChildren().size() == 0) {
                nextNodes.addAll(node.getParents());
                knownNodes.add(node);
                order.addFirst(node);
            }
        }

        while (nextNodes.size() > 0) {
            BBNNode node = (BBNNode) nextNodes.removeFirst();
            if (knownNodes.contains(node)) continue;

            List children = node.getChildren();
            if (knownNodes.containsAll(children)) {
                order.addFirst(node);
                nextNodes.addAll(node.getParents());
                knownNodes.add(node);
            }
        }
        return order;
    }

    protected List sort() {
        MaxCardinalitySearch search = new MaxCardinalitySearch(graph);
        return search.getOrder();
    }

    public InferenceResult getMarginals() {
        result = new InferenceResult();

        // If no given ordering, assume topological sort order
        if (order == null) order = sort();
        //if (order == null) order = topologicalSort();

        //System.out.println("Order = "+order);

        // Pre-process
        nodeCount = order.size();
        nodes = new BBNNode[nodeCount];
        lambda = new BBNCPF[nodeCount];
        lambdaParameters = new Set[nodeCount];
        lambdaTable = new Set[nodeCount];
        baseNodes = new List[nodeCount];
        indexCache = new Hashtable();

        int idx = 0;
        for (Iterator i = order.iterator(); i.hasNext(); idx++) {
            BBNNode node = nodes[idx] = (BBNNode) i.next();
            indexCache.put(node, new Integer(idx));
            lambdaParameters[idx] = new HashSet();
            lambdaTable[idx] = new HashSet();
            baseNodes[idx] = new LinkedList();
        }

        //System.out.println("Backward phase");
        //System.out.println("Processing bucket#");
        backwardPhase();
        //System.out.println();
        //System.out.println("Forward phase");
        forwardPhase();

        return result;
    }

    /**
     * Backward phase of variable elimination. This involves constructing
     * buckets and building lambda functions (or, rather, lambda table)
     */
    protected void backwardPhase() {
        HashSet seenBefore = new HashSet();

        for (int i = nodeCount - 1; i >= 0; i--) {
            BBNNode node = nodes[i];
            //System.out.print(i+": "+node+" = ");

            if (!seenBefore.contains(node)) {
                seenBefore.add(node);
                baseNodes[i].add(node);
                lambdaParameters[i].add(node);

                //System.out.print("P("+node);

                /*
                 * Adding the parents to be included into the lambda parameter
                 */
                List parents = node.getParents();
                if (parents != null) {
                    //System.out.print("|");
                    for (Iterator j = parents.iterator(); j.hasNext();) {
                        BBNNode parent = (BBNNode) j.next();
                        lambdaParameters[i].add(parent);
                        //System.out.print(parent.toString());
                        //if (j.hasNext()) System.out.print(", ");
                    }
                }
                //System.out.print(")");
            }

            /*
             * Looking for P(child | curNode, otherParents...) to be included in the
             * current bucket
             */
            for (Iterator j = node.getChildren().iterator(); j.hasNext();) {
                BBNNode child = (BBNNode) j.next();
                if (!seenBefore.contains(child)) {
                    //System.out.print(" P("+child);
                    seenBefore.add(child);
                    baseNodes[i].add(child);
                    lambdaParameters[i].add(child);

                    List childParents = child.getParents();
                    if (childParents != null) {
                        //System.out.print("|");
                        for (Iterator k = child.getParents().iterator(); k.hasNext();) {
                            BBNNode childParent = (BBNNode) k.next();
                            lambdaParameters[i].add(childParent);
                            //System.out.print(childParent.toString());
                            //System.out.print(", ");
                        }
                    }
                    //System.out.print(")");
                }
            }

            assert (lambdaParameters[i].size() > 0);

            //            for (Iterator j = lambdaTable[i].iterator(); j.hasNext(); ) {
            //                int idx = ((Integer) j.next()).intValue();
            //                System.out.print(" lambda_"+nodes[idx]+"(");
            //                for (Iterator k = lambdaParameters[idx].iterator(); k.hasNext(); ) {
            //                    BBNNode n = (BBNNode) k.next();
            //                    if (n != nodes[idx]) System.out.print(n+",");
            //                }
            //                System.out.print(")");
            //            }
            //            System.out.println();

            // Build the lambda
            LinkedList nodeList = new LinkedList();
            LinkedList nodeNameList = new LinkedList();
            for (Iterator j = lambdaParameters[i].iterator(); j.hasNext();) {
                BBNNode param = (BBNNode) j.next();
                nodeList.add(param);
                nodeNameList.add(param.getLabel());
            }
            lambda[i] = new BBNCPF(nodeNameList);
            nodeNameList = null; // we don't use this, so let it be garbage collected
            //System.out.print("lambda parameters "+nodeList);

            buildLambda(i, nodeList, new Hashtable());

            // Do this except the lowest bucket
            if (i > 0) {
                // Find the highest bucket index that is lower than the current bucket
                int highestBucketIndex = -1;
                for (Iterator j = nodeList.iterator(); j.hasNext();) {
                    Object param = j.next();
                    int idx = ((Integer) indexCache.get(param)).intValue();
                    if (idx < i && idx > highestBucketIndex) {
                        highestBucketIndex = idx;
                    }
                }
                if (highestBucketIndex > -1) {
                    lambdaTable[highestBucketIndex].add(new Integer(i));
                    // make this node's lambda parameters as the highest bucket's parameters
                    // except this current node.
                    nodeList.remove(node);
                    lambdaParameters[highestBucketIndex].addAll(nodeList);

                    //System.out.print(", to bucket "+highestBucketIndex);
                }
            }
            //System.out.println();
        }
    }

    protected void buildLambda(int curIndex, LinkedList nodeList, Hashtable curInst) {
        BBNNode node = (BBNNode) nodeList.removeFirst();
        BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();

        for (Iterator i = dval.iterator(); i.hasNext();) {
            Object value = i.next();
            curInst.put(node.getLabel(), value.toString());
            if (nodeList.size() == 0) {
                double result = 1.0;
                for (Iterator j = baseNodes[curIndex].iterator(); j.hasNext();) {
                    BBNNode base = (BBNNode) j.next();
                    double p;
                    if (base.isEvidence() && base.getEvidenceValue().equals(curInst.get(base.getLabel()))) {
                        p = base.getCPF().query(curInst);
                    } else {
                        p = base.query(curInst);
                    }

                    result *= p;
                }

                for (Iterator j = lambdaTable[curIndex].iterator(); j.hasNext();) {
                    int idx = ((Integer) j.next()).intValue();
                    double p = lambda[idx].query(curInst);
                    result *= p;
                }

                lambda[curIndex].put((Hashtable) curInst.clone(), new BBNConstant(result));
            } else {
                buildLambda(curIndex, nodeList, curInst);
            }
        }
        nodeList.addFirst(node);
    }

    protected void forwardPhase() {
        for (int i = 0; i < nodeCount; i++) {

            // See if we have lambda function placed upon
            for (Iterator j = lambdaTable[i].iterator(); j.hasNext();) {
                int idx = ((Integer) j.next()).intValue();
                //System.out.println("("+i+"x"+idx+")");
                HashSet sepset = new HashSet();
                sepset.addAll(lambdaParameters[idx]);
                sepset.retainAll(lambdaParameters[i]);
                if (sepset.size() > 0) {
                    LinkedList sepsetNames = new LinkedList();
                    for (Iterator k = sepset.iterator(); k.hasNext();) {
                        BBNNode n = (BBNNode) k.next();
                        sepsetNames.add(n.getLabel());
                    }

                    BBNCPF lambdaDivisor = lambda[idx].extract(sepsetNames);
                    lambda[idx].divide(lambdaDivisor);
                    lambda[idx].multiply(lambda[i]);
                }
            }

            // Construct the result table (which is a simple marginalization
            Hashtable tbl = new Hashtable();
            Hashtable q = new Hashtable();
            String nodeName = nodes[i].getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) nodes[i].getValues();
            double total = 0.0;
            for (Iterator j = dval.iterator(); j.hasNext();) {
                String val = (String) j.next();
                q.put(nodeName, val);
                double p = lambda[i].normalizedQuery(q);
                total += p;
                tbl.put(val, new Double(p));
            }

            // renormalize... (Somehow the normalizedQuery doesn't work for lambda with only one node)
            for (Iterator j = dval.iterator(); j.hasNext();) {
                String val = (String) j.next();
                double p = ((Double) tbl.get(val)).doubleValue();
                tbl.put(val, new Double(p / total));
            }

            result.put(nodeName, tbl);
        }
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.elimbel.ElimBel -i:inputfile [-e:evidencefile] [-o:outputfile] [-q]");
            System.out.println("-q = quiet mode");
            return;
        }

        if (!quiet) {
            System.out.println("Variable Elimination");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        ElimBel bel = new ElimBel(g);
        InferenceResult result = bel.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for ElimBel = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }
    }
}
