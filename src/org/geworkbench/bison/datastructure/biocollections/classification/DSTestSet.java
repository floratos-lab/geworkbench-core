package org.geworkbench.bison.datastructure.biocollections.classification;

import java.util.List;
import java.util.RandomAccess;

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
public interface DSTestSet <T> extends List<T>, RandomAccess {
    T getObject(int i);
}
