package edu.ksu.cis.bnj.bbn.inference.ls;

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

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.ExactInference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * <P>Lauritzen-Spiegelhalter algorithm.
 * <p/>
 * <P>This algorithm is divided into two main phase: Constructing the clique tree
 * and propagating the belief.
 * <p/>
 * <P>For clique tree construction, please see CliqueTree.java at
 * edu.ksu.cis.kdd.bisonparsers.graph package (not at this package)
 * <p/>
 * <P>For propagation, see Clique.java at this package.
 * <p/>
 * <P>I decided to split these implementation because Sparse Candidate also need
 * clique tree but of different representation. This package's CliqueTree simply
 * "repackage" the result of clique tree algorithm.
 *
 * @author Roby Joehanes
 */
public class LS extends ExactInference {

    protected CliqueTree tree;

    public LS() {
    }

    /**
     * Constructor for LS.
     *
     * @param g
     */
    public LS(BBNGraph g) {
        super(g);
    }

    public String getName() {
        return "Lauritzen-Spiegelhalter Algorithm";
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.inference.Inference#getMarginals()
     */
    public InferenceResult getMarginals() {
        tree = new CliqueTree(graph);  // Construct a clique tree

        // Start lambda propagation at the leaf
        Set cliques = tree.getNodes();
        for (Iterator i = cliques.iterator(); i.hasNext();) {
            Clique clique = (Clique) i.next();
            if (clique.getChildrenSize() == 0) {
                clique.lambdaPropagation();
            }
        }

        InferenceResult result = new InferenceResult();

        // Get the result of each node. Since each node has been
        // assigned to a particular clique, we can simply marginalize
        // the phi of that clique
        for (Iterator i = cliques.iterator(); i.hasNext();) {
            Clique clique = (Clique) i.next();
            for (Iterator j = clique.getBaseNodes().iterator(); j.hasNext();) {
                BBNNode node = (BBNNode) j.next();
                String nodeName = node.getLabel();
                Hashtable qtbl = new Hashtable();
                Hashtable rtbl = new Hashtable();
                BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();
                for (Iterator k = dval.iterator(); k.hasNext();) {
                    Object val = k.next();
                    String valStr = val.toString();
                    qtbl.put(nodeName, valStr);
                    double p = clique.getCPF().normalizedQuery(qtbl);
                    rtbl.put(valStr, new Double(p));
                }
                result.put(nodeName, rtbl);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");
        String outputFile = params.getString("-o");
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.ls.LS -i:inputfile [-e:evidencefile] [-o:outputfile] [-q]");
            System.out.println("-q = quiet mode");
            return;
        }

        if (!quiet) {
            System.out.println("LS");
        }

        Runtime r = Runtime.getRuntime();
        long origfreemem = r.freeMemory();
        long freemem;
        long initTime = System.currentTimeMillis();

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);

        long origTime = System.currentTimeMillis();
        System.out.println("Load time = " + ((origTime - initTime) / 1000.0));

        LS ls = new LS(g);
        InferenceResult result = ls.getMarginals();

        freemem = origfreemem - r.freeMemory();
        long learnTime = System.currentTimeMillis();

        if (outputFile != null) result.save(outputFile);

        if (!quiet) {
            System.out.println("Final result:");
            System.out.println(result.toString());
            System.out.println("Memory needed for LS = " + freemem);
            System.out.println("Inference time = " + ((learnTime - origTime) / 1000.0));
        }
    }
}
