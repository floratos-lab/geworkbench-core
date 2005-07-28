/*
 * Created on Wed 12 Mar 2003
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
package edu.ksu.cis.kdd.ga;

/**
 * @author Roby Joehanes
 *         Last updated Fri 18 Apr 2003
 */
public abstract class Chromosome implements Cloneable, Comparable {

    public static final Class baseClass = Chromosome.class;

    protected Object[] object;
    protected boolean isEvaluated = false;
    protected double fitness;
    protected int size;

    public Chromosome(int size) {
        this.size = size;
    }

    public abstract Object clone();

    public abstract Chromosome createObject();

    public abstract boolean equals(Object other);

    public Object get(int i) {
        return object[i];
    }

    public void set(int i, Object o) {
        object[i] = o;
    }

    public int getSize() {
        return size;
    }

    /**
     * @return Fitness
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * @return boolean
     */
    public boolean isEvaluated() {
        return isEvaluated;
    }

    /**
     * Sets the fitness.
     *
     * @param fitness The fitness to set
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Sets the isEvaluated.
     *
     * @param isEvaluated The isEvaluated to set
     */
    public void setEvaluated(boolean isEvaluated) {
        this.isEvaluated = isEvaluated;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        double diff = ((Chromosome) o).fitness - fitness;
        return diff > 0.0 ? 1 : diff < 0.0 ? -1 : 0;
    }

}
