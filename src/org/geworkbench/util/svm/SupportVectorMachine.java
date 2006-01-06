package org.geworkbench.util.svm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author John Watkinson
 */
public class SupportVectorMachine {

    private static final double EPSILON = 0.001;

    private static final Log log = LogFactory.getLog(SupportVectorMachine.class);

    private static final Random RANDOM = new Random(0);

    private List<float[]> trainingSet;
    private int[] trainingClassifications;
    private KernelFunction kernel;
    private int maxIterations;
    private double convergenceThreshold;

    private float[] alpha;
    private float lambda;
    private double b;
    private int n;
    private int nPos, nNeg;

    /**
     * Computes A·B + 1
     */
    public static final KernelFunction LINEAR_KERNAL_FUNCTION = new KernelFunction() {
        public double eval(float[] a, float[] b) {
            int n = a.length;
            double dot = 0;
            for (int i = 0; i < n; i++) {
                dot += a[i] * b[i];
            }
//            double manual = (dot + 1) * (dot +1);
            return dot + 1;
//            if (manual != pow) {
//                log.error("Not equal");
//            }
//            return manual;
        }
    };

    public SupportVectorMachine(List<float[]> caseList, List<float[]> controlList, KernelFunction kernel, float lambdaFactor) {
        this.lambda = lambdaFactor;
        int caseSize = caseList.size();
        if (caseSize == 0) {
            throw new RuntimeException("Must have at least one case.");
        }
        int controlSize = controlList.size();
        if (controlSize == 0) {
            throw new RuntimeException("Must have at least one control.");
        }
        n = caseSize + controlSize;
        trainingSet = new ArrayList<float[]>(n);
        trainingSet.addAll(caseList);
        trainingSet.addAll(controlList);
        trainingClassifications = new int[n];
        nPos = 0;
        nNeg = 0;
        for (int i = 0; i < n; i++) {
            if (i < caseSize) {
                trainingClassifications[i] = 1;
                nPos++;
            } else {
                trainingClassifications[i] = -1;
                nNeg++;
            }
        }
        this.kernel = kernel;
    }

    public void buildSupportVectors(int maxIterations, double convergenceThreshold) {
        this.maxIterations = maxIterations;
        this.convergenceThreshold = convergenceThreshold;
        this.compute();
    }

    public void buildSupportVectorsSMO(float c) {
        this.c = c;
        computeSMO();
    }

    private double discriminant(float[] input) {
        double v = 0;
        for (int i = 0; i < n; i++) {
            if (alpha[i] > 0) {
                v += alpha[i] * trainingClassifications[i] * kernel.eval(input, trainingSet.get(i));
            }
        }
        return v - b;
    }

    private double biasedDiscriminant(int index) {
        double v = 0;
        for (int i = 0; i < n; i++) {
            if (alpha[i] > 0) {
                v += alpha[i] * trainingClassifications[i] * biasedKernelEval(index, i);
            }
        }
        return v - b;
    }

    private double objective() {
        double v = 0;
        for (int i = 0; i < n; i++) {
            if (alpha[i] > 0) {
                v += alpha[i] * (2 - trainingClassifications[i] * discriminant(trainingSet.get(i)));
            }
        }
        return v;
    }

    private void compute() {
        alpha = new float[n];
        // Initialize to 0.5
        for (int i = 0; i < n; i++) {
            alpha[i] = 0.5f;
        }
        double lastObjective = Double.NEGATIVE_INFINITY;
        double objectiveVal = objective();
        int iteration = 0;
        while ((Math.abs(objectiveVal - lastObjective) > convergenceThreshold) && (iteration < maxIterations)) {
            for (int i = 0; i < n; i++) {
                float[] item = trainingSet.get(i);
                double kernelEval = kernel.eval(item, item);
                double newLambda = (1 - trainingClassifications[i] * discriminant(item) + alpha[i] * kernelEval) / kernelEval;
                if (newLambda < 0) {
                    alpha[i] = 0;
                } else if (newLambda > 1) {
                    alpha[i] = 1;
                } else {
                    alpha[i] = (float) newLambda;
                }
            }
            iteration++;
            lastObjective = objectiveVal;
            objectiveVal = objective();
            log.debug("Iteration " + iteration + " had delta " + Math.abs(objectiveVal - lastObjective));
        }
    }

