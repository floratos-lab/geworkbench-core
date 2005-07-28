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

import java.util.*;

/**
 * Tallying tuples. Contains cache. So, if you modify the tuples, it won't
 * reflect the changes. You will have to reset the cache manually. OR you can
 * always create a new Tally object.
 *
 * @author Roby Joehanes
 */
public class TableTally implements WeightedTally {

    protected Table table;
    protected Hashtable valueCache;
    protected Hashtable indexCache;
    protected HashSet continuousValues;
    Tuple[] tuple;
    protected int tupleCount;
    protected Attribute[] attrs;

    // These variables used for PRM Database tally for identification
    protected int tallyID;
    protected DatabaseTally owner;
    protected int[] local2globalIndex;
    protected Set indexableAttr;
    protected Set[] indexedValues;
    protected boolean needIndexing;

    private TableTally() {
    }

    /**
     * Constructor for Tallyer.
     */
    TableTally(Table t) {
        setUnderlyingData(t);
    }

    //    /**
    //     * Constructor for Tallyer. Only for PRM based DatabaseTally
    //     */
    //    public Tally(Table t, int id, DatabaseTally o) {
    //        setUnderlyingData(t);
    //        tallyID = id;
    //        owner = o;
    //    }

    public int getTallyID() {
        return tallyID;
    }

    void precache() {
        valueCache = new Hashtable();
        indexCache = new Hashtable();
        continuousValues = new HashSet();
        int idx = 0;
        needIndexing = false;
        attrs = (Attribute[]) table.getAttributes().toArray(new Attribute[0]);
        int max = attrs.length;
        indexableAttr = new HashSet();
        indexedValues = new Set[max];
        for (int i = 0; i < max; i++) {
            Attribute attr = attrs[i];
            String name = attr.getName().trim();
            indexedValues[i] = new HashSet();
            if (attr.isReferenceKey()) {
                indexableAttr.add(new Integer(i));
                needIndexing = true;
            }
            indexCache.put(name, new Integer(i));
            if (attr.getType() != Attribute.DISCRETE) {
                continuousValues.add(name);
                continue;
            }
            valueCache.put(name, new Integer(attr.getArity()));
        }
        tuple = (Tuple[]) table.getTuples().toArray(new Tuple[0]);
        tupleCount = tuple.length;
    }

    public void resetCache() {
        valueCache = null;
        indexCache = null;
        continuousValues = null;
    }

