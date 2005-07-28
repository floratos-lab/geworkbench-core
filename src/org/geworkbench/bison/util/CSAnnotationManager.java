package org.geworkbench.bison.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSAnnotationManager implements DSAnnotationManager {
    HashMap<DSAnnotLabel, ArrayList<DSAnnotValue>> labelValues = new HashMap<DSAnnotLabel, ArrayList<DSAnnotValue>>();

    public CSAnnotationManager() {
    }

    public void addLabelValue(DSAnnotLabel label, DSAnnotValue value) {
        ArrayList<DSAnnotValue> values = labelValues.get(label);
        if (values == null) {
            values = new ArrayList<DSAnnotValue>();
            labelValues.put(label, values);
        }
        if (!values.contains(value)) {
            values.add(value);
        }
    }

    public int getLabelValueNo(DSAnnotLabel label) {
        ArrayList<DSAnnotValue> values = labelValues.get(label);
        if (values != null) {
            return values.size();
        }
        return 0;
    }

    public DSAnnotValue getLabelValue(DSAnnotLabel label, int i) {
        ArrayList<DSAnnotValue> values = labelValues.get(label);
        if ((values != null) && (values.size() > i)) {
            return values.get(i);
        }
        return null;
    }

    public List<DSAnnotValue> getLabelValues(DSAnnotLabel label) {
        ArrayList<DSAnnotValue> values = labelValues.get(label);
        return values;
    }

    public int size() {
        return labelValues.keySet().size();
    }
}
