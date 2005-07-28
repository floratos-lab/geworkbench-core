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
import edu.ksu.cis.kdd.util.Settings;

/**
 * GAWK = Genetic Algorithm Wrapper for K2
 *
 * @author Roby Joehanes
 * @author William H. Hsu
 *         Last updated Fri 18 Apr 2003
 */
class GAWKChrom extends Chromosome {

    int[] elements;

    /**
     * @param size
     */
    public GAWKChrom(int size) {
        super(size);
        elements = new int[size];

        for (int i = 0; i < size; i++)
            elements[i] = i;

        // shuffle
        int temp;
        int max = Settings.random.nextInt(size);
        for (int i = 0; i < max; i++) {
            int pos1 = Settings.random.nextInt(size);
            int pos2 = Settings.random.nextInt(size);
            temp = elements[pos1];
            elements[pos1] = elements[pos2];
            elements[pos2] = temp;
        }
    }

    /**
     * @param perm, size
     *              constructor that takes a specified permutation - WHH 18 Apr 2003
     */
    public GAWKChrom(int perm[], int size) {
        super(size);
        elements = new int[size];

        for (int i = 0; i < size; i++)
            elements[i] = perm[i];
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        GAWKChrom c = new GAWKChrom(size);
        c.elements = new int[size];
        System.arraycopy(elements, 0, c.elements, 0, size);
        return c;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (!(other instanceof GAWKChrom)) return false;
        GAWKChrom c = (GAWKChrom) other;
        if (c.size != size) return false;
        for (int i = 0; i < size; i++) {
            if (elements[i] != c.elements[i]) return false;
        }

        return true;
    }

    /**
     * @see edu.ksu.cis.kdd.ga.Chromosome#createObject()
     */
    public Chromosome createObject() {
        return new GAWKChrom(size);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < size; i++) {
            buf.append(elements[i] + " ");
        }
        return buf.toString().trim() + "]";
    }

}
