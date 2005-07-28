package edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk;

/*
 * Created on Tue 18 Mar 2003
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
import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.ga.Population;
import edu.ksu.cis.kdd.ga.Subpopulation;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.gui.OptionGUI;

import java.util.LinkedList;

/**
 * @author Roby Joehanes
 */
public class GAWK extends ScoreBasedLearner {

    public static final int defaultGenerations = 100;
    public static final int defaultPopulationSize = 10;

    protected Population pop;
    protected int populationSize = defaultPopulationSize;
    protected int generations = defaultGenerations;
    protected GAWKFitness fitnessFunction = null;
    protected int attributeSize;
    public K2 k2;

    public GAWK() {
    }

    /**
     * @param t
     */
    public GAWK(Data t) {
        super(t);
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#getGraph()
     */
    public BBNGraph getGraph() {
        if (fitnessFunction == null) fitnessFunction = new GAWKFitness(this);
        attributeSize = data.getAttributes().size();

        pop = new Population();
        pop.subpop = new Subpopulation[1];
        pop.subpop[0] = new Subpopulation(populationSize, new GAWKChrom(attributeSize), fitnessFunction);

        pop.subpop[0].addOperator(new ShuffleOp(), 1.0);

        pop.evolve(generations);

        GAWKChrom gc = (GAWKChrom) pop.getBestChromosome();

        return getGraph(gc);
    }

    protected BBNGraph getGraph(GAWKChrom gc) {
        LinkedList order = new LinkedList();
        for (int i = 0; i < attributeSize; i++) {
            order.add(new Integer(gc.elements[i]));
        }

        k2 = new K2(data);
        k2.setOrdering(order);
        k2.setParentLimit(parentLimit);
        return k2.getGraph();
    }

    /**
     * @return the fitness function
     */
    public GAWKFitness getFitnessFunction() {
        return fitnessFunction;
    }

    public double getK2Score() {
        return k2.getNetworkScore();
    }


    /**
     * @param fitness The fitness function
     */
    public void setFitnessFunction(GAWKFitness fitness) {
        fitnessFunction = fitness;
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#getName()
     */
    public String getName() {
        return "Genetic Algorithm Wrapper for K2";
    }

    /**
     * @return int
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Sets the populationSize.
     *
     * @param populationSize The populationSize to set
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * @return int
     */
    public int getGenerations() {
        return generations;
    }

    /**
     * Sets the generations.
     *
     * @param generations The generations to set
     */
    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public OptionGUI getOptionsDialog() {
        return new GAWKOptionGUI(this);
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        String goldFile = params.getString("-r");
        int maxParent = params.getInt("-k", defaultParentLimit);
        int maxGeneration = params.getInt("-g", defaultGenerations);
        int maxPopSize = params.getInt("-p", defaultPopulationSize);
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.gawk.GAWK -i:inputfile [-o:outputfile] [-f:outputformat] [-r:goldnetwork] [-q] [-k:parentlimit] [-g:generations] [-p:populationsize]");
            System.out.println("-o: file to which best network of final GA generation is serialized");		// ### ALWAYS DOCUMENT OUTPUT SPEC - WHH 20 Apr 2003
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn, dsc, dsl, ergo, libb}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            System.out.println("-g: number of generations. Default=" + defaultGenerations);
            System.out.println("-p: population size. Default=" + defaultPopulationSize);
            System.out.println("-r: compute the true RMSE value against a gold standard network.");
            System.out.println("-q: quiet mode");
            return;
        }
        System.out.println("Genetic Wrapper for K2 learning");

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            GAWK gawk = new GAWK(tuples);
            //gawk.setCalculateCPT(false);
            gawk.setParentLimit(maxParent);
            gawk.setPopulationSize(maxPopSize);
            gawk.setGenerations(maxGeneration);

            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }

            BBNGraph g = gawk.getGraph();
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after learning Genetic Wrapper for K2 = " + freemem);
                System.out.println("Learning time = " + ((learnTime - afterLoadTime) / 1000.0));
            }

            if (outputFile != null) {
                if (outputFormat != null) {
                    g.save(outputFile, outputFormat);
                } else {
                    g.save(outputFile);
                }
            }

            if (goldFile != null) {
                LS ls = new LS(BBNGraph.load(goldFile));
                InferenceResult r1 = ls.getMarginals();
                ls = new edu.ksu.cis.bnj.bbn.inference.ls.LS(g);
                InferenceResult r2 = ls.getMarginals();
                System.out.println("Actual RMSE = " + r1.computeRMSE(r2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
