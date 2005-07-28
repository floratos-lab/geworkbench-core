package edu.ksu.cis.kdd.classifier.validator;

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

import edu.ksu.cis.kdd.classifier.ClassifierEngine;
import edu.ksu.cis.kdd.data.Table;

/**
 * @author Roby Joehanes
 */
public class StandardValidator extends Validator {

    /**
     * The proportion to reserve as train data. Default 0.8
     * (It means that 0.8 portion of the train data will be
     * set apart as the test data ONLY IF the test data doesn't
     * exist)
     */
    protected double split = 0.8;
    protected Table train = null, test = null;

    /**
     * Constructor for StandardValidator.
     */
    public StandardValidator() {
        super();
    }

    /**
     * Constructor for StandardValidator.
     *
     * @param o
     */
    public StandardValidator(ClassifierEngine o) {
        super(o);
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#hasNext()
     */
    public boolean hasNext() {
        return false; // We never split
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#next()
     */
    public void next() {
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#getTrainData()
     */
    public Table getTrainData() {
        assert train != null;
        return train;
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#getTestData()
     */
    public Table getTestData() {
        assert test != null;
        return test;
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#init()
     */
    public void init() {
        assert owner != null;
        Table train = owner.getTrainData();
        Table test = owner.getTestData();
        if (test != null) {
            this.train = train;
            this.test = test;
        } else {
            this.train = train.getFirst((int) Math.round(train.size() * split));
            this.test = train.getFirst((int) Math.round(train.size() * (1.0 - split)));
        }
    }

    /**
     * Returns the split.
     *
     * @return double
     */
    public double getSplit() {
        return split;
    }

    /**
     * Sets the split.
     *
     * @param split The split to set
     */
    public void setSplit(double split) {
        this.split = split;
    }

}
