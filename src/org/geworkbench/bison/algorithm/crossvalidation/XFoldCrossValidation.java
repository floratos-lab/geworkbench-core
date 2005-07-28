package org.geworkbench.bison.algorithm.crossvalidation;

import org.geworkbench.bison.algorithm.DSInput;
import org.geworkbench.bison.algorithm.DSOutput;
import org.geworkbench.bison.algorithm.classification.DSClassifier;
import org.geworkbench.bison.datastructure.biocollections.DataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.DSTrainingSet;
import org.geworkbench.bison.datastructure.biocollections.classification.TrainingSet;
import org.geworkbench.bison.datastructure.bioobjects.DSClassification;
import org.geworkbench.bison.datastructure.bioobjects.DSParameters;
import org.geworkbench.bison.datastructure.exceptions.DSInitException;
import org.geworkbench.bison.datastructure.exceptions.DSRunException;
import org.geworkbench.bison.util.Randomizer;

import java.util.Collection;

/**
 * This Class performs universal X-fold validation given
 * a Classifier   - received via the init method
 * a Training Set - received via the run method
 * <p/>
 * it produces a Collection of Classifications with one
 * Classification for each object classified. each time
 * the run method is called, a randomization of X
 * object in the training set is performed. These are
 * inserted into a temporary TestSet and removed from
 * the TrainingSet. The classifier is trained on the
 * partial TrainingSet and the TestSet is then classified
 * The results are returned. By running this method
 * several times, an ROC (Receiver Operating Curve)
 * or an RPC (Recall Precision Curve) can be produced.
 */
public class XFoldCrossValidation <T> implements DSCrossValidation<T> {

    private DSClassifier<T> classifier = null;
    private DSParameters parameters = null;
    private int foldSize = 1;

    /**
     * Creates a new XFoldCrossValidation.
     */
    public XFoldCrossValidation() {
    }

    /**
     * Initializes the validator with a classifier.
     *
     * @param classifier the classifier with which to validate.
     * @throws DSInitException never.
     */
    public void init(DSClassifier<T> classifier) throws DSInitException {
        this.classifier = classifier;
    }

    /**
     * Runs X-fold validation on the given trainingSet set.
     *
     * @param trainingSet the training set on which to perform the cross-validation.
     * @return the classification resulting from the x-fold validation run.
     * @throws DSRunException never.
     */
    @DSOutput("Collection<DSClassification<T>>") public Collection<DSClassification<T>> run(@DSInput("DSTrainingSet<T>>") DSTrainingSet<T> trainingSet) throws DSRunException {
        // Create temporary test and trainingSet sets
        DSTrainingSet<T> tmpTrainingSet = new TrainingSet<T>();
        DataSet<T> tmpTestSet = new DataSet<T>();

        // Create a foldSize randomization
        Randomizer r = new Randomizer();
        r.init(trainingSet.size());
        r.run(foldSize);

        // Assemble the temporary trainingSet and test set
        for (int i = 0; i < trainingSet.size(); i++) {
            if (!r.contains(i)) {
                tmpTrainingSet.add(trainingSet.get(i));
            } else {
                tmpTestSet.add(trainingSet.get(i).getObject());
            }
        }
        classifier.init(tmpTrainingSet);
        return classifier.run(tmpTestSet);
    }

    /**
     * Sets the parameters for this x-fold validation.
     *
     * @param parameters the value {@link org.geworkbench.bison.datastructure.bioobjects.DSParameters#getCDENo()} will become the fold size.
     */
    public void setParameters(DSParameters parameters) {
        this.parameters = parameters;
        if (parameters.getCDENo() > 0) {
            foldSize = Integer.parseInt(parameters.getCDE(0));
        }
    }
}
