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

/**
 * @author Roby Joehanes
 */
public class CrossOverOp extends GAOp {

    /**
     * For this generic Crossover operator, we need two chromosomes.
     */
    public CrossOverOp() {
        super(2);
    }

    /**
     * @see edu.ksu.cis.kdd.ga.GAOp#apply(edu.ksu.cis.kdd.ga.Chromosome, edu.ksu.cis.kdd.ga.Chromosome, edu.ksu.cis.kdd.ga.Population)
     */
    public Chromosome apply(final Chromosome[] ind) {
        Object temp;
        int chromSize = ind[0].getSize();
        // take the shorter of the two
        if (chromSize > ind[1].getSize()) chromSize = ind[1].getSize();
        Chromosome newInd = (Chromosome) ind[0].clone();

        for (int i = 0; i < chromSize; i++) {
            if (random.nextDouble() < rate) {
                temp = newInd.get(i);
                newInd.set(i, ind[1].get(i));
                ind[1].set(i, temp);
            }
        }
        return newInd;
    }
}
