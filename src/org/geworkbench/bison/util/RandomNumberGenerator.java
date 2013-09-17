package org.geworkbench.bison.util;

import java.util.Random;

/**
 * A generator for unique ID's.
 */
public class RandomNumberGenerator {
    private static Random r = new Random();

    public static void setSeed(long seed) {
        r.setSeed(seed);
    }

    public static String getID() {
        return new Integer(r.nextInt()).toString();
    }
}