    /**
     * Evaluates an input using this SVM.
     *
     * @return <tt>true</tt> if the input is evaluated as a case, <tt>false</tt> otherwise.
     */
    public boolean evaluate(float[] input) {
        double v = discriminant(input);
        if (v < 0) {
            return false;
        } else {
            return true;
        }

    }

    //// SEQUENTIAL MINIMAL OPTIMIZATION

    private float[] errorCache;

    private float c;

    private float computeError(int index) {
        return (float) (biasedDiscriminant(index) - trainingClassifications[index]);
    }

    private void recacheErrors(int i1, int i2) {
        errorCache[i1] = 0;
        errorCache[i2] = 0;
        for (int i = 0; i < n; i++) {
            if ((i == i1) || (i == i2)) {
                continue;
            }
            if ((alpha[i] > 0) && (alpha[i] < c)) {
                errorCache[i] = computeError(i);
            }
        }
    }

    private float getError(int index) {
        if ((alpha[index] > 0) && (alpha[index] < c)) {
            return errorCache[index];
        } else {
            return computeError(index);
        }
    }

    private double biasedKernelEval(int i1, int i2) {
        double bias = 0;
        if (i1 == i2) {
            if (trainingClassifications[i1] == 1) {
                bias = lambda / nPos;
            } else {
                bias = lambda / nNeg;
            }
        }
        // Bias disabled
        return kernel.eval(trainingSet.get(i1), trainingSet.get(i2));
    }

    private boolean takeStep(int i1, int i2) {
        if (i1 == i2) {
            return false;
        }
        float alph1 = alpha[i1];
        float alph2 = alpha[i2];
        int y1 = trainingClassifications[i1];
        int y2 = trainingClassifications[i2];
        float e1 = getError(i1);
        float e2 = getError(i2);
        int s = y1 * y2;
        double L;
        double H;
        if (y1 != y2) {
            L = Math.max(0, alpha[i2] - alpha[i1]);
            H = Math.min(c, c + alpha[i2] - alpha[i1]);
        } else {
            L = Math.max(0, alpha[i1] + alpha[i2] - c);
            H = Math.min(c, alpha[i1] + alpha[i2]);
        }
        double k11 = biasedKernelEval(i1, i1);
        double k12 = biasedKernelEval(i1, i2);
        double k22 = biasedKernelEval(i2, i2);
        double eta = 2 * k12 - k11 - k22;
        double a1;
        double a2;
        if (eta < 0) {
            a2 = alpha[i2] - y2 * (e1 - e2) / eta;
            if (a2 < L) {
                a2 = L;
            } else if (a2 > H) {
                a2 = H;
            }
        } else {
            alpha[i2] = (float) L;
            double Lobj = objective();
            alpha[i2] = (float) H;
            double Hobj = objective();
            alpha[i2] = alph2;
            if (Lobj > Hobj + EPSILON) {
                a2 = L;
            } else if (Lobj < Hobj - EPSILON) {
                a2 = H;
            } else {
                a2 = alph2;
            }
        }
        if (a2 < 1e-8) {
            a2 = 0;
        } else if (a2 > c - 1e-8) {
            a2 = c;
        }
        if (Math.abs(a2 - alph2) < EPSILON * (a2 + alph2 + EPSILON)) {
            return false;
        }
        a1 = alph1 + s * (alph2 - a2);
        double bOld = b;
        double b1 = e1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12 + b;
        double b2 = e2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22 + b;
        if ((a1 > 0) && (a1 < c)) {
            b = b1;
        } else if ((a2 > 0) && (a2 < c)) {
            b = b2;
        } else if (L != H) {
            b = (b1 + b2) / 2;
        }
        // Recompute errors
        for (int i = 0; i < n; i++) {
            if ((i == i1) || (i == i2)) {
                errorCache[i] = 0;
            } else if ((alpha[i] > 0) && (alpha[i] < c)) {
                // Update error value
                errorCache[i] = (float) (errorCache[i] + y1 * (a1 - alph1) * biasedKernelEval(i1, i)
                        + y2 * (a2 - alph2) * biasedKernelEval(i2, i) + bOld - b);
            }
        }
        alpha[i1] = (float) a1;
        alpha[i2] = (float) a2;
        return true;
    }

