package org.geworkbench.bison.datastructure.biocollections.classification;

import org.geworkbench.bison.datastructure.bioobjects.DSClassification;

import java.util.ArrayList;

/**
 * An interface for a training set for a classifier.
 *
 * @see org.geworkbench.bison.algorithm.classification.DSClassifier
 */
public class TrainingSet <T> extends ArrayList<DSClassification<T>> implements DSTrainingSet<T> {

    /**
     * Get the training object.
     *
     * @param index the index of the training object.
     * @return the object at the specified index.
     */
    public T getObject(int index) {
        return get(index).getObject();
    }

    /**
     * Gets the classification of an object.
     *
     * @param index the index of the training object.
     * @return the classification of the object at the specified index.
     */
    public DSClassification<T> getClassification(int index) {
        return get(index);
    }
}
