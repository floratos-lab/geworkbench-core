/*
 * Created on Mar 7, 2003
 *
 * This file is part of Bayesian Network tools in Java (BNJ).
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

package edu.ksu.cis.bnj.bbn.inference.approximate.sampling;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Logic sampling gets samples by instantiating each node of the network
 * (using the likelihood of each state given the state of the parent nodes)
 * in topological order. Evidence nodes are sampled just like query nodes.
 * nodes does not match the evidence file. Approximate probabilities are
 * computed by looking at how many times each state for each node appeared
 * in the satisfactory samples.
 *
 * @author Roby Joehanes
 */
public class LogicSampling extends MCMC {
    public LogicSampling() {
    }

    /**
     * The constructor for LogicSampling, which initializes the
     * graph on which to run inference.
     *
     * @param g - the BBNGraph on which to run inference
     */
    public LogicSampling(BBNGraph g) {
        super(g);
    }

    /**
     * An alternate constructor, which intializes the graph
     * on which to run inference and the number of samples to use
     *
     * @param g - the BBNGraph on which to run inference
     * @param i - the number of samples to generate during inference
     */
    public LogicSampling(BBNGraph g, int i) {
        super(g);
        setMaxIteration(i);
    }

    /**
     * Gets the name of the inference algorithm
     *
     * @return String - the name of the inference algorithm (LogicSampling)
     */
    public String getName() {
        return "Logic Sampling";
    }

