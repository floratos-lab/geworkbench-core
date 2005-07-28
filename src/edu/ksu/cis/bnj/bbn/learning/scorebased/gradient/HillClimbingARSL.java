package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

/*
 * Created on Wed 26 Jun 2003
 *
 * This file is part of Bayesian Networks in Java (BNJ).
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

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.score.WeightedBDEScore;
import edu.ksu.cis.kdd.data.*;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Julie Thornton
 */

public class HillClimbingARSL extends HillClimbingSL {
    private double[] prevRMSE;

    public HillClimbingARSL() {
    }

    public HillClimbingARSL(Data data) {
        super(data);
    }

    /**
     * adversarial reweighting
     */
    public void adversarialReweighting(double tao, int t) {
        double weights[] = new double[data.getTuples().size()];
        weights = data.getWeights();
        computeCPT(bbnGraph);
        LS ls = new LS(bbnGraph);
        InferenceResult approxResult = ls.getMarginals();
        double curRMSE[] = new double[data.getTuples().size()];

        for (int i = 0; i < data.getTuples().size(); i++) {
            //get an InferenceResult for each tuple, calculate RMSE
            InferenceResult actualResult = getInferenceResult(i);
            double rmse = actualResult.computeRMSE(approxResult);
            curRMSE[i] = rmse;
        }

        //update weights
        for (int i = 0; i < weights.length; i++) {
            double deltaRMSE = prevRMSE[i] - curRMSE[i];
            weights[i] = weights[i] - tao * (deltaRMSE / t);
            prevRMSE[i] = curRMSE[i];
        }

        double[] newWeights = normalize(weights);
        data.setWeights(newWeights);
    }

    private double[] normalize(double[] curWeights) {
        double total = 0;
        for (int i = 0; i < curWeights.length; i++) {
            total += curWeights[i];
        }
        if (total == 0) return curWeights; // Avoid NaN bug, -- robbyjo
        for (int i = 0; i < curWeights.length; i++) {
            curWeights[i] /= total;
        }
        return curWeights;
    }

    private InferenceResult getInferenceResult(int i) {
        InferenceResult result = new InferenceResult();
        Tuple curTuple = (Tuple) data.getTuples().get(i);
        Table curTable = new Table();
        curTable.addTuple(curTuple);
        curTable.addAttributes(data.getAttributes());
        Tally tally = curTable.getTallyer(); //new Tally(curTable);
        List attrs = curTable.getAttributes();

        double total = tally.size();
        int index = 0;

        for (Iterator j = attrs.iterator(); j.hasNext(); index++) {
            Attribute attr = (Attribute) j.next();
            int arity = attr.getArity();
            Hashtable tbl = new Hashtable();
            List values = attr.getValues();
            for (int k = 0; k < arity; k++) {
                int t = tally.tally(index, k);
                tbl.put(values.get(k), new Double(t / total));
            }
            result.put(attr.getName(), tbl);
        }
        return result;
    }

    public BBNGraph getGraph() {
        double tao = 1;
        double DELTA = 0.95;
        int t = 0;
        double weights[] = new double[data.getTuples().size()];
        errors = new Vector[100];
        int count = 0;
        prevRMSE = new double[data.getTuples().size()];

        //		initialize the weights to a uniform distribution
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1.0 / weights.length;
            prevRMSE[i] = 0;
        }
        data.setWeights(weights);

        if (candidateScorer == null)
            candidateScorer = new WeightedBDEScore(this);

        maxParents = 5;
        //minNumChanges = (new Double(Math.pow(numOfNodes, 2))).intValue();
        initializeNodes();
        initializeNodeParents();
        initializePossibleParents();
        initializeGraph();

        while (tao > 0.002 && t < 100) {

            // learn the graph by hillclimbing
            curErrors = new Vector();
            learnGraph();
            errors[count] = curErrors;
            computeCPT(bbnGraph);
            resetCPT();
            count++;

            //	random reweighting
            if (t > 0) {
                adversarialReweighting(tao, t);
                resetCPT();
            }

            // annealing
            tao *= DELTA;
            t++;
        }

        weights = new double[data.getTuples().size()];

        //	change the weights back to a uniform distribution
        for (int i = 0; i < weights.length; i++)
            weights[i] = 1.0 / weights.length;
        data.setWeights(weights);
        computeCPT(bbnGraph);
        return bbnGraph;
    }


    public static void main(String[] args) {

        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        int maxParent = params.getInt("-k", defaultParentLimit);
        boolean quiet = params.getBool("-q");
        String goldFile = params.getString("-g");
        String resultsFile = params.getString("-r");
        String errorType = params.getString("-t");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.greedy.HillClimbingARSL -i:inputfile [-o:outputfile] [-f:outputformat] [-q] [-k:parentlimit] [-g:goldfile] [-t:errorType] [-r:resultsfile]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            System.out.println("-t: acceptable values are {g (graph errors), r (rmse), b(bde score), l (log-likelihood)}");
            System.out.println("must define -g (goldFile) if want graph error count");

            return;
        }

        if (errorType != null) {
            if (errorType.equals("g")) {
                if (goldFile == null) {
                    System.out.println("must define -g (goldFile) if want graph error count");
                    return;
                }
            }
        }

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            HillClimbingARSL greedyLearner = new HillClimbingARSL(tuples);
            greedyLearner.inputFile = inputFile;

            greedyLearner.setParentLimit(maxParent);
            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }
            if (errorType != null) {
                greedyLearner.setResultsFile(resultsFile);
                greedyLearner.errorType = errorType;
                if (errorType.equals("g")) {
                    greedyLearner.setGoldGraph(goldFile);
                }
            }

            BBNGraph g = greedyLearner.getGraph();
            System.out.println(g);
            if (errorType != null) greedyLearner.printErrors();
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after learning HillClimbingARSL = " + freemem);
                System.out.println("Learning time = " + ((learnTime - afterLoadTime) / 1000.0));
            }

            if (outputFile != null) {
                if (outputFormat != null) {
                    g.save(outputFile, outputFormat);
                } else {
                    g.save(outputFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
