/*
 * Created on Jun 26, 2003
 * 
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
package edu.ksu.cis.bnj.bbn.inference;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.analysis.Analyzer;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * A convenience class to calculating RMSE
 *
 * @author Roby Joehanes
 */
public class RMSECalculator extends Analyzer {
    protected static final String ln = System.getProperty("line.separator"); // $NON-NLS-1$
    protected InferenceResult exactResult = null;
    protected LinkedList storedRMSE = new LinkedList();
    protected OutputStream out = System.out;
    protected int numResult = 0;
    protected double max = Double.MIN_VALUE;
    protected double min = Double.MAX_VALUE;

    public void setOutputStream(OutputStream writer) {
        this.out = writer;
    }

    public RMSECalculator() {
    }

    public RMSECalculator(BBNGraph graph) {
        setGraph(graph);
    }

    public void setGraph(BBNGraph graph) {
        LS ls = new LS(graph);
        exactResult = ls.getMarginals();
    }

    public double calculateRMSE(InferenceResult result) {
        double r = exactResult.computeRMSE(result);
        if (r > max) max = r;
        if (r < min) min = r;
        storedRMSE.add(new Double(r));
        numResult++;
        return r;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public int getNumberOfRMSEPoints() {
        return numResult;
    }

    public double[] getRMSEPoints() {
        double[] d = new double[numResult];
        int idx = 0;
        for (Iterator i = storedRMSE.iterator(); i.hasNext(); idx++) {
            d[idx] = ((Double) i.next()).doubleValue();
        }
        return d;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        for (Iterator i = storedRMSE.iterator(); i.hasNext();) {
            s.append(((Double) i.next()).doubleValue() + ln);
        }
        return s.toString();
    }

    public void dump(OutputStream stream) {
        try {
            PrintWriter writer = new PrintWriter(stream);
            writer.print(toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dump() {
        dump(out);
    }

    public void close() {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }
}
