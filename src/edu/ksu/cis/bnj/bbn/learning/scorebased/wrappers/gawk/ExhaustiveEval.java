/*
 * Created on Tue 08 Mar 2003
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
package edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tally;
import edu.ksu.cis.kdd.ga.Chromosome;
import edu.ksu.cis.kdd.ga.Fitness;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.*;

/**
 * @author William H. Hsu
 *         Last updated Tue 29 Apr 2003
 */
public class ExhaustiveEval extends ScoreBasedLearner implements Fitness {

    protected Tally tally;
    protected InferenceResult actualResult;
    protected int attributeSize;			// ### NEEDS MORE MEANINGFUL MNEMONIC - WHH
    protected LinkedList permutations;

    /**
     * @param t
     */
    public ExhaustiveEval(Table t) {
        super(t);
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#initialize()
     */
    public void initialize() {
    }

    /**
     * ### LIFT THIS WHERE IT BELONGS - WHH
     */
    public int factorial_aux(int n, int k) {
        if (0 == n)
            return k;
        else
            return (factorial_aux(n - 1, n * k));
    }

    public int factorial(int n) {
        return (factorial_aux(n, 1));
    }

    // remove ith element from l, resulting in a list of n-1 integers
    public static int[] removeFromList(int[] l, int i, int n) {
        int[] newList = new int[n - 1];
        for (int j = 0; j < i; j++)		// [0..i-1]
            newList[j] = l[i];
        for (int j = i + 1; j < n; j++)	// [i+1..n-1]
            newList[j] = l[i];

        return (newList);
    }

    /**
     * @param start
     * @param end
     * @return a
     */
    private static int[] integersFromTo(int start, int end) {
        int a[] = new int[end - start];
        for (int i = start, j = 0; i < end; i++, j++)
            a[j] = i;

        return (a);
    }

    /**
     * Generates all permutations of [1..n]
     */
    //	private static PermList genPerms(int n, int[] l)
    //	{
    //		PermList bigList = null;
    //
    //		if (1 == n)
    //		{
    //			PermList singleton = null;
    //			Integer[] p = new Integer[n];
    //			for (int i = 0; i < n; i++) {
    //				p[i] = new Integer(l[i]);
    //			}
    //			Permutation perm = new Permutation(p);
    //			//singleton.insertAtBack(perm);
    //			return (singleton);
    //		} else {
    //			for (int i = 0; i < n; i++)
    //			{
    //				int[] subList = removeFromList(l, i, n);
    //				LinkedList subPermList = genPerms(n-1,subList);	// (n-1)! subperms
    //				ListIterator subPermListIterator = subPermList.listIterator();
    //
    //				while ( subPermListIterator.hasNext() ) {
    //					Permutation subPerm = (Permutation) subPermListIterator.next();
    //					Integer[] curPerm = new Integer[n];
    //					curPerm[0] = new Integer(i);
    //					for (int k = 1; k < n; k++)
    //						curPerm[k] = subPerm.ordering[k-1];
    //					Permutation newPerm = new Permutation (curPerm);
    //
    //					//bigList.insertAtBack(curPerm);
    //				}
    //			}
    //			return (bigList);
    //		}	// else
    //	}	// genPerms()


    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#getGraph()
     *      ### Gets BEST graph - WHH
     */
    public BBNGraph getGraph() {
        // Compute the actual posterior probability first
        actualResult = new InferenceResult();
        tally = data.getTallyer();

        List attrs = data.getAttributes();
        attributeSize = attrs.size();			// ### bad name - WHH
        double total = tally.size();

        LinkedList allPerms = null; //genPerms(attributeSize, integersFromTo(0, n-1));
        GAWKChrom gc = null;

        int index = 0;
        for (Iterator i = attrs.iterator(); i.hasNext(); index++) {
            Attribute attr = (Attribute) i.next();
            int arity = attr.getArity();
            Hashtable tbl = new Hashtable();
            List values = attr.getValues();
            for (int j = 0; j < arity; j++) {
                int t = tally.tally(index, j);
                tbl.put(values.get(j), new Double(t / total));
            }
            actualResult.put(attr.getName(), tbl);
        }

        ListIterator permListIterator = allPerms.listIterator();
        while (permListIterator.hasNext()) {
            int[] nextPerm = (int[]) permListIterator.next();
            gc = new GAWKChrom(nextPerm, attributeSize);
        }

        System.out.println("Best RMSE = " + (1.0 - (1.0 / gc.getFitness())));
        return getGraph(gc);	// call overloaded method
    }

    /**
     * overloaded method that takes a GAWKChrom
     */
    protected BBNGraph getGraph(GAWKChrom gc) {
        LinkedList order = new LinkedList();
        for (int i = 0; i < attributeSize; i++) {
            order.add(new Integer(gc.elements[i]));
        }

        K2 k2 = new K2(data);
        k2.setOrdering(order);
        k2.setParentLimit(parentLimit);

        return k2.getGraph();
    }

    /**
     * @see edu.ksu.cis.kdd.ga.Fitness#getFitness(edu.ksu.cis.kdd.ga.Chromosome)
     */
    public double getFitness(Chromosome c) {
        BBNGraph g = getGraph((GAWKChrom) c);
        LS ls = new LS(g);
        InferenceResult approxResult = ls.getMarginals();

        double rmse = actualResult.computeRMSE(approxResult);

        return 1.0 / (1.0 + rmse);  // ensure fitness score between 0 and 1
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#processParameters(java.lang.String[])
     */
    public void processParameters(String[] args) {
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learner#getName()
     */
    public String getName() {
        return "Genetic Algorithm Wrapper for K2";
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        String goldFile = params.getString("-r");
        int maxParent = params.getInt("-k", defaultParentLimit);
        //		int maxGeneration = params.getInt("-g", defaultGenerations);
        //		int maxPopSize = params.getInt("-p", defaultPopulationSize);
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.gawk.ExhaustiveEval -i:inputfile [-o:outputfile] [-f:outputformat] [-r:goldnetwork] [-q] [-k:parentlimit] [-g:generations] [-p:populationsize]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn, dsc, dsl, ergo, libb}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            System.out.println("-r: compute the true RMSE value against a gold standard network.");
            System.out.println("-q: quiet mode");
            return;
        }
        System.out.println("Exhaustive evaluation of variable orderings for K2");

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            List attrs = tuples.getAttributes();
            int attributeSize = attrs.size();			// ### bad name - WHH
            ExhaustiveEval evaluator = new ExhaustiveEval(tuples);

            evaluator.setParentLimit(maxParent);

            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }

            // generate the full permutation list
            int[] l = new int[attributeSize];
            // integersFromTo(0,attributeSize-1)
            //PermList perms = genPerms(attributeSize, integersFromTo(0,attributeSize-1));

            // loop through all permutations, evaluating fitness

            BBNGraph g = evaluator.getGraph();
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after exhaustive eval for K2 = " + freemem);
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
