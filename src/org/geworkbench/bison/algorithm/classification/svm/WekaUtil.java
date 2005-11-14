package org.geworkbench.bison.algorithm.classification.svm;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

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
 * @author not attributable
 * @version 3.0
 */
public class WekaUtil {
    public WekaUtil() {
    }

    public Instances normalizeInstances(Instances origInstances) {
        try {
            Filter filter = new Normalize();
            filter.setInputFormat(origInstances);
            for (int i = 0; i < origInstances.numInstances(); i++) {
                filter.input(origInstances.instance(i));
            }
            filter.batchFinished();
            Instances newData = filter.getOutputFormat();
            Instance processed;
            while ((processed = filter.output()) != null) {
                newData.add(processed);
            }
            return newData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
