package org.geworkbench.util.svm;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.algorithm.classification.Classifier;

import java.util.List;
import java.util.ArrayList;

public class SVMPhenotypeClassifier extends Classifier<DSMicroarray> {

    private float[] alpha;
    private int[] trainingClassifications;
    private KernelFunction kernel;
    private List<float[]> trainingSet;
    private double b;

    public SVMPhenotypeClassifier(DSDataSet parent, String label, float[] alpha,
                                  int[] trainingClassifications, KernelFunction kernel, List<float[]> trainingSet, double b) {
        super(parent, label, new String[]{"case", "control"});
        this.alpha = alpha;
        this.trainingClassifications = trainingClassifications;
        this.kernel = kernel;
        this.trainingSet = trainingSet;
        this.b = b;
    }

    public String classify(DSMicroarray array) {
        double v = discriminant(array.getRawMarkerData());
        if (v < 0) {
            return getClassifications()[0];
        } else {
            return getClassifications()[1];
        }
    }

    private double discriminant(float[] input) {
        double v = 0;
        for (int i = 0; i < trainingClassifications.length; i++) {
            if (alpha[i] > 0) {
                v += alpha[i] * trainingClassifications[i] * kernel.eval(input, trainingSet.get(i));
            }
        }
        return v - b;
    }

}
