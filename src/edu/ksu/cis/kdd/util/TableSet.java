package edu.ksu.cis.kdd.util;

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

import java.util.*;

// Table which value is sets. By: Roby Joehanes

public class TableSet implements Cloneable {
    protected Hashtable tbl = new Hashtable();

    public HashSet get(Object key) {
        return (HashSet) tbl.get(key);
    }

    public List getList(Object o) {
        HashSet set = get(o);
        LinkedList result = new LinkedList();
        if (set != null) result.addAll(set);
        return result;
    }

    public Enumeration keys() {
        return tbl.keys();
    }

    public Set keySet() {
        return tbl.keySet();
    }

    public Set valueSet() {
        HashSet set = new HashSet();
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            set.addAll(get(key));
        }
        return set;
    }

    public void put(Object key, Object item) {
        HashSet s = (HashSet) tbl.get(key);
        if (s == null) s = new HashSet();
        s.add(item);
        tbl.put(key, s);
    }

    public void putAll(Object key, Collection item) {
        HashSet s = (HashSet) tbl.get(key);
        if (s == null) s = new HashSet();
        s.addAll(item);
        tbl.put(key, s);
    }

    public void putAll(Object key, Object[] item) {
        HashSet s = (HashSet) tbl.get(key);
        if (s == null) s = new HashSet();
        int max = item.length;
        for (int i = 0; i < max; i++)
            s.add(item[i]);
        tbl.put(key, s);
    }

    public void putAll(TableSet t) {
        // You can't do tbl.putAll(t.tbl)
        // Because we unionize the colliding entries,
        // not replacing them.
        for (Iterator i = t.tbl.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            Set set = (Set) t.tbl.get(key);
            if (set != null) putAll(key, set);
        }
    }

    public void remove(Object key) {
        tbl.remove(key);
    }

    public void remove(Object key, Object item) {
        HashSet s = (HashSet) tbl.get(key);
        if (s == null) return;
        s.remove(item);
    }

    public String toString() {
        return tbl.toString();
    }

    public int hashCode() {
        return tbl.hashCode();
    }

    public boolean equals(Object o) {
        return (o instanceof TableSet) && (tbl.equals(((TableSet) o).tbl));
    }

    public int size() {
        return tbl.size();
    }

    public Object clone() {
        TableSet newTbl = new TableSet();
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            HashSet set = (HashSet) tbl.get(key);
            newTbl.tbl.put(key, set.clone());
        }
        return newTbl;
    }
}
