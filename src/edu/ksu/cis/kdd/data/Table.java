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

import edu.ksu.cis.bnj.bbn.converter.ConverterData;
import edu.ksu.cis.kdd.data.converter.Converter;
import edu.ksu.cis.kdd.data.converter.arff.ArffParser;
import edu.ksu.cis.kdd.util.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * @author Roby Joehanes
 */
public class Table implements Cloneable, Data {
    protected LinkedList tuples = new LinkedList();
    protected LinkedList attributes = new LinkedList();
    protected int classIndex = -1;
    protected TableProperty property;
    protected boolean fixedLength = true;
    protected String name = ""; //$NON-NLS-1$
    protected Database owner = null;

    public static final String CSF_FORMAT = "csf";
    public static final String ARFF_FORMAT = "arff";
    public static final String XML_FORMAT = "xml";
    public static final String LIBB_FORMAT = "libb";
    public static final String DAT_FORMAT = "dat";

    private static final String interfaceName = "edu.ksu.cis.kdd.data.converter.Converter"; //$NON-NLS-1$
    //private static TableSet converterTable = Settings.getDataConverterTable();
    protected TableTally tally = null;

    public Table() {

    }

    public Table(boolean isFixed) {
        fixedLength = isFixed;
    }

    public void addTuple(Tuple tuple) {
        assert tuple != null;
        tuple.setOwner(this);
        tuples.add(tuple);
        //        classValues.add(tuple.getClassValue());
    }

    public void addAttribute(Attribute attr) {
        attributes.add(attr);
        attr.setOwner(this);
    }

    public void removeTuple(Tuple tuple) {
        tuples.remove(tuple);
    }

    public void removeAttribute(Attribute attr) {
        if (attributes.remove(attr)) attr.setOwner(null);
    }

    // added by prashanth for convenience, fixed by robbyjo
    public void removeAttributes(List attributeList) {
        // attributes.removeAll(attributeList); // can't do this anymore
        for (Iterator i = attributeList.iterator(); i.hasNext();) {
            removeAttribute((Attribute) i.next());
        }
    }

    /* added by prashanth for convienience*/
    public void addAttributes(List attributeList) {
        // attributes.addAll(attributeList); // can't do this anymore
        for (Iterator i = attributeList.iterator(); i.hasNext();) {
            addAttribute((Attribute) i.next());
        }
    }

    public Tally getTallyer() {
        if (tally == null) {
            tally = new TableTally(this);
        }
        return tally;
    }

