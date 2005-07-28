/*
 * Created on 20 May 2003
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

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;


/**
 * @author Roby Joehanes
 */
public class LocalDatabaseTally extends DatabaseTally {

    protected boolean alreadyPrefiltered = false;
    protected TreeSet tablesInvolved = new TreeSet();
    protected Tuple[] joinCache = null;

    /**
     * @param d
     */
    LocalDatabaseTally(Database d) {
        super(d);
    }

    /**
     * @see edu.ksu.cis.kdd.data.TallyInterface#createSubTally(int[], int[])
     */
    public Tally createSubTally(int[] indices, int[] values) {
        assert (indices.length == values.length);
        LocalDatabaseTally ourTally = this;
        // I know, I know. I'm lazy on doing this, but this works and this module
        // is going to be dumped anyway....
        for (int i = 0; i < indices.length; i++) {
            ourTally = (LocalDatabaseTally) ourTally.createSubTally(indices[i], values[i]);
        }
        return ourTally;
    }

    /**
     * @see edu.ksu.cis.kdd.data.TallyInterface#size()
     */
    public int size() {
        if (joinCache == null) joinAll();
        return joinCache.length;
    }

    /* (non-Javadoc)
     * @see edu.ksu.cis.kdd.data.TallyInterface#tally(int[], int[])
     */
    public int tally(int[] indices, int[] values) {
        assert (indices.length == values.length);
        if (joinCache == null) joinAll();
        int max = joinCache.length, maxj = indices.length;
        int counter = 0;
        for (int i = 0; i < max; i++) {
            boolean match = true;
            for (int j = 0; j < maxj; j++) {
                if (joinCache[i].valueCache[indices[j]] != values[j]) {
                    match = false;
                    break; // doesn't match
                }
            }
            if (!match) continue;
            match = true;
            for (int j = 0; j < allAttributeIndices.length; j++) {
                Cache cache = attributeCache[j];
                if (cache.refID == -1) continue; // not a reference key, skip.
                if (joinCache[i].valueCache[cache.refID] != joinCache[i].valueCache[j]) { // reference doesn't match
                    match = false;
                    break;
                }
            }
            if (match) counter++;
        }

        return counter;
    }

    public int tally(int attr, int value) {
        if (attributeCache == null) preprocess();
        int numTables = tallies.length;

        if (numTables == 1) { // short circuit for single table
            return tallies[0].tally(attr, value);
        }

        // Prefilter. You'll see the payoff of the lengthy preprocessing step above.
        if (!alreadyPrefiltered) {
            alreadyPrefiltered = true;
            for (int i = 0; i < allAttributeIndices.length; i++) {
                Cache cache = attributeCache[i];
                if (cache.refID <= i) continue; // not a reference key, skip.
                Cache targetCache = attributeCache[cache.refID];
                Set indexedValues = tallies[targetCache.tableID].getIndexedValues(targetCache.localID);
                if (indexedValues != null) { // maybe it's not filtered
                    tallies[cache.tableID].filter(cache.localID, indexedValues);
                }
                //                indexedValues = tallies[cache.tableID].getIndexedValues(cache.localID);
                //                if (indexedValues != null) { // maybe it's not filtered
                //                    tallies[targetCache.tableID].filter(targetCache.localID, indexedValues);
                //                }
            }
        }

        // No context at all
        //        Cache cache = attributeCache[attr];
        //        if (tablesInvolved.size() == 0) {
        //            return tallies[cache.tableID].tally(attr, value);
        //        }

        //        // From this and below, it's not correct.
        //        TreeSet tablesInvolved = (TreeSet) this.tablesInvolved.clone();
        //        tablesInvolved.add(new Integer(cache.tableID));
        //
        //        HashSet seenBefore = new HashSet();
        //        int min = Integer.MAX_VALUE;
        //        for (Iterator i = tablesInvolved.iterator(); i.hasNext(); ) {
        //            int tableID = ((Integer) i.next()).intValue();
        //            int tallySize = tallies[tableID].size();
        //            if (tallySize < min) min = tallySize;
        //        }
        //
        //        return min;

        // Do join operation to ensure correctness
        if (joinCache == null) joinAll();
        // Then filter the join...
        int max = joinCache.length;
        int counter = 0;
        for (int i = 0; i < max; i++) {
            if (joinCache[i].valueCache[attr] != value) continue; // doesn't match
            boolean match = true;
            for (int j = 0; j < allAttributeIndices.length; j++) {
                Cache cache = attributeCache[j];
                if (cache.refID == -1) continue; // not a reference key, skip.
                if (joinCache[i].valueCache[cache.refID] != joinCache[i].valueCache[j]) { // reference doesn't match
                    match = false;
                    break;
                }
            }
            if (match) counter++;
        }

        return counter;
    }

    protected Tuple[] joinAll() {
        for (int i = 0; i < tallies.length; i++) {
            if (tallies[i].size() == 0) {
                joinCache = new Tuple[0];
                return joinCache; // because we know that the join size would be 0;
            }
        }
        joinCache = tallies[0].getTuples();
        for (int i = 1; i < tallies.length; i++) {
            joinCache = join(joinCache, tallies[i].getTuples());
        }
        return joinCache;
    }

    protected Tuple[] join(Tuple[] t1, Tuple[] t2) {
        int max1 = t1.length, max2 = t2.length;
        int t1Attr = t1[0].valueCache.length;
        int t2Attr = t2[0].valueCache.length;
        int maxlen = t1Attr * t2Attr;
        Tuple[] newTuples = new Tuple[max1 * max2];
        int idx = 0;
        for (int i = 0; i < max1; i++) {
            for (int j = 0; j < max2; j++, idx++) {
                Tuple t = new Tuple();
                LinkedList values = new LinkedList();
                values.addAll(t1[i].getValues());
                values.addAll(t2[i].getValues());
                t.setValues(values);
                t.valueCache = new double[maxlen]; // copy the cache as well
                System.arraycopy(t1[i].valueCache, 0, t.valueCache, 0, t1Attr);
                System.arraycopy(t2[i].valueCache, 0, t.valueCache, t1Attr, t2Attr);
                newTuples[idx] = t;
            }
        }
        return newTuples;
    }

    /**
     * Create a sub tally that is filtered according the attribute number <tt>attr</tt> with its
     * value. This does NOT propagate the ref keys -- too expensive. Doing it latter in tally phase
     *
     * @param attr
     * @param value
     * @return Prefiltered tally
     */
    public Tally createSubTally(int attr, int value) {
        if (attributeCache == null) preprocess();
        LocalDatabaseTally newTally = new LocalDatabaseTally(db);
        newTally.allAttributeIndices = allAttributeIndices;
        newTally.attributeIndices = attributeIndices;
        newTally.tables = tables;
        newTally.tablesInvolved = (TreeSet) tablesInvolved;

        int numTables = tallies.length;
        newTally.tallies = new TableTally[numTables];

        if (numTables == 1) { // short circuit for single table
            newTally.tallies[0] = (TableTally) tallies[0].createSubTally(attr, value);
            return newTally;
        }

        System.arraycopy(tallies, 0, newTally.tallies, 0, numTables);
        Cache cache = attributeCache[attr];
        newTally.tallies[cache.tableID] = (TableTally) newTally.tallies[cache.tableID].createSubTally(cache.localID, value);

        //newTally.requestBuffer.add(new int[] { attr, value });
        newTally.tablesInvolved.add(new Integer(cache.tableID));

        return newTally;
    }
}
