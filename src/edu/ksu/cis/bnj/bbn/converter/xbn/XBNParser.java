package edu.ksu.cis.bnj.bbn.converter.xbn;

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

import edu.ksu.cis.bnj.bbn.*;
import edu.ksu.cis.bnj.bbn.converter.Converter;
import edu.ksu.cis.kdd.util.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/**
 * <dl>
 * <dt>Microsoft XBN Parser
 * <dd>
 * <p/>
 * <dt>Description:
 * <dd> This class is used to parse XML data given in the form of a file (see constructor)
 * and add the data into the BNJ nodes. It also adds probability to the child
 * nodes. This is a DOM implementation
 * <p/>
 * <DT>TO DO:
 * <dd><ul>
 * <li>Continuous chance / distribution</li>
 * <li>Shared distribution (esp. in OOBN)</li>
 * <li>This format need to be extended to handle utility/decision nodes</li>
 * </ul>
 * <p/>
 * </dl>
 *
 * @author Roby Joehanes
 * @version 0.1.0
 */
public class XBNParser implements Converter {

    protected BBNGraph graph = null;
    protected Hashtable nodeCache = new Hashtable();
    protected Hashtable valueCache = new Hashtable();
    protected static String ln = System.getProperty("line.separator"); //$NON-NLS-1$
    protected static String XBNDTD = "<?xml version=\"1.0\"?>" + ln + ln + //$NON-NLS-1$
            "<!DOCTYPE ANALYSISNOTEBOOK [" + ln + //$NON-NLS-1$
            "<!-- Taken from http://research.microsoft.com/dtas/bnformat/xbn_dtd.html -->" + ln + //$NON-NLS-1$
            "<!-- DTD for sets of belief network models -->" + ln + //$NON-NLS-1$
            "<!ELEMENT ANALYSISNOTEBOOK (BNMODEL)+>" + ln + //$NON-NLS-1$
            "   <!ATTLIST ANALYSISNOTEBOOK" + ln + //$NON-NLS-1$
            "      NAME ID #REQUIRED" + ln + //$NON-NLS-1$
            "      ROOT IDREF #IMPLIED" + ln + //$NON-NLS-1$
            "      FILENAME CDATA #IMPLIED>" + ln + //$NON-NLS-1$
            "<!-- a single belief network -->" + ln + //$NON-NLS-1$
            "<!ELEMENT BNMODEL (  STATICPROPERTIES" + ln + //$NON-NLS-1$
            "                   | DYNAMICPROPERTIES" + ln + //$NON-NLS-1$
            "                   | VARIABLES" + ln + //$NON-NLS-1$
            "                   | STRUCTURE" + ln + //$NON-NLS-1$
            "                   | DISTRIBUTIONS" + ln + //$NON-NLS-1$
            "                  )+>" + ln + //$NON-NLS-1$
            "   <!ATTLIST BNMODEL NAME ID #REQUIRED>" + ln + //$NON-NLS-1$
            "<!-- comment element declarations -->" + ln + //$NON-NLS-1$
            "<!ELEMENT COMMENT (#PCDATA)>" + ln + //$NON-NLS-1$
            "<!ELEMENT PROPVALUE (#PCDATA)>" + ln + //$NON-NLS-1$
            "<!ELEMENT STATENAME (#PCDATA)>" + ln + //$NON-NLS-1$
            "<!ELEMENT PROPERTY (PROPVALUE)+>" + ln + //$NON-NLS-1$
            "    <!ATTLIST PROPERTY NAME NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "<!ELEMENT PROPXML (#PCDATA)>" + ln + //$NON-NLS-1$
            "    <!ATTLIST PROPXML NAME NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "<!-- static header declaration section -->" + ln + //$NON-NLS-1$
            "<!ELEMENT STATICPROPERTIES (#PCDATA | FORMAT | VERSION | CREATOR )*>" + ln + //$NON-NLS-1$
            "    <!ELEMENT FORMAT EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST FORMAT VALUE CDATA \"MSR DTAS XML\">" + ln + //$NON-NLS-1$
            "    <!ELEMENT VERSION EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST VERSION VALUE CDATA #REQUIRED>" + ln + //$NON-NLS-1$
            "    <!ELEMENT CREATOR EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST CREATOR VALUE CDATA #IMPLIED>" + ln + //$NON-NLS-1$
            "<!-- dynamic properties declaration section -->" + ln + //$NON-NLS-1$
            "<!ELEMENT DYNAMICPROPERTIES (PROPERTYTYPE|PROPERTY|PROPXML)+>" + ln + //$NON-NLS-1$
            "    <!ELEMENT PROPERTYTYPE (COMMENT)?>" + ln + //$NON-NLS-1$
            "        <!ATTLIST PROPERTYTYPE" + ln + //$NON-NLS-1$
            "         NAME NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "         ENUMSET NMTOKENS #IMPLIED" + ln + //$NON-NLS-1$
            "         TYPE (real | string | realarray | stringarray | enumeration) \"string\">" + ln + //$NON-NLS-1$
            "<!-- random variables declaration section -->" + ln + //$NON-NLS-1$
            "<!ELEMENT VARIABLES (VAR)+>" + ln + //$NON-NLS-1$
            "    <!ELEMENT VAR ( STATENAME | PROPERTY | PROPXML | DESCRIPTION )+>" + ln + //$NON-NLS-1$
            "        <!ATTLIST VAR" + ln + //$NON-NLS-1$
            "         TYPE (discrete | continuous) \"discrete\"" + ln + //$NON-NLS-1$
            "         NAME NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "         XPOS CDATA #IMPLIED" + ln + //$NON-NLS-1$
            "         YPOS CDATA #IMPLIED>" + ln + //$NON-NLS-1$
            "      <!ELEMENT DESCRIPTION (#PCDATA)>" + ln + //$NON-NLS-1$
            "<!-- topological dependency structure information -->" + ln + //$NON-NLS-1$
            "<!ELEMENT STRUCTURE (ARC|MEMBER)*>" + ln + //$NON-NLS-1$
            "   <!-- specify dependency arc -->" + ln + //$NON-NLS-1$
            "   <!ELEMENT ARC EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST ARC" + ln + //$NON-NLS-1$
            "         PARENT NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "         CHILD NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "   <!-- specify set inclusion for parentless variables -->" + ln + //$NON-NLS-1$
            "   <!ELEMENT MEMBER EMPTY>" + ln + //$NON-NLS-1$
            "        <!ATTLIST MEMBER NAME NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "<!-- distributions -->" + ln + //$NON-NLS-1$
            "<!ELEMENT DISTRIBUTIONS (DIST)*>" + ln + //$NON-NLS-1$
            "    <!ELEMENT DIST ( (CONDSET)?, ( ( (PRIVATE|SHARED), DPIS) | REFERENCE ) )*>" + ln + //$NON-NLS-1$
            "        <!ATTLIST DIST" + ln + //$NON-NLS-1$
            "         TYPE (discrete|ci) \"discrete\"" + ln + //$NON-NLS-1$
            "         FUNCTYPE (max|plus) #IMPLIED>" + ln + //$NON-NLS-1$
            "        <!-- conditioning set declaration -->" + ln + //$NON-NLS-1$
            "        <!ELEMENT CONDSET (CONDELEM)*>" + ln + //$NON-NLS-1$
            "            <!ELEMENT CONDELEM EMPTY>" + ln + //$NON-NLS-1$
            "                <!ATTLIST CONDELEM" + ln + //$NON-NLS-1$
            "               NAME NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "               STATES CDATA #IMPLIED>" + ln + //$NON-NLS-1$
            "        <!-- private/shared declarations -->" + ln + //$NON-NLS-1$
            "      <!ELEMENT PRIVATE EMPTY>" + ln + //$NON-NLS-1$
            "         <!ATTLIST PRIVATE NAME NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "      <!ELEMENT SHARED EMPTY>" + ln + //$NON-NLS-1$
            "         <!ATTLIST SHARED" + ln + //$NON-NLS-1$
            "            NAME NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "            STATES CDATA #IMPLIED>" + ln + //$NON-NLS-1$
            "        <!-- discrete parent instantiation probability vectors -->" + ln + //$NON-NLS-1$
            "      <!ELEMENT DPIS (DPI)*>" + ln + //$NON-NLS-1$
            "         <!ELEMENT DPI  (#PCDATA)>" + ln + //$NON-NLS-1$
            "            <!ATTLIST DPI INDEXES NMTOKENS #IMPLIED>" + ln + //$NON-NLS-1$
            "        <!-- distribution reference (binding) declaration -->" + ln + //$NON-NLS-1$
            "      <!ELEMENT REFERENCE EMPTY>" + ln + //$NON-NLS-1$
            "         <!ATTLIST REFERENCE" + ln + //$NON-NLS-1$
            "            VAR NMTOKEN #REQUIRED" + ln + //$NON-NLS-1$
            "            SHAREDDIST NMTOKEN #REQUIRED>" + ln + //$NON-NLS-1$
            "]>"; //$NON-NLS-1$


    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#initialize()
     */
    public void initialize() {
        nodeCache = new Hashtable();
        valueCache = new Hashtable();
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#load(java.io.Reader)
     */
    public BBNGraph load(InputStream stream) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        //Parse the document
        try {
            parser = factory.newDocumentBuilder();
            doc = parser.parse(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        visitDocument(doc);
        System.gc();
        return graph;
    }

    /**
     * This routine is supposed to save the BBN Graph into MS XBN format.
     * Currently, I decided to scrap all of the properties. Maybe I'll add them
     * later. Also, I don't rebuild through XML, since we're only interested in
     * storing the contents, not actually converting it to XML.
     *
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#save(java.io.Writer, edu.ksu.cis.bnj.bbn.BBNGraph)
     */
    public void save(OutputStream stream, BBNGraph graph) {
        Writer w = new OutputStreamWriter(stream);
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        nodeCache = new Hashtable();
        valueCache = new Hashtable();
        try {
            w.write(XBNDTD);

            String graphName = mangleXMLString(graph.getName());
            w.write("<ANALYSISNOTEBOOK NAME=\"Notebook." + graphName + "\" ROOT=\"" + graphName + "\">" + ln); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            w.write("<BNMODEL NAME=\"" + graphName + "\">" + ln); //$NON-NLS-1$ //$NON-NLS-2$

            // Sorry no properties are written back yet

            Set nodes = graph.getNodes();
            w.write("<VARIABLES>" + ln); //$NON-NLS-1$
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                BBNValue value = node.getValues();
                String nodeName = node.getLabel();
                w.write("  <VAR NAME=\"" + mangleXMLString(nodeName) + "\" TYPE="); //$NON-NLS-1$ //$NON-NLS-2$
                nodeCache.put(nodeName, node);

                if (value instanceof BBNContinuousValue)
                    w.write("\"continuous\" "); //$NON-NLS-1$
                else
                    w.write("\"discrete\" "); //$NON-NLS-1$
                try {
                    List pos = (List) node.getProperty().get("position"); //$NON-NLS-1$
                    Double xpos = (Double) pos.get(0);
                    Double ypos = (Double) pos.get(1);
                    w.write("XPOS=\"" + Math.round(xpos.doubleValue() + 100 * 100) + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
                    w.write("YPOS=\"" + Math.round(ypos.doubleValue() + 100 * 100) + "\">" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (Exception ex) {
                    w.write("XPOS=\"0\" YPOS=\"0\">" + ln); //$NON-NLS-1$
                }
                String label = node.getLabel();
                if (label != null && !label.equals("")) { //$NON-NLS-1$
                    w.write("    <DESCRIPTION>" + mangleXMLString(label) + "</DESCRIPTION>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (value instanceof BBNContinuousValue) {
                    throw new RuntimeException("Don't know how to handle continuous nodes yet");
                } else {
                    BBNDiscreteValue dvalue = (BBNDiscreteValue) value;
                    LinkedList values = new LinkedList();
                    values.addAll(dvalue);
                    valueCache.put(nodeName, values);
                    for (Iterator j = values.iterator(); j.hasNext();) {
                        w.write("    <STATENAME>" + mangleXMLString(j.next().toString()) + "</STATENAME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                // Node properties are also skipped, sorry. Dunno how to save it.
                // If you want to save the node properties, here is the right place
                // to plug it

                w.write("  </VAR>" + ln); //$NON-NLS-1$
            }
            w.write("</VARIABLES>" + ln); //$NON-NLS-1$

            // Process the arcs
            w.write("<STRUCTURE>" + ln); //$NON-NLS-1$
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                List children = node.getChildren();
                if (children == null || children.size() == 0) continue;
                String header = "  <ARC PARENT=\"" + mangleXMLString(node.getLabel()) + "\" CHILD=\""; //$NON-NLS-1$ //$NON-NLS-2$
                for (Iterator j = children.iterator(); j.hasNext();) {
                    BBNNode child = (BBNNode) j.next();
                    w.write(header + mangleXMLString(child.getLabel()) + "\" />" + ln); //$NON-NLS-1$
                }
            }
            w.write("</STRUCTURE>" + ln); //$NON-NLS-1$

            // Process the CPT
            w.write("<DISTRIBUTION>" + ln); //$NON-NLS-1$
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                String nodeName = node.getLabel();
                BBNValue value = node.getValues();
                List parents = node.getParents();
                if (parents == null || parents.size() == 0) {
                    w.write("  <DIST TYPE=\"discrete\">" + ln); //$NON-NLS-1$
                } else {
                    w.write("  <DIST TYPE=\"ci\">" + ln); //$NON-NLS-1$
                    w.write("    <CONDSET>" + ln); //$NON-NLS-1$
                    for (Iterator j = parents.iterator(); j.hasNext();) {
                        BBNNode parent = (BBNNode) j.next();
                        w.write("      <CONDELEM NAME=\"" + mangleXMLString(parent.getLabel()) + "\" />" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    w.write("    </CONDSET>" + ln); //$NON-NLS-1$
                }
                w.write("    <PRIVATE NAME=\"" + mangleXMLString(nodeName) + "\"/>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                w.write("    <DPIS>" + ln); //$NON-NLS-1$
                Hashtable CPF = node.getCPF().getTable();
                if (parents != null && parents.size() > 0) {
                    Hashtable idxCache = new Hashtable();
                    for (Enumeration dpi = CPF.keys(); dpi.hasMoreElements();) {
                        Hashtable query = (Hashtable) dpi.nextElement();
                        String index = ""; //$NON-NLS-1$

                        for (Iterator j = parents.iterator(); j.hasNext();) {
                            BBNNode parent = (BBNNode) j.next();
                            String parentName = parent.getLabel();
                            Object parValue = query.get(parentName);
                            List parValues = (List) valueCache.get(parentName);
                            int valIndex = parValues.indexOf(parValue);
                            if (valIndex == -1)
                                throw new RuntimeException("Value " + parValue + " is not in " + parValues);
                            index = index + valIndex + " "; //$NON-NLS-1$
                        }
                        // We have the index
                        index = index.trim();
                        Object[] vals = (Object[]) idxCache.get(index);
                        if (vals == null) {
                            if (value instanceof BBNContinuousValue)
                                vals = new Object[1];
                            else
                                vals = new Object[((BBNDiscreteValue) value).size()];
                            idxCache.put(index, vals);
                        }
                        Object nodeValue = query.get(nodeName);
                        assert nodeValue != null;
                        int nodeValIndex = ((List) valueCache.get(nodeName)).indexOf(nodeValue);
                        assert nodeValIndex != -1;
                        vals[nodeValIndex] = CPF.get(query);
                    }

                    for (Enumeration dpi = idxCache.keys(); dpi.hasMoreElements();) {
                        String index = (String) dpi.nextElement();
                        String valueString = ""; //$NON-NLS-1$
                        Object[] vals = (Object[]) idxCache.get(index);
                        for (int j = 0; j < vals.length; j++) {
                            assert vals[j] != null;
                            valueString += vals[j] + " "; //$NON-NLS-1$
                        }
                        StringTokenizer toks = new StringTokenizer(valueString);
                        boolean isAllZeroes = true;
                        while (toks.hasMoreTokens()) {
                            if (!toks.nextToken().equals("0.0")) { //$NON-NLS-1$
                                isAllZeroes = false;
                                break;
                            }
                        }
                        if (!isAllZeroes) {
                            w.write("      <DPI INDEX=\"" + index + "\">" + mangleXMLString(valueString.trim()) + "</DPI>" + ln); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        }
                    }
                } else {
                    List l = (List) valueCache.get(nodeName);
                    w.write("      <DPI> "); //$NON-NLS-1$
                    for (Iterator j = l.iterator(); j.hasNext();) {
                        Hashtable query = new Hashtable();
                        query.put(nodeName, j.next());
                        w.write(mangleXMLString(CPF.get(query) + " ")); //$NON-NLS-1$
                    }
                    w.write("</DPI>" + ln); //$NON-NLS-1$
                }
                w.write("    </DPIS>" + ln); //$NON-NLS-1$
            }
            w.write("</DISTRIBUTION>" + ln); //$NON-NLS-1$
            w.write("</BNMODEL>" + ln); //$NON-NLS-1$
            w.write("</ANALYSISNOTEBOOK>" + ln); //$NON-NLS-1$
            w.flush();
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
                    if (name.equals("ANALYSISNOTEBOOK")) { //$NON-NLS-1$
                        visitDocument(node);
                    } else if (name.equals("BNMODEL")) { //$NON-NLS-1$
                        visitModel(node);
                        // if multi-model, add graph to a list
                    } else
                        throw new RuntimeException("Unhandled element " + name);
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

    protected void visitModel(Node parent) {
        graph = new BBNGraph();

        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    graph.setName(value);
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled property type attribute " + name);
            }
        }

        Hashtable prop = graph.getProperty();
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        // Split into two loops to handle forward reference
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("STATICPROPERTIES")) { //$NON-NLS-1$
                        visitStaticProperties(node, prop);
                    } else if (name.equals("DYNAMICPROPERTIES")) { //$NON-NLS-1$
                        visitDynamicProperties(node, prop);
                    } else if (name.equals("VARIABLES")) { //$NON-NLS-1$
                        visitVariables(node);
                    }
                    break;
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.DEBUG)
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }

        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("STRUCTURE")) { //$NON-NLS-1$
                        visitStructure(node);
                    } else if (name.equals("DISTRIBUTIONS")) { //$NON-NLS-1$
                        visitDistributions(node);
                    }
                    break;
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

    protected void visitVariables(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("VAR")) { //$NON-NLS-1$
                        BBNNode bbnnode = visitVariable(node);
                        graph.add(bbnnode);
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

    protected BBNNode visitVariable(Node parent) {
        NodeList l = parent.getChildNodes();

        BBNNode bbnnode = new BBNNode();
        int max;
        String propName = "", propType = "discrete"; //$NON-NLS-1$ //$NON-NLS-2$
        Double xpos = new Double(0), ypos = new Double(0);
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    propName = value;
                } else if (name.equals("TYPE")) { //$NON-NLS-1$
                    propType = value;
                } else if (name.equals("XPOS")) { //$NON-NLS-1$
                    try {
                        xpos = new Double(value);
                    } catch (Exception e) {
                    }
                } else if (name.equals("YPOS")) { //$NON-NLS-1$
                    try {
                        ypos = new Double(value);
                    } catch (Exception e) {
                    }
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled variable property type attribute " + name);
            }
        }

        if (!propType.equals("continuous") && !propType.equals("discrete")) { //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException("Unknown node type " + propType);
        }

        /**
         * TO DO: Don't know yet how to handle continuous nodes!
         */
        if (propType.equals("continuous")) { //$NON-NLS-1$
            throw new RuntimeException("Don't know how to handle continuous nodes yet");
        }

        if (l == null) return null;
        LinkedList values = new LinkedList();
        Hashtable prop = bbnnode.getProperty();
        bbnnode.setName(propName);
        nodeCache.put(propName, bbnnode);
        values.add(new Double(Math.round(xpos.doubleValue() / 100) - 100));
        values.add(new Double(Math.round(ypos.doubleValue() / 100) - 100));
        prop.put("position", values); //$NON-NLS-1$
        values = new LinkedList();

        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("DESCRIPTION")) { //$NON-NLS-1$
                        String desc = getElementValue(node);
                        if (desc != null) bbnnode.setLabel(desc);
                    } else if (name.equals("STATENAME")) { //$NON-NLS-1$
                        String value = getElementValue(node);
                        if (value != null) values.add(value);
                    } else if (name.equals("PROPERTY")) { //$NON-NLS-1$
                        visitDynamicProperty(node, prop, "dynamic."); //$NON-NLS-1$
                    } else if (name.equals("PROPXML")) { //$NON-NLS-1$
                        visitDynamicPropertyXML(node, prop, "dynamic."); //$NON-NLS-1$
                    } else if (Settings.DEBUG)
                        System.out.println("Unhandled variable element " + name);
                    break;
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

        if (propType.equals("discrete")) { //$NON-NLS-1$
            BBNDiscreteValue vals = new BBNDiscreteValue();
            vals.addAll(values);
            bbnnode.setValues(vals);
        } else if (propType.equals("continuous")) { //$NON-NLS-1$
        }

        valueCache.put(propName, values);
        return bbnnode;
    }

    protected void visitStructure(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) return;
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("ARC")) { //$NON-NLS-1$
                        visitArc(node);
                    } else if (name.equals("MEMBER")) { //$NON-NLS-1$
                        // Ignore this because it's redundant
                    } else if (Settings.DEBUG)
                        System.out.println("Unhandled variable element " + name);
                    break;
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

    protected void visitArc(Node parent) {
        String parentName = "", childName = ""; //$NON-NLS-1$ //$NON-NLS-2$
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("PARENT")) { //$NON-NLS-1$
                    parentName = value;
                } else if (name.equals("CHILD")) { //$NON-NLS-1$
                    childName = value;
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled property type attribute " + name);
            }
        }

        BBNNode bparent = (BBNNode) nodeCache.get(parentName);
        BBNNode child = (BBNNode) nodeCache.get(childName);
        if (bparent == null || child == null)
            throw new RuntimeException("Cannot make an arc from " + parentName + " to " + childName);

        graph.addEdge(bparent, child);
    }

    protected void visitDistributions(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("DIST")) { //$NON-NLS-1$
                        visitDistribution(node);
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

    /**
     * Fill in the CPF/CPT of a given node. Rather messed up, but works
     *
     * @param parent
     */
    protected void visitDistribution(Node parent) {
        NodeList l = parent.getChildNodes();

        int max;
        String propName = null, propType = "discrete", funcType = ""; //$NON-NLS-1$ //$NON-NLS-2$
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("TYPE")) { //$NON-NLS-1$
                    propType = value;
                } else if (name.equals("FUNCTYPE")) { //$NON-NLS-1$
                    funcType = value; // dunno what to do with it
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled variable property type attribute " + name);
            }
        }

        if (!propType.equals("ci") && !propType.equals("discrete")) { //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException("Unknown node distribution " + propType);
        }

        if (l == null) return;
        List condset = new LinkedList(), dpis = null;

        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("CONDSET")) { //$NON-NLS-1$
                        condset = visitCondSet(node);
                    } else if (name.equals("PRIVATE")) { //$NON-NLS-1$
                        propName = visitPrivate(node);
                    } else if (name.equals("SHARED")) { //$NON-NLS-1$
                        throw new RuntimeException("We don't know yet how to handle shared distributions");
                    } else if (name.equals("DPIS")) { //$NON-NLS-1$
                        dpis = visitDPIs(node);
                    } else if (name.equals("REFERENCE")) { //$NON-NLS-1$
                        throw new RuntimeException("We don't know yet how to handle references in shared distributions");
                    } else if (Settings.DEBUG)
                        System.out.println("Unhandled variable element " + name);
                    break;
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

        if (propName == null)
            throw new RuntimeException("Cannot find the name for the given distribution");

        if (dpis == null)
            throw new RuntimeException("Cannot find the distribution table");

        // Post-processing
        BBNNode bbnnode = (BBNNode) nodeCache.get(propName);
        List bbnvalue = (List) valueCache.get(propName);
        List parents = bbnnode.getParents();
        Hashtable CPF = new Hashtable();

        // Check whether parents size matches
        if (parents.size() != condset.size())
            throw new RuntimeException("Node parent size mismatch!");

        if (parents != null && parents.size() > 0) { // implies condset != null
            List condsetClone = (List) ((LinkedList) condset).clone();
            List[] bbnvalues = new List[condset.size()];
            for (Iterator i = parents.iterator(); i.hasNext();) {
                BBNNode bbnparent = (BBNNode) i.next();
                String name = bbnparent.getLabel();
                int idx = condset.indexOf(name);
                if (idx == -1)
                    throw new RuntimeException("Node parent name mismatch!");
                condsetClone.remove(name);
                bbnvalues[idx] = (List) valueCache.get(name);
            }
            if (condsetClone.size() != 0)
                throw new RuntimeException("Node parent mismatch!");

            // Fill ini the CPF
            for (Iterator i = dpis.iterator(); i.hasNext();) {
                Object[] o = (Object[]) i.next();
                Hashtable query = new Hashtable();
                StringTokenizer idxtok = new StringTokenizer((String) o[0]);
                int idxtokCount = idxtok.countTokens();
                assert idxtokCount == bbnvalues.length;
                for (int j = 0; j < idxtokCount; j++) {
                    try {
                        int parvalueidx = Integer.parseInt(idxtok.nextToken());
                        query.put(condset.get(j), bbnvalues[j].get(parvalueidx));
                    } catch (Exception e) {
                        throw new RuntimeException("Non numeric data detected in the distribution indices");
                    }
                }

                StringTokenizer tok = new StringTokenizer((String) o[1]);
                assert tok.countTokens() == bbnvalue.size();

                Iterator it = bbnvalue.iterator();
                while (tok.hasMoreTokens()) {
                    String val = (String) it.next();
                    query.put(propName, val);

                    String token = tok.nextToken();
                    Double dbl = new Double(0);
                    try {
                        dbl = new Double(token);
                    } catch (Exception e) {
                        throw new RuntimeException("Non-numeric value found in distribution table " + token);
                    }
                    CPF.put(query.clone(), new BBNConstant(dbl.doubleValue())); // Don't forget to clone
                }
            }

            // We must pad other entries with 0
            condset.add(propName);
            padCPFWithZeroes((LinkedList) condset, new Hashtable(), CPF);
            condset.remove(propName);
        } else {
            assert dpis.size() == 1;
            String dpistring = (String) ((Object[]) dpis.get(0))[1];
            StringTokenizer tok = new StringTokenizer(dpistring);
            assert tok.countTokens() == bbnvalue.size();

            Iterator it = bbnvalue.iterator();
            while (tok.hasMoreTokens()) {
                Hashtable query = new Hashtable();
                query.put(propName, it.next());

                String token = tok.nextToken();
                Double dbl = new Double(0);
                try {
                    dbl = new Double(token);
                } catch (Exception e) {
                    throw new RuntimeException("Non-numeric value found in distribution table " + token);
                }
                CPF.put(query, new BBNConstant(dbl.doubleValue()));
            }
        }
        bbnnode.setCPF(CPF);
    }

