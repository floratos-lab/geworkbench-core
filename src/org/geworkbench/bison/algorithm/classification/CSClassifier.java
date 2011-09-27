package org.geworkbench.bison.algorithm.classification;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

/**
 * Implementing classifiers are able to run classifications on objects.
 * 
 * @version $Id$
 */
@SuppressWarnings("rawtypes")
public abstract class CSClassifier extends CSAncillaryDataSet {

	private static final long serialVersionUID = -735452597517969294L;
	
	private String[] classifications;

    @SuppressWarnings("unchecked")
	protected CSClassifier(DSDataSet<?> parent, String label, String[] classifications) {
        super(parent, label);
        this.classifications = classifications;
    }

    /**
     * Runs a classification on the given object.
     *
     * @param data the data to classify.
     * @return a classification of the given object, which is an index in to the array returned by {@link org.geworkbench.bison.algorithm.classification.CSClassifier#getClassifications()}
     */
    public abstract int classify(float[] data);

    /**
     * The array of class names that this classifier supports. At least two classes must be present.
     * @return
     */
    public String[] getClassifications() {
        return classifications;
    }

    /**
     * Not used.
     */
    public File getDataSetFile() {
        return null;
    }

    /**
     * Not used.
     */
    public void setDataSetFile(File file) {
        // no-op
    }

}