    private boolean examineExample(int i2) {
        int y2 = trainingClassifications[i2];
        float alph2 = alpha[i2];
        float e2 = getError(i2);
        float r2 = e2 * y2;
        if (((r2 < -EPSILON) && (alph2 < c)) || ((r2 > EPSILON) && (alph2 > 0))) {
            int numNonDegenerates = 0;
            for (int i = 0; i < n; i++) {
                if ((alpha[i] != 0) && (alpha[i] != c)) {
                    numNonDegenerates++;
                }

            }
            {
                // Second choice heuristic #1
                if (numNonDegenerates > 1) {
                    int i1 = i2;
                    double bestValue = 0;
                    for (int i = 0; i < n; i++) {
                        if ((alpha[i] > 0) && (alpha[i] < c)) {
                            double value = Math.abs(getError(i) - e2);
                            if (value > bestValue) {
                                bestValue = value;
                                i1 = i;
                            }
                        }
                    }
                    if (takeStep(i1, i2)) {
                        return true;
                    }
                }
            }
            {
                // Second choice heuristic #2
                int randomOffset = RANDOM.nextInt(n);
                for (int i = 0; i < n; i++) {
                    int i1 = (i + randomOffset) % n;
                    if ((alpha[i1] > 0) && (alpha[i1] < c)) {
                        if (takeStep(i1, i2)) {
                            return true;
                        }
                    }
                }
            }
            {
                // Third choice heuristic #3
                int randomOffset = RANDOM.nextInt(n);
                for (int i = 0; i < n; i++) {
                    int i1 = (i + randomOffset) % n;
                    if (takeStep(i1, i2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void computeSMO() {
        log.debug("Starting SMO computation...");
        // Initialize lambdas and threshold
        alpha = new float[n];
        b = 0;
        // Initialize error cache
        errorCache = new float[n];
        for (int i = 0; i < n; i++) {
            computeError(i);
        }
        int numChanged = 0;
        boolean examineAll = true;
        int steps = 0;
        while ((numChanged > 0) || examineAll) {
            steps++;
            numChanged = 0;
            if (examineAll) {
                for (int i = 0; i < n; i++) {
                    if (examineExample(i)) {
                        numChanged++;
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    if ((alpha[i] != 0) && (alpha[i] != c)) {
                        if (examineExample(i)) {
                            numChanged++;
                        }
                    }
                }

            }
            if (examineAll) {
                examineAll = false;
            } else if (numChanged == 0) {
                examineAll = true;
            }
//            log.debug("  Step: " + steps + ", Changed: " + numChanged);
        }
        log.debug("... done, total steps: " + steps + ".");
    }

    //// END SMO

    public void test(float a, float b) {
        float[] t = {a, b};
        boolean result = evaluate(t);
        System.out.println("(" + a + ", " + b + "): " + (result ? "case" : "control"));
    }

    public static void main(String[] args) {
        float[] example1 = {(float) 1, (float) 2};
        float[] example2 = {(float) 4, (float) 1};
        float[] example3 = {(float) 5, (float) 2};
        float[] example4 = {(float) 3, (float) 5};
        ArrayList<float[]> cases = new ArrayList<float[]>();
        cases.add(example1);
        cases.add(example2);
        ArrayList<float[]> controls = new ArrayList<float[]>();
        controls.add(example3);
        controls.add(example4);
        {
            /// NORMAL
            SupportVectorMachine svm = new SupportVectorMachine(cases, controls, LINEAR_KERNAL_FUNCTION, 0.1f);
            svm.buildSupportVectors(50, 1e-6);
            svm.compute();
            svm.test(4, 1);
            svm.test(1, 2);
            svm.test(3, 5);
            svm.test(4, 6);
            svm.test(0, 0);
            svm.test(2, 1.5f);
            System.out.println("" + Math.pow(-1.3, 2));
            System.out.println("" + Math.pow(0, 2));
            System.out.println("" + Math.pow(1, 2));
            System.out.println("" + Math.pow(1.5, 2));
        }
        {
            /// SMO
            SupportVectorMachine svm = new SupportVectorMachine(cases, controls, LINEAR_KERNAL_FUNCTION, 0.1f);
            svm.buildSupportVectorsSMO(1);
            svm.compute();
            svm.test(4, 1);
            svm.test(1, 2);
            svm.test(3, 5);
            svm.test(4, 6);
            svm.test(0, 0);
            svm.test(2, 1.5f);
            System.out.println("" + Math.pow(-1.3, 2));
            System.out.println("" + Math.pow(0, 2));
            System.out.println("" + Math.pow(1, 2));
            System.out.println("" + Math.pow(1.5, 2));
        }
    }
}
