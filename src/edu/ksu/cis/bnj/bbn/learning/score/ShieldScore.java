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
import edu.ksu.cis.kdd.data.TableTally;
import edu.ksu.cis.kdd.data.Tally;

import java.util.Iterator;
import java.util.Set;

/**
 * Measure_Shield score from Friedman, Nachman, and Pe'er [1999]
 * Measure_Shield(X_i, X_j | B) = I(X_i ; X_j, Pa(X_i))
 *
 * @author Roby Joehanes
 *         <p/>
 *         Last modified 05 Apr 2003
 */

public class ShieldScore extends SparseScore {

    // cache variables inherited from SparseScore:
    // nodes, arityCache, tupleSize, attributeSize, maxArity

    /**
     * default constructor for ShieldScore
     */
    public ShieldScore() {
        super();
    }

    /**
     * constructor for ShieldScore given a Learner object
     *
     * @param l
     */
    public ShieldScore(Learner l) {
        super(l);
    }

    /**
     * calculate mutual information I(X_i ; X_j, Pa(X_i)) =
     * D_KL(\hat P(X_i, X_j, Pa(X_i)) || \hat P(X_i) * \hat P(X_j, Pa(X_i))) =
     * \Sum_{X_i, X_j, Pa(X_i)} \hat P(X_i, X_j, Pa(X_i)) log \frac{\hat P(X_i, X_j, Pa(X_i))}{\hat P(X_i) * \hat P(X_j, Pa(X_i))}
     *
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#getScore(java.lang.String, java.util.Set)
     */
    public double getScore(int X_i, int X_j, Set[] parentTable) {
        double result = 0.0;

        nodes = new int[parentTable[X_i].size()];
        int index = 0;

        // prepare list of parent nodes
        for (Iterator i = parentTable[X_i].iterator(); i.hasNext(); index++) {
            Integer in = (Integer) i.next();
            nodes[index] = in.intValue();
        }

        // locals for computing P(X_i), P(X_j, Pa(X_i)), and P(X_i, X_j, Pa(X_i))
        int iArity = arityCache[X_i];
        Tally iTally[] = new Tally[iArity];     // LHS of || in K-L div

        for (int i = 0; i < iArity; i++)
            iTally[i] = tally.createSubTally(X_i, i);   // calculate P(X_i)

        if (X_j != NO_CANDIDATES) {
            int jArity = arityCache[X_j];
            Tally jTally[] = new TableTally[jArity];

            for (int j = 0; j < jArity; j++)
                jTally[j] = tally.createSubTally(X_j, j);   // calculate P(X_j)

            for (int i = 0; i < iArity; i++) {
                for (int j = 0; j < jArity; j++) {
                    // compute P(X_i, Pa(X_i))
                    int pXjCommaPaXi;
                    if (nodes.length > 0) {
                        pXjCommaPaXi = iterateUniqueInstantiation(0, jTally[j], 0);
                    } else {
                        pXjCommaPaXi = jTally[j].size();
                    }
                    if (pXjCommaPaXi < 1) continue;

                    Tally iTallyFilteredWithj = iTally[i].createSubTally(X_j, j);
                    // compute P(X_i, X_j, Pa(X_i))
                    int pXiXjCommaPaXi;

                    if (nodes.length > 0) {
                        pXiXjCommaPaXi = iterateUniqueInstantiation(0, iTallyFilteredWithj, 0);
                    } else {
                        pXiXjCommaPaXi = iTallyFilteredWithj.size();
                    }
                    if (pXiXjCommaPaXi < 1) continue;

                    result += (pXiXjCommaPaXi * 1.0 / totalRows) * Math.log(((double) pXiXjCommaPaXi * totalRows) / (iTally[i].size() * pXjCommaPaXi));
                }
            }
        } else {
            for (int i = 0; i < iArity; i++) {
                // compute P(Pa(X_i))
                int pPaXi;
                if (nodes.length > 0) {
                    pPaXi = iterateUniqueInstantiation(0, tally, 0);
                } else {
                    pPaXi = iTally[i].size();
                }
                if (pPaXi < 1) continue;

                // compute P(X_i, Pa(X_i))
                int pXiCommaPaXi;

                if (nodes.length > 0) {
                    pXiCommaPaXi = iterateUniqueInstantiation(0, iTally[i], 0);
                } else {
                    pXiCommaPaXi = iTally[i].size();
                }
                if (pXiCommaPaXi < 1) continue;

                result += (pXiCommaPaXi * 1.0 / totalRows) * Math.log(((double) pXiCommaPaXi * totalRows) / (iTally[i].size() * pPaXi));
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
    protected int iterateUniqueInstantiation(int depth, Tally tally, int total) {
        int node = nodes[depth];
        int curArity = arityCache[node];
        if (tally.size() == 0) return total;

        for (int i = 0; i < curArity; i++) {
            Tally newTally = tally.createSubTally(node, i);
            int n_ij = newTally.size();
            if (n_ij == 0) continue; // we don't need to go further because total will be added only with zeroes

            if (depth == nodes.length - 1) {
                total += n_ij;
            } else {
                iterateUniqueInstantiation(depth + 1, newTally, total);
            }
        }
        return total;
    }

    /**
     * This is just to reset the cache.  Since it is not dependent on
     * tuple numbers, we can simply ignore this.
     *
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#initialize()
     */
    public void initialize() {
        initializeCache();
    }
}