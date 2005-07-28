package edu.ksu.cis.bnj.bbn.datagen;

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
 *
 */

import edu.ksu.cis.bnj.bbn.*;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.analysis.DataAnalyzer;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.data.converter.arff.ArffParser;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.Settings;

import java.util.*;

/**
 * @author Prashanth Boddhireddy
 *         This class generates flat data from specified parameters and structure.
 *         The generation of data is similar to forward sampling. *
 *         Input: network.xml, numberOfSamples
 *         Output:samples in the form of xml or plain data
 *         Additional Notes: Samples, tuples mean the same
 */

public class ForwardSampling implements DataGenerator {
    private static boolean DEBUG = false;
    protected BBNGraph mBBNGraph;   // graph we are working
    private Hashtable mNodeToIntMap = new Hashtable(); // helps in mapping node to int
    private Table tuples;  // attrinute names + tuples
    private List mNetworkNodes; //contains all node objects
    Hashtable nodeName2Value;

    public ForwardSampling(BBNGraph g) {
        mBBNGraph = g;
    }

    public String getName() {
        return "Forward Sampling";
    }

    public Table generateData(int noOfSamples) {
        Hashtable evTable = mBBNGraph.getEvidenceTable();
        mNetworkNodes = mBBNGraph.getNodeList();

        mNodeToIntMap = new Hashtable();
        tuples = new Table();
        tuples.setName(mBBNGraph.getName());
        int nodeIndex = 0;
        List attributes = new LinkedList();
        for (Iterator i = mNetworkNodes.iterator(); i.hasNext(); nodeIndex++) {
            BBNNode node = (BBNNode) i.next();
            mNodeToIntMap.put(node, new Integer(nodeIndex));
            Attribute attribute = new Attribute(node.getLabel());
            BBNValue values = node.getValues();
            if (values instanceof BBNDiscreteValue) {
                BBNDiscreteValue dval = (BBNDiscreteValue) values;
                for (Iterator j = dval.iterator(); j.hasNext();) {
                    Object val = j.next();
                    attribute.addValue(val.toString());
                }
            } else {
                attribute.setType(Attribute.REAL);
            }

            tuples.addAttribute(attribute);
            attributes.add(attribute);
        }

        for (int sampleIndex = 0; sampleIndex < noOfSamples; sampleIndex++) {
            mBBNGraph.clearEvidenceNodes();
            mBBNGraph.setEvidenceTable(evTable);
            nodeName2Value = new Hashtable();
            instantiateNetwork();
            Tuple mCurrentTuple = new Tuple();
            for (Iterator i = attributes.iterator(); i.hasNext();) {
                Attribute attr = (Attribute) i.next();
                mCurrentTuple.addValue(nodeName2Value.get(attr.getName()));
            }
            tuples.addTuple(mCurrentTuple);
        }

        mBBNGraph.setEvidenceTable(evTable);

        if (DEBUG)
            System.out.println(tuples);
        return tuples;
    }

    private void instantiateNetwork() {
        for (Iterator i = mNetworkNodes.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            if (!node.isEvidence())
                instantiateNodeState(node);
        }
    }

    private void instantiateNodeState(BBNNode node) {
        if (!node.isEvidence()) {
            List parentNodes = (List) node.getParents();
            for (Iterator i = parentNodes.iterator(); i.hasNext();)
                instantiateNodeState((BBNNode) i.next());
            setEvidence(node);
        }
    }

    private void setEvidence(BBNNode node) {
        int nodeIndex = ((Integer) mNodeToIntMap.get(node)).intValue();
        Hashtable cpfValues = node.getCPF().getTable();
        BBNDiscreteValue stateValues = (BBNDiscreteValue) node.getValues();
        double cummulativeCPT[] = new double[stateValues.size()];
        if ((node.getParents()).size() == 0)
            setEvidenceToRootNodes(node, nodeIndex, cpfValues, cummulativeCPT, stateValues);
        else
            setEvidenceToNodes(node, nodeIndex, cummulativeCPT, stateValues);
    }


