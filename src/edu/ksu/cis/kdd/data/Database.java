/*
 * Created on 27 Apr 2003
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

import edu.ksu.cis.bnj.bbn.converter.ConverterData;
import edu.ksu.cis.kdd.data.converter.Converter;
import edu.ksu.cis.kdd.data.converter.arff.ArffParser;
import edu.ksu.cis.kdd.util.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;

/**
 * @author Roby Joehanes
 */
public class Database implements Data {
    protected Hashtable tableCache = new Hashtable();
    protected String name = ""; //$NON-NLS-1$

    public static final String CSF_FORMAT = "csf";
    public static final String ARFF_FORMAT = "arff";
    public static final String XML_FORMAT = "xml";
    public static final String LIBB_FORMAT = "libb";
    public static final String DAT_FORMAT = "dat";

    private static final String interfaceName = "edu.ksu.cis.kdd.data.converter.Converter"; //$NON-NLS-1$
    private static TableSet converterTable = Settings.getDataConverterTable();

    public static int maxSQLStringLength = 30;
    public static int maxArity = 30;

    protected Connection dbConnection = null;
    protected Statement remoteStatement = null;
    protected Tally tally = null;

    // Cached sorted table list
    protected List sortedTableListCache = null;
    protected List attributes = null;
    protected List relevantAttributes = null;
    //protected List joinCache = null;

    public Database() {
    }

    public Database(Connection c) {
        dbConnection = c;
    }

    public static boolean isKnownFormat(String formatExt) {
        return formatExt != null && Settings.getDataConverterTable().get(formatExt) != null;
    }

    /**
     * @see edu.ksu.cis.kdd.data.Data#subsample(int, long)
     */
    public Data subsample(int n, long seed) {
        // TODO Auto-generated method stub
        throw new RuntimeException("Do this NOW! This is not done!");
    }

    /**
     * @see edu.ksu.cis.kdd.data.Data#subsample(int)
     */
    public Data subsample(int n) {
        // TODO Auto-generated method stub
        throw new RuntimeException("Do this NOW! This is not done!");
    }

    /**
     * @see edu.ksu.cis.kdd.data.Data#getWeights()
     */
    public double[] getWeights() {
        // TODO Auto-generated method stub
        throw new RuntimeException("Do this NOW! This is not done!");
    }

    /**
     * @see edu.ksu.cis.kdd.data.Data#setWeights(double[])
     */
    public void setWeights(double[] weights) {
        throw new RuntimeException("Do this NOW! This is not done!");

    }

    public int tableCount() {
        return tableCache.size();
    }

    /**
     * Add a table
     *
     * @param tbl
     */
    public void addTable(Table tbl) {
        tableCache.put(tbl.getName().toUpperCase(), tbl);
        tbl.setOwner(this);

        sortedTableListCache = null;
        //joinCache = null;
    }

    public List getAttributes() {
        if (sortedTableListCache == null) getSortedTables();
        return attributes;
    }

    public List getRelevantAttributes() {
        if (sortedTableListCache == null) getSortedTables();
        return relevantAttributes;
    }

    /**
     * This is the outer join function
     */
    public List getTuples() {
        if (sortedTableListCache == null) getSortedTables();
        if (sortedTableListCache.size() == 1) return ((Table) sortedTableListCache.get(0)).getTuples();
        if (isRemote()) importDatabaseToLocal();
        return joinAll();
    }

    protected List joinAll() {
        //if (joinCache != null) return joinCache;
        for (Iterator i = sortedTableListCache.iterator(); i.hasNext();) {
            Table t = (Table) i.next();
            if (t.size() == 0) return new LinkedList(); // because we know that the join size would be 0;
        }
        List joinCache = new LinkedList();
        Iterator i = sortedTableListCache.iterator();
        joinCache.addAll(((Table) i.next()).getTuples());
        for (; i.hasNext();) {
            Table t = (Table) i.next();
            joinCache = join(joinCache, t.getTuples());
        }
        return joinCache;
    }

