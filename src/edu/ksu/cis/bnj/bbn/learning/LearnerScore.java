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

import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tally;

import java.util.Set;


/**
 * Abstract class for scores in score-based learning.
 *
 * @author Roby Joehanes
 */
public abstract class LearnerScore {

    protected Learner owner;
    protected Tally tally;
    public static final int NO_CANDIDATES = -1;

    public LearnerScore() {
    }

    /**
     * Constructor for LearnerScore.
     */
    public LearnerScore(Learner l) {
        setOwner(l);
    }

    /**
     * Get the score
     *
     * @param curNode     The index of the currently evaluated node
     * @param candidate   The index of the parent candidate
     * @param parentTable The array of Sets of integers. Each integer in the set
     *                    is the index of currently assembled parent of each node.
     * @return double the score
     */
    public abstract double getScore(int curNode, int candidate, Set[] parentTable);

    public void initialize() {
    }

    public void setData(Table t) {
        tally.setUnderlyingData(t);
        initialize();
    }

    public void setOwner(Learner l) {
        owner = l;
        //tally = new Tally(l.getTable());
        tally = l.getData().getTallyer();
    }

    public Tally getTallyer() {
        return tally;
    }
}
