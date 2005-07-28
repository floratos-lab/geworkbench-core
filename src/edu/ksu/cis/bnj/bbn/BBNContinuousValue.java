package edu.ksu.cis.bnj.bbn;

/*
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
 * 
 */

import edu.ksu.cis.kdd.util.Settings;

/**
 * @author Roby Joehanes
 */
public class BBNContinuousValue implements BBNValue, Cloneable {
    public static final int UNIFORM = 0;
    public static final int GAUSSIAN = 1;
    public static final int NORMAL = 1;
    public static final int POISSON = 2;
    public static final String[] typeString = {"uniform", "normal", "poisson"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    protected double mean = 0;
    protected double variance = 1;
    protected int type = UNIFORM;

    public BBNContinuousValue() {
    }

    public BBNContinuousValue(double newMean, double newVariance) {
        setMean(newMean);
        setVariance(newVariance);
    }

    public BBNContinuousValue(double newLowerBound, double newUpperBound, int type) {
        this((newUpperBound + newLowerBound) / 2.0, Math.abs(newUpperBound - newLowerBound) / 2.0);
        setType(type);
    }

    /**
     * Returns the lowerBound.
     *
     * @return double
     */
    public double getLowerBound() {
        return mean - variance;
    }

    /**
     * Returns the upperBound.
     *
     * @return double
     */
    public double getUpperBound() {
        return mean + variance;
    }

    public boolean contains(Object d) {
        if (!(d instanceof Double)) return false;

        double n = ((Double) d).doubleValue();
        return (getLowerBound() <= n) && (n <= getUpperBound());
    }

    /**
     * Generate a value out of the continuous value
     *
     * @return double
     */
    public double generateValue() {
        switch (type) {
            case NORMAL:
                return Settings.random.nextGaussian() * variance + mean;
            case POISSON:
                throw new RuntimeException("Not implemented yet");
            default:  // Uniform
                return Settings.random.nextDouble() * 2 * variance + mean;
        }
    }

    public boolean equals(Object o) {
        return (o instanceof BBNContinuousValue) && (((BBNContinuousValue) o).mean == mean) && (((BBNContinuousValue) o).variance == variance) && (((BBNContinuousValue) o).type == type);
    }

    /**
     * Standard toString
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        switch (type) {
            case UNIFORM:
                return "uniform (" + getLowerBound() + ", " + getUpperBound() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            case NORMAL:
                return "normal (" + mean + ", " + variance + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            case POISSON:
                return "poisson (" + mean + ", " + variance + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        throw new RuntimeException("Unexpected format!");
    }

    /**
     * Same as toString().hashCode()
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    public Object clone() {
        return new BBNContinuousValue(mean, variance, type);
    }

    /**
     * Returns the type.
     *
     * @return int
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type The type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the mean.
     *
     * @return double
     */
    public double getMean() {
        return mean;
    }

    /**
     * Returns the variance.
     *
     * @return double
     */
    public double getVariance() {
        return variance;
    }

    /**
     * Sets the mean.
     *
     * @param mean The mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * Sets the variance.
     *
     * @param variance The variance to set
     */
    public void setVariance(double variance) {
        this.variance = variance;
    }

}
