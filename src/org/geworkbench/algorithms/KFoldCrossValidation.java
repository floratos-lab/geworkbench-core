package org.geworkbench.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provides convenient handling of a dataset for k-fold cross validation purposes.
 * User: mhall
 * Date: Dec 14, 2005
 * Time: 11:50:54 AM
 */
public class KFoldCrossValidation {

    int numFolds;
    List<float[]> caseData;
    List<float[]> controlData;
    List<List<float[]>> caseSegments;
    List<List<float[]>> controlSegments;

    Random rand = new Random();

    public KFoldCrossValidation(int numFolds, List<float[]> caseData, List<float[]> controlData) {
        this.numFolds = numFolds;
        this.caseData = new ArrayList<float[]>(caseData);
        this.controlData = new ArrayList<float[]>(controlData);
        this.caseSegments = new ArrayList<List<float[]>>();
        this.controlSegments = new ArrayList<List<float[]>>();

        int caseSegmentSize = caseData.size() / numFolds;
        int caseRemainder = caseData.size() % numFolds;
        int controlSegmentSize = controlData.size() / numFolds;
        int controlRemainder = controlData.size() % numFolds;

        createSegments(numFolds, caseSegmentSize, caseRemainder, this.caseData, this.caseSegments);
        createSegments(numFolds, controlSegmentSize, controlRemainder, this.controlData, this.controlSegments);
    }

    private void createSegments(int numFolds, int segmentSize, int remainder, List<float[]> inputData, List<List<float[]>> segmentList) {
        for (int i = 0; i < numFolds; i++) {
            int thisSize = segmentSize;
            if (i == numFolds - 1) {
                // Last fold, so add on remainder to make sure we use all the data
                thisSize += remainder;
            }
            List<float[]> data = new ArrayList<float[]>();
            for (int j = 0; j < thisSize; j++) {
                data.add(inputData.remove(rand.nextInt(inputData.size())));
            }
            segmentList.add(data);
        }
    }

    public int getNumFolds() {
        return numFolds;
    }

    public CrossValidationData getData(int crossNumber) {
        if (crossNumber > numFolds) {
            return null;
        }
        List<float[]> trainingCaseData = new ArrayList<float[]>();
        List<float[]> trainingControlData = new ArrayList<float[]>();
        List<float[]> testCaseData = new ArrayList<float[]>();
        List<float[]> testControlData = new ArrayList<float[]>();
        createDataForCrossNumber(crossNumber, caseSegments, trainingCaseData, testCaseData);
        createDataForCrossNumber(crossNumber, controlSegments, trainingControlData, testControlData);
        return new CrossValidationData(trainingCaseData, trainingControlData, testCaseData, testControlData);
    }

    private void createDataForCrossNumber(int crossNumber, List<List<float[]>> segments, List<float[]> trainingData, List<float[]> testData) {
        for (int i = 0; i < segments.size(); i++) {
            List<float[]> caseSegment = segments.get(i);
            if (i == crossNumber) {
                testData.addAll(caseSegment);
            } else {
                trainingData.addAll(caseSegment);
            }
        }
    }

    public static class CrossValidationData {
        private List<float[]> trainingCaseData;
        private List<float[]> trainingControlData;
        private List<float[]> testCaseData;
        private List<float[]> testControlData;

        public CrossValidationData(List<float[]> trainingCaseData, List<float[]> trainingControlData, List<float[]> testCaseData, List<float[]> testControlData) {
            this.trainingCaseData = trainingCaseData;
            this.trainingControlData = trainingControlData;
            this.testCaseData = testCaseData;
            this.testControlData = testControlData;
        }

        public List<float[]> getTrainingCaseData() {
            return trainingCaseData;
        }

        public List<float[]> getTrainingControlData() {
            return trainingControlData;
        }

        public List<float[]> getTestCaseData() {
            return testCaseData;
        }

        public List<float[]> getTestControlData() {
            return testControlData;
        }
    }

}
