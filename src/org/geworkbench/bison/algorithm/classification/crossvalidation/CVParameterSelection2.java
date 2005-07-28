package org.geworkbench.bison.algorithm.classification.crossvalidation;

import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.core.*;

import java.util.Random;
import java.util.Vector;


/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CVParameterSelection2 extends RandomizableSingleClassifierEnhancer {
    public CVParameterSelection2() {
    }

    /**
     * The base classifier options (not including those being set
     * by cross-validation)
     */
    protected String[] m_ClassifierOptions;

    /**
     * The set of all classifier options as determined by cross-validation
     */
    protected String[] m_BestClassifierOptions;

    /**
     * The set of all options at initialization time. So that getOptions
     * can return this.
     */
    protected String[] m_InitOptions;

    /**
     * The cross-validated performance of the best options
     */
    protected double m_BestPerformance;

    /**
     * The set of parameters to cross-validate over
     */
    protected FastVector m_CVParams = new FastVector();

    /**
     * The number of attributes in the data
     */
    protected int m_NumAttributes;

    /**
     * The number of instances in a training fold
     */
    protected int m_TrainFoldSize;

    /**
     * The number of folds used in cross-validation
     */
    protected int m_NumFolds = 10;

    CVParameterMatrix parameterResults = new CVParameterMatrix();

    public double getBestPerformance() {
        return this.m_BestPerformance;
    }


    public void buildClassifier(Instances instances) throws Exception {

        if (instances.checkForStringAttributes()) {
            throw new UnsupportedAttributeTypeException("Cannot handle string attributes!");
        }
        Instances trainData = new Instances(instances);
        trainData.deleteWithMissingClass();
        if (trainData.numInstances() == 0) {
            throw new IllegalArgumentException("No training instances without " + "missing class.");
        }
        if (trainData.numInstances() < m_NumFolds) {
            throw new IllegalArgumentException("Number of training instances " + "smaller than number of folds.");
        }
        if (!(m_Classifier instanceof OptionHandler)) {
            throw new IllegalArgumentException("Base classifier should be OptionHandler.");
        }
        m_InitOptions = ((OptionHandler) m_Classifier).getOptions();
        m_BestPerformance = -99;
        m_NumAttributes = trainData.numAttributes();
        Random random = new Random(m_Seed);
        trainData.randomize(random);
        m_TrainFoldSize = trainData.trainCV(m_NumFolds, 0, random).numInstances();

        // Check whether there are any parameters to optimize
        if (m_CVParams.size() == 0) {
            m_Classifier.buildClassifier(trainData);
            m_BestClassifierOptions = m_InitOptions;
            return;
        }

        if (trainData.classAttribute().isNominal()) {
            trainData.stratify(m_NumFolds);
        }

        m_BestClassifierOptions = null;

        // Set up m_ClassifierOptions -- take getOptions() and remove
        // those being optimised.
        m_ClassifierOptions = ((OptionHandler) m_Classifier).getOptions();
        for (int i = 0; i < m_CVParams.size(); i++) {
            Utils.getOption(((CVParameter2) m_CVParams.elementAt(i)).m_ParamChar, m_ClassifierOptions);
        }

        //        for(int paramCtr = 0; paramCtr < m_CVParams.size(); paramCtr++){
        //        int paramCtr = 0;
        //        CVParameter2 param = (CVParameter2) m_CVParams.elementAt(paramCtr);

        for (int i = 0; i < m_CVParams.size(); i++) {
            parameterResults.addParameter((CVParameter2) m_CVParams.elementAt(i));
        }

        CVParameter2 param = (CVParameter2) m_CVParams.elementAt(0);
        for (int paramValueCtr = 0; paramValueCtr < param.testValues.length; paramValueCtr++) {
            param.m_ParamValue = param.testValues[paramValueCtr];

            CVParameter2 param2 = (CVParameter2) m_CVParams.elementAt(1);
            for (int paramValueCtr2 = 0; paramValueCtr2 < param2.testValues.length; paramValueCtr2++) {
                param2.m_ParamValue = param2.testValues[paramValueCtr2];
                double errorRate = computeErrorRate(trainData, random);
                //                System.out.print(errorRate + "\t");
                Vector paramValues = new Vector();
                paramValues.add(new Double(param.m_ParamValue));
                paramValues.add(new Double(param2.m_ParamValue));
                parameterResults.addValue(paramValues, new Double(errorRate));
            }
            //            System.out.println();
        }

        //        findParamsByCrossValidation(0, trainData, random, results);

        String[] options = (String[]) m_BestClassifierOptions.clone();

        ((OptionHandler) m_Classifier).setOptions(options);

        m_Classifier.buildClassifier(trainData);

    }


    protected double computeErrorRate(Instances trainData, Random random) throws Exception {

        Evaluation evaluation = new Evaluation(trainData);

        // Set the classifier options
        String[] options = createOptions();

        if (m_Debug) {
            System.err.print("Setting options for " + m_Classifier.getClass().getName() + ":");
            for (int i = 0; i < options.length; i++) {
                System.err.print(" " + options[i]);
            }
            System.err.println("");
        }
        ((OptionHandler) m_Classifier).setOptions(options);
        for (int j = 0; j < m_NumFolds; j++) {
            Instances train = trainData.trainCV(m_NumFolds, j, random);
            Instances test = trainData.testCV(m_NumFolds, j);
            m_Classifier.buildClassifier(train);
            evaluation.setPriors(train);
            evaluation.evaluateModel(m_Classifier, test);
        }
        double error = evaluation.errorRate();

        if (m_Debug) {
            System.err.println("Cross-validated error rate: " + Utils.doubleToString(error, 6, 4));
        }
        if ((m_BestPerformance == -99) || (error < m_BestPerformance)) {

            m_BestPerformance = error;
            m_BestClassifierOptions = createOptions();
        }
        return error;
    }


    /**
     * Create the options array to pass to the classifier. The parameter
     * values and positions are taken from m_ClassifierOptions and
     * m_CVParams.
     *
     * @return the options array
     */
    protected String[] createOptions() {

        String[] options = new String[m_ClassifierOptions.length + 2 * m_CVParams.size()];
        int start = 0, end = options.length;

        // Add the cross-validation parameters and their values
        for (int i = 0; i < m_CVParams.size(); i++) {
            CVParameter2 cvParam = (CVParameter2) m_CVParams.elementAt(i);
            double paramValue = cvParam.m_ParamValue;
            //            if (cvParam.m_RoundParam) {
            //                paramValue = (double) ((int) (paramValue + 0.5));
            //            }
            //            if (cvParam.m_AddAtEnd) {
            //                options[--end] = "" +
            //                                 Utils.doubleToString(paramValue, 4);
            //                options[--end] = "-" + cvParam.m_ParamChar;
            //            } else {
            options[start++] = "-" + cvParam.m_ParamChar;
            options[start++] = "" + Utils.doubleToString(paramValue, 4);
            //            }
        }
        // Add the static parameters
        System.arraycopy(m_ClassifierOptions, 0, options, start, m_ClassifierOptions.length);

        return options;
    }

    public void addCVParameter(CVParameter2 cvParam) throws Exception {
        m_CVParams.addElement(cvParam);
    }

    public CVParameterMatrix getParameterMatrix() {
        return this.parameterResults;
    }

    /**
     * Sets the number of folds for the cross-validation.
     *
     * @param numFolds the number of folds for the cross-validation
     * @throws Exception if parameter illegal
     */
    public void setNumFolds(int numFolds) throws Exception {

        if (numFolds < 0) {
            throw new IllegalArgumentException("Stacking: Number of cross-validation " + "folds must be positive.");
        }
        m_NumFolds = numFolds;
    }


}
