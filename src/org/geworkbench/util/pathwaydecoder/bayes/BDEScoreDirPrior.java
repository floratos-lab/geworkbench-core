package org.geworkbench.util.pathwaydecoder.bayes;

import edu.ksu.cis.bnj.bbn.learning.score.BDEScore;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Tally;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BDEScoreDirPrior extends BDEScore {

    //  double alpha_ijk = 0;
    double alpha;
    double alpha_ijk;

    public BDEScoreDirPrior(double alpha) {
        //    this.alpha_ijk = alpha;
        this.alpha = alpha;
    }

    protected void precache() {
        // Precache factorial and arity table
        Data tuples = tally.getUnderlyingData();
        int maxArity = 0;
        int idx = 0;
        List attributes = tuples.getAttributes();
        arityCache = new int[attributes.size()];
        for (Iterator i = attributes.iterator(); i.hasNext(); idx++) {
            Attribute attr = (Attribute) i.next();
            int arity = attr.getArity();
            arityCache[idx] = arity;
            if (maxArity < arity) {
                maxArity = arity;
            }
        }
        tupleSize = tuples.getTuples().size();

        //int tableSize = maxArity + tupleSize + (alpha_ijk * maxArity);
        int tableSize = (int) (maxArity + tupleSize + (alpha_ijk * maxArity));
        lnFactorialCache = new double[tableSize];
        for (int i = 2; i < tableSize; i++) {
            lnFactorialCache[i] = lnFactorialCache[i - 1] + Math.log(i);
        }

    }

    public double getScore(int curNode, int candidate, Set[] parentTable) {
        this.curNode = curNode;
        if (lnFactorialCache == null) { // Precache
            precache();
        }

        nodeArity = arityCache[curNode];

        ri_minus_one = nodeArity - 1;
        ri_minus_one_fact = lnFactorialCache[ri_minus_one];


        // convert candidate and parents to an array of int
        int arraySize = 0;
        int parentSize = 0;
        if (parentTable != null && parentTable.length > curNode && parentTable[curNode] != null) {
            parentSize = parentTable[curNode].size();
            arraySize += parentSize;
        }
        if (candidate != NO_CANDIDATES) arraySize++;

        nodes = new int[arraySize];
        int offset = 0;
        if (candidate != NO_CANDIDATES) {
            nodes[0] = candidate;
            offset = 1;
        }
        if (parentSize > 0) {
            for (Iterator i = parentTable[curNode].iterator(); i.hasNext(); offset++) {
                int value = ((Integer) i.next()).intValue();
                nodes[offset] = value;
            }
        }
        // You can't embed children as well since the formula is
        // different. If we have parent, then the formula is unchanged.
        if (arraySize > 0) {
            result = 0.0;
            iterateUniqueInstantiation(0, tally);
        } else {
            // Otherwise, Nij = N
            iterateNoParentInstantiation();
        }

        return result;
    }


    public void iterateNoParentInstantiation() {
        double alpha_ij = 0;
        //    alpha_ijk = 1.0 / nodeArity;
        alpha_ijk = alpha / nodeArity;

        for (int k = 0; k < nodeArity; k++) {
            alpha_ij += alpha_ijk; // just make this uniform;
        }

        //double num_coef = lnFactorialCache[(int)alpha_ij + ri_minus_one];
        double num_coef = Statistics.lnGamma(alpha_ij);
        //double num_coef = Statistics.lnGamma(alpha_ij + ri_minus_one + 1);

        //double denom_coef = lnFactorialCache[(int)alpha_ij + tupleSize + ri_minus_one];
        double denom_coef = Statistics.lnGamma(alpha_ij + tupleSize);
        //double denom_coef = Statistics.lnGamma(alpha_ij + tupleSize + ri_minus_one + 1);

        result = num_coef - denom_coef;

        for (int j = 0; j < nodeArity; j++) {
            int n_ijk = tally.tally(curNode, j);

            //result += lnFactorialCache[n_ijk + (int)alpha_ijk];
            result += Statistics.lnGamma(n_ijk + alpha_ijk);
            //result += Statistics.lnGamma(n_ijk + alpha_ijk + 1);

            //result -= lnFactorialCache[(int)alpha_ijk];
            result -= Statistics.lnGamma(alpha_ijk);
            //result -= Statistics.lnGamma(alpha_ijk + 1);
        }
    }

    /**
     * Recursively filter out the data according to the current node
     *
     * @param depth
     * @param tally
     */
    protected void iterateUniqueInstantiation(int depth, Tally tally) {

        int node = nodes[depth];
        int curArity = arityCache[node];

        double alpha_ij = 0;
        //    alpha_ijk = 1.0 / (curArity * Math.pow(curArity, nodes.length));
        alpha_ijk = alpha / (curArity * Math.pow(curArity, nodes.length));

        for (int k = 0; k < curArity; k++) {
            alpha_ij += alpha_ijk;
        }

        for (int i = 0; i < curArity; i++) {
            Tally newTally = tally.createSubTally(node, i);
            int n_ij = newTally.size();
            //if (n_ij == 0) {
            //  continue; // we don't need to go further because total will be added only with zeroes
            //}

            if (depth == nodes.length - 1) {
                double pi_n_ijk = 0;
                //System.out.println(tbl+" = "+n_ij);
                if (n_ij > 0) {
                    for (int j = 0; j < nodeArity; j++) {
                        int n_ijk = newTally.tally(curNode, j);

                        //pi_n_ijk += lnFactorialCache[(int)alpha_ijk + n_ijk];
                        pi_n_ijk += Statistics.lnGamma(alpha_ijk + n_ijk);
                        //pi_n_ijk += Statistics.lnGamma(alpha_ijk + n_ijk + 1);

                        //pi_n_ijk -= lnFactorialCache[(int)alpha_ijk];
                        pi_n_ijk -= Statistics.lnGamma(alpha_ijk);
                        //pi_n_ijk -= Statistics.lnGamma(alpha_ijk + 1);
                    }
                }

                //double num_coef = lnFactorialCache[(int)alpha_ij + ri_minus_one];
                double num_coef = Statistics.lnGamma(alpha_ij);
                //double num_coef = Statistics.lnGamma(alpha_ij + ri_minus_one + 1);

                //double denom_coef = lnFactorialCache[(int)alpha_ij + n_ij + ri_minus_one];
                double denom_coef = Statistics.lnGamma(alpha_ij + n_ij);
                //double denom_coef = Statistics.lnGamma(alpha_ij + n_ij + ri_minus_one + 1);

                result += num_coef - denom_coef + pi_n_ijk;
            } else {
                iterateUniqueInstantiation(depth + 1, newTally);
            }
        }
    }

}
