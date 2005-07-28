/*
 * Created on Mar 12, 2003
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
import java.util.*;

/**
 * Adaptive importance sampling gets samples by instantiating each node
 * of the network (using the likelihood of each state given the state of
 * the parent nodes) in topological order, using the probabilities in
 * the importance function (which are initially
 * set equal to the conditional probability tables).
 * In this sampling process, evidence nodes are automatically
 * instantiated to the value given in the evidence file. After 10% of
 * the samples have been taken, the approximate probabilities for those
 * samples are used to update the importance function. This
 * allows the probabilities for each node to change to reflect the values
 * of the evidence nodes.
 *
 * @author Julie Thornton, Roby Joehanes
 */

public class AIS extends MCMC implements DataGenerator, Runnable {

    public static final int defaultUpdateIteration = 100;
    public static final double defaultWeight = 100.0;
    public static final String ln = System.getProperty("line.separator"); // $NON-NLS-1$

    protected int updateIteration = defaultUpdateIteration;
    protected double weight = defaultWeight;

    private BBNNode[] nodes;
    private int max;
    private BBNCPF[] totalTable; // sample frequency
    private BBNCPF[] icpt;
    private BBNCPF[] origCPT;
    private Hashtable valueCache;
    private InferenceResult result;
    private Hashtable indexCache;
    //private Hashtable needUpdated;
    protected HashSet needUpdated;

    public AIS() {
    }

    /**
     * The constructor for AIS, which initializes the graph
     * on which to run inference.
     *
     * @param g - the BBNGraph on which to run inference
     */
    public AIS(BBNGraph g) {
        super(g);
    }

    /**
     * An alternate constructor for AIS, which intializes the graph
     * on which to run inference and the number of samples to use
     *
     * @param g - the BBNGraph on which to run inference
     * @param i - the number of samples to generate during inference
     */

    public AIS(BBNGraph g, int i) {
        super(g);
        setMaxIteration(i);
    }

    /**
     * Gets the name of the inference algorithm
     *
     * @return String - the name of the inference algorithm (AIS)
     */

    public String getName() {
        return "Adaptive Importance Sampling";
    }

    /**
     * heuristicUpdate1
     * changes  the ICPT tables to the parents of evidence nodes to uniform
     * distribution when P(E=e) for that evidence node
     * is less than 1/(2*ne), where ne is the number of outcomes possible
     * for that evidence node.
     */
    private void heuristicUpdate1() {
        for (int i = 0; i < max; i++) {
            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            if (node.isEvidence()) {
                String[] values = (String[]) valueCache.get(nodeName);
                int arity = values.length;

                //threshold = 1/(2*arity), set parent node probs to uniform
                double threshold = 1.0 / (2.0 * arity + 0.0);

                String evValue = node.getEvidenceValue().toString();
                Hashtable queryTable = new Hashtable();
                queryTable.put(nodeName, evValue);

                //double resultProb = node.getCPF().query(queryTable);
                double resultProb = origCPT[i].query(queryTable);
                List parents = node.getParents();
                int numParents = node.getParents().size();

                int numColumns = 1;
                for (int j = 0; j < numParents; j++) {
                    BBNNode parent = (BBNNode) parents.get(j);
                    String parentName = parent.getLabel();
                    String[] parentValues = (String[]) valueCache.get(parentName);
                    numColumns *= parentValues.length;
                    int parentIndex = ((Integer) indexCache.get(parentName)).intValue();
                    needUpdated.add(new Integer(parentIndex));
                }

                resultProb = resultProb / numColumns;
                // set all parent probs to uniform distribution
                if (resultProb < threshold) {
                    for (int j = 0; j < numParents; j++) {
                        BBNNode parent = (BBNNode) parents.get(j);
                        String parentName = parent.getLabel();
                        int parentIndex = ((Integer) indexCache.get(parentName)).intValue();
                        icpt[parentIndex].resetEntries();
                        icpt[parentIndex].normalizeAndFill(parentName);
                    }
                }
            }
            icpt[i].normalize(nodeName);
        }
    }

