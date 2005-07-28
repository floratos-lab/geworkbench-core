package edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk;

/*
 * Created on Tue 06 May 2003
 *
 * This file is part of Bayesian Networks in Java (BNJ).
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
 *
 */

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.analysis.DataAnalyzer;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Tally;
import edu.ksu.cis.kdd.ga.Chromosome;
import edu.ksu.cis.kdd.ga.Fitness;

import java.util.List;

/**
 * @author Roby Joehanes
 */
public class GAWKFitness implements Fitness {

    protected GAWK owner;
    protected Tally tally = null;
    protected InferenceResult actualResult = null;
    protected int attributeSize;

    public GAWKFitness(GAWK g) {
        owner = g;
    }

    protected void precache() {
        Data tuples = owner.getData();
        tally = tuples.getTallyer(); // new Tally(tuples);

        List attrs = tuples.getAttributes();
        attributeSize = attrs.size();
        actualResult = new DataAnalyzer(tuples).getDataMarginals();

        //        actualResult = new InferenceResult();
        //        double total = tally.size();
        //        int index = 0;
        //        for (Iterator i = attrs.iterator(); i.hasNext(); index++) {
        //            Attribute attr = (Attribute) i.next();
        //            int arity = attr.getArity();
        //            Hashtable tbl = new Hashtable();
        //            List values = attr.getValues();
        //            for (int j = 0; j < arity; j++) {
        //                int t = tally.tally(index,j);
        //                tbl.put(values.get(j), new Double(t/total));
        //            }
        //            actualResult.put(attr.getLabel(), tbl);
        //        }
    }

    /**
     * @see edu.ksu.cis.kdd.ga.Fitness#getFitness(edu.ksu.cis.kdd.ga.Chromosome)
     */
    public double getFitness(Chromosome c) {
        if (actualResult == null) precache();
        BBNGraph g = owner.getGraph((GAWKChrom) c);
        LS ls = new LS(g);
        InferenceResult approxResult = ls.getMarginals();

        double rmse = actualResult.computeRMSE(approxResult);

        return 1.0 / (1.0 + rmse);  // ensure fitness score between 0 and 1
    }

}
