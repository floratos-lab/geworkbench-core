package org.geworkbench.bison.util;

import java.util.RandomAccess;
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
public interface DSRandomizer extends Set<Integer>, RandomAccess {
    void init(int maxN);

    Set<Integer> run(int n);
}
