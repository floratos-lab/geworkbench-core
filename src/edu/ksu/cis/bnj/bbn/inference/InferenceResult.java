package edu.ksu.cis.bnj.bbn.inference;

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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Inference Result wrapper
 *
 * @author Roby Joehanes
 */
public class InferenceResult extends Hashtable {

    /**
     * Constructor for InferenceResult.
     *
     * @param initialCapacity
     * @param loadFactor
     */
    public InferenceResult(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor for InferenceResult.
     *
     * @param initialCapacity
     */
    public InferenceResult(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor for InferenceResult.
     */
    public InferenceResult() {
        super();
    }

    /**
     * Constructor for InferenceResult.
     *
     * @param t
     */
    public InferenceResult(Map t) {
        super(t);
    }

    public Object put(Object key, Object value) {
        return put((String) key, (Hashtable) value);
    }

    public Object put(String key, Hashtable value) {
        return super.put(key, value);
    }

    public boolean equals(Object o) {
        return o instanceof InferenceResult && super.equals(o);
    }

    public void add(InferenceResult r) {
        if (!keySet().containsAll(r.keySet()) || size() != r.size()) {
            throw new RuntimeException("Incompatible r");
        }
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object nodeName = e.nextElement();
            Hashtable valTable = (Hashtable) get(nodeName);
            Hashtable rvalTable = (Hashtable) r.get(nodeName);
            // TO DO: check and validate entries before doing things!
            for (Enumeration f = rvalTable.keys(); f.hasMoreElements();) {
                Object value = f.nextElement();
                double val = ((Double) valTable.get(value)).doubleValue();
                double rval = ((Double) rvalTable.get(value)).doubleValue();
                valTable.put(value, new Double(val + rval));
            }
        }
    }

    public void multiply(double m) {
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object nodeName = e.nextElement();
            Hashtable valTable = (Hashtable) get(nodeName);
            for (Enumeration f = valTable.keys(); f.hasMoreElements();) {
                Object value = f.nextElement();
                double val = ((Double) valTable.get(value)).doubleValue();
                valTable.put(value, new Double(val * m));
            }
        }
    }

    public void normalize() {
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object nodeName = e.nextElement();
            Hashtable valTable = (Hashtable) get(nodeName);

            double total = 0.0;
            for (Enumeration f = valTable.keys(); f.hasMoreElements();) {
                Object value = f.nextElement();
                double val = ((Double) valTable.get(value)).doubleValue();
                total += val;
            }

            if (total == 0.0) continue;

            for (Enumeration f = valTable.keys(); f.hasMoreElements();) {
                Object value = f.nextElement();
                double val = ((Double) valTable.get(value)).doubleValue();
                valTable.put(value, new Double(val / total));
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator");
        buf.append("----------------------------" + ln);
        for (Iterator i = keySet().iterator(); i.hasNext();) {
            Object o = i.next();
            Hashtable temp = (Hashtable) get(o);
            for (Iterator j = temp.keySet().iterator(); j.hasNext();) {
                Object val = j.next();
                buf.append("[" + o + " = " + val + "] = " + temp.get(val) + ln);
            }
        }
        buf.append("----------------------------" + ln);
        return buf.toString();
    }

    public void save(OutputStream out) {
        Writer w = new OutputStreamWriter(out);
        try {
            w.write(toString());
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String filename) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        save(out);
    }

    /**
     * Compute the Root Mean Square Error (RMSE) value between two inference
     * results. Does some validation
     *
     * @param other The other inference result
     * @return double The RMSE result
     */
    public double computeRMSE(InferenceResult other) {
        if (size() != other.size())
            throw new RuntimeException("Error: The result is not of the same nodes!");

        Set ekeys = keySet();
        Set akeys = other.keySet();
        if (!ekeys.equals(akeys))
            throw new RuntimeException("Error: The result is not of the same nodes!");

        double sum = 0.0;

        for (Iterator i = ekeys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            Hashtable etbl = (Hashtable) get(key);
            Hashtable atbl = (Hashtable) other.get(key);
            Set esubkeys = etbl.keySet();
            Set asubkeys = atbl.keySet();
            if (!esubkeys.equals(asubkeys))
                throw new RuntimeException("Error: The values are mismatched!");
            for (Iterator j = esubkeys.iterator(); j.hasNext();) {
                String value = (String) j.next();
                double evalue = ((Double) etbl.get(value)).doubleValue();
                double avalue = ((Double) atbl.get(value)).doubleValue();
                evalue -= avalue;
                sum += evalue * evalue;
            }
        }

        return sum;
    }

    public Object clone() {
        InferenceResult r = new InferenceResult();
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object nodeName = e.nextElement();
            Hashtable valTable = (Hashtable) get(nodeName);
            r.put(nodeName, valTable.clone());
        }
        return r;
    }
}
