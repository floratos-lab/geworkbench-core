package org.geworkbench.bison.algorithm.classification.crossvalidation;

import java.util.Vector;


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
public class CVParameterMatrix extends CVHashMatrix {
    public CVParameterMatrix() {
    }

    public void addParameter(CVParameter2 param) {
        dimensionNames.add(Character.toString(param.m_ParamChar));
        Vector paramVals = new Vector();
        for (int i = 0; i < param.testValues.length; i++) {
            paramVals.add(new Double(param.testValues[i]));
        }
        dimensionValues.add(paramVals);
    }
}
