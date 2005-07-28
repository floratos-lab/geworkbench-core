/*
 * Created on Jun 12, 2003
 *
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
package edu.ksu.cis.bnj.bbn.learning.score;

/**
 * @author Haipeng Guo, Roby Joehanes
 * 
 * K2 score computed on weighted instances
 *
 */

import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.kdd.data.TableTally;
import edu.ksu.cis.kdd.data.WeightedTally;

import java.util.Iterator;
import java.util.Set;

public class WeightedBDEScore extends BDEScore {
    public static final double oneThird = 1.0 / 3.0;
    public static final double lnPi = Math.log(Math.PI);

    public WeightedBDEScore() {
        super();
    }

    public WeightedBDEScore(Learner l) {
        super(l);
    }

    public double getScore(int curNode, int candidate, Set[] parentTable) {
        this.curNode = curNode;
        if (lnFactorialCache == null) { // Precache
            precache();
        }
        assert (tally instanceof WeightedTally); // TODO: This is a dirty hack
        WeightedTally tally = (WeightedTally) this.tally;

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
            result = ri_minus_one_fact - lnFactorialCache[tupleSize + ri_minus_one];
            for (int j = 0; j < nodeArity; j++) {
                double n_ijk = tally.weightedTally(curNode, j);
                //System.out.println(tbl+" = "+n_ijk);
                result += stirlingGosperApprox(n_ijk);
            }
        }
        return result;
    }

    /**
     * Stirling-Gosper approximation for ln n!  (Roby Joehanes)
     * ln n! = 0.5*(ln(2*n+ 1/3) + ln pi) + n ln n - n
     * I did this because the weights may be real valued
     */
    protected static double stirlingGosperApprox(double n) {
        // Note: Math.log is in base e already.
        if (n == 0.0) return 0.0;
        return 0.5 * (Math.log(2 * n + oneThird) + lnPi) + n * Math.log(n) - n;
    }

    /**
     * Recursively filter out the data according to the current node (Roby Joehanes)
     *
     * @param depth
     * @param tally
     */
    protected void iterateUniqueInstantiation(int depth, TableTally tally) {
        int node = nodes[depth];
        int curArity = arityCache[node];

        for (int i = 0; i < curArity; i++) {
            WeightedTally newTally = (WeightedTally) tally.createSubTally(node, i);
            double n_ij = newTally.weightedTally();
            if (n_ij == 0.0) continue; // we don't need to go further because total will be added only with zeroes

            if (depth == nodes.length - 1) {
                double pi_n_ijk = 0;
                //System.out.println(tbl+" = "+n_ij);
                if (n_ij > 0) {
                    for (int j = 0; j < nodeArity; j++) {
                        double n_ijk = newTally.weightedTally(curNode, j);
                        //System.out.println(tbl+" = "+n_ijk);
                        pi_n_ijk += stirlingGosperApprox(n_ijk);
                    }
                }
                result += ri_minus_one_fact + pi_n_ijk - stirlingGosperApprox(n_ij + ri_minus_one);
            } else {
                iterateUniqueInstantiation(depth + 1, newTally);
            }
        }
    }

    /**
     * This is just to reset the cache. But since it is not dependent on
     * tuple numbers, we can simply ignore this.
     *
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#initialize()
     */
    public void initialize() {
    }
}
