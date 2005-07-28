/*
 * Created on Apr 3, 2003
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
import edu.ksu.cis.bnj.bbn.learning.LearnerScore;

/**
 * @author Roby Joehanes
 */
public abstract class SparseScore extends LearnerScore {

    // accumulator variables

    // cache variables
    protected int[] nodes;
    protected int[] arityCache;
    protected int tupleSize;
    protected int attributeSize;
    protected int maxArity;
    protected int totalRows;

    public SparseScore() {
        super(null);
    }

    /**
     * @param l
     */
    public SparseScore(Learner l) {
        super(l);
    }

    /**
     * Precaching the arity table and P(Xi, Xj) table.
     */
    protected void initializeCache() {
        // Precache arity table
        // TODO: Revamp this
        //        Table tuples = tally.getTuples();
        //        int idx = 0;
        //        maxArity = 0;
        //        attributeSize = tuples.getAttributes().size();
        //        arityCache = new int[attributeSize];
        //        for (Iterator i = tuples.getAttributes().iterator(); i.hasNext(); idx++) {
        //            Attribute attr = (Attribute) i.next();
        //            int arity = attr.getArity();
        //            arityCache[idx] = arity;
        //            if (maxArity < arity) maxArity = arity;
        //        }
        //        tupleSize = tuples.size();
        //        totalRows = tally.size();
    }
}
