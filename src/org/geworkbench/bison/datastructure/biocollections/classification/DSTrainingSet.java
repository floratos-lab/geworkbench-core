package org.geworkbench.bison.datastructure.biocollections.classification;

import org.geworkbench.bison.datastructure.bioobjects.DSClassification;

import java.util.List;
import java.util.RandomAccess;

/**
 * This interface defines the general concept of a Training Set.
 * It contains a list of preclassified objects.
 */
public interface DSTrainingSet <T> extends List<DSClassification<T>>, RandomAccess {

    /**
     * Gets the object by index.
     *
     * @param index the index in to the training set.
     * @return the object at the given index.
     */
    T getObject(int index);

    /**
     * Gets the classification of a training set object by index.
     *
     * @param index the index in to the training set.
     * @return the classification of the object at the given index.
     */
    DSClassification<T> getClassification(int index);
}