    public Tuple getTuple(int index) {
        if (isRemote()) {
            try {
                Statement stmt = owner.remoteStatement;
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + name); // $NON-NLS-1$
                rs.absolute(index);
                int numColumns = attributes.size();
                LinkedList values = new LinkedList();
                for (int i = 1; i <= numColumns; i++) {
                    Attribute attr = (Attribute) attributes.get(i - 1);
                    switch (attr.getType()) {
                        case Attribute.INTEGER:
                            values.add(new Integer(rs.getInt(i)));
                            break;
                        case Attribute.REAL:
                            values.add(new Double(rs.getDouble(i)));
                            break;
                        default:
                            values.add(rs.getString(i));
                    }
                }
                return new Tuple(values);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (Tuple) tuples.get(index);
    }

    public List getTuples() {
        if (isRemote()) {
            LinkedList ll = new LinkedList();
            try {
                Statement stmt = owner.remoteStatement;
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + name); // $NON-NLS-1$
                int numColumns = attributes.size();
                while (rs.next()) {
                    LinkedList values = new LinkedList();
                    for (int i = 1; i <= numColumns; i++) {
                        Attribute attr = (Attribute) attributes.get(i - 1);
                        switch (attr.getType()) {
                            case Attribute.INTEGER:
                                values.add(new Integer(rs.getInt(i)));
                                break;
                            case Attribute.REAL:
                                values.add(new Double(rs.getDouble(i)));
                                break;
                            default:
                                values.add(rs.getString(i));
                        }
                    }
                    ll.add(new Tuple(values));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ll;
        }
        return tuples;
    }


    /**
     * get the weights of all instances
     *
     * @return
     */
    public double[] getWeights() {
        double weights[] = new double[this.tuples.size()];
        for (int i = 0; i < weights.length; i++)
            weights[i] = ((Tuple) tuples.get(i)).getWeight();
        return weights;
    }

    /**
     * set the weights of all instances.
     *
     * @param weights[]: a vector of double numbers summing up to 1
     * @return
     */
    public void setWeights(double weights[]) {
        if (weights.length == tuples.size()) {
            for (int i = 0; i < weights.length; i++)
                ((Tuple) tuples.get(i)).setWeight(weights[i]);
        }
        tally = null;
    }

    public List getPrimaryKeys() {
        LinkedList keys = new LinkedList();
        for (Iterator i = attributes.iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            if (attr.isPrimaryKey()) keys.add(attr);
        }
        return keys;
    }

    public List getReferenceKeys() {
        LinkedList keys = new LinkedList();
        for (Iterator i = attributes.iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            if (attr.isReferenceKey()) keys.add(attr);
        }
        return keys;
    }

    public Data subsample(int n) {
        return subsample(n, System.currentTimeMillis());
    }

    public Data subsample(int n, long seed) {
        int size = tuples.size();

        Table newInstance = new Table(fixedLength);
        newInstance.classIndex = classIndex;
        newInstance.setAttributes(attributes);
        if (property != null)
            newInstance.setProperty((TableProperty) property.clone());

        Random random = new Random(seed);

        for (int i = 0; i < n; i++) {
            int idx = random.nextInt(size);
            newInstance.tuples.add(tuples.get(idx));
        }

        return newInstance;
    }

    /**
     * Get the first n tuples
     */
    public Table getFirst(int n) {
        assert n >= 0 : "n must be at least 0";
        int size = tuples.size();
        if (n > size) n = size;

        return get(0, n);
    }

    /**
     * Get the last n tuples
     */
    public Table getLast(int n) {
        assert n >= 0 : "n must be at least 0";
        int size = tuples.size();
        if (n > size) n = size;
        int startIdx = size - n;

        return get(startIdx, size);
    }

    public Table get(int start, int end) {
        assert start >= 0 && end >= 0: "start or end must be at least 0";
        assert end >= start: "End index must be at least the same as start index";

        int size = tuples.size();
        if (end > size) end = size;
        if (start > size) start = size;

        Table newInstance = new Table(fixedLength);
        newInstance.classIndex = classIndex;
        newInstance.setAttributes(attributes);
        if (property != null)
            newInstance.setProperty((TableProperty) property.clone());

        newInstance.tuples.addAll(tuples.subList(start, end));

        return newInstance;
    }

    public Table getAllExcept(int start, int end) {
        assert start >= 0 && end >= 0: "start or end must be at least 0";
        assert end >= start: "End index must be at least the same as start index";

        int size = tuples.size();
        if (end > size) end = size;
        if (start > size) start = size;

        Table newInstance = new Table(fixedLength);
        newInstance.classIndex = classIndex;
        newInstance.setAttributes(attributes);
        if (property != null)
            newInstance.setProperty((TableProperty) property.clone());

        newInstance.tuples.addAll(tuples.subList(0, start));
        if (end < size) newInstance.tuples.addAll(tuples.subList(end, size));

        return newInstance;
    }


    public void remove(int start, int end) {
        int size = tuples.size();
        if (end > size) end = size;
        if (start > size) start = size;
        if (end < 0) end = 0;
        if (start < 0) start = 0;

        for (int i = start; i < end; i++) {
            tuples.remove(i);
        }
    }

    public void randomizeSequence(long seed) {
        Random random = new Random(seed);
        LinkedList newTuples = new LinkedList();
        Object[] tupleArray = tuples.toArray();
        int size = tupleArray.length;

        for (int i = 0; i < size; i++) {
            int n = random.nextInt(size);
            if (tupleArray[n] == null) continue;
            newTuples.add(tupleArray[n]);
            tupleArray[n] = null;
        }

        for (int i = 0; i < size; i++) {
            if (tupleArray[i] == null) continue;
            newTuples.add(tupleArray[i]);
        }

        tuples = newTuples;
    }

    public void randomizeSequence() {
        randomizeSequence(new Date().getTime());
    }

    public void setTuples(List newTuples) {
        tuples.clear();
        tuples.addAll(newTuples);
        tally = null;
    }

    public void setOwner(Database db) {
        owner = db;
    }

    public Database getOwner() {
        return owner;
    }

    public Attribute getAttribute(int index) {
        return (Attribute) attributes.get(index);
    }

    public Attribute getAttribute(String name) {
        Attribute attr = new Attribute(name);
        int idx = attributes.indexOf(attr);
        return idx > -1 ? (Attribute) attributes.get(idx) : null;
    }

    public int getAttributeIndex(Attribute attr) {
        return attributes.indexOf(attr);
    }

    public int getAttributeIndex(String attrName) {
        return getAttributeIndex(new Attribute(attrName));
    }

    public List getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public List getRelevantAttributes() {
        return getAttributes();
    }

    public List getAttributeNames() {
        LinkedList names = new LinkedList();
        for (Iterator i = getAttributes().iterator(); i.hasNext();) {
            Attribute a = (Attribute) i.next();
            names.add(a.getName().trim());
        }
        return names;
    }

    /* convenient method added by prashanth ******/
    public List getTableAttributes() {
        List attributes = new LinkedList();
        for (Iterator i = getAttributes().iterator(); i.hasNext();) {
            Attribute a = (Attribute) i.next();
            if (a.isPrimaryKey() || a.isReferenceKey() || a.isString())
                continue;
            attributes.add(a);
        }
        return attributes;
    }

    public void setAttributes(List newAttr) {
        attributes.clear();
        attributes.addAll(newAttr);
        tally = null;
    }

    public Attribute getClassAttribute() {
        if (classIndex == -1) return null;
        return getAttribute(classIndex);
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassAttribute(Attribute attr) {
        if (!attributes.contains(attr))
            throw new RuntimeException(attr + " is not found!");
        setClassAttribute(getAttributeIndex(attr));
        tally = null;
    }

    public void setClassAttribute(int index) {
        classIndex = index;
        tally = null;
    }

    /*    public Set getClassValues()
        {
            return classValues;
        }*/

    public int size() {
        if (isRemote()) {
            try {
                Statement stmt = owner.remoteStatement;
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + name); // $NON-NLS-1$
                rs.next();
                return rs.getInt(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tuples.size();
    }

    public String toString() {
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        StringBuffer buf = new StringBuffer();
        if (isRemote()) buf.append("Remote ");
        buf.append("Table " + getName() + ln);
        buf.append("Attributes = " + ln + attributes + ln);
        buf.append("Tuple:" + ln);
        if (isRemote()) {
            try {
                Statement stmt = owner.remoteStatement;
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + name); // $NON-NLS-1$
                ResultSetMetaData rsmeta = rs.getMetaData();
                int numColumn = rsmeta.getColumnCount();
                while (rs.next()) {
                    buf.append(rs.getString(1));
                    for (int i = 2; i <= numColumn; i++) {
                        buf.append(", " + rs.getString(i)); // $NON-NLS-1$
                    }
                    buf.append(ln);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (Iterator i = tuples.iterator(); i.hasNext();) {
                Tuple tuple = (Tuple) i.next();
                buf.append(tuple + ln);
            }
        }

        return buf.toString();
    }

    /**
     * Returns the property.
     *
     * @return TuplesProperty
     */
    public TableProperty getProperty() {
        return property;
    }

    /**
     * Sets the property.
     *
     * @param property The property to set
     */
    public void setProperty(TableProperty property) {
        this.property = property;
    }

    /**
     * Returns the fixedLength.
     *
     * @return boolean
     */
    public boolean isFixedLength() {
        return fixedLength;
    }

    /**
     * Sets the fixedLength.
     *
     * @param fixedLength The fixedLength to set
     */
    public void setFixedLength(boolean fixedLength) {
        this.fixedLength = fixedLength;
    }

    /**
     * Table are cloned, but list of attributes are not!
     */
    public Object clone() {
        return get(0, tuples.size());
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

    public static Table load(String filename, String format) {
        return Database.load(filename, format).pickOneTable();
    }

    public static Table load(String filename) {
        return load(filename, null);
    }

    public void save(String filename) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            Database db = new Database();
            db.addTable(this);
            new ArffParser().save(out, db);
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
                Database db = new Database();
                db.addTable(this);
                c.save(out, db);
                out.close();
            } catch (Exception e) {
                if (Settings.isDebug())
                    System.out.println("Warning: Cannot load reflection object" + className);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the isRemote.
     *
     * @return boolean
     */
    public boolean isRemote() {
        return owner != null && owner.isRemote();
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
        Table t = null;
        try {
            t = Table.load(inputFile);
            freemem = freemem - r.freeMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!quiet) {
            System.out.println("Memory needed = " + freemem);
        }

        if (t != null) {
            if (outputFormat == null) outputFormat = ARFF_FORMAT;
            try {
                t.save(out, outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
