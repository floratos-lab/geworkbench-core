package org.geworkbench.bison.datastructure.biocollections;

import java.util.ArrayList;

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
 *
 * This class represents a data set over which to run algorithms.
 * @todo - watkin - The name of this class conflicts with {@link DSDataSet}, resulting in confusion.
 */
public class DataSet <T> extends ArrayList<T> {
    public DataSet() {
        DSDataSet t = null;
    }
}
