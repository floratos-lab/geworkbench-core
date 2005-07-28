/*
 * Created on Mar 8, 2003
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
package edu.ksu.cis.kdd.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Roby Joehanes
 */
public class ParameterTable extends Hashtable {

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public ParameterTable(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * @param initialCapacity
     */
    public ParameterTable(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     *
     */
    public ParameterTable() {
        super();
    }

    /**
     * @param t
     */
    public ParameterTable(Map t) {
        super(t);
    }

    public String getString(Object o) {
        Object r = get(o);
        if (r == null) return null;
        if (!(r instanceof String)) {
            throw new RuntimeException("Error: Missing value in parameter " + o);
        }

        return (String) r;
    }

    public boolean getBool(Object o) {
        Object r = get(o);
        if (r == null) return false;
        if (!(r instanceof Boolean)) {
            throw new RuntimeException("Error: There should be no value in parameter " + o);
        }
        return ((Boolean) r).booleanValue();
    }

    public int getInt(Object o, int defaultVal) {
        Object r = get(o);
        if (r == null) return defaultVal;
        if (!(r instanceof String)) {
            throw new RuntimeException("Error: Missing value in parameter " + o);
        }
        return Integer.parseInt((String) r);
    }

    public double getDouble(Object o, double defaultVal) {
        Object r = get(o);
        if (r == null) return defaultVal;
        if (!(r instanceof String)) {
            throw new RuntimeException("Error: Missing value in parameter " + o);
        }
        return Double.parseDouble((String) r);
    }

    public long getLong(Object o, long defaultVal) {
        Object r = get(o);
        if (r == null) return defaultVal;
        if (!(r instanceof String)) {
            throw new RuntimeException("Error: Missing value in parameter " + o);
        }
        return Long.parseLong((String) r);
    }
}
