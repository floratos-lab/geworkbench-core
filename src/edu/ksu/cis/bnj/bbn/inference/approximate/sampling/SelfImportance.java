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

import edu.ksu.cis.bnj.bbn.*;
import edu.ksu.cis.bnj.bbn.datagen.DataGenerator;
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
 * Self-importance sampling, much like adaptive importance sampling,
 * gets samples by instantiating each node of the network (using the
 * likelihood of each state given the state of the parent nodes)
 * in topological order, using the probabilities in the importance
 * function (which are initially set equal to the conditional probability
 * tables). In this sampling process, evidence nodes are automatically
 * instantiated to the value given in the evidence file. After 10% of
 * the samples have been taken, the approximate probabilities for those
 * samples are used to update the importance function. This
 * allows the probabilities for each node to change to reflect the values
 * of the evidence nodes.
 *
 * @author Roby Joehanes
 */
public class SelfImportance extends MCMC implements DataGenerator {

    public static final int defaultUpdateIteration = 100;
    public static final double defaultWeight = 100.0;

    protected int updateIteration = defaultUpdateIteration;
    protected double weight = defaultWeight;

    public SelfImportance() {
    }

    /**
     * The constructor for SIS, which initializes the
     * graph on which to run inference.
     *
     * @param g - the BBNGraph on which to run inference
     */
    public SelfImportance(BBNGraph g) {
        super(g);
    }

    /**
     * An alternate constructor, which intializes the graph
     * on which to run inference and the number of samples to use
     *
     * @param g - the BBNGraph on which to run inference
     * @param i - the number of samples to generate during inference
     */
    public SelfImportance(BBNGraph g, int i) {
        super(g);
        setMaxIteration(i);
    }

    /**
     * Gets the name of the inference algorithm
     *
     * @return String - the name of the inference algorithm (SIS)
     */
    public String getName() {
        return "Self Importance Sampling";
    }