    protected List join(List t1, List t2) {
        Tuple t1_i = (Tuple) t1.get(0);
        Tuple t2_j = (Tuple) t2.get(0);
        if (t1_i.valueCache == null) t1_i.precache();
        if (t2_j.valueCache == null) t2_j.precache();
        int t1Attr = ((Tuple) t1.get(0)).valueCache.length;
        int t2Attr = ((Tuple) t2.get(0)).valueCache.length;
        int maxlen = t1Attr * t2Attr;
        LinkedList result = new LinkedList();
        int idx = 0;
        for (Iterator i = t1.iterator(); i.hasNext();) {
            t1_i = (Tuple) i.next();
            if (t1_i.valueCache == null) t1_i.precache();
            for (Iterator j = t2.iterator(); j.hasNext(); idx++) {
                t2_j = (Tuple) j.next();
                if (t2_j.valueCache == null) t2_j.precache();
                Tuple t = new Tuple();
                LinkedList values = new LinkedList();
                values.addAll(t1_i.getValues());
                values.addAll(t2_j.getValues());
                t.setValues(values);
                t.valueCache = new double[maxlen]; // copy the cache as well
                System.arraycopy(t1_i.valueCache, 0, t.valueCache, 0, t1Attr);
                System.arraycopy(t2_j.valueCache, 0, t.valueCache, t1Attr, t2Attr);
                result.add(t);
            }
        }
        return result;
    }


    public Tally getTallyer() {
        if (tally == null) {
            if (sortedTableListCache == null) getSortedTables();
            if (sortedTableListCache.size() == 1) {
                tally = ((Table) sortedTableListCache.get(0)).getTallyer();
            }
            if (isRemote()) {
                tally = new JDBCTally(this);
            } else {
                tally = new LocalDatabaseTally(this);
            }
        }
        return tally;
    }

    /**
     * Get all tables in reverse sorted dependency order.
     * The table that gets reverenced the most gets the first place.
     * <p/>
     * <p>This big chunk of code is for preprocessing the database. The process is roughly
     * as follows:
     * <ol>
     * <li>If it contains single table, short circuit it. Do the preparation in a single tally.</li>
     * <li>Detect forward and backward reference -- to determine the table graph</li>
     * <li>Do a topological sort on that graph. Of course, if the table graph is cyclic, it should
     * be able to handle that as well since the topological sort property is minimizing the backedges.</li>
     * </ol>
     * <p/>
     * <P>Note to self: Do NOT call this method in the constructor as it would slow down the createSubTally.
     */
    public List getSortedTables() {
        if (sortedTableListCache != null) return sortedTableListCache;
        Table[] unsortedTables = (Table[]) getTables().toArray(new Table[0]);

        int numTables = unsortedTables.length;

        // contains only a single table
        if (numTables == 1) {
            sortedTableListCache = new LinkedList();
            sortedTableListCache.add(unsortedTables[0]);
            attributes = new LinkedList();
            attributes.addAll(unsortedTables[0].getAttributes());
            relevantAttributes = attributes;
            return sortedTableListCache;
        }

        // Reachability analysis
        // Detecting orphan table or inconsistent attribute declaration
        TableSet tableGraph = new TableSet();
        for (int i = 0; i < numTables; i++) {
            String tableName = unsortedTables[i].getName();
            List rKeys = unsortedTables[i].getReferenceKeys();

            for (Iterator j = rKeys.iterator(); j.hasNext();) {
                Attribute attr = (Attribute) j.next();
                // If this table has a reference key, then it's better be having
                // a primary key counterpart of some other table
                String refTableName = attr.getReferencedTableName();
                String refAttrName = attr.getReferencedAttributeName();
                if (refTableName == null || refAttrName == null || getTable(refTableName) == null) {
                    // It's an orphan. Throw an exception
                    throw new RuntimeException("Bad reference " + tableName);
                } else {
                    Table childTable = getTable(refTableName);
                    assert (childTable.getAttribute(refAttrName) != null) :
                            "Invalid attribute reference detected!";
                    tableGraph.put(tableName, childTable.getName());
                }
            }
        }

        // Do topological sort
        List sortedTableNames = topologicalSort(tableGraph, unsortedTables);
        assert (sortedTableNames.size() == numTables);

        LinkedList sortedTable = new LinkedList();
        for (Iterator i = sortedTableNames.iterator(); i.hasNext();) {
            String tableName = (String) i.next();
            sortedTable.add(getTable(tableName));
        }
        sortedTableListCache = sortedTable;

        attributes = new LinkedList();
        relevantAttributes = new LinkedList();
        for (Iterator i = sortedTable.iterator(); i.hasNext();) {
            Table t = (Table) i.next();
            for (Iterator j = t.getAttributes().iterator(); j.hasNext();) {
                Attribute attr = (Attribute) j.next();
                attributes.add(attr);
                if (!attr.isPrimaryKey() && !attr.isReferenceKey()) {
                    relevantAttributes.add(attr);
                }
            }
        }
        return sortedTable;
    }

