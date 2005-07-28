/*
 * Created on Mar 3, 2003
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
package edu.ksu.cis.kdd.data.converter.dat;

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.data.converter.Converter;

import java.io.*;
import java.util.*;

/**
 * .DAT saver and loader
 *
 * @author Roby Joehanes
 */
public class DATConverter implements Converter {

    protected int maxAttrValueSize = 100;

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#initialize()
     */
    public void initialize() {

    }

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#load(java.io.InputStream)
     */
    public Database load(InputStream stream) {
        int maxAttr = 0;
        Database db = new Database();
        Set[] attrValues = null;
        int rownum = 0, linenum = 0;
        try {
            String s = null;
            LineNumberReader r = new LineNumberReader(new InputStreamReader(stream));
            Table tuples = new Table();

            do {
                // Fetch the Max Attribute
                do {
                    s = r.readLine();
                    if (s == null) throw new RuntimeException("Unexpected end of document");
                    linenum++;
                    s = s.trim();
                    if (s.length() == 0) continue; // skip empty string
                    maxAttr = Integer.parseInt(s);
                    attrValues = new Set[maxAttr];
                    for (int i = 0; i < maxAttr; i++) {
                        attrValues[i] = new HashSet();
                    }
                    break;
                } while (true);

                // Fetch the attribute names
                do {
                    s = r.readLine();
                    if (s == null) throw new RuntimeException("Unexpected end of document");
                    linenum++;
                    s = s.trim();
                    if (s.length() == 0) continue; // skip empty string

                    int curAttr = 0;
                    StringTokenizer tok = new StringTokenizer(s, "|"); // $NON-NLS-1$
                    while (tok.hasMoreTokens()) {
                        String token = tok.nextToken().trim();
                        Attribute attr = new Attribute(token);
                        tuples.addAttribute(attr);
                        curAttr++;
                    }
                    if (curAttr != maxAttr) {
                        throw new RuntimeException("Error: Attribute name count doesn't match with the declaration");
                    }
                    break;
                } while (true);

                // Fetch the attribute value size
                do {
                    s = r.readLine();
                    if (s == null) throw new RuntimeException("Unexpected end of document");
                    linenum++;
                    s = s.trim();
                    if (s.length() == 0) continue; // skip empty string
                    break; // ignore it
                } while (true);


                // Fetch the data count
                int tupleCount = 0;
                do {
                    s = r.readLine();
                    if (s == null) throw new RuntimeException("Unexpected end of document");
                    linenum++;
                    s = s.trim();
                    if (s.length() == 0) continue; // skip empty string
                    tupleCount = Integer.parseInt(s);
                    break; // ignore it
                } while (true);

                for (int ii = 0; ii < tupleCount; ii++) {
                    s = r.readLine();
                    if (s == null) break;
                    linenum++;
                    s = s.trim();
                    if (s.length() == 0) continue; // skip empty string
                    int curAttr = 0;
                    Tuple t = new Tuple();
                    StringTokenizer tok = new StringTokenizer(s, " "); // $NON-NLS-1$
                    while (tok.hasMoreTokens()) {
                        String token = tok.nextToken().trim();
                        t.addValue(token);
                        if (curAttr >= maxAttr)
                            throw new RuntimeException("Attribute count doesn't match on line number " + linenum);

                        if (attrValues[curAttr] != null) {
                            attrValues[curAttr].add(token);
                            if (attrValues[curAttr].size() > maxAttrValueSize) {
                                try {
                                    Double.parseDouble(token);
                                    attrValues[curAttr] = null;
                                } catch (Exception ee) {
                                    throw new RuntimeException("Too many discrete elements in an attribute");
                                }
                            }
                        }
                        curAttr++;
                    }

                    tuples.addTuple(t);
                    rownum++;
                }
                db.addTable(tuples);
            } while (s != null);
            r.close();

            // Recapitulate the attributes
            for (int i = 0; i < maxAttr; i++) {
                Attribute attr = tuples.getAttribute(i);
                if (attrValues[i] == null) {
                    attr.setType(Attribute.REAL);
                } else {
                    attr.setValues(attrValues[i]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return db;
    }

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#save(java.io.OutputStream, edu.ksu.cis.kdd.data.datastructure.Table)
     */
    public void save(OutputStream stream, Database db) {
        String ln = System.getProperty("line.separator"); // $NON-NLS-1$
        Writer w = new OutputStreamWriter(stream);
        try {
            for (Iterator idb = db.getTables().iterator(); idb.hasNext();) {
                Table t = (Table) idb.next();
                List attrs = t.getAttributes();
                w.write(attrs.size() + ln);
                for (Iterator i = attrs.iterator(); i.hasNext();) {
                    Attribute attr = (Attribute) i.next();
                    w.write(attr.getName());
                    if (i.hasNext()) w.write("|"); // $NON-NLS-1$
                }
                w.write(ln + ln);
                for (Iterator i = attrs.iterator(); i.hasNext();) {
                    Attribute attr = (Attribute) i.next();
                    if (attr.getType() == Attribute.DISCRETE) {
                        w.write(String.valueOf(attr.getArity()));
                    } else {
                        w.write("2"); // anything, because we'll ignore it anyway
                    }

                    if (i.hasNext()) w.write("|"); // $NON-NLS-1$
                }

                List tuples = t.getTuples();
                w.write(tuples.size() + ln);

                for (Iterator i = tuples.iterator(); i.hasNext();) {
                    Tuple tuple = (Tuple) i.next();
                    for (Iterator j = tuple.getValues().iterator(); j.hasNext();) {
                        Object val = j.next();
                        w.write(val.toString());
                        if (j.hasNext()) w.write(" "); // $NON-NLS-1$
                    }
                    w.write(ln);
                }
            }
            w.flush();
            w.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @return int
     */
    public int getMaxAttributeValueSize() {
        return maxAttrValueSize;
    }

    /**
     * Sets the maxAttributeValueSize.
     *
     * @param maxAttributeValueSize The maxAttributeValueSize to set
     */
    public void setMaxAttributeValueSize(int maxAttributeValueSize) {
        maxAttrValueSize = maxAttributeValueSize;
    }

}