    /**
     * Computes the LogicSampling algorithm and calculates the
     * posterior probabilities of the network
     *
     * @param generateSamples - whether you want to print the samples
     *                        generated to a file (to perhaps use a data in structure learning)
     * @return InferenceResult - contains the posterior probs
     * @see edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMC#getMarginalsImpl(boolean)
     */
    public InferenceResult getMarginals() {
        InferenceResult result = new InferenceResult();
        Hashtable valueCache = new Hashtable();
        BBNNode[] nodes = (BBNNode[]) graph.topologicalSort().toArray(new BBNNode[0]);
        int max = nodes.length;
        if (generateSamples) tuples = new Table();

        // XXX
        setAbortTimer();

        // pre-process
        for (int i = 0; i < max; i++) {
            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();
            Hashtable values = new Hashtable();
            String[] valueArray = new String[dval.size()];
            Attribute attr = new Attribute(nodeName);
            int idx = 0;
            for (Iterator j = dval.iterator(); j.hasNext(); idx++) {
                String value = j.next().toString();
                valueArray[idx] = value;
                values.put(value, new Double(0.0));
                if (generateSamples) attr.addValue(value);
            }
            valueCache.put(nodeName, valueArray);
            result.put(nodeName, values);
            if (generateSamples) {
                tuples.addAttribute(attr);
            }
        }

        if (rmseWriter != null) rmseWriter.setGraph(graph);

        // main loop
        // note: This is not optimized yet. the proper way to do it is
        // starting from the root and proceed. Also eliminate unused
        // entries from chosen Hashtable.
        for (int iteration = 0; (!abort && (iteration < maxIteration)); iteration++) { //XXX
            Hashtable chosen = new Hashtable();

            // choose the values randomly
            for (int i = 0; i < max; i++) {
                BBNNode node = nodes[i];
                String nodeName = node.getLabel();
                String chosenValue = null;
                String[] values = (String[]) valueCache.get(nodeName);
                int arity = values.length;
                double total = 0.0;
                double rouletteValue = random.nextDouble();
                for (int j = 0; j < arity; j++) {
                    chosenValue = values[j];
                    chosen.put(nodeName, chosenValue);
                    double temp = node.query(chosen);
                    total += temp;
                    if (total > rouletteValue) {
                        break;
                    }
                }
            }

            if (generateSamples) {
                Tuple t = new Tuple();
                for (int i = 0; i < max; i++) {
                    BBNNode node = nodes[i];
                    String nodeName = node.getLabel();
                    String chosenVal = (String) chosen.get(nodeName);
                    t.addValue(chosenVal);
                }
                tuples.addTuple(t);
            }

            if (!useMarkovBlanketScore) {
                for (int i = 0; i < max; i++) {
                    BBNNode node = nodes[i];
                    String nodeName = node.getLabel();
                    String chosenVal = (String) chosen.get(nodeName);
                    Hashtable resultTbl = (Hashtable) result.get(nodeName);
                    double d = ((Double) resultTbl.get(chosenVal)).doubleValue();
                    resultTbl.put(chosenVal, new Double(1.0 + d));
                }
            } else {
                for (int i = 0; i < max; i++) {
                    BBNNode node = nodes[i];
                    String nodeName = node.getLabel();
                    Hashtable resultTbl = (Hashtable) result.get(nodeName);

                    Hashtable markov = getMarkovBlanketScore(node, chosen);
                    for (Enumeration e = markov.keys(); e.hasMoreElements();) {
                        String val = (String) e.nextElement();
                        double d = ((Double) resultTbl.get(val)).doubleValue();
                        double m = ((Double) markov.get(val)).doubleValue();
                        resultTbl.put(val, new Double(d + m));
                    }
                }
            }

            if (rmseWriter != null) {
                InferenceResult rclone = (InferenceResult) result.clone();
                rclone.normalize();
                rmseWriter.calculateRMSE(rclone);
            }
        }

        // Normalize
        for (int i = 0; i < max; i++) {
            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            Hashtable resultTbl = (Hashtable) result.get(nodeName);

            double total = 0.0;
            for (Enumeration e = resultTbl.keys(); e.hasMoreElements();) {
                double d = ((Double) resultTbl.get(e.nextElement())).doubleValue();
                total += d;
            }

            for (Enumeration e = resultTbl.keys(); e.hasMoreElements();) {
                Object value = e.nextElement();
                double d = ((Double) resultTbl.get(value)).doubleValue();
                resultTbl.put(value, new Double(d / total));
            }
        }

        // XXX
        cancelAbortTimer();

        if (rmseWriter != null) {
            rmseWriter.dump();
            rmseWriter.close();
        }
        return result;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        String dataOutput = params.getString("-d");
        String rmseOut = params.getString("-ro");
        int numIter = params.getInt("-n", defaultMaxIteration);
        boolean quiet = params.getBool("-q");
        boolean rmse = params.getBool("-r");
        boolean markov = params.getBool("-m");
        long runningTimeLimit = params.getLong("-l", MCMC.NO_TIME_LIMIT); // XXX

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.approximate.sampling.LogicSampling -i:inputfile [-e:evidencefile] [-o:outputfile] [-q] [-n:numiteration] [-l:runTimeLimit] [-r] [-ro:rmseOutputFile] [-m] [-d:generateddataoutputfile]"); // XXX
            System.out.println("-q = quiet mode");
            System.out.println("-n = number of iteration, default = " + defaultMaxIteration);
            System.out.println("-l = use Running time limit"); // XXX
            System.out.println("-r = calculate RMSE error");
            System.out.println("-m = use Markov blanket score");
            System.out.println("-d = generate data and output it to a file");
            System.out.println("-ro = output RMSE result per iteration into a file");
            return;
        }

        if (!quiet) {
            System.out.println("Logic Sampling");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        LogicSampling logicSampling = new LogicSampling(g);
        logicSampling.setMaxIteration(numIter);
        logicSampling.setUseMarkovBlanketScore(markov);
        logicSampling.generateData(dataOutput != null);
        logicSampling.setRunningTimeLimit(runningTimeLimit); // XXX

        if (rmseOut != null) {
            try {
                FileOutputStream outfile = new FileOutputStream(rmseOut);
                logicSampling.setRMSEfile(outfile);
            } catch (IOException ioe) {
                System.out.println("error writing to rmse file");
            }
        }

        InferenceResult result = logicSampling.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);
        if (dataOutput != null) logicSampling.tuples.save(dataOutput);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for Logic Sampling = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }

        if (rmse) {
            LS ls = new LS(g);
            InferenceResult lsResult = ls.getMarginals();
            double rmseError = lsResult.computeRMSE(result);
            System.out.println("RMSE = " + rmseError);
            if (outputFile != null) {
                try {
                    String ln = System.getProperty("line.separator");
                    FileWriter fw = new FileWriter(outputFile, true);
                    fw.write("RMSE = " + rmseError + ln);
                    fw.flush();
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
