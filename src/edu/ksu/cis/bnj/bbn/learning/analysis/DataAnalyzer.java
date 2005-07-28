package edu.ksu.cis.bnj.bbn.learning.analysis;

/*
 * Created on 28 Jul 2003
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
 */

import edu.ksu.cis.bnj.bbn.analysis.Analyzer;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Tally;

import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class DataAnalyzer extends Analyzer {

    protected Data data;
    protected Tally tally;
    protected int attributeSize, dataSize;
    protected InferenceResult marginals;

    public DataAnalyzer(Data d) {
        setData(d);
    }

    public void setData(Data d) {
        data = d;
        marginals = null;
    }

    public InferenceResult getDataMarginals() {
        if (marginals != null) return marginals;
        marginals = new InferenceResult();
        tally = data.getTallyer(); // new Tally(tuples);

        List attrs = data.getAttributes();
        attributeSize = attrs.size();
        dataSize = tally.size();
        int index = 0;
        for (Iterator i = attrs.iterator(); i.hasNext(); index++) {
            Attribute attr = (Attribute) i.next();
            int arity = attr.getArity();
            Hashtable tbl = new Hashtable();
            List values = attr.getValues();
            for (int j = 0; j < arity; j++) {
                double t = tally.tally(index, j);
                tbl.put(values.get(j), new Double(t / dataSize));
            }
            marginals.put(attr.getName(), tbl);
        }
        return marginals;
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.analysis.Analyzer#dump(java.io.OutputStream)
     */
    public void dump(OutputStream o) {
        InferenceResult marginals = getDataMarginals();
        marginals.save(o);
    }
}
