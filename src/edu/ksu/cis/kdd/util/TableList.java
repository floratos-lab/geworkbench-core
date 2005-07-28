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

// Table which value is list. By: Roby Joehanes

public class TableList implements Cloneable {
    protected Hashtable tbl = new Hashtable();

    public List get(Object key) {
        return (List) tbl.get(key);
    }

    public Set getSet(Object o) {
        List list = get(o);
        HashSet result = new HashSet();
        if (list != null) result.addAll(list);
        return result;
    }

    public Enumeration keys() {
        return tbl.keys();
    }

    public Set keySet() {
        return tbl.keySet();
    }

    public void put(Object key, Object item) {
        List l = (List) tbl.get(key);
        if (l == null) l = new LinkedList();
        l.add(item);
        tbl.put(key, l);
    }

    public void putAll(Object key, Collection item) {
        List l = (List) tbl.get(key);
        if (l == null) l = new LinkedList();
        l.addAll(item);
        tbl.put(key, l);
    }

    public void putAll(Object key, Object[] item) {
        List l = (List) tbl.get(key);
        if (l == null) l = new LinkedList();
        int max = item.length;
        for (int i = 0; i < max; i++)
            l.add(item[i]);
        tbl.put(key, l);
    }

    public void putAll(TableList t) {
        // You can't do tbl.putAll(t.tbl)
        // Because we unionize the colliding entries,
        // not replacing them.
        for (Iterator i = t.tbl.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            List l = (List) t.tbl.get(key);
            if (l != null) putAll(key, l);
        }
    }

    public void remove(Object key) {
        tbl.remove(key);
    }

    public void remove(Object key, Object item) {
        List l = (List) tbl.get(key);
        if (l == null) return;
        l.remove(item);
    }

    public String toString() {
        return tbl.toString();
    }

    public int hashCode() {
        return tbl.hashCode();
    }

    public boolean equals(Object o) {
        return (o instanceof TableList) && (tbl.equals(((TableList) o).tbl));
    }

    public int size() {
        return tbl.size();
    }

    public Object clone() {
        TableList newTbl = new TableList();
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            LinkedList ll = (LinkedList) tbl.get(key);
            newTbl.tbl.put(key, ll.clone());
        }
        return newTbl;
    }
}
