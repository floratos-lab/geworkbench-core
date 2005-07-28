package org.geworkbench.bison.util;

import org.geworkbench.bison.algorithm.DSInput;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Randomizer extends HashSet<Integer> implements DSRandomizer {
    int maxN = 0;

    public void init(@DSInput("int") int n) {
        maxN = n;
    }

    public Set<Integer> run(int n) {
        if (n > maxN) {
            n = maxN;
        }
        for (int i = 0; i < n; i++) {
            int x = (int) (Math.random() * maxN);
            while (contains(x)) {
                x = (int) (Math.random() * maxN);
            }
            add(i);
        }
        return this;
    }
}
