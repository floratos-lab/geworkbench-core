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

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Roby Joehanes
 */
public class Statistics {
    public static final int TRAIN = 0;
    public static final int TEST = 1;
    protected int totalTrainCorrect = 0;
    protected int totalTrainWrong = 0;
    protected int totalTestCorrect = 0;
    protected int totalTestWrong = 0;
    protected String name = ""; //$NON-NLS-1$
    protected LinkedList list = new LinkedList();
    protected Hashtable trainConfMatrix = new Hashtable();
    protected Hashtable testConfMatrix = new Hashtable();

    public Statistics() {
    }

    public Statistics(String name) {
        setName(name);
    }

    public void tally(int mode, Object v, Object vbar) {
        Hashtable tbl;
        Integer _i;

        switch (mode) {
            case TRAIN:
                tbl = (Hashtable) trainConfMatrix.get(v);
                if (tbl == null) {
                    tbl = new Hashtable();
                    trainConfMatrix.put(v, tbl);
                }
                _i = (Integer) tbl.get(vbar);
                if (_i == null) _i = new Integer(0);

                tbl.put(vbar, new Integer(_i.intValue() + 1));

                if ((vbar == null && v == null) || (vbar != null && v != null && (v == vbar || v.equals(vbar)))) {
                    totalTrainCorrect++;
                } else {
                    totalTrainWrong++;
                }
                break;
            case TEST:
                tbl = (Hashtable) testConfMatrix.get(v);
                if (tbl == null) {
                    tbl = new Hashtable();
                    testConfMatrix.put(v, tbl);
                }
                _i = (Integer) tbl.get(vbar);
                if (_i == null) _i = new Integer(0);

                tbl.put(vbar, new Integer(_i.intValue() + 1));

                if ((vbar == null && v == null) || (vbar != null && v != null && (v == vbar || v.equals(vbar)))) {
                    totalTestCorrect++;
                } else {
                    totalTestWrong++;
                }
                break;
            default:
        }
    }

    public void add(Statistics s) {
        assert s != null;
        list.add(s);
    }

    public List getSubStatistics() {
        return list;
    }

    public Statistics getSubStatistic(int i) {
        return (Statistics) list.get(i);
    }

    public Statistics getLastSubStatistic() {
        return (list.size() > 0) ? (Statistics) list.getLast() : null;
    }

    public void removeSubStatistics(int idx) {
        list.remove(idx);
    }

    public void removeLastSubStatistics() {
        list.removeLast();
    }

    public void reset() {
        totalTrainCorrect = totalTrainWrong = totalTestCorrect = totalTestWrong = 0;
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the totalTestCorrect.
     *
     * @return int
     */
    public int getTotalTestCorrect() {
        return totalTestCorrect;
    }

    /**
     * Returns the totalTestWrong.
     *
     * @return int
     */
    public int getTotalTestWrong() {
        return totalTestWrong;
    }

    /**
     * Returns the totalTrainCorrect.
     *
     * @return int
     */
    public int getTotalTrainCorrect() {
        return totalTrainCorrect;
    }

    /**
     * Returns the totalTrainWrong.
     *
     * @return int
     */
    public int getTotalTrainWrong() {
        return totalTrainWrong;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isBetterThan(Statistics stat) {
        return getTestAccuracy() > stat.getTestAccuracy();
    }

    public boolean isOverfit() {
        return getTestAccuracy() < getTrainAccuracy();
    }

    public double getTestAccuracy() {
        return (totalTestCorrect * 1.0) / (totalTestCorrect + totalTestWrong);
    }

    public double getTrainAccuracy() {
        return (totalTrainCorrect * 1.0) / (totalTrainCorrect + totalTrainWrong);
    }

    public String toString() {
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("Statistics");
        if (name != null && !name.equals("")) //$NON-NLS-1$
            buf.append(" for: " + name + ln);
        else
            buf.append(ln);
        double temp = (totalTrainCorrect * 1.0) / (totalTrainCorrect + totalTrainWrong);
        DecimalFormat format = new DecimalFormat("#0.0#"); //$NON-NLS-1$
        buf.append("Train accuracy " + format.format(temp * 100) + "%" + ln); //$NON-NLS-2$
        buf.append("Number correct " + totalTrainCorrect + ln);
        temp = (totalTestCorrect * 1.0) / (totalTestCorrect + totalTestWrong);
        buf.append("Test accuracy " + format.format(temp * 100) + "%" + ln); //$NON-NLS-2$
        buf.append("Number correct " + totalTestCorrect + ln);

        buf.append(ln + "Train Confusion matrix" + ln);
        for (Enumeration e1 = trainConfMatrix.keys(); e1.hasMoreElements();) {
            Object v = e1.nextElement();
            Hashtable tbl = (Hashtable) trainConfMatrix.get(v);
            buf.append("true value" + v + ", but predicted as ");
            for (Enumeration e2 = tbl.keys(); e2.hasMoreElements();) {
                Object vbar = e2.nextElement();
                Integer _i = (Integer) tbl.get(vbar);
                if (_i == null) _i = new Integer(0);
                buf.append(vbar + "=" + _i + " "); //$NON-NLS-1$ //$NON-NLS-2$
            }
            buf.append(ln);
        }

        buf.append(ln + "Test Confusion matrix" + ln);
        for (Enumeration e1 = testConfMatrix.keys(); e1.hasMoreElements();) {
            Object v = e1.nextElement();
            Hashtable tbl = (Hashtable) testConfMatrix.get(v);
            buf.append("true value" + v + ", but predicted as ");
            for (Enumeration e2 = tbl.keys(); e2.hasMoreElements();) {
                Object vbar = e2.nextElement();
                Integer _i = (Integer) tbl.get(vbar);
                if (_i == null) _i = new Integer(0);
                buf.append(vbar + "=" + _i + " "); //$NON-NLS-1$ //$NON-NLS-2$
            }
            buf.append(ln);
        }

        buf.append(ln);

        if (list.size() > 1) {
            for (Iterator i = list.iterator(); i.hasNext();) {
                buf.append(i.next() + ln);
            }
        }

        return buf.toString();
    }
}
