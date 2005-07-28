package edu.ksu.cis.kdd.classifier;

/*
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * @author Roby Joehanes
 *
 */

import edu.ksu.cis.kdd.classifier.validator.StandardValidator;
import edu.ksu.cis.kdd.classifier.validator.Validator;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.data.converter.arff.ArffParser;
import edu.ksu.cis.kdd.data.converter.text.TextParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class ClassifierEngine extends Thread {
    public static final int MLC = 1;
    public static final int ARFF = 2;
    public static final int TEXT = 3;
    protected Classifier classifier;
    protected Validator validator = null;
    protected Table trainData, testData = null;
    protected Statistics statistics = null;
    protected TextParser tp = null;
    protected ArffParser ap = null;

    public void load(int type, String trainfile, String testfile) {
        try {
            trainData = loadImpl(type, trainfile);
            if (testfile != null) {
                testData = loadImpl(type, testfile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void load(int type, String trainfile) {
        load(type, trainfile, null);
    }

    protected Table loadImpl(int type, String filename) throws FileNotFoundException {
        switch (type) {
            case MLC:
                throw new RuntimeException("Not done yet!");
            case ARFF:
                if (ap == null) ap = new ArffParser();
                return ap.load(new FileInputStream(filename)).pickOneTable();
            case TEXT:
                if (tp == null) tp = new TextParser();
                return tp.load(new FileInputStream(filename)).pickOneTable();
            default:
                throw new RuntimeException("Unknown option!");
        }
    }

    /*
            System.out.println("Last token = "+t1.image);
            t1 = jj_consume_token(jj_ntk);
            System.out.println("Problem token = "+t1.image);
     */
    public void run() {
        assert classifier != null;
        Validator validator = this.validator;
        if (validator == null) validator = new StandardValidator(this);
        Statistics masterstat = statistics = new Statistics();
        int runNo = 0;

        validator.init();

        do {
            runNo++;
            validator.next();
            Table train = validator.getTrainData();
            classifier.init();
            Object output = classifier.build(train);
            // Dunno yet how we should use the output

            Statistics substat = new Statistics("run #" + runNo);
            for (Iterator i = train.getTuples().iterator(); i.hasNext();) {
                Tuple t = (Tuple) i.next();
                Object vbar = classifier.classify(t);
                Object v = new Double(t.getClassValue());
                masterstat.tally(Statistics.TRAIN, v, vbar);
                substat.tally(Statistics.TRAIN, v, vbar);
            }

            Table test = validator.getTestData();
            for (Iterator i = test.getTuples().iterator(); i.hasNext();) {
                Tuple t = (Tuple) i.next();
                Object vbar = classifier.classify(t);
                Object v = new Double(t.getClassValue());
                masterstat.tally(Statistics.TEST, v, vbar);
                substat.tally(Statistics.TEST, v, vbar);
            }
            masterstat.add(substat);
        } while (validator.hasNext());
        statistics = masterstat;
    }

    /**
     * Returns the testData.
     *
     * @return Table
     */
    public Table getTestData() {
        return testData;
    }

    /**
     * Returns the trainData.
     *
     * @return Table
     */
    public Table getTrainData() {
        return trainData;
    }

    /**
     * Sets the testData.
     *
     * @param testData The testData to set
     */
    public void setTestData(Table testData) {
        this.testData = testData;
    }

    /**
     * Sets the trainData.
     *
     * @param trainData The trainData to set
     */
    public void setTrainData(Table trainData) {
        this.trainData = trainData;
    }

    /**
     * Returns the statistics.
     *
     * @return Statistics
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Returns the classifier.
     *
     * @return Classifier
     */
    public Classifier getClassifier() {
        return classifier;
    }

    /**
     * Returns the validator.
     *
     * @return Validator
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Sets the classifier.
     *
     * @param classifier The classifier to set
     */
    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Sets the validator.
     *
     * @param validator The validator to set
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
        validator.setOwner(this);
    }

}
