package org.geworkbench.bison.algorithm.classification;

import org.geworkbench.bison.datastructure.biocollections.DataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.DSTrainingSet;
import org.geworkbench.bison.datastructure.bioobjects.DSClassification;
import org.geworkbench.bison.datastructure.bioobjects.DSParameters;

import java.util.Collection;

/**
 * Implementing classifiers are able to run classifications on objects.
 */
public interface DSClassifier <T> {

    /**
     * Runs a classification on the given object.
     *
     * @param object the object to classify.
     * @return a classification of the given object.
     */
    DSClassification<T> run(T object);

    /**
     * Runs classifications on a set of objects.
     *
     * @param db the data set to classify.
     * @return a collection of classifications.
     * @todo - watkin - Is Collection the right interface to use for the return value?
     * At this interface level, there is no contract on how the results are ordered or associated with the input.
     */
    Collection<DSClassification<T>> run(DataSet<T> db);

    /**
     * Sets the parameters for this classifier.
     *
     * @param p the parameters.
     */
    void setParameters(DSParameters p);

    /**
     * Initializes this classifier with a training set.
     *
     * @param db the training set for this classifier.
     */
    void init(DSTrainingSet<T> db);
}
