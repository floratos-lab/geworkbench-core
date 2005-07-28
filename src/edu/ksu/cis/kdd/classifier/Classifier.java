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

import edu.ksu.cis.kdd.classifier.estimator.Estimator;
import edu.ksu.cis.kdd.classifier.estimator.StandardEstimator;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author Roby Joehanes
 */
public abstract class Classifier {
    protected Table data;
    protected Hashtable classCounter = new Hashtable();
    protected Estimator estimator = new StandardEstimator(this);

    public void tallyClassValues() {
        for (Iterator i = data.getTuples().iterator(); i.hasNext();) {
            Tuple t = (Tuple) i.next();
            Double classValue = new Double(t.getClassValue());
            Integer _i = (Integer) classCounter.get(classValue);
            if (_i == null) _i = new Integer(0);
            classCounter.put(classValue, new Integer(_i.intValue() + 1));
        }
    }

    public abstract void init();

    public abstract Object build(Table tuples);

    public abstract Object classify(Tuple tuple);

    /**
     * Returns the estimator.
     *
     * @return Estimator
     */
    public Estimator getEstimator() {
        return estimator;
    }

    /**
     * Sets the estimator.
     *
     * @param estimator The estimator to set
     */
    public void setEstimator(Estimator estimator) {
        assert estimator != null;
        this.estimator = estimator;
        estimator.setOwner(this);
    }

    public int getClassTally(Object value) {
        Integer i = (Integer) classCounter.get(value);
        if (i == null) return 0;
        return i.intValue();
    }

    public int getTupleSize() {
        assert data != null : "Need to tally the tuple first!";
        return data.size();
    }

    public int getClassSize() {
        return classCounter.size();
    }

    /**
     * Returns the data.
     *
     * @return Table
     */
    public Table getData() {
        return data;
    }

}
