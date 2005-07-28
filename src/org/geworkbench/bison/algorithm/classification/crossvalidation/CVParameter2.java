package org.geworkbench.bison.algorithm.classification.crossvalidation;

import java.io.Serializable;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CVParameter2 implements Serializable {
    public CVParameter2(char m_ParamChar, double[] testValues) {
        this.m_ParamChar = m_ParamChar;
        this.testValues = testValues;
    }

    /*
     * A data structure to hold values associated with a single
     * cross-validation search parameter
     */

    /**
     * Char used to identify the option of interest
     */
    public char m_ParamChar;

    public double[] testValues;

    /**
     * The parameter value with the best performance
     */
    public double m_ParamValue;


}
