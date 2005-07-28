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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Roby Joehanes
 */
public class Subpopulation {
    protected Chromosome[] chromosomes;
    protected int size;

    protected LinkedList op = new LinkedList();
    protected LinkedList opProb = new LinkedList();
    protected double opTotalProb = 0.0;
    protected int opSize = 0;
    protected GAOp[] opCache;
    protected double[] opProbCache;

    protected double elitePercentage = 0.2;
    protected int eliteSize;
    protected Fitness fitness;
    protected Constraint constraint = null;
    public static final MersenneTwisterFast random = Settings.random;

    protected Class[] constParam = new Class[]{int.class};

    public Subpopulation(int size, Chromosome chrom, Fitness f) {
        this.size = size;
        setFitness(f);
        chromosomes = new Chromosome[size];
        eliteSize = (int) Math.round(elitePercentage * size);
        try {
            for (int i = 0; i < size; i++) {
                Chromosome ch = chrom.createObject();
                ch.fitness = fitness.getFitness(ch);
                ch.isEvaluated = true;
                chromosomes[i] = ch;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void newGeneration() {
        Arrays.sort(chromosomes);
        Chromosome[] newChrom = new Chromosome[size];
        System.arraycopy(chromosomes, 0, newChrom, 0, eliteSize);

        int index = eliteSize;
        while (index < size) {
            GAOp op = chooseOperator();
            int paramSize = op.parameterSize;
            Chromosome[] params = new Chromosome[paramSize];
            for (int i = 0; i < paramSize; i++) {
                params[i] = chooseChromosome();
            }
            Chromosome newIndividual = op.apply(params);
            if (constraint == null || constraint.isAccepted(newIndividual)) {
                newIndividual.fitness = fitness.getFitness(newIndividual);
                newIndividual.isEvaluated = true;
                newChrom[index] = newIndividual;
                index++;
            }
        }

        chromosomes = newChrom;
    }

    protected Chromosome chooseChromosome() {
        return chromosomes[random.nextInt(eliteSize)];
    }

    protected GAOp chooseOperator() {
        if (opCache == null) precacheOp();
        double d = random.nextDouble() * opTotalProb;

        for (int i = 0; i < opSize; i++) {
            if (d <= opProbCache[i]) {
                return opCache[i];
            }
        }

        return opCache[opSize - 1]; // should never reach here
    }

    public Chromosome getBestChromosome() {
        Arrays.sort(chromosomes);
        return chromosomes[0];
    }

    protected void precacheOp() {
        opSize = op.size();
        opCache = (GAOp[]) op.toArray(new GAOp[opSize]);
        opProbCache = new double[opSize];
        int idx = 0;
        for (Iterator i = opProb.iterator(); i.hasNext(); idx++) {
            opProbCache[idx] = ((Double) i.next()).doubleValue();
        }
    }

    public void addOperator(GAOp op, double p) {
        this.op.add(op);
        this.opProb.add(new Double(p));
        opTotalProb += p;
        opCache = null;
    }

    public void removeOp(GAOp op) {
        int idx = this.op.indexOf(op);
        if (idx == -1) return;
        this.op.remove(op);
        double p = ((Double) opProb.remove(idx)).doubleValue();
        opTotalProb -= p;
        opCache = null;
    }

    /**
     * @return double
     */
    public double getElitePercentage() {
        return elitePercentage;
    }

    /**
     * Sets the elitePercentage.
     *
     * @param elitePercentage The elitePercentage to set
     */
    public void setElitePercentage(double elitePercentage) {
        if (elitePercentage > 1.0)
            elitePercentage = 1.0;
        else if (elitePercentage < 0.0) elitePercentage = 0.0;
        this.elitePercentage = elitePercentage;
        eliteSize = (int) Math.round(size * elitePercentage);
    }

    /**
     * @return Fitness
     */
    public Fitness getFitness() {
        return fitness;
    }

    /**
     * Sets the fitness.
     *
     * @param fitness The fitness to set
     */
    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }

    /**
     * @return Constraint
     */
    public Constraint getConstraint() {
        return constraint;
    }

    /**
     * Sets the constraint.
     *
     * @param constraint The constraint to set
     */
    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

}

