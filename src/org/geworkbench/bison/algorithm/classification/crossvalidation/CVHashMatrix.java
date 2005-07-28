package org.geworkbench.bison.algorithm.classification.crossvalidation;

import java.util.HashMap;
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
public class CVHashMatrix {
    protected Vector dimensionNames = new Vector();
    protected Vector dimensionValues = new Vector();
    protected HashMap valuesMap = new HashMap();

    public CVHashMatrix() {
    }

    public void setDimensionNames(Vector dimensionNames) {
        this.dimensionNames = dimensionNames;
    }

    public void setDimensionValues(Vector dimensionValues) {
        this.dimensionValues = dimensionValues;
    }

    public void setValuesMap(HashMap valuesMap) {
        this.valuesMap = valuesMap;
    }

    public Vector getDimensionNames() {
        return dimensionNames;
    }

    public Vector getDimensionValues() {
        return dimensionValues;
    }

    public HashMap getValuesMap() {
        return valuesMap;
    }

    public void addValue(Vector parameterValues, Object value) {
        Object val1 = parameterValues.get(0);
        HashMap val1Map = (HashMap) valuesMap.get(val1);
        if (val1Map == null) {
            val1Map = new HashMap();
            valuesMap.put(val1, val1Map);
        }
        Object val2 = parameterValues.get(1);
        val1Map.put(val2, value);
    }

    public Object getValue(Vector parameterValues) {
        Object val1 = parameterValues.get(0);
        HashMap val1Map = (HashMap) valuesMap.get(val1);
        Object val2 = parameterValues.get(1);
        return val1Map.get(val2);
    }

    public void addDimensionName(String dimensionName) {
        dimensionNames.add(dimensionName);
    }
}
