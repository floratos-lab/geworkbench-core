package org.geworkbench.bison.algorithm.classification;

import org.geworkbench.bison.datastructure.bioobjects.DSClassification;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Collection;

/**
 * Implementing classifiers are able to run classifications on objects.
 */
public interface DSClassifier<T extends DSNamed> {

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
    Collection<DSClassification<T>> run(DSItemList<T> db);

}
