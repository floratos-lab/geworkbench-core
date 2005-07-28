package edu.ksu.cis.bnj.bbn.learning;

/*
 * Created on Feb 19, 2003
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

/**
 * @author Roby Joehanes
 */
public abstract class StructureScore extends LearnerScore {

    protected LearnerScore baseScore;

    /**
     * @param l
     */
    public StructureScore(Learner l) {
        super(l);
    }

    /**
     * @param l
     */
    public StructureScore(Learner l, LearnerScore base) {
        super(l);
        setBaseScore(base);
    }

    /**
     * Sets the baseScore.
     *
     * @param baseScore The baseScore to set
     */
    public void setBaseScore(LearnerScore baseScore) {
        this.baseScore = baseScore;
    }

}
