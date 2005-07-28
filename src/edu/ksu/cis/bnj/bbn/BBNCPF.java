/*
 * Created on Feb 26, 2003
 *
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
 * 
 *
 *
 */
package edu.ksu.cis.bnj.bbn;

import java.util.*;

/**
 * @author Roby Joehanes
 */
public class BBNCPF implements Cloneable {
    /**
     * We store the PDF in a hashtable.
     */
    protected Hashtable cpf = new Hashtable();

    protected Collection nodeNames = null;
    protected int tableArity;

    /**
     *
     */
    public BBNCPF(Collection nodeNames) {
        this.nodeNames = nodeNames;
        tableArity = nodeNames.size();
    }

    /**
     * Syntactical candy for get(Hashtable) to make naming consistent
     *
     * @param v
     * @return double
     */
    public double query(Hashtable v) {
        return get(v);
    }

    /**
     * Queries to the CPT (or CPF, in case of continuous value).
     *
     * @param values The query values in a hash table
     * @return double the return value.
     */
    public double get(Hashtable values) {
        if (tableArity == 0) return 1.0;

        // Trim out unused query values
        Hashtable newQuery = new Hashtable();
        for (Enumeration e = values.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            if (nodeNames.contains(name))
                newQuery.put(name, values.get(name));
        }
        int qsize = newQuery.size();
        assert (qsize <= tableArity);

        // If exact matches, then do a table look up
        if (qsize == tableArity) {
            BBNPDF val = (BBNPDF) cpf.get(newQuery);
            if (val == null)
                throw new RuntimeException("Query is not defined in the cpf! " + values);
            return val.evaluate(values);
        }

        // Otherwise, we have to sum up the matching values.
        double sum = 0.0;
        Set querySet = newQuery.entrySet();
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF val = (BBNPDF) cpf.get(q);
            double p = val.evaluate(values);
            if (q.entrySet().containsAll(querySet)) {
                sum += p;
            }
        }
        return sum;
    }

    /**
     * Same as query, but normalized
     *
     * @param values The query values in a hash table
     * @return double the return value.
     */
    public double normalizedQuery(Hashtable values) {
        if (tableArity == 0) return 1.0;

        // Trim out unused query values
        Hashtable newQuery = new Hashtable();
        for (Enumeration e = values.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            if (nodeNames.contains(name))
                newQuery.put(name, values.get(name));
        }
        int qsize = newQuery.size();
        assert (qsize <= tableArity);

        // If exact matches, then do a table look up
        if (qsize == tableArity) {
            BBNPDF val = (BBNPDF) cpf.get(newQuery);
            if (val == null)
                throw new RuntimeException("Query is not defined in the cpf! " + values);
            return val.evaluate(values);
        }

        // Otherwise, we have to sum up the matching values.
        double sum = 0.0;
        double total = 0.0;
        Set querySet = newQuery.entrySet();
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF val = (BBNPDF) cpf.get(q);
            double p = val.evaluate(values);
            if (q.entrySet().containsAll(querySet)) {
                sum += p;
            }
            total += p;
        }
        return sum / total;
    }

    /**
     * Reset all CPT entries to zeroes.
     */
    public void resetEntries() {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            cpf.put(q, new BBNConstant(0.0));
        }
    }

    /**
     * Populating the conditional probability function (CPF)
     *
     * @param q The query
     * @param v The actual value
     */
    public void put(Hashtable q, BBNPDF v) {
        // Trim out unused query values
        Hashtable newQuery = new Hashtable();
        for (Enumeration e = q.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            if (nodeNames.contains(name))
                newQuery.put(name, q.get(name));
        }
        cpf.put(newQuery, v);
    }

    /**
     * Removing a specific entry from CPF.
     *
     * @param q the query
     */
    public void remove(Hashtable q) {
        cpf.remove(q);
    }

    public void zeroEntryExcept(Hashtable q) {
        Hashtable newQuery = new Hashtable();
        for (Enumeration e = q.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            if (nodeNames.contains(name))
                newQuery.put(name, q.get(name));
        }

        Set entrySet = newQuery.entrySet();
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable tbl = (Hashtable) e.nextElement();
            if (!tbl.entrySet().containsAll(entrySet)) {
                BBNPDF v = (BBNPDF) cpf.get(tbl);
                v.toConstant(0.0);
            }
        }
    }

    /**
     * Dumping out the PDF. For debugging only.
     */
    public String dumpPDF() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            buf.append(k + " = " + cpf.get(k) + ln); //$NON-NLS-1$
        }
        return buf.toString();
    }

    /**
     * Multiplication with another CPT. (this_cpt = this_cpt * other_cpt)
     * Note: This function must be overhauled if we deal with continuous values!
     *
     * @param cpt
     */
    public void multiply(BBNCPF cpt) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF pdf = (BBNPDF) cpf.get(q);
            pdf.multiply(new BBNConstant(cpt.get(q)));
        }
    }

    /**
     * Division with another CPT. (this_cpt = this_cpt / other_cpt)
     * Note: This function must be overhauled if we deal with continuous values!
     * Also, if the denominator is zero, the division is not carried out (otherwise
     * Java will complain division by zero error).
     *
     * @param cpt
     */
    public void divide(BBNCPF cpt) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF pdf = (BBNPDF) cpf.get(q);
            double p = cpt.get(q);
            if (p > 0)
                pdf.divide(new BBNConstant(p));
        }
    }

    /**
     * Addition with another CPT. (this_cpt = this_cpt + other_cpt)
     * Note: This function must be overhauled if we deal with continuous values!
     *
     * @param cpt
     */
    public void add(BBNCPF cpt) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF pdf = (BBNPDF) cpf.get(q);
            pdf.add(new BBNConstant(cpt.get(q)));
        }
    }

    /**
     * Subtraction with another CPT. (this_cpt = this_cpt - other_cpt)
     * Note: This function must be overhauled if we deal with continuous values!
     *
     * @param cpt
     */
    public void subtract(BBNCPF cpt) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF pdf = (BBNPDF) cpf.get(q);
            pdf.subtract(new BBNConstant(cpt.get(q)));
        }
    }

    /* written by prashanth */
    public List queryColumn(Hashtable values) {
        LinkedList list = new LinkedList();
        Set querySet = values.entrySet();

        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF val = (BBNPDF) cpf.get(q);
            double p = val.evaluate(values);

            if (q.entrySet().containsAll(querySet)) {
                list.add(new Double(p));
            }
        }
        return list;
    }

    public BBNCPF extract(Collection subset) {
        if (!nodeNames.containsAll(subset)) {
            throw new RuntimeException("Extract parameter must be a subset of this cpt node names!");
        }
        BBNCPF newcpt = new BBNCPF(subset);

        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            Hashtable newq = new Hashtable();
            for (Enumeration f = q.keys(); f.hasMoreElements();) {
                Object nodeName = f.nextElement();
                if (subset.contains(nodeName)) {
                    newq.put(nodeName, q.get(nodeName));
                }
            }

            BBNPDF val = (BBNPDF) cpf.get(q);

            BBNPDF oldval = (BBNPDF) newcpt.cpf.get(newq);
            if (oldval == null) {
                newcpt.cpf.put(newq, val.clone());
            } else {
                oldval.add(val);
            }
        }

        return newcpt;
    }


    public BBNCPF extractExcept(Collection subset) {
        if (!nodeNames.containsAll(subset)) {
            throw new RuntimeException("Extract parameter must be a subset of this cpt node names!");
        }
        HashSet set = new HashSet();
        set.addAll(nodeNames);
        set.removeAll(subset);
        BBNCPF newcpt = new BBNCPF(set);

        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            Hashtable newq = new Hashtable();
            for (Enumeration f = q.keys(); f.hasMoreElements();) {
                Object nodeName = f.nextElement();
                if (set.contains(nodeName)) {
                    newq.put(nodeName, q.get(nodeName));
                }
            }

            BBNPDF val = (BBNPDF) cpf.get(q);

            BBNPDF oldval = (BBNPDF) newcpt.cpf.get(newq);
            if (oldval == null) {
                newcpt.cpf.put(newq, val.clone());
            } else {
                oldval.add(val);
            }
        }

        return newcpt;
    }

    /**
     * Normalize. If the column total is 0.0, it may throw a divide by zero error
     *
     * @param base Name of the variable to be marginalized to 1.0
     */
    public void normalize(String base) {
        HashSet subset = new HashSet();
        subset.addAll(nodeNames);
        subset.remove(base);
        if (subset.size() > 0) {
            BBNCPF divisor = extract(subset);
            divide(divisor);
        } else {
            double total = 0.0;
            for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                BBNPDF val = (BBNPDF) cpf.get(key);
                total += val.getValue();
            }

            for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                BBNPDF val = (BBNPDF) cpf.get(key);
                val.divide(new BBNConstant(total));
            }
        }
    }

    /**
     * Normalize. If the column total is 0.0, fill it with uniform values
     *
     * @param base Name of the variable to be marginalized to 1.0
     */
    public void normalizeAndFill(String base) {
        Hashtable subtotal = new Hashtable();
        int arity = 0;

        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            Hashtable newq = new Hashtable();
            for (Enumeration f = q.keys(); f.hasMoreElements();) {
                Object nodeName = f.nextElement();
                if (!base.equals(nodeName)) {
                    newq.put(nodeName, q.get(nodeName));
                } else
                    arity++;
            }

            BBNPDF val = (BBNPDF) cpf.get(q);
            BBNPDF oldval = (BBNPDF) subtotal.get(newq);
            if (oldval == null) {
                subtotal.put(newq, val.clone());
            } else {
                oldval.add(val);
            }
        }

        if (arity == 0) arity = 1;

        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            Hashtable newq = new Hashtable();
            for (Enumeration f = q.keys(); f.hasMoreElements();) {
                Object nodeName = f.nextElement();
                if (!base.equals(nodeName)) {
                    newq.put(nodeName, q.get(nodeName));
                }
            }

            BBNPDF val = (BBNPDF) cpf.get(q);
            BBNPDF oldval = (BBNPDF) subtotal.get(newq);
            if (oldval.getValue() == 0) {
                //val.setType(oldval);
                val.toConstant(1.0 / arity);
                //oldval.toConstant(1.0/arity);
            } else {
                val.divide(oldval);
            }
        }
    }

    public void multiply(BBNPDF arg) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF val = (BBNPDF) cpf.get(q);
            val.multiply(arg);
        }
    }

    public void divide(BBNPDF arg) {
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Hashtable q = (Hashtable) e.nextElement();
            BBNPDF val = (BBNPDF) cpf.get(q);
            val.divide(arg);
        }
    }

    public void setTable(Hashtable t) {
        cpf = t;
    }

    public Hashtable getTable() {
        return cpf;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Object o = e.nextElement();
            buf.append(o + ": " + cpf.get(o) + ln); //$NON-NLS-1$
        }
        return buf.toString();
    }

    public Object clone() {
        BBNCPF cpt = new BBNCPF(nodeNames);
        cpt.cpf = new Hashtable();
        for (Enumeration e = cpf.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            BBNPDF pdf = (BBNPDF) cpf.get(key);
            cpt.cpf.put(key, pdf.clone());
        }
        return cpt;
    }
}

