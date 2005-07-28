package org.geworkbench.bison.algorithm.classification.svm;

import org.geworkbench.bison.algorithm.classification.CSClassifier;
import org.geworkbench.bison.datastructure.biocollections.classification.DSTrainingSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.DSClassification;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public abstract class SVM <T extends DSBioObject> extends CSClassifier<T> {
    public SVM() {
    }

    public DSClassification<T> run(T object) {
        return null;
    }

    public void init(DSTrainingSet<T> db) {
    }

}