    /**
     * <P>Count the number of tuples that is compliant to the
     * spec mentioned in the q table (i.e. the query table).
     * <p/>
     * <P>If you only have one entry in q table, better use
     * tally(int, int).
     *
     * @param indices is the array of index
     * @param values  is the array of values of that index
     * @return int the count.
     */
    public int tally(int[] indices, int[] values) {
        if (tupleCount == 0) return 0;
        assert (indices.length == values.length);
        //        int[] indices = new int[q.size()];
        //        double[] values = new double[q.size()];
        //        int max = 0;
        //        for (Enumeration e = q.keys(); e.hasMoreElements(); max++) {
        //            Integer key = (Integer) e.nextElement();
        //            indices[max] = key.intValue();
        //            values[max] = ((Integer) q.get(key)).intValue();
        //        }

        //        max = q.size();
        int max = indices.length;
        int total = 0;
        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];
            boolean allEqual = true;
            for (int j = 0; j < max; j++) {
                if (t.getValue(indices[j]) != values[j]) {
                    allEqual = false;
                    break;
                }
            }
            if (allEqual) total++;
        }
        return total;
    }

    /**
     * Count the number of tuples that has val[nodeindex] = value.
     *
     * @param idx   Node index
     * @param value Node value
     * @return int The count
     */
    public int tally(int idx, int value) {
        if (tupleCount == 0) return 0;

        int total = 0;

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];

            if (t.getValue(idx) == value) {
                total++;
            }
        }

        return total;
    }


    /**
     * Count the weights of tuples that has val[nodeindex] = value.
     *
     * @param idx   Node index
     * @param value Node value
     * @return double The count
     */
    public double weightedTally(int idx, int value) {
        if (tupleCount == 0) return 0;

        double total = 0.0;

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];

            if (t.getValue(idx) == value) {
                total += t.getWeight();
            }
        }

        return total;
    }

    /**
     * Count total weights of the tuples
     */
    public double weightedTally() {
        if (tupleCount == 0) return 0;

        double total = 0.0;

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];
            total += t.getWeight();
        }

        return total;
    }

    /**
     * <P>Create a new tally that basically filtered according
     * to the spec mentioned in the q table (i.e. the query table).
     * <p/>
     * <P>If you only have one entry in q table, better use
     * createSubTally(int, int).
     *
     * @param indices is the array of index
     * @param values  is the array of values of that index
     * @return Tally the sub tally
     */
    public Tally createSubTally(int[] indices, int[] values) {
        if (tupleCount == 0) return this;
        assert (indices.length == values.length);
        TableTally ta = new TableTally();
        ta.attrs = attrs;
        ta.indexCache = indexCache;
        ta.valueCache = valueCache;
        ta.table = table;
        ta.owner = owner;
        ta.local2globalIndex = local2globalIndex;
        ta.tallyID = tallyID;
        ta.indexableAttr = indexableAttr;
        ta.needIndexing = needIndexing;

        //        int[] indices = new int[q.size()];
        //        double[] values = new double[q.size()];
        //        int max = 0;
        //        for (Enumeration e = q.keys(); e.hasMoreElements(); max++) {
        //            Integer key = (Integer) e.nextElement();
        //            indices[max] = key.intValue();
        //            values[max] = ((Integer) q.get(key)).intValue();
        //        }

        //        max = q.size();
        int max = indices.length;
        int ctra = 0;
        ta.tuple = new Tuple[tupleCount];

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];
            boolean allEqual = true;
            for (int j = 0; j < max; j++) {
                if (t.getValue(indices[j]) != values[j]) {
                    allEqual = false;
                    break;
                }
            }
            if (allEqual) {
                ta.tuple[ctra] = t;
                ctra++;
            }
        }

        ta.tupleCount = ctra;
        return ta;
    }

    /**
     * <P>Create subtally that has val[nodeindex] = value.
     *
     * @param idx   Node index
     * @param value Node value
     * @return Tally the sub tally.
     */
    public Tally createSubTally(int idx, int value) {
        if (tupleCount == 0) return this;
        TableTally ta = new TableTally();
        ta.attrs = attrs;
        ta.indexCache = indexCache;
        ta.valueCache = valueCache;
        ta.table = table;
        ta.owner = owner;
        ta.local2globalIndex = local2globalIndex;
        ta.tallyID = tallyID;
        ta.indexableAttr = indexableAttr;
        ta.needIndexing = needIndexing;

        int ctra = 0;
        ta.tuple = new Tuple[tupleCount];

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];

            if (t.getValue(idx) == value) {
                ta.tuple[ctra] = t;
                ctra++;
            }
        }

        ta.tupleCount = ctra;
        return ta;
    }

    /**
     * Pretty much like createSubTally, except that it doesn't create a new
     * Tally object and apply the filter to this object.
     *
     * @param idx
     * @param values
     */
    public void filter(int idx, Set values) {
        Tuple[] newTuple = new Tuple[tupleCount];
        int ctra = 0;

        for (int i = 0; i < tupleCount; i++) {
            Tuple t = tuple[i];
            int value = (int) t.getValue(idx);

            if (values.contains(new Integer(value))) {
                newTuple[ctra] = t;
                ctra++;
            }
        }
        tuple = newTuple;
        tupleCount = ctra;
    }

    public int getValueIndex(String attr, String value) {
        Integer i = (Integer) indexCache.get(attr);
        if (i == null) return -1;
        return attrs[i.intValue()].getValueIndex(value);
    }

    /**
     * Get the values of a particular atribute name
     *
     * @param attr
     * @return Set
     */
    public int getArity(String attr) {
        return ((Integer) valueCache.get(attr)).intValue();
    }

    /**
     * Get the values of a particular atribute name
     */
    public int getArity(int i) {
        return attrs[i].getArity();
    }

    /**
     * @return Table
     */
    public Data getUnderlyingData() {
        return table;
    }

    /**
     * Sets the tuples.
     *
     * @param tuples The tuples to set
     */
    public void setUnderlyingData(Data tuples) {
        this.table = (Table) tuples;
        precache();
    }

    /**
     * @return int
     */
    public int size() {
        return tupleCount;
    }

    /**
     * @return
     */
    public DatabaseTally getOwner() {
        return owner;
    }

    /**
     * @param tally
     */
    public void setOwner(DatabaseTally tally) {
        owner = tally;
    }

    /**
     * Setting up localIndex &lt;--&gt; globalIndex translation for PRM purposes.
     * This is an ugly hack, but for the sake of speed up...
     * <p/>
     * Note: This is primarily for caching. No one should mess up with this.
     *
     * @param localIndex
     * @param globalIndex
     */
    void setGlobalIndex(int localIndex, int globalIndex) {
        if (local2globalIndex == null) {
            local2globalIndex = new int[table.attributes.size()];
        }
        local2globalIndex[localIndex] = globalIndex;
    }

    /**
     * Get global index given a local index.
     * Note: This is primarily for caching. No one should mess up with this.
     *
     * @param localIndex
     * @return
     */
    int getGlobalIndex(int localIndex) {
        return local2globalIndex[localIndex];
    }

    /**
     * Return indexed values of a certain attribute after summarizing the tuples
     * Note: This is primarily for caching. No one should mess up with this.
     *
     * @param localIndex
     * @return
     */
    Set getIndexedValues(int localIndex) {
        if (!needIndexing) return null;
        if (indexedValues != null) {
            for (int i = 0; i < attrs.length; i++) {
                indexedValues[i] = new HashSet();
            }
            for (int i = 0; i < tupleCount; i++) {
                Tuple t = tuple[i];
                for (Iterator j = indexableAttr.iterator(); j.hasNext();) {
                    int attr = ((Integer) j.next()).intValue();
                    int value = (int) t.getValue(attr);
                    indexedValues[attr].add(new Integer(value));
                }
            }
        }
        return indexedValues[localIndex];
    }

    /**
     * For God sake, DON'T MAKE THIS METHOD PUBLIC!!! This is a dirty hack! Achtung!
     * This is for LocalDatabaseTally for the sake of experiments only. Bad API! Bad API!
     *
     * @return
     */
    Tuple[] getTuples() {
        Tuple[] newTuples = new Tuple[tupleCount];
        System.arraycopy(tuple, 0, newTuples, 0, tupleCount);
        return newTuples;
    }

    public int[] getRelevantAttributeIndices() {
        int[] idx = new int[attrs.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        return idx;
    }

    /**
     * @see edu.ksu.cis.kdd.data.TallyInterface#groupedTally(int[])
     */
    public List groupedTally(int[] indices) {
        int[] values = new int[indices.length];
        return groupedTally(indices, values, 0, new LinkedList());
    }

    protected List groupedTally(int[] indices, int[] values, int depth, List valueList) {
        int maxArity = attrs[indices[depth]].getArity();
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
