package org.geworkbench.bison.datastructure.bioobjects;

/**
 * Implementing classifications have the notion of a classification and its associated confidence value (p-value).
 */
public interface DSClassification <T> extends DSPValued<T> {

    /**
     * Gets the classification of this object.
     *
     * @return the classification.
     */
    String getClassification();

    /**
     * Sets the classification of this object.
     *
     * @param classification the new classification.
     */
    void setClassification(String classification);
}

