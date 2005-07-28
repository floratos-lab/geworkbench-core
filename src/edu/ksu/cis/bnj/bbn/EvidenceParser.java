package edu.ksu.cis.bnj.bbn;

/*
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
 * 
 */

import edu.ksu.cis.kdd.util.Settings;
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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Evidence Parser
 *
 * @author Roby Joehanes
 */
public class EvidenceParser {

    protected static EvidenceParser parser = new EvidenceParser();

    protected Hashtable tbl;
    protected boolean visitedOnce;
    protected String ln = System.getProperty("line.separator"); //$NON-NLS-1$
    protected String header = "<?xml version=\"1.0\"?>" + ln + ln + //$NON-NLS-1$
            "<!-- Evidence file -->" + ln + //$NON-NLS-1$
            "<!-- By: Roby Joehanes -->" + ln + ln + //$NON-NLS-1$
            "<!-- DTD for the evidence file format -->" + ln + //$NON-NLS-1$
            "<!DOCTYPE EVIDENCES [" + ln + //$NON-NLS-1$
            "    <!ELEMENT EVIDENCES ( EVIDENCE )+>" + ln + //$NON-NLS-1$
            "    <!ELEMENT EVIDENCE EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST EVIDENCE NAME CDATA #REQUIRED" + ln + //$NON-NLS-1$
            "                          VALUE CDATA #REQUIRED>" + ln + //$NON-NLS-1$
            "]>" + ln + ln; //$NON-NLS-1$

    public static Hashtable load(InputStream stream) {
        return parser.loadImpl(stream);
    }

    public static void save(OutputStream stream, Hashtable tbl) {
        parser.saveImpl(stream, tbl);
    }

    public Hashtable loadImpl(InputStream stream) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        tbl = new Hashtable();
        visitedOnce = false;

        //Parse the document
        try {
            parser = factory.newDocumentBuilder();
            doc = parser.parse(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        visitDocument(doc);
        System.gc();

        if (!visitedOnce) {
            throw new RuntimeException("Invalid evidence file!");
        }

        return tbl;
    }

    /**
     * Saving evidence to an output stream. The evidence is a hashtable of
     * node name (String) to its evidence value (String)
     *
     * @param stream
     * @param tbl
     */
    public void saveImpl(OutputStream stream, Hashtable tbl) {
        if (tbl == null || tbl.size() == 0) return; // nothing to save
        Writer w = new OutputStreamWriter(stream);
        try {
            w.write(header);
            w.write("<EVIDENCES>" + ln); //$NON-NLS-1$
            for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                Object value = tbl.get(key);
                w.write("    <EVIDENCE NAME=\"" + mangleXMLString(key.toString()) + "\" VALUE=\"" + //$NON-NLS-1$ //$NON-NLS-2$
                        mangleXMLString(value.toString()) + "\" />" + ln); //$NON-NLS-1$
            }
            w.write("</EVIDENCES>" + ln); //$NON-NLS-1$
            w.flush();
            System.gc();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void visitDocument(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("EVIDENCES")) { //$NON-NLS-1$
                        visitedOnce = true;
                        visitDocument(node);
                    } else if (name.equals("EVIDENCE")) { //$NON-NLS-1$
                        visitEntry(node);
                        // if multi-model, add graph to a list
                    } else if (Settings.DEBUG)
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.DEBUG)
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    protected void visitEntry(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at the <ENTRY> tag");
        int max = attrs.getLength();
        String nodeName = null, nodeValue = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                nodeName = value;
            } else if (name.equals("VALUE")) { //$NON-NLS-1$
                nodeValue = value;
            } else
                throw new RuntimeException("Superfluous attributes at <ENTRY> tag");
        }
        tbl.put(nodeName, nodeValue);
        //tbl.put(ext, new ConverterData(desc, ext, pkgname, clsname));
    }

    /**
     * Escape the string s to oblige to the XML rules (e.g.: "'" becomes &apos;,
     * "&" becomes &amp;, and so on).
     *
     * @param s The raw string
     * @return String The formatted string
     */
    protected static String mangleXMLString(String s) {
        StringBuffer buf = new StringBuffer();
        int max = s.length();
        for (int i = 0; i < max; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\'':
                    buf.append("&apos;");
                    break; //$NON-NLS-1$
                case '&':
                    buf.append("&amp;");
                    break; //$NON-NLS-1$
                case '<':
                    buf.append("&lt;");
                    break; //$NON-NLS-1$
                case '>':
                    buf.append("&gt;");
                    break; //$NON-NLS-1$
                case '\"':
                    buf.append("&quot;");
                    break; //$NON-NLS-1$
                default:
                    buf.append(c);
            }
        }
        return buf.toString();
    }
}
