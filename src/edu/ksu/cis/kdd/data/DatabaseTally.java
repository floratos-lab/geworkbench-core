/*
 * Created on 3 May 2003
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
package edu.ksu.cis.kdd.data;

import edu.ksu.cis.kdd.util.TableSet;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Tallying for database. This is incorrect yet
 *
 * @author Roby Joehanes
 */
public abstract class DatabaseTally implements Tally {

    protected Database db;
    protected TableTally[] tallies;

    class Cache {
        public static final int INVALID = -1;
        int tableID;
        int localID;
        int refID = -1;

        public Cache(int tid, int lid) {
            tableID = tid;
            localID = lid;
        }

        public String toString() {
            return tableID + "." + localID + "=>" + refID; // $NON-NLS-1 // $NON-NLS-2$
        }
    }

    // These are cache variables
    protected Table[] tables;
    protected Cache[] attributeCache;
    protected int[] attributeIndices;
    protected int[] allAttributeIndices;
    protected int[] tableBaseIndex;
    protected int numTables;
    protected Attribute[] attributes;

    DatabaseTally(Database d) {
        db = d;
    }

    /**
     * <p>This big chunk of code is for preprocessing the database. The process is roughly
     * as follows:
     * <ol>
     * <li>Sort the table according to the dependency (see Database.getSortedTables) </li>
     * <li>After sorting, assign each attribute of each table a number in ascending order.</li>
     * <li>Find the linking reference index (on either forward or backward reference).</li>
     * </ol>
     * <p/>
     * <P>Note to self: Do NOT call this method in the constructor as it would slow down the createSubTally.
     */
    protected void preprocess() {
        List sortedTable = db.getSortedTables();
        tables = (Table[]) sortedTable.toArray(new Table[0]);
        numTables = sortedTable.size();

        // assign global attribute numbers
        LinkedList allAttrList = new LinkedList();
        LinkedList normalAttrList = new LinkedList();
        LinkedList cacheList = new LinkedList();
        LinkedList attrObjectList = new LinkedList();
        TableSet refKey2Cache = new TableSet();
        Hashtable primaryKey2Cache = new Hashtable();
        Hashtable cache2GlobalIndex = new Hashtable();
        tableBaseIndex = new int[numTables];
        tallies = new TableTally[numTables];
        int globalIndex = 0;

        for (int tableIndex = 0; tableIndex < numTables; tableIndex++) {
            String tableName = tables[tableIndex].getName();
            tallies[tableIndex] = new TableTally(tables[tableIndex]);
            tallies[tableIndex].setOwner(this);
            tableBaseIndex[tableIndex] = globalIndex;
            List attrs = tables[tableIndex].getAttributes();

            int localIndex = 0;
            for (Iterator i = attrs.iterator(); i.hasNext(); localIndex++, globalIndex++) {
                Attribute attr = (Attribute) i.next();
                String attrName = attr.getName();
                allAttrList.add(new Integer(globalIndex));
                attrObjectList.add(attr);
                Cache cache = new Cache(tableIndex, localIndex);
                cacheList.add(cache);
                cache2GlobalIndex.put(cache, new Integer(globalIndex));
                if (attr.isPrimaryKey()) {
                    primaryKey2Cache.put(tableName.toUpperCase() + "." + attrName, cache); // $NON-NLS-1$
                } else if (attr.isReferenceKey()) {
                    refKey2Cache.put(attr.getReferencedTableName().toUpperCase() + "." + attr.getReferencedAttributeName(), cache);
                } else {
                    if (attr.getArity() != 0) // detect whether it is a discrete variable. otherwise, just discard
                        normalAttrList.add(new Integer(globalIndex));
                }
            }
        }

        attributeCache = (Cache[]) cacheList.toArray(new Cache[0]);

        attributeIndices = new int[normalAttrList.size()];
        globalIndex = 0;
        for (Iterator i = normalAttrList.iterator(); i.hasNext(); globalIndex++) {
            attributeIndices[globalIndex] = ((Integer) i.next()).intValue();
        }

        allAttributeIndices = new int[allAttrList.size()];
        globalIndex = 0;
        for (Iterator i = allAttrList.iterator(); i.hasNext(); globalIndex++) {
            allAttributeIndices[globalIndex] = ((Integer) i.next()).intValue();
        }

        attributes = new Attribute[attrObjectList.size()];
        globalIndex = 0;
        for (Iterator i = attrObjectList.iterator(); i.hasNext(); globalIndex++) {
            attributes[globalIndex] = (Attribute) i.next();
        }

        // Post linking on index caches
        for (Iterator i = refKey2Cache.keySet().iterator(); i.hasNext();) {
            String refName = (String) i.next();
            Cache fwCache = (Cache) primaryKey2Cache.get(refName);
            assert (fwCache != null); // make sure it's no orphan
            int idx = ((Integer) cache2GlobalIndex.get(fwCache)).intValue();
            for (Iterator j = refKey2Cache.get(refName).iterator(); j.hasNext();) {
                Cache refCache = (Cache) j.next();
                refCache.refID = idx;
            }
        }
    }

