package edu.ksu.cis.kdd.data;

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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class Tuple implements Cloneable {
    public static final Object UNKNOWN = new Object();

    protected Table owner = null;
    protected LinkedList values = new LinkedList();
    protected double weight = 1.0;
    double[] valueCache = null;

    public static boolean isUnknown(Object o) {
        return o == UNKNOWN;
    }

    public Tuple() {
    }

    public Tuple(List l) {
        setValues(l);
    }

    public Tuple(List l, Table o) {
        setValues(l);
        setOwner(o);
    }


    public void addValue(Object value) {
        values.add(value);
        valueCache = null;
    }

    public void addValue(int idx, Object value) {
        values.add(idx, value);
        valueCache = null;
    }

    /**
     * Returns the owner.
     *
     * @return Table
     */
    public Table getOwner() {
        return owner;
    }


    protected void precache() {
        int max = values.size();
        valueCache = new double[max];
        for (int i = 0; i < max; i++) {
            Attribute attr = owner.getAttribute(i);
            if (attr.isNumeric()) {
                valueCache[i] = Double.parseDouble(values.get(i).toString().trim());
            } else {
                valueCache[i] = attr.getValueIndex(values.get(i));
            }
        }
    }

    public double getValue(int index) {
        if (valueCache == null) precache();
        //return values.get(index);
        return valueCache[index];
    }

    public double getValue(String attrName) {
        if (!owner.isFixedLength())
            throw new RuntimeException("This call is unavailable for variable length tuples");
        return getValue(owner.getAttributeIndex(attrName));
    }

    public double getClassValue() {
        if (valueCache == null) precache();
        if (owner.isFixedLength())
            return getValue(owner.getClassIndex());
        //return values.getLast();
        return valueCache[valueCache.length - 1];
    }

    /**
     * Returns the values.
     *
     * @return LinkedList
     */
    public List getValues() {
        return values;
    }

    /**
     * Sets the owner.
     *
     * @param owner The owner to set
     */
    public void setOwner(Table owner) {
        this.owner = owner;
    }

    public void setClassValue(Object value) {
        if (owner.isFixedLength())
            setValue(owner.getClassIndex(), value);
        else {
            values.removeLast();
            values.add(value);
        }
    }

    public void setValue(int index, Object value) {
        values.set(index, value);
    }

    public void setValue(String attrName, Object value) {
        if (!owner.isFixedLength())
            throw new RuntimeException("This call is unavailable for variable length tuples");
        values.set(owner.getAttributeIndex(attrName), value);
    }

    /**
     * Sets the values.
     *
     * @param values The values to set
     */
    public void setValues(List values) {
        this.values = new LinkedList();
        this.values.addAll(values);
    }

    /**
     * Returns the weight.
     *
     * @return double
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight.
     *
     * @param weight The weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("[" + weight + "] <"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Iterator i = values.iterator(); i.hasNext();) {
            Object o = i.next();
            if (i.hasNext())
                buf.append(o + ", "); //$NON-NLS-1$
            else
                buf.append(o + ">"); //$NON-NLS-1$
        }

        return buf.toString();
    }

    public Object clone() {
        LinkedList newValues = new LinkedList();
        newValues.addAll(values);
        return new Tuple(newValues);
    }
}