    /**
     * heuristicInitialize2
     * deals with extremely small probabilities.  If a node has
     * a    probability table with an entry that is less than the set
     * threshold,    0.04, then this probability p is replaced by (0.04)^2,
     * and (0.04-p) is    subtracted from the largest probability IN THE SAME
     * COLUMN of the probability table.
     */
    private void heuristicUpdate2() {
        for (int i = 0; i < max; i++) {

            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            Hashtable CPF = icpt[i].getTable();
            Enumeration e = CPF.elements();
            Enumeration keysSmall = CPF.keys();
            Hashtable keySmall = new Hashtable();
            String[] values = (String[]) valueCache.get(nodeName);

            while (e.hasMoreElements()) {
                keySmall = (Hashtable) keysSmall.nextElement();
                double cptValue = (new Double(e.nextElement().toString())).doubleValue();
                if (cptValue < 0.04 && !node.isEvidence()) {

                    //get list of values in same column as keySmall
                    Enumeration keys = keySmall.keys();
                    Hashtable columnHash = new Hashtable();
                    while (keys.hasMoreElements()) {
                        String curKey = keys.nextElement().toString();
                        String valHash = keySmall.get(curKey).toString();
                        if (!curKey.equals(nodeName)) {
                            columnHash.put(curKey, valHash);
                        }
                    }

                    List listColumnVals = node.queryColumn(columnHash);
                    Iterator it = listColumnVals.iterator();
                    double maxProb = 0;
                    while (it.hasNext()) {
                        double prob = (new Double(it.next().toString())).doubleValue();
                        if (prob > maxProb) {
                            maxProb = prob;
                        }
                    }

                    Enumeration f = CPF.elements();
                    Enumeration keysBig = CPF.keys();
                    Hashtable keyBig = new Hashtable();

                    while (f.hasMoreElements()) {
                        keyBig = (Hashtable) keysBig.nextElement();
                        double val = (new Double(f.nextElement().toString())).doubleValue();
                        if (val == maxProb) {
                            if (keyBig.keySet().containsAll(columnHash.keySet())) {
                                break;
                            }
                        }
                    }

                    BBNCPF origVals = (BBNCPF) icpt[i].clone();
                    BBNCPF newVals = (BBNCPF) icpt[i].clone();

                    Hashtable addVals = newVals.getTable();
                    Enumeration addEnum = addVals.keys();
                    while (addEnum.hasMoreElements()) {
                        Hashtable key = (Hashtable) addEnum.nextElement();
                        addVals.put(key, new BBNConstant(0));
                    }

                    addVals.put(keySmall, new BBNConstant(0.04 * 0.04 - cptValue));

                    addVals.put(keyBig, new BBNConstant(-0.04 + cptValue));
                    icpt[i].setTable(addVals);
                    icpt[i].add(origVals);
                    icpt[i].normalize(nodeName);
                }
            }
        }
    }

    /**
     * Computes the AIS algorithm and calculates the posterior
     * probabilities of the network
     *
     * @param generateSamples - whether you want to print the samples
     *                        generated to a file (to perhaps use a data in structure learning)
     * @return InferenceResult - contains the posterior probs
     * @see edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMC#getMarginalsImpl(boolean)
     */

    public InferenceResult getMarginals() {
        assert (updateIteration < maxIteration);
        result = new InferenceResult();
        valueCache = new Hashtable();
        indexCache = new Hashtable();
        needUpdated = new HashSet();
        nodes = (BBNNode[]) graph.topologicalSort().toArray(new BBNNode[0]);
        max = nodes.length;
        totalTable = new BBNCPF[max]; // sample frequency
        icpt = new BBNCPF[max];
        origCPT = new BBNCPF[max];
        //if (generateSamples) tuples = new Tuples();
        if (generateSamples) tuples = new Table();

        double a = 0.4;
        double b = 0.14;
        int k_max = maxIteration / updateIteration;
        int k = 0;
        double learnrate = 0;
        long start = System.currentTimeMillis();
        // pre-process
        for (int i = 0; i < max; i++) {
            BBNNode node = nodes[i];
            String nodeName = node.getLabel();
            indexCache.put(nodeName, new Integer(i));
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
            icpt[i] = (BBNCPF) node.getCPF().clone();
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
            }

            if (generateSamples) {
                tuples.addAttribute(attr);
            }
        }

        heuristicUpdate1();
        heuristicUpdate2();

        AISEvent event = new AISEvent();

        if (rmseWriter != null) rmseWriter.setGraph(graph);
        //      InferenceResult lsResult = null;
        //      if (rmseWriter != null) {
        //      	LS ls = new LS(graph);
        //         lsResult = ls.getMarginals();
        //      }