    /**
     * @return The array of attribute indices that includes primary and reference keys
     */
    public int[] getAllAttributeIndices() {
        if (allAttributeIndices == null) preprocess();
        return allAttributeIndices;
    }

    /**
     * @return The array of attribute indices that does not include primary or reference keys
     */
    public int[] getRelevantAttributeIndices() {
        if (attributeIndices == null) preprocess();
        return attributeIndices;
    }

    public Attribute getAtttribute(int idx) {
        if (attributes == null) preprocess();
        return attributes[idx];
    }

    public Table[] getTables() {
        if (tables == null) preprocess();
        return tables;
    }

    /**
     * Returns the tally for each table. That is, if the learner algo doesn't want the single
     * table abstraction. Note: Do not modify this if you later want to use database tally.
     * Rather, create a subtally instead
     *
     * @return Tally[]
     */
    public TableTally[] getTallies() {
        if (tallies == null) preprocess();
        return tallies;
    }

    public abstract int tally(int attr, int value);

    /**
     * Create a sub tally that is filtered according the attribute number <tt>attr</tt> with its
     * value. This does NOT propagate the ref keys -- too expensive. Doing it latter in tally phase
     * @param attr
     * @param value
     * @return Prefiltered tally
     */
    //public abstract DatabaseTally createSubTally(int attr, int value);

    /**
     * Dump tallyer status. For debugging purposes only
     */
    public String toString() {
        if (tables == null) preprocess();
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); // $NON-NLS-1$

        buf.append("Tables : [" + tables[0].getName());
        for (int i = 1; i < numTables; i++) {
            buf.append(", " + tables[i].getName()); // $NON-NLS-1$
        }
        buf.append("]" + ln); // $NON-NLS-1$

        buf.append("Table base indices: [" + tableBaseIndex[0]);
        for (int i = 1; i < numTables; i++) {
            buf.append(", " + tableBaseIndex[i]); // $NON-NLS-1$
        }
        buf.append("]" + ln); // $NON-NLS-1$

        buf.append("All attributes: [" + attributes[0].getFullyQualifiedName() + "(0)"); // $NON-NLS-2$
        int length = attributes.length;
        for (int i = 1; i < length; i++) {
            buf.append(", " + attributes[i].getFullyQualifiedName() + "(" + i + ")"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
        }
        buf.append("]" + ln); // $NON-NLS-1$

        buf.append("All attributes indices: [" + allAttributeIndices[0]);
        length = allAttributeIndices.length;
        for (int i = 1; i < length; i++) {
            buf.append(", " + allAttributeIndices[i]); // $NON-NLS-1$
        }
        buf.append("]" + ln); // $NON-NLS-1$

        buf.append("Relevant attributes indices: [" + attributeIndices[0]);
        length = attributeIndices.length;
        for (int i = 1; i < length; i++) {
            buf.append(", " + attributeIndices[i]); // $NON-NLS-1$
        }
        buf.append("]" + ln); // $NON-NLS-1$

        buf.append("Cache contents: [" + attributeCache[0]);
        length = attributeCache.length;
        for (int i = 1; i < length; i++) {
            buf.append(", " + attributeCache[i]); // $NON-NLS-1$
        }
        buf.append("]" + ln); // $NON-NLS-1$
        return buf.toString();
    }

    public void dumpTallyStatus() {
        System.out.println(toString());
    }

    public Data getUnderlyingData() {
        return db;
    }

    public void setUnderlyingData(Data d) {
        db = (Database) d;
        preprocess();
    }

    /**
     * @see edu.ksu.cis.kdd.data.TallyInterface#groupedTally(int[])
     */
    public List groupedTally(int[] indices) {
        int[] values = new int[indices.length];
        return groupedTally(indices, values, 0, new LinkedList());
    }

    protected List groupedTally(int[] indices, int[] values, int depth, List valueList) {
        int maxArity = attributes[indices[depth]].getArity();
        for (int i = 0; i < maxArity; i++) {
            values[depth] = i;
            if (depth == indices.length - 1) {
                int tally = tally(indices, values);
                valueList.add(new Integer(tally));
            } else {
                groupedTally(indices, values, depth + 1, valueList);
            }
        }
        return valueList;
    }
}

