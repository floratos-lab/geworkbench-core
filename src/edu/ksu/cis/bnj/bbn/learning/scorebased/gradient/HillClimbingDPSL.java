package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

/*
 * Created on Wed 18 Jun 2003
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
import edu.ksu.cis.bnj.bbn.learning.score.WeightedBDEScore;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.stat.distribution.Gamma;

import java.util.Vector;

/**
 * @author Haipeng Guo
 */
public class HillClimbingDPSL extends HillClimbingSL {

    /**
     * @param data
     */
    protected Gamma seedGamma = new Gamma(0.5, 0.5); // (alpha,lambda)

    public HillClimbingDPSL() {
    }

    public HillClimbingDPSL(Data data) {
        super(data);
    }

    /**
     * This method generate distribution functions with Dirichlet priors (alphas) *
     *
     * @param n      :
     * @param alphas : a vector of double numbers
     * @return
     */
    public double[] generateDirichletDistribution(int n, double alphas[], double beta) {
        double distribution[] = new double[n];
        double normalization = 0.0;
        for (int i = 0; i < n; i++) {
            distribution[i] = generateGamma(Math.pow(alphas[i], beta - 1));
            normalization += distribution[i];
        }
        for (int i = 0; i < n; i++)
            distribution[i] /= normalization;
        return (distribution);
    }


    /**
     * sample from a Gamma distribution
     *
     * @param alpha
     * @return
     */
    private double generateGamma(double alpha) {
        double gamma;
        //		System.out.println("alpha="+alpha);
        if (alpha == 0)
            alpha = Double.MIN_VALUE;
        gamma = seedGamma.nextDouble(alpha, 1);

        return (gamma);

    }

    /**
     * random Rewighting
     */
    public void randomReweighting(double beta) {
        double weights[] = new double[data.getTuples().size()];
        weights = data.getWeights();
        double[] newWeights = this.generateDirichletDistribution(weights.length, weights, beta);
        data.setWeights(newWeights);
    }

    public BBNGraph getGraph() {		//System.out.println(tuples.getTuples().size());
        double tao = 1;
        double DELTA = 0.95;
        int t = 0;
        double beta = 1.0 / Math.pow(tao, t);
        double weights[] = new double[data.getTuples().size()];
        //		initialize the weights to a uniform distribution
        for (int i = 0; i < weights.length; i++)
            weights[i] = 1.0 / weights.length;
        data.setWeights(weights);

        if (candidateScorer == null)
            candidateScorer = new WeightedBDEScore(this);
        //candidateScorer = new BDEScore(this);

        allGraphs = new Vector();
        allGraphScores = new Vector();
        errors = new Vector[101];
        maxParents = 5;
        initializeNodes();
        initializeNodeParents();
        initializePossibleParents();
        initializeGraph();
        curErrors = new Vector();
        learnGraph();
        errors[0] = curErrors;

        while (tao > 0.002 && t < 100) {
            System.out.println(curErrors.toString());
            // random reweighting
            if (t > 0)
                randomReweighting(beta);
            curErrors = new Vector();

            // learn the graph by hillcliming
            learnGraph();
            errors[t + 1] = curErrors;
            // annealing
            tao *= DELTA;
            t++;
            beta = 1.0 / Math.pow(tao, t);
            if (beta == Double.NEGATIVE_INFINITY)
                beta = Double.MIN_VALUE;
            //			System.out.println("beta = " + beta);
            //			System.out.println("t = " + t);
            //			System.out.println("tao = " + tao);
        }
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
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.greedy.HillClimbingDPSL -i:inputfile [-o:outputfile] [-f:outputformat] [-q] [-k:parentlimit] [-g:goldfile] [-t:errorType] [-r:resultsfile]");
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
            HillClimbingDPSL greedyLearner = new HillClimbingDPSL(tuples);
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
                System.out.println("Memory needed after learning HillClimbingDPSL = " + freemem);
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
