/*
 * Created 02 Apr 2003 - WHH
 */
package edu.ksu.cis.bnj.bbn.learning.score;

/*
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

import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.kdd.data.Tally;

import java.util.Hashtable;
import java.util.Set;

/**
 * Measure_Score score from Friedman, Nachman, and Pe'er [1999]
 * Measure_Score(X_i, X_j | B) = Score(X_i ; X_j, Pa(X_i), D)
 *
 * @author William H. Hsu
 *         Last modified 03 Apr 2003
 */

public class MaximizeScore extends SparseScore {

    // accumulator variables
    protected Hashtable value2tally;
    protected double total = 0.0;

    // cache variables inherited from SparseScore:
    // nodes, arityCache, tupleSize, attributeSize, maxArity

    protected int curNode;
    protected int nodeArity;

    /**
     * default constructor for MaximizeScore
     */
    public MaximizeScore() {
        super();
    }

    /**
     * constructor for MaximizeScore given a Learner object
     *
     * @param l
     */
    public MaximizeScore(Learner l) {
        super(l);
    }


    protected void precache() {
        // ### store precomputed results - WHH
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#getScore(java.lang.String, java.util.Set)
     */
    public double getScore(int curNode, int candidate, Set[] parentTable) {

        // Precache P(x)
        this.curNode = curNode;
        if (value2tally == null) precache();

        total = 0.0;

        return total;
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

    /**
     * This is just to reset the cache.  Since it is not dependent on
     * tuple numbers, we can simply ignore this.
     *
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#initialize()
     */
    public void initialize() {
    }
}