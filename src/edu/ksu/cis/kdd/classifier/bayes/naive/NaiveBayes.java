package edu.ksu.cis.kdd.classifier.bayes.naive;

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

import edu.ksu.cis.kdd.classifier.Classifier;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * <P>Note: Please, this is a very very outdated Naive Bayes classifier. It starts as my
 * class project and it's been out of pace from the current infrastructure.
 * <p/>
 * <P>Note to self: Update this when you have time. Improve performance by doing log instead
 * of simple logarithms.
 *
 * @author Roby Joehanes
 */
public class NaiveBayes extends Classifier {

    protected Hashtable attrCounter = new Hashtable();
    protected Hashtable classAttrCounter = new Hashtable();

    /**
     * @see edu.ksu.cis.kdd.data.Classifier#build(Table)
     */
    public Object build(Table tuples) {

        data = (Table) tuples.clone();
        tallyClassValues();

        // Simple frequency tallying
        for (Iterator i = data.getTuples().iterator(); i.hasNext();) {
            Tuple t = (Tuple) i.next();
            Object classValue = new Double(t.getClassValue());
            Hashtable clsTable = (Hashtable) classAttrCounter.get(classValue);
            if (clsTable == null) {
                clsTable = new Hashtable();
                classAttrCounter.put(classValue, clsTable);
            }

            for (Iterator j = t.getValues().iterator(); j.hasNext();) {
                Object val = j.next();
                if (classValue == val) continue;

                Hashtable valTable = (Hashtable) attrCounter.get(val);
                if (valTable == null) {
                    valTable = new Hashtable();
                    attrCounter.put(val, valTable);
                }
                Integer _i = (Integer) valTable.get(classValue);
                if (_i == null) _i = new Integer(0);
                valTable.put(classValue, new Integer(_i.intValue() + 1));

                _i = (Integer) clsTable.get(val);
                if (_i == null) _i = new Integer(0);
                clsTable.put(val, new Integer(_i.intValue() + 1));
            }
        }

        // Unused return value
        return null;
    }

    /**
     * @see edu.ksu.cis.kdd.data.Classifier#classify(Tuple)
     */
    public Object classify(Tuple tuple) {
        assert data != null : "Classifier is not built yet!";

        Object v = null;
        double maxP = 0.0;

        Object clsValue = new Double(tuple.getClassValue());
        int size = getTupleSize();
        double pk = 1.0 / size;

        for (Enumeration j = classAttrCounter.keys(); j.hasMoreElements();) {
            Object vj = j.nextElement();
            Hashtable clsTable = (Hashtable) classAttrCounter.get(vj);
            assert clsTable != null;
            Integer _i = (Integer) classCounter.get(vj);
            if (_i == null) {
                // It must be because of the lack of examples
                // So, skip
                continue;
            }
            double pv = _i.intValue() * pk; // P(v)
            double p = pv;

            for (Iterator i = tuple.getValues().iterator(); i.hasNext();) {
                Object val = i.next();
                if (val == clsValue) continue;
                _i = (Integer) clsTable.get(val);

                // Just to make the code more readable
                double pa_v;  // P(a|v)
                if (_i == null) {
                    //                    pa_v = estimator.estimate(tuple, vj) * pk;
                    pa_v = 1.0 / (tuple.getValues().size() - 1 + attrCounter.size());
                } else {
                    //                    pa_v = _i.intValue() * pk;
                    pa_v = (_i.intValue() + 1.0) / (tuple.getValues().size() - 1 + attrCounter.size());
                }
                p *= pa_v; // p = P(v) \Pi P(a_i|v_j)
            }
            if (p > maxP) {
                v = vj;
                maxP = p;
            }
        }
        return v;
    }

    /**
     * Returns the attrCounter.
     *
     * @return Hashtable
     */
    public Hashtable getAttributeTable() {
        return attrCounter;
    }

    /**
     * Returns the classCounter.
     *
     * @return Hashtable
     */
    public Hashtable getClassTable() {
        return classCounter;
    }

    /**
     * Returns the data.
     *
     * @return Table
     */
    public Table getData() {
        return data;
    }

    /**
     * Returns the attrCounter.
     *
     * @return Hashtable
     */
    public Hashtable getAttrCounter() {
        return attrCounter;
    }

    /**
     * Returns the classAttrCounter.
     *
     * @return Hashtable
     */
    public Hashtable getClassAttrCounter() {
        return classAttrCounter;
    }

    /**
     * @see edu.ksu.cis.kdd.data.Classifier#init()
     */
    public void init() {
        attrCounter = new Hashtable();
        classAttrCounter = new Hashtable();
    }

}
