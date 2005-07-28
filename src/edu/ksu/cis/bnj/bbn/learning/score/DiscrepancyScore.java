/*
 * Created on Feb 11, 2003
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

import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.TableTally;
import edu.ksu.cis.kdd.data.Tally;

import java.util.Iterator;
import java.util.Set;

/**
 * A class that implements the discrepancy score as described in Friedman's paper
 * (i.e. MDisc).
 *
 * @author Roby Joehanes
 */
public class DiscrepancyScore extends SparseScore {

    protected int total = 0;
    protected Table tuples;
    protected short[][] tallyMatrix;  // big fat cache
    protected Set[] parentTableCache;

    public DiscrepancyScore() {
        super(null);
    }

    /**
     * Shielding Score constructor
     *
     * @param l
     */
    public DiscrepancyScore(Learner l) {
        super(l);
    }

    protected void tallyPrecache() {
        tallyMatrix = new short[attributeSize][maxArity];
        for (int i = 0; i < attributeSize; i++) {
            for (int j = 0; j < arityCache[i]; j++) {
                tallyMatrix[i][j] = (short) tally.tally(i, j);
            }
        }
    }

    // extract parents of a node (into a List) given a parent table
    protected int[] prepareNodes(int node, Set[] parentTable) {
        int parentSize = 0;
        if (parentTable != null && parentTable.length > node && parentTable[node] != null) {
            parentSize = parentTable[node].size();
        }

        if (parentSize == 0) return null;

        int[] nodes = new int[parentSize];
        int offset = 0;
        for (Iterator i = parentTable[node].iterator(); i.hasNext(); offset++) {
            int value = ((Integer) i.next()).intValue();
            nodes[offset] = value;
        }
        return nodes;
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#getScore(java.lang.String, java.util.Set)
     */
    public double getScore(int curNode, int candidate, Set[] parentTable) {
        if (arityCache == null) initializeCache();

        double result = 0.0;

        if (candidate == NO_CANDIDATES) {
            int iArity = arityCache[curNode];
            nodes = prepareNodes(curNode, parentTable);
            for (int i = 0; i < iArity; i++) {
                Tally iTally = tally.createSubTally(curNode, i); // on the fly pre-cache
                double px = ((double) iTally.size()) / tupleSize;
                if (px == 0) continue;
                if (nodes != null) {
                    total = 0;
                    iterateUniqueInstantiation(0, iTally);
                    double qx = (total == 0) ? 1.0 : ((double) total) / tupleSize;
                    result += (px * Math.log(px / qx));
                } else {
                    result += px;
                }
            }
        } else {
            // Pre-caching begins
            int iArity = arityCache[curNode];
            int jArity = arityCache[candidate];
            Tally iTally[] = new TableTally[iArity];
            double[] iqx = new double[iArity];
            double[] jqx = new double[jArity];

            // Calculate PB(Xi | Parent(Xi))
            nodes = prepareNodes(curNode, parentTable);
            for (int i = 0; i < iArity; i++) {
                iTally[i] = tally.createSubTally(curNode, i);
                if (nodes != null) {
                    total = 0;
                    iterateUniqueInstantiation(0, iTally[i]);
                    iqx[i] = ((double) total) / tupleSize;
                } else {
                    iqx[i] = 1;
                }
            }

            // Calculate PB(Xj | Parent(Xj))
            nodes = prepareNodes(candidate, parentTable);
            for (int j = 0; j < jArity; j++) {
                Tally jTally = tally.createSubTally(candidate, j);
                if (nodes != null) {
                    total = 0;
                    iterateUniqueInstantiation(0, jTally);
                    jqx[j] = ((double) total) / tupleSize;
                } else {
                    jqx[j] = 1;
                }
            }
            // Pre-caching ends

            // Calculate P(Xi, Xj)
            for (int i = 0; i < iArity; i++) {
                for (int j = 0; j < jArity; j++) {
                    double pXiXj = ((double) iTally[i].tally(candidate, j)) / tupleSize;

                    if (iqx[i] > 0 && jqx[j] > 0) {
                        result += (pXiXj * Math.log(pXiXj / (iqx[i] * jqx[j])));
                    } else {
                        result += pXiXj * Math.log(pXiXj);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Recursively filter out the data according to the current node
     *
     * @param nodes
     * @param tally
     */
    protected void iterateUniqueInstantiation(int depth, Tally tally) {
        int node = nodes[depth];
        int curArity = arityCache[node];
        if (tally.size() == 0) return;

        for (int i = 0; i < curArity; i++) {
            Tally newTally = tally.createSubTally(node, i);
            int n_ij = newTally.size();
            if (n_ij == 0) continue; // we don't need to go further because total will be added only with zeroes

            if (depth == nodes.length - 1) {
                total += n_ij;
            } else {
                iterateUniqueInstantiation(depth + 1, newTally);
            }
        }
    }

    public void initialize() {
        initializeCache();
        parentTableCache = null;
    }

}
