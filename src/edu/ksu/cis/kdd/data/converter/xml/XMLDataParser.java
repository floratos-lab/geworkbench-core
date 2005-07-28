package edu.ksu.cis.kdd.data.converter.xml;

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

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.data.converter.Converter;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <dl>
 * <dt>XML Data Parser
 * <dd>
 * <p/>
 * <dt>Description:
 * <dd> This class is used to parse data set encoded as (abused) XML file.
 * This is a DOM implementation. I did this because I don't want to convert
 * the file by hand.
 *
 * @author Roby Joehanes
 * @version 0.1.0
 */
public class XMLDataParser implements Converter {

    protected Database db;
    protected Table tuples = null;
    protected boolean isPRM = false;
    protected LinkedList rKeys, pKeys; // referenceKeys and primaryKeys

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#initialize()
     */
    public void initialize() {
    }

    public void save(OutputStream stream, Database db) {
        Writer w = new OutputStreamWriter(stream);
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        try {
            // Dump out the DTD
            w.write("<?xml version=\"1.0\"?>" + ln + ln + //$NON-NLS-1$
                    "<!-- DTD for the XMLBIF 0.3 format -->" + ln + //$NON-NLS-1$
                    "<!DOCTYPE BIF [" + ln + //$NON-NLS-1$
                    "    <!ELEMENT BIF ( NETWORK )*, SAMPLES>" + ln + //$NON-NLS-1$
                    "        <!ATTLIST BIF VERSION CDATA #REQUIRED PRM CDATA #IMPLIED>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT NETWORK ( NAME, ( PROPERTY | PRM_CLASS | VARIABLE | DEFINITION )* )>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_CLASS (PRM_CLASSNAME, PRM_ATTRIBUTESET)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_CLASSNAME (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_ATTRIBUTESET (PRM_PKEY+, PRM_RKEY*, PRM_ATTRIBUTE*)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_PKEY (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_RKEY (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PRM_ATTRIBUTE (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT NAME (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT VARIABLE ( NAME, ( OUTCOME | PROPERTY )* ) >" + ln + //$NON-NLS-1$
                    "        <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">" + ln + //$NON-NLS-1$
                    "    <!ELEMENT OUTCOME (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >" + ln + //$NON-NLS-1$
                    "    <!ELEMENT FOR (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT GIVEN (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT TABLE (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT PROPERTY (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT SAMPLES ( NUMBER, (SET)* )>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT NUMBER (#PCDATA)>" + ln + //$NON-NLS-1$
                    "    <!ELEMENT SET (#PCDATA)>" + ln + //$NON-NLS-1$
                    "]>" + ln + ln + //$NON-NLS-1$

                    "<BIF VERSION=\"0.3\"");

            List tables = db.getTables();
            isPRM = tables.size() > 1;

            if (isPRM) {
                w.write(" PRM=\"0.1\"");
            }

            w.write(">" + ln); //$NON-NLS-1$

            for (Iterator idb = tables.iterator(); idb.hasNext();) {
                tuples = (Table) idb.next();
                w.write("  <NETWORK>" + ln + //$NON-NLS-1$
                        "    <NAME>" + StringUtil.mangleXMLString(tuples.getName()) + "</NAME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                w.write("      <!-- Variables -->" + ln); //$NON-NLS-1$

                if (isPRM) {
                    w.write("      <PRM_CLASS>" + ln); // $NON-NLS-1$
                    w.write("           <PRM_CLASSNAME>" + StringUtil.mangleXMLString(tuples.getName()) + // $NON-NLS-1$
                            "</PRM_CLASSNAME>" + ln); // $NON-NLS-1$
                    List pKeys = tuples.getPrimaryKeys();
                    if (pKeys != null) {
                        for (Iterator i = pKeys.iterator(); i.hasNext();) {
                            Attribute attr = (Attribute) i.next();
                            w.write("           <PRM_PKEY>" + attr.getName() + "</PRM_PKEY>" + ln); // $NON-NLS-1$ // $NON-NLS-2$
                        }
                    }
                    List rKeys = tuples.getReferenceKeys();
                    if (rKeys != null) {
                        for (Iterator i = rKeys.iterator(); i.hasNext();) {
                            Attribute attr = (Attribute) i.next();
                            w.write("           <PRM_RKEY>" + attr.getName() + "</PRM_RKEY>" + ln); // $NON-NLS-1$ // $NON-NLS-2$
                        }
                    }
                    w.write("      </PRM_CLASS>" + ln); // $NON-NLS-1$
                }

                // Dump out the variables
                List nodes = tuples.getAttributes();
                for (Iterator i = nodes.iterator(); i.hasNext();) {
                    Attribute attr = (Attribute) i.next();
                    String attrName = attr.getName();
                    w.write("      <VARIABLE TYPE=\"nature\">" + ln); //$NON-NLS-1$
                    w.write("         <NAME>" + StringUtil.mangleXMLString(attrName) + "</NAME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$

                    if (attr.getType() == Attribute.DISCRETE) {
                        for (Iterator j = attr.getValues().iterator(); j.hasNext();) {
                            w.write("        <OUTCOME>" + StringUtil.mangleXMLString(j.next().toString()) + "</OUTCOME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                    w.write("      </VARIABLE>" + ln); //$NON-NLS-1$
                }

                w.write("  </NETWORK>" + ln); //$NON-NLS-1$

                w.write("  <SAMPLES>" + ln); //$NON-NLS-1$
                List tupleList = tuples.getTuples();
                w.write("     <NUMBER>" + tupleList.size() + "</NUMBER>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                for (Iterator i = tupleList.iterator(); i.hasNext();) {
                    Tuple t = (Tuple) i.next();
                    w.write("     <SET>"); //$NON-NLS-1$
                    for (Iterator j = t.getValues().iterator(); j.hasNext();) {
                        w.write(StringUtil.mangleXMLString(j.next().toString()));
                        if (j.hasNext()) w.write(" "); // $NON-NLS-1$
                    }
                    w.write("</SET>" + ln); //$NON-NLS-1$
                }

                w.write("  </SAMPLES>" + ln); //$NON-NLS-1$
            }

            w.write("</BIF>" + ln); //$NON-NLS-1$
            w.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#load(java.io.InputStream)
     */
    public Database load(InputStream stream) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);  // Turn off validation because this is a misuse of XML file
        factory.setNamespaceAware(false);

        //Parse the document
        try {
            parser = factory.newDocumentBuilder();
            doc = parser.parse(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        db = new Database();
        visitDocument(doc);
        System.gc();
        return db;
    }

    public void visitDocument(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("BIF")) { //$NON-NLS-1$
                        NamedNodeMap attrs = node.getAttributes();
                        if (attrs != null) {
                            int amax = attrs.getLength();
                            for (int j = 0; j < amax; j++) {
                                Node attr = attrs.item(j);
                                String aname = attr.getNodeName();
                                if (aname.equals("PRM")) { //$NON-NLS-1$
                                    isPRM = true;
                                    break;
                                }
                            }
                        }
                        visitDocument(node);
                    } else if (name.equals("NETWORK")) { //$NON-NLS-1$
                        tuples = new Table();
                        rKeys = new LinkedList();
                        pKeys = new LinkedList();
                        visitModel(node);
                        db.addTable(tuples);
                    } else if (name.equals("SAMPLES")) {
                        visitSamples(node);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    public void visitModel(Node parent) {
        tuples = new Table();
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("NAME")) { //$NON-NLS-1$
                        tuples.setName(getElementValue(node));
                    } else if (isPRM && name.equals("PRM_CLASS")) { //$NON-NLS-1$
                        visitPRMClass(node);
                    } else if (name.equals("VARIABLE")) { //$NON-NLS-1$
                        Attribute attr = visitVariable(node);
                        tuples.addAttribute(attr);
                    }
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    public void visitPRMClass(Node parent) {
        assert (isPRM);
        NodeList l = parent.getChildNodes();

        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        // Split into two loops so that it can handle forward reference
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("PRM_CLASSNAME")) { //$NON-NLS-1$
                        String className = getElementValue(node);
                        tuples.setName(className.trim());
                    } else if (name.equals("PRM_ATTRIBUTESET")) { //$NON-NLS-1$
                        visitPRMAttributeSet(node);
                    }
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    protected void visitPRMAttributeSet(Node parent) {
        assert (isPRM);
        NodeList l = parent.getChildNodes();

        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        // Split into two loops so that it can handle forward reference
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("PRM_PKEY")) { //$NON-NLS-1$
                        String pKeyName = getElementValue(node);
                        pKeys.add(pKeyName);
                    } else if (name.equals("PRM_RKEY")) { //$NON-NLS-1$
                        String rKeyName = getElementValue(node);
                        rKeys.add(rKeyName);
                    } else if (name.equals("PRM_ATTRIBUTE")) { //$NON-NLS-1$
                        getElementValue(node); // Ignore since the rest of the attributes are of PRM ATTRIBUTE
                    }
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    protected Attribute visitVariable(Node parent) {
        NodeList l = parent.getChildNodes();

        Attribute attribute = new Attribute("");
        int max;
        String propType = "nature"; //$NON-NLS-1$
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("TYPE")) { //$NON-NLS-1$
                    propType = value;
                    if (value.equals("decision")) { //$NON-NLS-1$
                    } else if (value.equals("utility")) { //$NON-NLS-1$
                    } // otherwise it's just "nature"
                } else if (Settings.isDebug())
                    System.out.println("Unhandled variable property attribute " + name);
            }
        }

        if (!propType.equals("nature") && !propType.equals("decision") && !propType.equals("utility")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new RuntimeException("Unknown node type " + propType);
        }

        if (l == null) return null;
        LinkedList values = new LinkedList();

        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("NAME")) { //$NON-NLS-1$
                        String desc = getElementValue(node);
                        attribute.setName(desc);
                    } else if (name.equals("OUTCOME") || name.equals("VALUE")) { //$NON-NLS-1$ //$NON-NLS-2$
                        String value = getElementValue(node);
                        if (value != null) values.add(value);
                    } else if (name.equals("PROPERTY")) { //$NON-NLS-1$
                        // Ignore properties
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled variable element " + name);
                    break;
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }

        if (propType.equals("nature") || propType.equals("utility")) { //$NON-NLS-1$ //$NON-NLS-2$
            attribute.setValues(values);
        }

        if (isPRM) {
            if (pKeys.contains(attribute.getName())) {
                attribute.setPrimaryKey();
            } else if (rKeys.contains(attribute.getName())) {
                attribute.setKey(Attribute.REFERENCE);
            }
        }

        return attribute;
    }


    public void visitSamples(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("NUMBER")) { //$NON-NLS-1$
                        // Ignore this parameter
                    } else if (name.equals("SET")) { //$NON-NLS-1$
                        Tuple t = visitSampleSet(node);
                        tuples.addTuple(t);
                    }
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    protected Tuple visitSampleSet(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) return null;

        StringBuffer buf = new StringBuffer();
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    buf.append(node.getNodeValue());
                    break;
                case Node.ELEMENT_NODE:
                case Node.COMMENT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }

        LinkedList ll = new LinkedList();
        StringTokenizer tok = new StringTokenizer(buf.toString());

        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();
            ll.add(token);
        }

        return new Tuple(ll);
    }

    protected String getElementValue(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) return null;

        StringBuffer buf = new StringBuffer();
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    buf.append(node.getNodeValue());
                    break;
                case Node.ELEMENT_NODE:
                case Node.COMMENT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        return buf.toString().trim();
    }

}
