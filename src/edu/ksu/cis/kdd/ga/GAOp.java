/*
 * Created on Mar 12, 2003
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
package edu.ksu.cis.kdd.ga;

import edu.ksu.cis.kdd.util.MersenneTwisterFast;
import edu.ksu.cis.kdd.util.Settings;

/**
 * @author Roby Joehanes
 */
public abstract class GAOp {
    protected int parameterSize = 1;
    protected final MersenneTwisterFast random = Settings.random;
    protected double rate = 0.5;

    /**
     * The GA Operator constructor
     *
     * @param size The number of arguments (chromosomes) needed for this GA Operator
     */
    public GAOp(int size) {
        parameterSize = size;
    }

    /**
     * @return double
     */
    public final double getRate() {
        return rate;
    }

    /**
     * Sets the rate.
     *
     * @param rate The rate to set
     */
    public final void setRate(double rate) {
        this.rate = rate > 1.0 ? 1.0 : rate < 0.0 ? 0.0 : rate;
    }

    public abstract Chromosome apply(final Chromosome[] ind);

    public final int getParameterSize() {
        return parameterSize;
    }
}
