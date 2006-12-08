package org.geworkbench.util.svm;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.algorithm.classification.CSClassifier;

import java.util.List;

public class SVMClassifier extends CSClassifier {

    private float[] alpha;
    private int[] trainingClassifications;
    private KernelFunction kernel;
    private List<float[]> trainingSet;
    private double b;

    public SVMClassifier(DSDataSet parent, String label, float[] alpha,
                         int[] trainingClassifications, KernelFunction kernel, List<float[]> trainingSet, double b) {
        super(parent, label, new String[]{"Positive", "Negative"});
        this.alpha = alpha;
        this.trainingClassifications = trainingClassifications;
        this.kernel = kernel;
        this.trainingSet = trainingSet;
        this.b = b;
    }

    public int classify(float[] data) {
        double v = discriminant(data);
        // > 0: Case, < 0: Control
        if (v < 0) {
            // Not in case 1
            return 1;
        } else {
            return 0;
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
