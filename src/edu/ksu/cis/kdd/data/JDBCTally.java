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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class JDBCTally extends DatabaseTally {

    protected String constraints = "";
    protected HashSet tablesInvolved = new HashSet();

    // Another cache
    protected String[] tableNames;
    protected String[] attrNames;

    /* (non-Javadoc)
     * @see edu.ksu.cis.kdd.data.TallyInterface#createSubTally(int[], int[])
     */
    public Tally createSubTally(int[] indices, int[] values) {
        // TODO Auto-generated method stub
        throw new RuntimeException();
    }

    /* (non-Javadoc)
     * @see edu.ksu.cis.kdd.data.TallyInterface#size()
     */
    public int size() {
        // TODO Auto-generated method stub
        throw new RuntimeException();
    }

    /* (non-Javadoc)
     * @see edu.ksu.cis.kdd.data.TallyInterface#tally(int[], int[])
     */
    public int tally(int[] indices, int[] values) {
        // TODO Auto-generated method stub
        throw new RuntimeException();
    }

    /**
     * @param d
     */
    JDBCTally(Database d) {
        super(d);
        if (!d.isRemote()) throw new RuntimeException("This needs a remote database!");
    }

    protected void preprocess() {
        super.preprocess();
        int numAttrs = attributeCache.length;
        tableNames = new String[numAttrs];
        attrNames = new String[numAttrs];
        for (int i = 0; i < numAttrs; i++) {
            Cache attrCache = attributeCache[i];
            Table table = tables[attrCache.tableID];
            tableNames[i] = table.getName();
            attrNames[i] = table.getName() + "." + attributes[i].getName(); // $NON-NLS-1$
        }
    }

    /**
     * @see edu.ksu.cis.kdd.data.DatabaseTally#tally(int, int)
     */
    public int tally(int attr, int value) {
        if (tables == null) preprocess();
        String tableName = tableNames[attr];
        String valueString = attributes[attr].getValues().get(value).toString();
        String involvedTables = getInvolvedTableNames();
        if (!tablesInvolved.contains(tableName)) {
            if (involvedTables.length() > 0)
                involvedTables += (", " + tableName); // $NON-NLS-1$
            else
                involvedTables = tableName;
        }

        try {
            Statement stmt = db.getRemoteStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + involvedTables + // $NON-NLS-1$
                    " WHERE " + concatConstraints(constraints, attrNames[attr] + "='" + valueString + "'"));  // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List groupedTally(int[] attrs, int[] values) {
        if (tables == null) preprocess();
        assert(attrs != null && values != null && attrs.length == values.length);
        int numEntry = attrs.length;
        HashSet oldSet = (HashSet) tablesInvolved.clone();
        String newConstraint = constraints;
        for (int i = 0; i < numEntry; i++) {
            int attr = attrs[i];
            String valueString = attributes[attr].getValues().get(values[i]).toString();
            tablesInvolved.add(tableNames[attr]);
            newConstraint = concatConstraints(newConstraint, attrNames[attr] + "='" + valueString + "'"); // $NON-NLS-1$ // $NON-NLS-2$
        }
        String involvedTables = getInvolvedTableNames();
        tablesInvolved = oldSet;
        try {
            Statement stmt = db.getRemoteStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + involvedTables + // $NON-NLS-1$
                    " WHERE " + newConstraint);  // $NON-NLS-1$
            LinkedList result = new LinkedList();
            while (rs.next()) {
                result.add(new Integer(rs.getInt(1)));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List groupedTally(int[] attrs) {
        if (attributes == null) preprocess();
        assert(attrs != null);
        int numEntry = attrs.length;
        String involvedTables = tableNames[attrs[0]];
        String newConstraint = " GROUP BY " + attrNames[attrs[0]]; // $NON-NLS-1$
        HashSet seen = new HashSet();
        seen.add(involvedTables);
        for (int i = 1; i < numEntry; i++) {
            String tableName = tableNames[attrs[i]];
            if (!seen.contains(tableName)) {
                involvedTables += (", " + tableName); // $NON-NLS-1$
                seen.add(tableName);
            }
            newConstraint += (", " + attrNames[attrs[i]]); // $NON-NLS-1$
        }
        try {
            Statement stmt = db.getRemoteStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + involvedTables + // $NON-NLS-1$
                    newConstraint);
            LinkedList result = new LinkedList();
            while (rs.next()) {
                result.add(new Integer(rs.getInt(1)));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getInvolvedTableNames() {
        if (tablesInvolved.size() == 0) return "";
        StringBuffer buf = new StringBuffer();
        for (Iterator i = tablesInvolved.iterator(); i.hasNext();) {
            buf.append(i.next().toString());
            if (i.hasNext()) buf.append(", "); // $NON-NLS-1$
        }
        return buf.toString();
    }

    protected static String concatConstraints(String constraints, String newCondition) {
        if (constraints.length() > 0) constraints += " AND ";  // $NON-NLS-1$
        constraints = constraints + newCondition;
        return constraints;
    }

    /**
     * @see edu.ksu.cis.kdd.data.DatabaseTally#createSubTally(int, int)
     */
    public Tally createSubTally(int attr, int value) {
        if (tables == null) preprocess();
        String tableName = tableNames[attr];
        String valueString = attributes[attr].getValues().get(value).toString();

        JDBCTally newTally = new JDBCTally(db);
        newTally.tablesInvolved = (HashSet) tablesInvolved.clone();
        newTally.tablesInvolved.add(tableName);
        newTally.tallies = tallies;
        newTally.tables = tables;
        newTally.attributeCache = attributeCache;
        newTally.attributeIndices = attributeIndices;
        newTally.allAttributeIndices = allAttributeIndices;
        newTally.tableBaseIndex = tableBaseIndex;
        newTally.attributes = attributes;
        newTally.attrNames = attrNames;
        newTally.tableNames = tableNames;
        newTally.constraints = concatConstraints(constraints, attrNames[attr] + "='" + valueString + "'"); // $NON-NLS-1$ // $NON-NLS-2$

        return newTally;
    }

}
