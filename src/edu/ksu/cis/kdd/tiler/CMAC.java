package edu.ksu.cis.kdd.tiler;

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

import edu.ksu.cis.kdd.util.Settings;

/**
 * <P>Adaptation of Rich Sutton's CMAC tiler v2.0. See:<BR>
 * <a href="http://www-anw.cs.umass.edu/~rich/tiles2.html">
 * http://www-anw.cs.umass.edu/~rich/tiles2.html</a>
 * <p/>
 * <P>Basic version.
 *
 * @author Roby Joehanes
 */
public class CMAC {

    private int[] rand = null;
    public static CMAC v = new CMAC();

    /**
     * Default constructor with 2048 entries of random numbers
     *
     * @see java.lang.Object#Object()
     */
    public CMAC() {
        this(2048);
    }

    public CMAC(int randomsize) {
        assert (randomsize > 0);
        rand = new int[randomsize];

        for (int i = 0; i < randomsize; i++) {
            rand[i] = Settings.random.nextInt();
        }
    }

    /**
     * @see getTiles(int, int, double[], int[])
     */
    public int[] getTiles(int numTile, int maxVarSize, double[] nums) {
        return getTiles(numTile, maxVarSize, nums, null);
    }

    /**
     * @see getTiles(int, int, double[], int[])
     */
    public int[] getTiles(int numTile, int maxVarSize, double[] nums, int h1) {
        return getTiles(numTile, maxVarSize, nums, new int[]{h1});
    }

    /**
     * Do the CMAC tiling.
     *
     * @param numTile      The number of tile elements to output (corresponds to the
     *                     num_tilings)
     * @param maxVarSize   The maximum number elements if there were no tilings
     *                     (corresponds to the memory_size), try to make it a power of two if
     *                     possible.
     * @param nums         The numbers to tile (corresponds to floats)
     * @param hashModifier An array of integers to modify the hashing
     *                     procedures. If null, then we do the default hashing. (corresponds to
     *                     ints).
     * @return int[] The tiles (corresponds to the parameter tiles)
     */
    public int[] getTiles(int numTile, int maxVarSize, double[] nums, int[] hashModifier) {
        assert(nums != null && nums.length > 0 && maxVarSize > 0 && numTile > 0 && maxVarSize >= numTile);

        int max = nums.length;
        int[] tiles = new int[numTile];

        int[] qstate = new int[max];
        int[] base = new int[max];
        int[] coord = new int[(max << 1) + 1];

        for (int i = 0; i < max; i++) {
            qstate[i] = (int) Math.floor(nums[i] * numTile);
            base[i] = 0;
        }

        if (hashModifier != null) {
            for (int i = 0; i < hashModifier.length; i++) {
                coord[max + i] = hashModifier[i];
            }
        }

        for (int j = 0; j < numTile; j++) {
            for (int i = 0; i < max; i++) {
                if (qstate[i] >= base[i])
                    coord[i] = qstate[i] - ((qstate[i] - base[i]) % numTile);
                else
                    coord[i] = qstate[i] + 1 + ((base[i] - qstate[i] - 1) % numTile);
                base[i] = 1 + i + i;
            }
            coord[max] = j;
            tiles[j] = hash(coord, maxVarSize, 449);
        }

        return tiles;
    }

    /**
     * UNH hashing procedure. It utilizes the variable rand[], array of random
     * numbers. I implemented the random call using MersenneTwisterFast.
     *
     * @param num      The number arrays to determine the hashing
     * @param maxValue The maximum value of the result
     * @param incr     The increment parameter
     * @return int The hash value output
     */
    protected int hash(int[] num, int maxValue, int incr) {
        assert (num != null && num.length > 0);

        long sum = 0;
        int index = 0;
        int max = num.length;
        int rlen = rand.length;

        for (int i = 0; i < max; i++) {
            index = (num[i] + incr * i) % rlen;
            while (index < rlen) index += rlen;
            sum += rand[index];
        }
        index = (int) (sum % maxValue);
        while (index < 0) index += maxValue;

        return index;
    }
}
