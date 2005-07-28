package edu.ksu.cis.kdd.classifier.estimator;

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

import edu.ksu.cis.kdd.classifier.Classifier;
import edu.ksu.cis.kdd.data.Tuple;

/**
 * @author Roby Joehanes
 */
public class LaplaceEstimator extends Estimator {

    private double m = 0.005;

    public LaplaceEstimator(Classifier o) {
        super(o);
    }

    /**
     * @see edu.ksu.cis.kdd.data.estimator.Estimator#estimate(Tuple, Object)
     */
    public double estimate(Tuple t, Object classValue) {
        return (owner.getClassTally(classValue) + m * (1.0 / owner.getClassSize())) / (owner.getTupleSize() + m);
    }

    /**
     * Returns the m.
     *
     * @return double
     */
    public double getM() {
        return m;
    }

    /**
     * Sets the m.
     *
     * @param m The m to set
     */
    public void setM(double m) {
        this.m = m;
    }

}