    private void setEvidenceToRootNodes(BBNNode node, int nodeIndex, Hashtable cpfValues, double[] cummulativeCPT, BBNDiscreteValue stateValues) {
        int probIndex = 0;
        double randomValue = Settings.random.nextDouble();
        for (Enumeration e = cpfValues.keys(); e.hasMoreElements(); probIndex++) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNConstant val = (BBNConstant) cpfValues.get(q);
            cummulativeCPT[probIndex] = val.getValue();
            if (probIndex > 0)
                cummulativeCPT[probIndex] += cummulativeCPT[probIndex - 1];
        }
        setEvidenceValue(node, nodeIndex, randomValue, stateValues, cummulativeCPT);
    }

    private void setEvidenceToNodes(BBNNode node, int nodeIndex, double[] cummulativeCPT, BBNDiscreteValue stateValues) {
        double randomValue = Settings.random.nextDouble();
        Hashtable parentEvidenceSet = node.getParentsEvidenceSet();
        List columnProbs = node.queryColumn(parentEvidenceSet);
        int probIndex = 0;
        int length = columnProbs.size();
        for (Iterator i = columnProbs.iterator(); i.hasNext(); probIndex++) {
            cummulativeCPT[probIndex] = ((Double) i.next()).doubleValue();
            if (probIndex > 0)
                cummulativeCPT[probIndex] += cummulativeCPT[probIndex - 1];
        }
        setEvidenceValue(node, nodeIndex, randomValue, stateValues, cummulativeCPT);
    }

    private void setEvidenceValue(BBNNode node, int nodeIndex, double randomValue, BBNDiscreteValue stateValues, double[] cummulativeCPT) {
        int probIndex = 0;
        for (Iterator i = stateValues.iterator(); i.hasNext(); probIndex++, i.next()) {
            if (randomValue <= cummulativeCPT[probIndex]) {
                String stateValue = (String) i.next();
                node.setEvidenceValue(stateValue);
                nodeName2Value.put(node.getLabel(), stateValue);
                //mCurrentTuple.addValue(stateValue);
                break;
            }
        }
    }

    public static void main(String[] args) {
        int defaultMaxIteration = 1000;
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        int numIter = params.getInt("-n", defaultMaxIteration);
        boolean quiet = params.getBool("-q");
        boolean rmse = params.getBool("-r");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.datagen.ForwardSampling -i:inputfile [-e:evidencefile] [-o:outputfile] [-q] [-n:numdata]");
            System.out.println("-q = quiet mode");
            System.out.println("-n = number of data generated, default = " + defaultMaxIteration);
            System.out.println("-r = calculate RMSE error");
            return;
        }

        if (!quiet) {
            System.out.println("Forward Sampling");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;


        BBNGraph graph = BBNGraph.load(inputFile);
        if (evidenceFile != null) graph.loadEvidence(evidenceFile);

        LS ls = new LS(graph);
        InferenceResult actualMarginals = ls.getMarginals();

        long origTime = System.currentTimeMillis();

        ForwardSampling dg = new ForwardSampling(graph);
        Table tuples = dg.generateData(numIter);

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        System.out.println("Memory needed for Forward Sampling = " + freemem);
        System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));

        if (rmse) {
            DataAnalyzer analyzer = new DataAnalyzer(tuples);
            InferenceResult dataMarginals = analyzer.getDataMarginals();
            double rr = actualMarginals.computeRMSE(dataMarginals);
            //            dataMarginals.save(System.out);
            //            actualMarginals.save(System.out);
            System.out.println("RMSE = " + rr);
        }

        if (outputFile != null) {
            tuples.save(outputFile);
        } else {
            Database db = new Database();
            db.addTable(tuples);
            new ArffParser().save(System.out, db);
        }

    }
}