        // main loop
        long main = System.currentTimeMillis();
        setAbortTimer(); // XXX
        for (int iteration = 0; (!abort && (iteration < maxIteration)); iteration++) { // XXX
            Hashtable chosen = new Hashtable();
            double p = 1.0;
            double pSelecting = 1.0;

            if ((iteration + 1) % updateIteration == 0) {
                learnrate = a * (Math.pow((b / a), k / k_max));

                // normalize total table
                for (int i = 0; i < max; i++) {
                    if (!needUpdated.contains(new Integer(i))) continue;
                    BBNCPF newTotalTable = (BBNCPF) totalTable[i].clone();
                    BBNCPF tempICPT = (BBNCPF) icpt[i].clone();
                    tempICPT.multiply(new BBNConstant(-1));
                    newTotalTable.add(tempICPT);
                    newTotalTable.multiply(new BBNConstant(learnrate));
                    icpt[i].add(newTotalTable);
                    icpt[i].normalize(nodes[i].getLabel());
                }
                k++;
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

            // Send event to the hook
            if (listeners.size() > 0) {
                event.temporaryResult = result;
                event.icpt = icpt;
                sendEvent(event);
            }
        }

        long middle = System.currentTimeMillis();

        // resetting total table
        totalTable = new BBNCPF[max]; // sample frequency

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
                values.put(value, new Double(0.0));  // initialize the result to zero
                if (generateSamples) attr.addValue(value);
            }
            totalTable[i] = (BBNCPF) node.getCPF().clone();
            totalTable[i].resetEntries();
        }

        // actual sampling: no ICPT updating
        long middle2 = System.currentTimeMillis();

        for (int iteration = 0; iteration < maxIteration; iteration++) {
            Hashtable chosen = new Hashtable();
            double p = 1.0;
            double pSelecting = 1.0;


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

            //		  	if (rmseWriter != null) {
            //			  	InferenceResult rclone = (InferenceResult) result.clone();
            //			  	rclone.normalize();
            //			  	double rmse = rclone.computeRMSE(lsResult);
            //			  	rmseWriter.println(rmse);
            //		  	}
            if (rmseWriter != null) {
                InferenceResult rclone = (InferenceResult) result.clone();
                rclone.normalize();
                rmseWriter.calculateRMSE(rclone);
            }

            //	Send event to the hook
            if (listeners.size() > 0) {
                event.temporaryResult = result;
                event.icpt = icpt;
                sendEvent(event);
            }
        }
        long end = System.currentTimeMillis();

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
        //long end2 = System.currentTimeMillis();
        //System.out.println("s-main: " + (main-start));
        //System.out.println("main-middle: " + (middle-main));
        //System.out.println("m-m2: " + (middle2-middle));
        //System.out.println("m2-end: " + (end-middle2));
        //System.out.println("e1-e2: " + (end2-end));
        if (rmseWriter != null) {
            rmseWriter.dump();
            rmseWriter.close();
        }
        return result;
    }

    /**
     * This method is used in the bridge between AIS and BCC.  It
     * returns the current array of ICPT values
     *
     * @return BBNCPF[] - the array of the ICPT values
     */
    public BBNCPF[] getICPT() {
        return icpt;
    }

    /**
     * This method returns the array of nodes in the network
     *
     * @return - the array of nodes in the network
     */
    public BBNNode[] getNodes() {
        return nodes;
    }

    /**
     * Runs the AIS algorithm without printing out the generated
     * samples.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        getMarginals();
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
     * Sets the updateIteration
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

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.approximate.sampling.AIS -i:inputfile [-e:evidencefile] [-o:outputfile] [-q] [-n:numiteration] [-u:updateiteration] [-w:weight] [-r] [-m] [-d:generateddataoutputfile] [-ro:rmseoutput]");
            System.out.println("-q = quiet mode");
            System.out.println("-n = number of iteration, default = " + defaultMaxIteration);
            System.out.println("-u = number of update iteration, default = " + defaultUpdateIteration);
            System.out.println("-w = weight factor, default = " + defaultWeight);
            System.out.println("-r = calculate RMSE error");
            System.out.println("-m = use Markov blanket score");
            System.out.println("-d = generate data and output it to a file");
            System.out.println("-ro = output RMSE result per iteration into a file");
            return;
        }

        if (!quiet) {
            System.out.println("Adaptive Importance Sampling");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;
        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();

        AIS ais = new AIS(g);
        ais.setMaxIteration(numIter);
        ais.setUpdateIteration(numUpdate);
        ais.setWeight(numWeight);
        ais.setUseMarkovBlanketScore(markov);
        ais.generateData(dataOutput != null);

        if (rmseOut != null) {
            try {
                FileOutputStream outfile = new FileOutputStream(rmseOut);
                ais.setRMSEfile(outfile);
            } catch (IOException ioe) {
                System.out.println("error writing to rmse file");
            }
        }

        InferenceResult result = ais.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);
        if (dataOutput != null) ais.tuples.save(dataOutput);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for Adaptive Importance Sampling = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }


        if (rmse || rmseOut != null) {
            LS ls = new LS(g);
            InferenceResult lsResult = ls.getMarginals();
            //System.out.println("LS Result:"+lsResult.toString());
            double rmseError = lsResult.computeRMSE(result);
            System.out.println("RMSE = " + rmseError);
            if (outputFile != null) {
                try {
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

