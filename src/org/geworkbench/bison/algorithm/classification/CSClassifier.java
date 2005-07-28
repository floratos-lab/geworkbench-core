package org.geworkbench.bison.algorithm.classification;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.DSClassification;
import org.geworkbench.bison.datastructure.bioobjects.DSParameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A straightforward, partial implementation of {@link DSClassifier}.
 */
public abstract class CSClassifier <T extends DSBioObject> implements DSClassifier<T> {

    private DSParameters parameters = null;
    private DSDataSet database = null;

    /**
     * Runs the classifier on the provided data set.
     *
     * @param db the data set on which to run the classifier.
     * @return a collection containing classifications for each member of the data set.
     */
    public Collection<DSClassification<T>> run(DSDataSet<T> db) {
        ArrayList<DSClassification> results = new ArrayList<DSClassification>();
        for (T object : db) {
            results.add(run(object));
        }
        return null;
    }

    /**
     * Sets the parameters of this classifer.
     *
     * @param parameters the new parameters for this classifier.
     */
    public void setParameters(DSParameters parameters) {
        this.parameters = parameters;
    }

    /**
     * @param db
     * @todo - watkin - this does not realize {@link DSClassifier#init(org.geworkbench.bison.datastructure.biocollections.classification.DSTrainingSet<T>)}!
     */
    public void init(DSDataSet db) {
        database = db;
    }
}
