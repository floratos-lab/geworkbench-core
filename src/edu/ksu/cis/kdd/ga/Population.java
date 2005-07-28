/*
 * Created on Mar 14, 2003
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

/**
 * @author Roby Joehanes
 */
public class Population {
    public Subpopulation[] subpop;

    public void evolve(int generations) {
        int maxSubPop = subpop.length;
        for (int i = 0; i < generations; i++) {
            for (int j = 0; j < maxSubPop; j++) {
                subpop[j].newGeneration();
            }
            //Chromosome c = getBestChromosome();
            //System.out.println("Generation "+i+", best individual = "+c+", score = "+c.fitness);
        }
    }

    public Chromosome getBestChromosome() {
        Chromosome c = null;
        int maxSubPop = subpop.length;

        for (int i = 0; i < maxSubPop; i++) {
            Chromosome b = subpop[i].getBestChromosome();
            if (c == null || c.fitness < b.fitness) {
                c = b;
            }
        }
        return c;
    }
}