    /**
     * Computes the SIS algorithm and calculates the
     * posterior probabilities of the network
     *
     * @param generateSamples - whether you want to print the samples
     *                        generated to a file (to perhaps use a data in structure learning)
     * @return InferenceResult - contains the posterior probs
     * @see edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMC#getMarginalsImpl(boolean)
     */
    public InferenceResult getMarginals() {
        assert (updateIteration < maxIteration);
        // Inference result is a hashtable from node name to another hashtable
        // which is basically a table from its value to its result (posteriori prob)
        // e.g.:
        // A ->  ("true" -> 42.56)
        //       ("false" -> 57.44)
        // B -> ...
        InferenceResult result = new InferenceResult();
        Hashtable valueCache = new Hashtable();  // a cache table from node name to its values (in String array)
        BBNNode[] nodes = (BBNNode[]) graph.topologicalSort().toArray(new BBNNode[0]);
        int max = nodes.length;

        // totalTable[i] is frequency of the i-th node
        // each totalTable[i] is a hashtable from node value -> frequency
        BBNCPF[] totalTable = new BBNCPF[max];
        BBNCPF[] icpt = new BBNCPF[max];  // the ICPT
        BBNCPF[] origCPT = new BBNCPF[max];  // the original CPT
        if (generateSamples) tuples = new Table();

        //HashSet isUpdated = new HashSet();
        //Hashtable indexCache = new Hashtable();

        //LS ls = new LS(graph);
        //InferenceResult lsResult = ls.getMarginals();

        // XXX
        setAbortTimer();

        // pre-process
        for (int i = 0; i < max; i++) {
            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            //indexCache.put(nodeName, new Integer(i));
            BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();
            Hashtable values = new Hashtable();
            String[] valueArray = new String[dval.size()];
            Attribute attr = new Attribute(nodeName);
            int idx = 0;
            for (Iterator j = dval.iterator(); j.hasNext(); idx++) {
                String value = j.next().toString();
                valueArray[idx] = value;
                values.put(value, new Double(0.0));  // initialize the result to zero
                if (generateSamples) attr.addValue(value);
            }
            icpt[i] = (BBNCPF) node.getCPF().clone();  // initially it's the same as the CPT
            origCPT[i] = (BBNCPF) node.getCPF().clone();
            totalTable[i] = (BBNCPF) node.getCPF().clone();
            totalTable[i].resetEntries();
            valueCache.put(nodeName, valueArray);
            result.put(nodeName, values);

            if (node.isEvidence()) {
                Hashtable q = new Hashtable();
                q.put(nodeName, node.getEvidenceValue().toString());
                icpt[i].zeroEntryExcept(q);
                origCPT[i].zeroEntryExcept(q);

                /*
                List parents = node.getAncestors();
                for (Iterator j = parents.iterator(); j.hasNext(); ) {
                BBNNode parent = (BBNNode) j.next();
                Integer parentIndex = (Integer) indexCache.get(parent.getLabel());
                isUpdated.add(parentIndex);
                }
                */
            }

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
            double p = 1.0;
            double pSelecting = 1.0;

            // The ICPT update
            if ((iteration + 1) % updateIteration == 0) {
                BBNPDF r = new BBNConstant(weight / (iteration + 1));
                // normalize total table
                for (int i = 0; i < max; i++) {
                    //if (isUpdated.contains(new Integer(i))) {
                    BBNCPF newTotalTable = (BBNCPF) totalTable[i].clone();
                    newTotalTable.normalizeAndFill(nodes[i].getLabel());
                    //totalTable[i].normalizeAndFill(nodes[i].getLabel());
                    icpt[i] = (BBNCPF) origCPT[i].clone(); // this is expensive
                    icpt[i].multiply(r);
                    //icpt[i].add(totalTable[i]);
                    icpt[i].add(newTotalTable);
                    icpt[i].normalize(nodes[i].getLabel());
                    //}
                }
            }

            // choose the values randomly
            for (int i = 0; i < max; i++) {
                BBNNode node = nodes[i];
                String nodeName = node.getLabel();
                String chosenValue = null;
                if (!node.isEvidence()) {
                    String[] values = (String[]) valueCache.get(nodeName);
                    int arity = values.length;
                    double total = 0.0;
                    double rouletteValue = random.nextDouble();
                    for (int j = 0; j < arity; j++) {
                        chosenValue = values[j];
                        chosen.put(nodeName, chosenValue);
                        double pBar = icpt[i].query(chosen);
                        double pOrig = origCPT[i].query(chosen);
                        total += pBar;
                        if (total > rouletteValue || j == (arity - 1)) {
                            p *= pOrig;
                            //if (!node.isEvidence())
                            pSelecting *= pBar;
                            break;
                        }
                    }
                } else {
                    chosenValue = node.getEvidenceValue().toString();
                    chosen.put(nodeName, chosenValue);
                    double temp = node.getCPF().query(chosen);
                    p *= temp;
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

            p /= pSelecting;

            if (!useMarkovBlanketScore) {
                for (int i = 0; i < max; i++) {
                    BBNNode node = nodes[i];
                    String nodeName = node.getLabel();
                    String chosenVal = (String) chosen.get(nodeName);
                    Hashtable resultTbl = (Hashtable) result.get(nodeName);
                    double d = ((Double) resultTbl.get(chosenVal)).doubleValue();
                    // update Total table
                    double subtotal = totalTable[i].get(chosen);
                    totalTable[i].put(chosen, new BBNConstant(subtotal + p));
                    resultTbl.put(chosenVal, new Double(p + d));
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
                        resultTbl.put(val, new Double(d + (m / p)));
                    }

                    String chosenVal = (String) chosen.get(nodeName);
                    double d = ((Double) resultTbl.get(chosenVal)).doubleValue();
                    // update Total table
                    double subtotal = totalTable[i].get(chosen);
                    totalTable[i].put(chosen, new BBNConstant(subtotal + p));
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

    /**
     * Gets the number of samples taken before updating the ICPTs
     *
     * @return int - the ICPT update interval
     */
    public int getUpdateIteration() {
        return updateIteration;
    }

    /**
     * Returns the weight of samples
     *
     * @return int - the sample weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the updateIteration.
     *
     * @param updateIteration - the updateIteration to set
     */
    public void setUpdateIteration(int updateIteration) {
        this.updateIteration = updateIteration;
    }

    /**
     * Sets the weight.
     *
     * @param weight - the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        String dataOutput = params.getString("-d");
        String rmseOut = params.getString("-ro");
        int numIter = params.getInt("-n", defaultMaxIteration);
        int numUpdate = params.getInt("-u", defaultUpdateIteration);
        double numWeight = params.getDouble("-w", defaultWeight);
        boolean quiet = params.getBool("-q");
        boolean rmse = params.getBool("-r");
        boolean markov = params.getBool("-m");
        long runningTimeLimit = params.getLong("-l", MCMC.NO_TIME_LIMIT); // XXX

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.approximate.sampling.SelfImportance -i:inputfile [-e:evidencefile] [-o:outputfile] [-q] [-n:numiteration] [-l:runTimeLimit] [-u:updateiteration] [-w:weight] [-r] [-ro:rmseOutputFile] [-m] [-d:generateddataoutputfile]"); // XXX
            System.out.println("-q = quiet mode");
            System.out.println("-n = number of iteration, default = " + defaultMaxIteration);
            System.out.println("-l = use Running time limit"); // XXX
            System.out.println("-u = number of update iteration, default = " + defaultUpdateIteration);
            System.out.println("-w = weight factor, default = " + defaultWeight);
            System.out.println("-r = calculate RMSE error");
            System.out.println("-m = use Markov blanket score");
            System.out.println("-d = generate data and output it to a file");
            System.out.println("-ro = output RMSE result per iteration into a file");
            return;
        }

        if (!quiet) {
            System.out.println("Self Importance Sampling");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        SelfImportance sis = new SelfImportance(g);
        sis.setMaxIteration(numIter);
        sis.setUpdateIteration(numUpdate);
        sis.setWeight(numWeight);
        sis.setUseMarkovBlanketScore(markov);
        sis.generateData(dataOutput != null);
        sis.setRunningTimeLimit(runningTimeLimit); // XXX

        if (rmseOut != null) {
            try {
                FileOutputStream outfile = new FileOutputStream(rmseOut);
                sis.setRMSEfile(outfile);
            } catch (IOException ioe) {
                System.out.println("error writing to rmse file");
            }
        }

        InferenceResult result = sis.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);
        if (dataOutput != null) sis.tuples.save(dataOutput);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for Self Importance Sampling = " + freemem);
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
