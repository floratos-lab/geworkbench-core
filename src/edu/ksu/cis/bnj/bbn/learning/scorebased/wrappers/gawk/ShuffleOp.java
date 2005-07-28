package edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk;

/*
 * Created on Mon 17 Mar 2003
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

import edu.ksu.cis.kdd.ga.Chromosome;
import edu.ksu.cis.kdd.ga.GAOp;
import edu.ksu.cis.kdd.util.MersenneTwisterFast;
import edu.ksu.cis.kdd.util.Settings;

/**
 * @author Roby Joehanes
 */
class ShuffleOp extends GAOp {

    protected MersenneTwisterFast random = Settings.random;

    /**
     * @param size
     */
    public ShuffleOp() {
        super(1);
    }

    /**
     * @see edu.ksu.cis.kdd.ga.GAOp#apply(edu.ksu.cis.kdd.ga.Chromosome[])
     */
    public Chromosome apply(Chromosome[] ind) {
        GAWKChrom newInd = (GAWKChrom) ind[0].clone();
        int temp;
        int size = ind[0].getSize();
        int max = Settings.random.nextInt(size);
        for (int i = 0; i < max; i++) {
            int pos1 = Settings.random.nextInt(size);
            int pos2 = Settings.random.nextInt(size);
            temp = newInd.elements[pos1];
            newInd.elements[pos1] = newInd.elements[pos2];
            newInd.elements[pos2] = temp;
        }

        return newInd;
    }
}
