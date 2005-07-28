package edu.ksu.cis.bnj.bbn.learning;

/*
 * Created on Wed 19 Feb 2003
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

import java.util.Set;

/**
 * @author Roby Joehanes
 */
public class SimpleStructureScore extends StructureScore {

    /**
     * @param l
     * @param base
     */
    public SimpleStructureScore(Learner l, LearnerScore base) {
        super(l, base);
        assert (base != null);
    }

    /**
     * Basic structure score
     *
     * @see edu.ksu.cis.bnj.bbn.learning.LearnerScore#getScore(java.lang.String, java.util.Set, edu.ksu.cis.kdd.util.TableSet)
     */
    public double getScore(int curNode, int candidate, Set[] parentTable) {
        double score = 0;
        // Ignore curNode and candidate
        int max = parentTable.length;
        for (int i = 0; i < max; i++) {
            score += baseScore.getScore(i, NO_CANDIDATES, parentTable);
        }
        return score;
    }

    public double getScore(Set[] parentTable) {
        return getScore(-1, -1, parentTable);
    }

    public void initialize() {
        baseScore.initialize();
    }
}