    /**
     * Do topological sort on tables. Required for getSortedTables.
     */
    protected List topologicalSort(TableSet table, Table[] tables) {
        LinkedList sorted = new LinkedList();
        HashSet seenBefore = new HashSet();
        int numTables = tables.length;
        for (int i = 0; i < numTables; i++) {
            String tableName = tables[i].getName();
            if (!seenBefore.contains(tableName)) {
                seenBefore.add(tableName);
                topologicalSort(tableName, table, sorted, seenBefore);
            }
        }
        return sorted;
    }

    /**
     * Do topological sort on tables. Required for getSortedTables.
     * This is the recursive feedback for the other topologicalSort method.
     */
    protected void topologicalSort(String curNode, TableSet table, LinkedList sortedList, HashSet seenBefore) {
        Set children = table.get(curNode);
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext();) {
                String child = (String) i.next();
                if (!seenBefore.contains(child)) {
                    seenBefore.add(child);
                    topologicalSort(child, table, sortedList, seenBefore);
                }
            }
        }
        sortedList.addFirst(curNode);
    }

    /**
     * Get all tables. Note that pickOneTable result is not necessarily
     * the same as getTables().get(0)!
     *
     * @return
     */
    public List getTables() {
        LinkedList tables = new LinkedList();
        tables.addAll(tableCache.values());
        return tables;
    }

    /**
     * Get all table names
     *
     * @return the list
     */
    public List getTableNames() {
        LinkedList ll = new LinkedList();
        ll.addAll(tableCache.keySet());
        return ll;
    }

    /**
     * Get the first table in the database
     *
     * @return
     */
    public Table pickOneTable() {
        return (Table) tableCache.values().iterator().next();
    }

    /**
     * Get a table based on its name
     *
     * @param name
     * @return the table
     */
    public Table getTable(String name) {
        return (Table) tableCache.get(name.toUpperCase());
    }

    /**
     * Remove a table based on its name
     *
     * @param name
     */
    public void removeTable(String name) {
        tableCache.remove(name.toUpperCase());
        sortedTableListCache = null;
        //joinCache = null;
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public static Database load(String filename, String format) {
        FileInputStream in;
        Database t = null;
        HashSet sourceSet = new HashSet();
        if (format == null) {
            sourceSet.addAll(converterTable.valueSet());
        } else {
            Set s = converterTable.get(format);
            assert(s != null);
            sourceSet.addAll(s);
        }

        for (Iterator i = sourceSet.iterator(); i.hasNext();) {
            try {
                in = new FileInputStream(filename);
            } catch (Exception e) {
                throw new RuntimeException("Error: Cannot load data file " + filename);
            }

            ConverterData d = (ConverterData) i.next();
            String className = d.getPackageName() + "." + d.getClassName(); //$NON-NLS-1$
            if (className.startsWith("null.") || className.endsWith(".null")) //$NON-NLS-1$ //$NON-NLS-2$
                throw new RuntimeException("Error in the configuration file");

            Converter c = null;
            try {
                c = (Converter) FileClassLoader.loadAndInstantiate(className, null, new String[]{interfaceName});
                t = c.load(in);
                in.close();
                if (t != null) return t;
            } catch (Exception e) {
                if (Settings.isDebug())
                    System.out.println("Warning: Cannot load reflection object" + className);
            }
        }
        throw new RuntimeException("Error: Cannot load data file " + filename);
    }

    public static Database load(String filename) {
        return load(filename, null);
    }

    /**
     * Connect to remote database. Assumption: We use all tables contained
     * in that database URL.
     */
    public static Database importRemoteSchema(Connection conn) {
        return importRemoteSchema(conn, null);
    }

    /**
     * A utility function to assign an attribute type based on its SQL type
     *
     * @param attr
     * @param type
     */
    protected static void assignDefaultType(Attribute attr, int sqlType) {
        switch (sqlType) {
            case Types.INTEGER:
                attr.setType(Attribute.INTEGER);
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
                attr.setType(Attribute.REAL);
                break;
            case Types.VARCHAR:
                attr.setType(Attribute.STRING);
                break;
            default:
                throw new RuntimeException("Unhandled types");
        }
    }

    /**
     * Connect to remote database. We use only tables listed in tableNames.
     * WARNING: In some databases (like Oracle), the table names are CASE SENSITIVE
     * IN SOME CASES (such as columns metadata inquiries), but CASE INSENSITIVE
     * in other cases (like the ordinary SQL queries).
     */
    public static Database importRemoteSchema(Connection conn, List tableNames) {
        Database db = null;
        try {
            boolean isMySQL = false;
            DatabaseMetaData dbmeta = conn.getMetaData();
            if (dbmeta.getDatabaseProductName().equals("MySQL")) {
                int version = dbmeta.getDatabaseMajorVersion();
                if (version < 4) throw new RuntimeException("Database not supported! MySQL must be at least v4.0.12");
                isMySQL = true;
            }

            if (tableNames == null) {
                ResultSet rs = dbmeta.getTables(null, null, null, new String[]{"TABLE"}); // $NON-NLS-1$
                tableNames = new LinkedList();
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME"); // $NON-NLS-1$
                    tableNames.add(tableName);
                }
            }

            db = new Database(conn);
            db.remoteStatement = db.dbConnection.createStatement();
            for (Iterator i = tableNames.iterator(); i.hasNext();) {
                String tableName = (String) i.next(); // $NON-NLS-1$
                Table table = new Table();
                table.setName(tableName);
                db.addTable(table);
                //                if (isMySQL) {
                //                    tableName = tableName.toLowerCase(); // mysql are all lowercase
                //                } else if(dbmeta.getDatabaseProductName().indexOf("Oracle") != -1) { // okay, oracle is all upper case
                //                    tableName = tableName.toUpperCase();
                //                }
                //System.out.println("Table name = "+tableName);
                ResultSet rs2 = db.remoteStatement.executeQuery("SELECT COUNT(*) FROM " + tableName); // $NON-NLS-1$
                rs2.next();
                int numRows = rs2.getInt(1);
                Hashtable underlyingTypeTable = new Hashtable();
                // Get all columns
                rs2 = dbmeta.getColumns(conn.getCatalog(), null, tableName, null);
                while (rs2.next()) {
                    String attrName = rs2.getString("COLUMN_NAME"); // $NON-NLS-1$
                    Attribute attr = new Attribute(attrName);
                    ResultSet rs3 = db.remoteStatement.executeQuery("SELECT COUNT(DISTINCT " + // $NON-NLS-1$
                            attrName + ") FROM " + tableName); // $NON-NLS-1$
                    rs3.next();
                    int arity = rs3.getInt(1);
                    underlyingTypeTable.put(attr.getName(), new Integer(rs2.getInt("DATA_TYPE"))); // $NON-NLS-1$

                    // We know it's discrete
                    if (arity <= maxArity && arity < numRows) {
                        rs3 = db.remoteStatement.executeQuery("SELECT DISTINCT " + // $NON-NLS-1$
                                attrName + " FROM " + tableName); // $NON-NLS-1$
                        while (rs3.next()) {
                            String value = rs3.getString(1);
                            attr.addValue(value);
                        }
                    } else { // If it's non-discrete
                        assignDefaultType(attr, rs2.getInt("DATA_TYPE")); // $NON-NLS-1$
                    }
                    table.addAttribute(attr);
                }
                // Retrieving primary keys
                try {
                    rs2 = dbmeta.getPrimaryKeys(conn.getCatalog(), null, tableName);
                    while (rs2.next()) {
                        String attrName = rs2.getString("COLUMN_NAME"); // $NON-NLS-1$
                        Attribute attr = table.getAttribute(attrName);
                        assert (attr != null);
                        attr.setKey(Attribute.PRIMARY);
                        int underlyingType = ((Integer) underlyingTypeTable.get(attrName)).intValue();
                        assignDefaultType(attr, underlyingType);
                        attr.setValues(new LinkedList());
                    }
                } catch (Exception ee) {
                    // Ignore if we don't know about the primary key stuff
                    // Some database is ill-behaved. We have to be more conservative
                }
                // Retrieving reference keys
                try {
                    if (isMySQL) {
                        // A hack to get around MySQL JDBC driver bug
                        rs2 = db.remoteStatement.executeQuery("SHOW CREATE TABLE " + tableName); // $NON-NLS-1$
                        rs2.next();
                        String createTableClause = rs2.getString(2);

                        // Okay, then build a crude parser to cope with this
                        StringTokenizer tok = new StringTokenizer(createTableClause, "\n"); // $NON-NLS-1$
                        while (tok.hasMoreTokens()) {
                            String lineToken = tok.nextToken().trim();
                            StringTokenizer lineTok = new StringTokenizer(lineToken, " "); // $NON-NLS-1$
                            int state = 0;
                            String[] triplet = new String[3];
                            while (lineTok.hasMoreTokens()) {
                                String token = lineTok.nextToken();
                                switch (state) {
                                    case 0:
                                        if (token.equals("CONSTRAINT"))
                                            state = 1; // $NON-NLS-1$
                                        else if (token.equals("FOREIGN"))
                                            state = 3; // $NON-NLS-1$
                                        else
                                            state = -1; // INVALID
                                        break;
                                    case 1:
                                        state = 2; // Skip constraint name, we're not interested
                                        break;
                                    case 2:
                                        if (token.equals("FOREIGN"))
                                            state = 3; // $NON-NLS-1$
                                        else
                                            state = -1; // INVALID
                                        break;
                                    case 3:
                                        if (token.equals("KEY"))
                                            state = 4; // $NON-NLS-1$
                                        else
                                            state = -1; // INVALID
                                        break;
                                    case 4:
                                        // This is the foreign key name
                                        token = token.substring(2, token.length() - 2);
                                        triplet[0] = token;
                                        state = 5;
                                        break;
                                    case 5:
                                        if (token.equals("REFERENCES"))
                                            state = 6; // $NON-NLS-1$
                                        else
                                            state = -1; // INVALID
                                        break;
                                    case 6:
                                        // This is the primary referencing table
                                        token = token.substring(1, token.length() - 1);
                                        triplet[1] = token;
                                        state = 7;
                                        break;
                                    case 7:
                                        // This is the primary referencing column
                                        // Cut of trailling commas, if present
                                        if (token.endsWith(",")) // $NON-NLS-1$
                                            token = token.substring(0, token.length() - 1);
                                        token = token.substring(2, token.length() - 2);
                                        triplet[2] = token;
                                        state = 8;
                                        break;
                                    case 8:
                                    default:
                                        state = -1;
                                        break;

                                }
                                if (state == -1) break;
                            } // end while (lineTok.hasMoreTokens())
                            if (state != -1) { // If no errors, consider it
                                Attribute attr = table.getAttribute(triplet[0]);
                                assert (attr != null);
                                attr.setKey(Attribute.REFERENCE);
                                attr.setReference(triplet[1], triplet[2]);
                                int underlyingType = ((Integer) underlyingTypeTable.get(attr.getName())).intValue();
                                assignDefaultType(attr, underlyingType);
                                attr.setValues(new LinkedList());
                            }
                        }
                        // End hack
                    } else {
                        rs2 = dbmeta.getImportedKeys(conn.getCatalog(), null, tableName);
                        while (rs2.next()) {
                            String attrName = rs2.getString("FKCOLUMN_NAME"); // $NON-NLS-1$
                            Attribute attr = table.getAttribute(attrName);
                            assert (attr != null);
                            attr.setKey(Attribute.REFERENCE);
                            attr.setReference(rs2.getString("PKTABLE_NAME"), rs2.getString("PKCOLUMN_NAME")); // $NON-NLS-1$ // $NON-NLS-2$
                            int underlyingType = ((Integer) underlyingTypeTable.get(attrName)).intValue();
                            assignDefaultType(attr, underlyingType);
                            attr.setValues(new LinkedList());
                        }
                    }
                } catch (Exception ee) {
                    // Ditto for reference keys
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return db;
    }

    private int checkValueType(String val) {
        try {
            Integer.parseInt(val);
            return Attribute.INTEGER;
        } catch (Exception ee) {
        }
        try {
            Double.parseDouble(val);
            return Attribute.REAL;
        } catch (Exception ee) {
        }
        return Attribute.DISCRETE;
    }

    /**
     * <P>Export the local data into server. The connection <tt>conn</tt> must
     * be valid AND make sure you closed all created statements on <tt>conn</tt>
     * because this routine will create one. This is because a lot of JDBC drivers
     * only support ONE statement creation per connection.
     *
     * @param conn
     */
    public void exportToServer(Connection conn) {
        Statement stmt = null;
        try {
            if (isRemote() && conn == dbConnection) {
                throw new RuntimeException("Cannot export to source database!");
            }

            // Specific hack for MySQL
            boolean isMySQL = false;
            DatabaseMetaData dbmeta = conn.getMetaData();
            if (dbmeta.getDatabaseProductName().equals("MySQL")) {
                int version = dbmeta.getDatabaseMajorVersion();
                if (version < 4) throw new RuntimeException("Database not supported! MySQL must be at least v4.0.12");
                isMySQL = true;
            }

            stmt = conn.createStatement();
            Table[] tables = (Table[]) getSortedTables().toArray(new Table[0]);
            int numTables = tables.length;
            Hashtable attrTypeCache = new Hashtable();
            for (int i = numTables - 1; i >= 0; i--) {
                Table table = tables[i];
                String tableName = table.getName();
                StringBuffer sqlCommand = new StringBuffer("CREATE TABLE " + tableName + " ("); // $NON-NLS-1$ // $NON-NLS-2$
                List attrs = table.getAttributes();
                boolean[] isNumeric = new boolean[attrs.size()];
                int attrIndex = 0;
                for (Iterator j = attrs.iterator(); j.hasNext(); attrIndex++) {
                    Attribute attr = (Attribute) j.next();
                    sqlCommand.append(attr.getName() + " "); // $NON-NLS-1$
                    switch (attr.getType()) {
                        case Attribute.INTEGER:
                            sqlCommand.append("INTEGER"); // $NON-NLS-1$
                            isNumeric[attrIndex] = true;
                            break;
                        case Attribute.REAL:
                            sqlCommand.append("REAL"); // $NON-NLS-1$
                            isNumeric[attrIndex] = true;
                            break;
                        case Attribute.DISCRETE:
                            // If an attribute is a DISCRETE, then we must detect
                            // the underlying type: whether it's an integer, real,
                            // or string
                            int curVarType = Attribute.INTEGER;
                            for (Iterator k = attr.getValues().iterator(); k.hasNext();) {
                                String curValue = k.next().toString();
                                switch (checkValueType(curValue)) {
                                    case Attribute.INTEGER:
                                        if (curVarType == Attribute.INTEGER) curVarType = Attribute.INTEGER;
                                        break;
                                    case Attribute.REAL:
                                        curVarType = Attribute.REAL;
                                        break;
                                    case Attribute.DISCRETE:
                                        curVarType = Attribute.DISCRETE;
                                        break;
                                }
                                if (curVarType == Attribute.DISCRETE) break;
                            }
                            switch (curVarType) {
                                case Attribute.INTEGER:
                                    isNumeric[attrIndex] = true;
                                    sqlCommand.append("INTEGER"); // $NON-NLS-1$
                                    break;
                                case Attribute.REAL:
                                    isNumeric[attrIndex] = true;
                                    sqlCommand.append("REAL"); // $NON-NLS-1$
                                    break;
                                case Attribute.DISCRETE:
                                    isNumeric[attrIndex] = false;
                                    sqlCommand.append("VARCHAR(" + maxSQLStringLength + ")"); // $NON-NLS-1$ // $NON-NLS-2$
                                    break;
                            }
                            break;
                        case Attribute.STRING:
                            sqlCommand.append("VARCHAR(" + maxSQLStringLength + ")"); // $NON-NLS-1$ // $NON-NLS-2$
                            isNumeric[attrIndex] = false;
                            break;
                        default:
                            throw new RuntimeException("Unknown SQL type (probably a bug)!");
                    }
                    // Enforcing non-null-ness to both primary keys and reference keys
                    // especially for *cough* *cough* Their^H^H^H^H^H MySQL
                    if (attr.isReferenceKey())
                        sqlCommand.append(" NOT NULL"); // $NON-NLS-1$
                    else if (attr.isPrimaryKey()) sqlCommand.append(" NOT NULL UNIQUE"); // $NON-NLS-1$

                    if (j.hasNext()) sqlCommand.append(", "); // $NON-NLS-1$
                }
                attrTypeCache.put(tableName, isNumeric);

                // Add reference key clause
                for (Iterator j = table.getReferenceKeys().iterator(); j.hasNext();) {
                    Attribute attr = (Attribute) j.next();
                    String attrName = attr.getName();

                    // Another MySQL-specific hack:
                    // Indices for foreign key must be declared explicitly
                    if (isMySQL) {
                        sqlCommand.append(", INDEX " + attrName + "_IDX (" + attrName + ")"); // $NON-NLS-1$ // $NON-NLS-2$
                    }
                    sqlCommand.append(", FOREIGN KEY (" + attrName + // $NON-NLS-1$
                            ") REFERENCES " + attr.getReferencedTableName() + "(" + // $NON-NLS-1$ // $NON-NLS-2$
                            attr.getReferencedAttributeName() + ")"); // $NON-NLS-1$
                }

                // Add primary key clause
                List pKeys = table.getPrimaryKeys();
                if (pKeys != null && pKeys.size() > 0) {
                    sqlCommand.append(", PRIMARY KEY("); // $NON-NLS-1$
                    for (Iterator j = pKeys.iterator(); j.hasNext();) {
                        Attribute attr = (Attribute) j.next();
                        sqlCommand.append(attr.getName());
                        if (j.hasNext()) sqlCommand.append(", "); // $NON-NLS-1$
                    }
                    sqlCommand.append(")"); // $NON-NLS-1$
                }
                sqlCommand.append(")"); // $NON-NLS-1$

                // Specific hack for MySQL
                // We MUST enable INNO-DB in order to obtain reference keys information
                if (isMySQL) {
                    sqlCommand.append(" TYPE=INNODB"); // $NON-NLS-1$
                }

                // Okay, up to this point, we already has a well formed
                // CREATE TABLE statement. Let's execute it
                if (Settings.isDebug())
                    System.out.println(sqlCommand.toString());
                stmt.executeUpdate(sqlCommand.toString());
            }

            // Up to this point, the schema should've been formed already...
            // Unto data upload!

            // In this case, we have a local database
            if (!isRemote()) {
                for (int i = numTables - 1; i >= 0; i--) {
                    String tableName = tables[i].getName();
                    String prefix = "INSERT INTO " + tableName + " VALUES (";  // $NON-NLS-1$ // $NON-NLS-2$
                    boolean[] isNumeric = (boolean[]) attrTypeCache.get(tableName);
                    for (Iterator j = tables[i].getTuples().iterator(); j.hasNext();) {
                        Tuple t = (Tuple) j.next();
                        StringBuffer sqlCommand = new StringBuffer(prefix);
                        int attrIndex = 0;
                        for (Iterator k = t.getValues().iterator(); k.hasNext(); attrIndex++) {
                            String value = k.next().toString();
                            if (!isNumeric[attrIndex]) value = '\'' + value + '\''; // Quote non numerics
                            sqlCommand.append(value);
                            if (k.hasNext()) sqlCommand.append(", "); // $NON-NLS-1$
                        }
                        sqlCommand.append(")"); // $NON-NLS-1$

                        // Values are already prepared
                        if (Settings.isDebug())
                            System.out.println(sqlCommand.toString());
                        stmt.executeUpdate(sqlCommand.toString());
                    }
                }
            } else { // Otherwise, it's a server to server export
                throw new RuntimeException("Server-to-server export: We haven't implemented this functionality yet");
            }
            stmt.close();
        } catch (Exception e) {
            try {
                stmt.close();
            } catch (Exception ee) {
            }
            throw new RuntimeException(e);
        }
    }

    public void importDatabaseToLocal() {
        if (!isRemote()) throw new RuntimeException("Import contents requires a remote database!");
        for (Iterator i = tableCache.values().iterator(); i.hasNext();) {
            Table t = (Table) i.next();
            LinkedList l = (LinkedList) t.getTuples();
            t.tuples = l;
        }
        tally = null;
    }

    public Connection getConnection() {
        return dbConnection;
    }

    public Statement getRemoteStatement() {
        return remoteStatement;
    }

    public boolean isRemote() {
        return dbConnection != null;
    }

    public void disconnect() {
        if (dbConnection != null) {
            try {
                remoteStatement.close();
                dbConnection.close();
                remoteStatement = null;
                dbConnection = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(String filename) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            new ArffParser().save(out, this);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * saves in different format
     *
     * @param filename name of the output file
     */
    public void save(String filename, String format) {
        try {
            save(new FileOutputStream(filename), format);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(OutputStream out, String format) {
        try {
            TableSet converterTable = Settings.getDataConverterTable();
            Set conv = converterTable.get(format);
            if (conv == null) throw new RuntimeException("Unkown Format");
            if (conv.size() > 1) throw new RuntimeException("Conflict in converter table");

            ConverterData d = (ConverterData) conv.iterator().next();
            String className = d.getPackageName() + "." + d.getClassName(); //$NON-NLS-1$
            if (className.startsWith("null.") || className.endsWith(".null")) //$NON-NLS-1$ //$NON-NLS-2$
                throw new RuntimeException("Error in the configuration file");

            Converter c = null;
            try {
                c = (Converter) FileClassLoader.loadAndInstantiate(className, null, new String[]{interfaceName});
                c.save(out, this);
                out.close();
            } catch (Exception e) {
                if (Settings.isDebug())
                    System.out.println("Warning: Cannot load reflection object" + className);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); // $NON-NLS-1$
        for (Iterator i = tableCache.values().iterator(); i.hasNext();) {
            buf.append(i.next().toString() + ln);
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i"); //$NON-NLS-1$
        String outputFormat = params.getString("-f"); //$NON-NLS-1$
        String outputFile = params.getString("-o"); //$NON-NLS-1$
        boolean quiet = params.getBool("-q"); //$NON-NLS-1$

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.kdd.data.converter.datastructure.Table -i:inputfile [-o:outputfile] [-f:saveformat] [-q]");
            System.out.println("-f: default=arff. Acceptable values are {arff, xml, csf, dat}");
            System.out.println("-q: quiet mode");
            System.out.println("If outputfile is not specified, it will default to the standard output.");
            return;
        }


        OutputStream out = System.out;

        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Runtime r = Runtime.getRuntime();
        long freemem = r.freeMemory();
        Database db = null;
        try {
            db = Database.load(inputFile);
            freemem = freemem - r.freeMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!quiet) {
            System.out.println("Memory needed = " + freemem);
            System.out.print("Dependency sort yields: [");
            for (Iterator i = db.getSortedTables().iterator(); i.hasNext();) {
                Table t = (Table) i.next();
                System.out.print(t.getName());
                if (i.hasNext()) System.out.print(", ");
            }
            System.out.println("]");

            System.out.println("Tallyer status:");
            DatabaseTally tally = new LocalDatabaseTally(db);
            tally.dumpTallyStatus();
        }

        if (db != null) {
            if (outputFormat == null) outputFormat = ARFF_FORMAT;
            try {
                db.save(out, outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
