package org.geworkbench.bison.datastructure.bioobjects.classification;

import org.geworkbench.bison.datastructure.bioobjects.DSClassification;

/**
 * A straightforward implementation of {@link DSClassification}.
 */
public class CSClassification <T> implements DSClassification<T> {

    private double pValue = 1.0;
    private T object = null;
    private String classification = "";

    /**
     * Creates a new classification.
     *
     * @param object the object to classify.
     */
    public CSClassification(T object) {
        this.object = object;
    }

    /**
     * Gets the p-value.
     *
     * @return the p-value for this classification.
     */
    public double getPValue() {
        return pValue;
    }

    /**
     * Gets the classified object.
     *
     * @return the object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Gets the classification for this object.
     *
     * @return the classification.
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the classification for this object.
     *
     * @param classification the new classification for this object.
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }
}