    /**
     * Because XBN distribution table format is compact, we must pad other
     * values with 0.0
     *
     * @param condset The node set
     * @param query   The query to pad
     * @param CPF     Current CPF
     */
    private void padCPFWithZeroes(LinkedList condset, Hashtable query, Hashtable CPF) {
        Object current = condset.removeFirst();
        List values = (List) valueCache.get(current);
        for (Iterator i = values.iterator(); i.hasNext();) {
            Object val = i.next();
            query.put(current, val);
            if (condset.size() == 0) {
                if (CPF.get(query) == null) {
                    CPF.put(query.clone(), new BBNConstant(0.0));
                }
            } else {
                padCPFWithZeroes(condset, query, CPF);
            }
        }
        condset.addFirst(current);
    }

    protected String visitPrivate(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    return value;
                }
            }
        }
        return null;
    }

    protected List visitCondSet(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        LinkedList ll = new LinkedList();
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("CONDELEM")) { //$NON-NLS-1$
                        String s = visitCondElem(node);
                        if (s != null) ll.add(s);
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
        return ll;
    }

    protected String visitCondElem(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    return value.trim();
                } else if (name.equals("STATES")) { //$NON-NLS-1$
                    // Ignore it
                    //throw new RuntimeException("We don't know on how to handle this yet");
                }
            }
        }
        return null;
    }

    protected List visitDPIs(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        LinkedList ll = new LinkedList();
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("DPI")) { //$NON-NLS-1$
                        Object[] o = visitDPI(node);
                        if (o != null) ll.add(o);
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
        return ll;
    }

    protected Object[] visitDPI(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        String idx = ""; //$NON-NLS-1$
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("INDEXES")) { //$NON-NLS-1$
                    idx = value.trim();
                }
            }
        }

        String val = getElementValue(parent).trim();

        return new Object[]{idx, val};
    }

    protected void visitStaticProperties(Node parent, Hashtable prop) {
        NodeList l = parent.getChildNodes();
        if (l == null) return;
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    NamedNodeMap attrs = node.getAttributes();
                    if (attrs != null) {
                        int maxattr = attrs.getLength();
                        for (int j = 0; j < maxattr; j++) {
                            Node attr = attrs.item(j);
                            String attrName = attr.getNodeName();
                            String value = attr.getNodeValue();
                            prop.put("static." + name, value); //$NON-NLS-1$
                        }
                    }
                    break;
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

    protected void visitDynamicProperties(Node parent, Hashtable prop) {
        NodeList l = parent.getChildNodes();
        if (l == null) return;
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("PROPERTY")) { //$NON-NLS-1$
                        visitDynamicProperty(node, prop, "dynamic."); //$NON-NLS-1$
                    } else if (name.equals("PROPERTYTYPE")) { //$NON-NLS-1$
                        visitDynamicPropertyType(node, prop, "dynamic."); //$NON-NLS-1$
                    } else if (name.equals("PROPXML")) { //$NON-NLS-1$
                        visitDynamicPropertyXML(node, prop, "dynamic."); //$NON-NLS-1$
                    }
                    break;
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

    protected void visitDynamicPropertyType(Node parent, Hashtable prop, String prefix) {
        NodeList l = parent.getChildNodes();

        int max;
        String propName = "", propType = "string", propEnum = null; //$NON-NLS-1$ //$NON-NLS-2$
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    propName = value;
                } else if (name.equals("TYPE")) { //$NON-NLS-1$
                    propType = value;
                } else if (name.equals("ENUMSET")) { //$NON-NLS-1$
                    propEnum = value;
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled property type attribute " + name);
            }
        }

        if (l == null) return;
        StringBuffer buf = new StringBuffer();
        max = l.getLength();
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
                    if (Settings.DEBUG)
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        prop.put(prefix + propName, propType);
        if (propEnum != null) prop.put(prefix + propName + ".enum", propEnum); //$NON-NLS-1$
    }

    protected void visitDynamicProperty(Node parent, Hashtable prop, String prefix) {
        NodeList l = parent.getChildNodes();

        int max;
        String propName = ""; //$NON-NLS-1$
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) //$NON-NLS-1$
                    propName = value;
            }
        }

        if (l == null) return;
        LinkedList list = new LinkedList();
        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("PROPVALUE")) { //$NON-NLS-1$
                        String value = getElementValue(node);
                        try {
                            if (value != null) {
                                Double dbl = new Double(value);
                                if (!dbl.isNaN())
                                    list.add(dbl);
                                else
                                    list.add(value);
                            } else if (Settings.DEBUG)
                                System.out.println("Error on value at " + node);
                        } catch (Exception e) {
                            list.add(value);
                        }
                    }
                case Node.TEXT_NODE:
                case Node.COMMENT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.DEBUG)
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        prop.put(prefix + propName, list);
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
                    if (Settings.DEBUG)
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        return buf.toString().trim();
    }

    protected void visitDynamicPropertyXML(Node parent, Hashtable prop, String prefix) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs != null) {
            int max = attrs.getLength();
            for (int i = 0; i < max; i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equals("NAME")) { //$NON-NLS-1$
                    prop.put(prefix + value, "true"); //$NON-NLS-1$
                    return;
                }
            }
        }
    }

    /**
     * Used only for testing. Otherwise, don't use it.
     *
     * @param domNode
     */
    protected void visit(Node domNode) {
        NodeList l = domNode.getChildNodes();
        if (l == null) return;

        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    System.out.println("Attribute node");
                    break;
                case Node.TEXT_NODE:
                    System.out.println("Text node");
                    break;
                case Node.COMMENT_NODE:
                    System.out.println("Comment node");
                    break;
                case Node.CDATA_SECTION_NODE:
                    System.out.println("CData Section node");
                    break;
                case Node.DOCUMENT_FRAGMENT_NODE:
                    System.out.println("Document Fragment node");
                    break;
                case Node.DOCUMENT_NODE:
                    System.out.println("Document node");
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    System.out.println("Document type node");
                    break;
                case Node.ELEMENT_NODE:
                    System.out.println("Element node");
                    break;
                case Node.ENTITY_NODE:
                    System.out.println("Entity node");
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    System.out.println("Entity Ref node");
                    break;
                case Node.NOTATION_NODE:
                    System.out.println("Notation node");
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    System.out.println("Processing Instr node");
                    break;
                default:
                    System.out.println("Unknown node");
            }
            visit(node);
        }
    }

    /**
     * Escape the string s to oblige to the XML rules (e.g.: "'" becomes &apos;,
     * "&" becomes &amp;, and so on).
     *
     * @param s The raw string
     * @return String The formatted string
     */
    protected String mangleXMLString(String s) {
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

    /**
     * Driver for testing this parser
     *
     * @param args
     */
    public static void main(String[] args) {
        XBNParser p = new XBNParser();
        try {
            p.load(new FileInputStream(args[0]));
            System.out.println(p.graph.toString());
            System.out.println("----------------------------------"); //$NON-NLS-1$
            p.save(System.out, p.graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
