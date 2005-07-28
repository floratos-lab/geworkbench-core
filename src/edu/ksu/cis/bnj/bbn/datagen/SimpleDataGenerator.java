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
 */

import edu.ksu.cis.bnj.bbn.*;
import edu.ksu.cis.bnj.bbn.inference.Inference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.analysis.DataAnalyzer;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.Settings;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Data Generator class
 *
 * @author Roby Joehanes
 */
public class SimpleDataGenerator implements DataGenerator {

    protected Inference inf;
    protected BBNGraph graph;

    protected double threshold = 0.0;

    /**
     * Constructor for DataGenerator.
     */
    public SimpleDataGenerator(Inference i) {
        graph = i.getGraph();
        setInference(i);
    }

    public String getName() {
        return "Inference Based Data Generator";
    }

    /**
     * Returns the inference.
     *
     * @return Inference
     */
    public Inference getInference() {
        return inf;
    }

    /**
     * Sets the inference.
     *
     * @param inf The inf to set
     */
    public void setInference(Inference inf) {
        this.inf = inf;
    }

    /**
     * Data Generator
     *
     * @see edu.ksu.cis.bnj.bbn.datagen.DataGenerator#generateData(int)
     */
    public Table generateData(int howmuch) {
        Table data = new Table();
        graph = inf.getGraph();
        data.setName(graph.getName());
        boolean allDiscrete = true;

        // Add the attributes
        LinkedList nodes = new LinkedList();
        nodes.addAll(graph.getNodes());
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            Attribute attr = new Attribute(node.getLabel());
            BBNValue values = node.getValues();
            if (values instanceof BBNDiscreteValue) {
                for (Iterator j = ((BBNDiscreteValue) values).iterator(); j.hasNext();) {
                    attr.addValue(j.next().toString());
                }
            } else {
                attr.setType(Attribute.REAL);
                allDiscrete = false;
            }
            data.addAttribute(attr);
        }

        InferenceResult marginals = inf.getMarginals();
        Hashtable result = processInferenceResult(nodes, new LinkedList(), 1.0, howmuch, marginals, new Hashtable());

        // Preprocess the result (i.e. setting up the roulette)
        int max = result.size(), i = 0;
        assert (max > 0);
        LinkedList[] candidates = new LinkedList[max];
        double[] probs = new double[max];

        for (Enumeration e = result.keys(); e.hasMoreElements(); i++) {
            candidates[i] = (LinkedList) e.nextElement();
            probs[i] = ((Double) result.get(candidates[i])).doubleValue();
            if (i > 0) probs[i] += probs[i - 1];
        }

        // Now generate the data
        for (i = 0; i < howmuch; i++) {
            double p = Settings.random.nextDouble(); // roll the roulete
            LinkedList chosen = candidates[max - 1];
            for (int j = 0; j < max; j++) { // see where it falls
                if (p < probs[j]) {
                    chosen = candidates[j];
                    break;
                }
            }

            if (allDiscrete) {
                data.addTuple(new Tuple(chosen));
            } else {  // There's some nodes with continuous values
                LinkedList realContent = new LinkedList();
                int idx = 0;
                for (Iterator j = chosen.iterator(); j.hasNext(); idx++) {
                    Object val = j.next();
                    if (val instanceof String) {
                        realContent.add(val);
                    } else {
                        BBNNode node = (BBNNode) nodes.get(idx);
                        BBNContinuousValue value = (BBNContinuousValue) node.getValues();
                        p = value.generateValue(); // Hope this works... cross your fingers
                        realContent.add(String.valueOf(p));
                    }
                }
            }
        }

        return data;
    }

    /**
     * Generate \Pi P table.
     *
     * @param nodes
     * @param valueList
     * @param probability
     * @param table
     * @param result
     * @return Hashtable
     */
    protected Hashtable processInferenceResult(LinkedList nodes, LinkedList valueList, double probability, int howmuch, InferenceResult table, Hashtable result) {
        BBNNode node = (BBNNode) nodes.removeFirst();
        String nodeName = node.getLabel();
        BBNValue values = node.getValues();

        if (values instanceof BBNDiscreteValue) {
            BBNDiscreteValue dvalue = (BBNDiscreteValue) values;
            for (Iterator i = dvalue.iterator(); i.hasNext();) {
                String value = i.next().toString();
                valueList.add(value);
                Hashtable tbl = (Hashtable) table.get(nodeName);
                double p = ((Double) tbl.get(value)).doubleValue() * probability;
                if (p * howmuch <= threshold) continue; // too small to consider

                if (nodes.size() > 0) {
                    processInferenceResult(nodes, valueList, p, howmuch, table, result);
                } else {
                    result.put(valueList.clone(), new Double(p));
                }

                valueList.removeLast();
            }
        } else {
            valueList.add(new Object()); // just create a stub
            processInferenceResult(nodes, valueList, probability, howmuch, table, result);
            valueList.removeLast();
        }

        nodes.addFirst(node);
        return result;
    }

    /**
     * @return double
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Sets the threshold.
     *
     * @param threshold The threshold to set
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String dataOutput = params.getString("-d");
        int numIter = params.getInt("-n", -1);
        boolean quiet = params.getBool("-q");
        boolean rmse = params.getBool("-r");

        if (inputFile == null || numIter < 1) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.datagen.SimpleDataGenerator -i:inputfile -n:howmany -d:generateddataoutputfile [-e:evidencefile] [-q]");
            System.out.println("-q = quiet mode");
            System.out.println("-d = generate data and output it to a file (in arff format)");
            System.out.println("-n = number of data generated");
            return;
        }

        if (!quiet) {
            System.out.println("Inference-Based Data Generator");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        SimpleDataGenerator datagen = new SimpleDataGenerator(new LS(g));

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        Table tuples = datagen.generateData(numIter);
        if (dataOutput != null) tuples.save(dataOutput);

        if (rmse) {
            LS ls = new LS(g);
            InferenceResult actualMarginals = ls.getMarginals();
            DataAnalyzer analyzer = new DataAnalyzer(tuples);
            InferenceResult dataMarginals = analyzer.getDataMarginals();
            double rr = actualMarginals.computeRMSE(dataMarginals);
            //            dataMarginals.save(System.out);
            //            actualMarginals.save(System.out);
            System.out.println("RMSE = " + rr);
        }


        if (!quiet) {
            System.out.println("Memory needed for Logic Sampling = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }
    }
}
