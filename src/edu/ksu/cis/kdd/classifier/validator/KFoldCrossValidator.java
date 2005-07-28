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

import edu.ksu.cis.kdd.classifier.Statistics;
import edu.ksu.cis.kdd.data.Table;

/**
 * @author Roby Joehanes
 */
public class KFoldCrossValidator extends Validator {
    protected int k = 10;
    protected int curK = 0;
    protected int bestK = -1;
    protected Statistics bestStat = null;

    public KFoldCrossValidator(int k) {
        this.k = k;
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#getTestData()
     */
    public Table getTestData() {
        if (curK <= k) {
            Table test = owner.getTrainData();
            int size = test.size();
            return test.get((curK - 1) * size / k, curK * size / k - 1);
        }
        return owner.getTestData();
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#getTrainData()
     */
    public Table getTrainData() {
        Table train = owner.getTrainData();
        int size = train.size();
        if (curK <= k)
            return train.getAllExcept((curK - 1) * size / k, curK * size / k - 1);
        return train.getAllExcept((bestK - 1) * size / k, bestK * size / k - 1);
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#hasNext()
     */
    public boolean hasNext() {
        if (curK == k) {
            owner.getStatistics().reset();
        } else if (curK == k + 1) {
            owner.getStatistics().removeLastSubStatistics();
        }
        return curK <= k;
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#init()
     */
    public void init() {
        assert owner != null;
        curK = 0;
        bestK = -1;
        bestStat = null;
    }

    /**
     * @see edu.ksu.cis.kdd.data.validator.Validator#next()
     */
    public void next() {
        if (curK < k) {
            Statistics stat = owner.getStatistics().getLastSubStatistic();
            if ((stat != null) && (bestStat == null || stat.isBetterThan(bestStat))) {
                bestStat = stat;
                bestK = curK;
            }
        }
        curK++;
    }

}
