package edu.ksu.cis.bnj.bbn.converter.xmlbif;

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
import edu.ksu.cis.bnj.bbn.prm.PRMClass;
import edu.ksu.cis.bnj.bbn.prm.PRMGraph;
import edu.ksu.cis.bnj.bbn.prm.PRMNode;
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
 * <dt>XMLBif Parser
 * <dd>
 * <p/>
 * <dt>Description:
 * <dd> This class is used to parse XML data given in the form of a file (see constructor)
 * and add the data into the BNJ nodes. It also adds probability to the child
 * nodes. This is a DOM implementation
 * <p/>
 * <DT>TO DO:
 * <dd>Basically this is what Subbu and Prashanth did. However, I rewrite it.
 * </dl>
 *
 * @author Roby Joehanes
 *         PRM Stuff by @author Prashanth Boddhireddy
 * @version 0.1.0
 */
public class XMLBifParser implements Converter {

    protected BBNGraph graph = null;
    protected Hashtable nodeCache = new Hashtable();
    protected Hashtable valueCache = new Hashtable();
    protected boolean rowFirst = true;
    protected boolean isPRM = false;

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#initialize()
     */
    public void initialize() {
        nodeCache = new Hashtable();
        valueCache = new Hashtable();
        isPRM = false;
    }

    /**
     * Save routine for XML BIF. Graph properties won't get saved. Node
     * properties aren't either, except for the position.
     *
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#save(java.io.OutputStream, edu.ksu.cis.bnj.bbn.BBNGraph)
     */
    public void save(OutputStream stream, BBNGraph graph) {
        Writer w = new OutputStreamWriter(stream);
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        nodeCache = new Hashtable();
        valueCache = new Hashtable();
        try {
            // Dump out the DTD
            w.write("<?xml version=\"1.0\"?>" + ln + ln + //$NON-NLS-1$
                    "<!-- DTD for the XMLBIF 0.3 format -->" + ln + //$NON-NLS-1$
                    "<!DOCTYPE BIF [" + ln + //$NON-NLS-1$
                    "    <!ELEMENT BIF ( NETWORK )*>" + ln + //$NON-NLS-1$
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
                    "]>" + ln + ln + //$NON-NLS-1$
                    "<BIF VERSION=\"0.3\"");

            if (graph instanceof PRMGraph) {
                w.write(" PRM=\"0.1\"");
            }

            w.write(">" + ln + //$NON-NLS-1$
                    "  <NETWORK>" + ln + //$NON-NLS-1$
                    "    <NAME>" + mangleXMLString(graph.getName()) + "</NAME>" + ln + //$NON-NLS-1$ //$NON-NLS-2$
                    "      <!-- Variables -->" + ln); //$NON-NLS-1$

            // Dump out the variables
            Set nodes = graph.getNodes();
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                String nodeName = node.getLabel();
                nodeCache.put(nodeName, node);
                w.write("      <VARIABLE TYPE=\""); //$NON-NLS-1$
                if (node.isDecision())
                    w.write("decision"); //$NON-NLS-1$
                else if (node.isUtility())
                    w.write("utility"); //$NON-NLS-1$
                else
                    w.write("nature"); //$NON-NLS-1$
                w.write("\">" + ln); //$NON-NLS-1$
                w.write("         <NAME>" + mangleXMLString(nodeName) + "</NAME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                BBNValue value = node.getValues();
                if (value instanceof BBNContinuousValue)
                    throw new RuntimeException("Don't know how to handle continuous nodes yet!");

                if (!node.isUtility()) {
                    LinkedList values = new LinkedList();
                    values.addAll((BBNDiscreteValue) value);
                    valueCache.put(nodeName, values);
                    for (Iterator j = values.iterator(); j.hasNext();) {
                        w.write("        <OUTCOME>" + mangleXMLString(j.next().toString()) + "</OUTCOME>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                Object o = node.getProperty().get("position"); //$NON-NLS-1$
                if (o instanceof List) { // implies o != null
                    String posString = null;
                    try {
                        Double xpos = (Double) ((List) o).get(0);
                        Double ypos = (Double) ((List) o).get(1);
                        posString = "(" + Math.round(xpos.doubleValue()) + ", " + //$NON-NLS-1$ //$NON-NLS-2$
                                Math.round(ypos.doubleValue()) + ")"; //$NON-NLS-1$

                    } catch (Exception e) {
                    }
                    if (posString != null)
                        w.write("        <PROPERTY> position = " + posString + " </PROPERTY>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                }
                w.write("      </VARIABLE>" + ln); //$NON-NLS-1$
            }

            // Dump out the CPTs
            w.write("      <!-- Probability Distributions -->" + ln); //$NON-NLS-1$
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                if (node.isDecision()) continue; // Decision nodes doesn't have CPT, so skip.

                String nodeName = node.getLabel();
                w.write("      <DEFINITION>" + ln); //$NON-NLS-1$
                w.write("        <FOR>" + mangleXMLString(nodeName) + "</FOR>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                List parents = node.getParents();
                LinkedList eligibleParents = new LinkedList();

                for (Iterator j = parents.iterator(); j.hasNext();) {
                    BBNNode parent = (BBNNode) j.next();
                    String parentName = parent.getLabel();
                    w.write("        <GIVEN>" + mangleXMLString(parentName) + "</GIVEN>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
                    if (!parent.isUtility()) eligibleParents.add(parentName);
                }

                // Dump out the CPT string
                if (!node.isUtility()) eligibleParents.addFirst(nodeName);
                String CPTString = saveCPT(eligibleParents, new Hashtable(), node.getCPF().getTable(), new StringBuffer()).trim();
                w.write("        <TABLE>" + CPTString + "</TABLE>" + ln); //$NON-NLS-1$ //$NON-NLS-2$

                w.write("      </DEFINITION>" + ln); //$NON-NLS-1$
            }

            w.write("  </NETWORK>" + ln); //$NON-NLS-1$
            w.write("</BIF>" + ln); //$NON-NLS-1$
            w.flush();
            w.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String saveCPT(LinkedList nodeList, Hashtable curQuery, Hashtable CPF, StringBuffer buf) {
        String name = (String) nodeList.removeFirst();
        List values = (List) valueCache.get(name);
        for (Iterator i = values.iterator(); i.hasNext();) {
            Object value = i.next();
            curQuery.put(name, value);
            if (nodeList.size() == 0) {
                Object cpfval = CPF.get(curQuery);
                //assert cpfval != null;  // Bug fix courtesy of Anshu Sakseena
                buf.append(cpfval + " "); //$NON-NLS-1$
            } else {
                saveCPT(nodeList, curQuery, CPF, buf);
            }
        }

        nodeList.addFirst(name);
        return buf.toString();
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#load(java.io.InputStream)
     */
    public BBNGraph load(InputStream stream) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        isPRM = false;

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

    public void visitModel(Node parent) {
        graph = isPRM ? new PRMGraph() : new BBNGraph();
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        // Split into two loops so that it can handle forward reference
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("NAME")) { //$NON-NLS-1$
                        graph.setName(getElementValue(node));
                    } else if (isPRM && name.equals("PRM_CLASS")) { //$NON-NLS-1$
                        PRMClass prmbbnclass = (PRMClass) visitPRMClass(node);
                        ((PRMGraph) graph).addClass(prmbbnclass);
                    } else if (name.equals("VARIABLE")) { //$NON-NLS-1$
                        BBNNode bbnnode = visitVariable(node);
                        graph.add(bbnnode);
                    }
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

        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("DEFINITION") || name.equals("PROBABILITY")) { //$NON-NLS-1$ //$NON-NLS-2$
                        visitDefinition(node);
                    }
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

    public PRMClass visitPRMClass(Node parent) {
        assert (isPRM);
        NodeList l = parent.getChildNodes();
        PRMClass prmClass = new PRMClass();

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
                        prmClass.setClassName(className);
                    } else if (name.equals("PRM_ATTRIBUTESET")) { //$NON-NLS-1$
                        visitPRMAttributeSet(node, prmClass);
                    }
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

        return prmClass;
    }

    protected void visitPRMAttributeSet(Node parent, PRMClass prmClass) {
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
                        prmClass.setPrimaryKey(pKeyName);
                    } else if (name.equals("PRM_RKEY")) { //$NON-NLS-1$
                        String rKeyName = getElementValue(node);
                        prmClass.addRefferenceAttribute(rKeyName);
                    } else if (name.equals("PRM_ATTRIBUTE")) { //$NON-NLS-1$
                        String attrName = getElementValue(node);
                        prmClass.addAttribute(attrName);
                    }
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

        BBNNode bbnnode = isPRM ? new PRMNode() : new BBNNode();
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
                        bbnnode.setType(BBNNode.DECISION);
                    } else if (value.equals("utility")) { //$NON-NLS-1$
                        bbnnode.setType(BBNNode.UTILITY);
                    } // otherwise it's just "nature"
                } else if (Settings.DEBUG)
                    System.out.println("Unhandled variable property attribute " + name);
            }
        }

        if (!propType.equals("nature") && !propType.equals("decision") && !propType.equals("utility")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new RuntimeException("Unknown node type " + propType);
        }

        if (l == null) return null;
        LinkedList values = new LinkedList();
        Hashtable prop = bbnnode.getProperty();

        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("NAME")) { //$NON-NLS-1$
                        String desc = getElementValue(node);
                        nodeCache.put(desc, bbnnode);
                        bbnnode.setName(desc);
                        bbnnode.setLabel(desc);
                    } else if (name.equals("OUTCOME") || name.equals("VALUE")) { //$NON-NLS-1$ //$NON-NLS-2$
                        String value = getElementValue(node);
                        if (value != null) values.add(value);
                    } else if (name.equals("PROPERTY")) { //$NON-NLS-1$
                        String value = getElementValue(node);
                        parseProperty(value, prop);
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

        if (propType.equals("nature") || propType.equals("utility")) { //$NON-NLS-1$ //$NON-NLS-2$
            BBNDiscreteValue vals = new BBNDiscreteValue();
            vals.addAll(values);
            bbnnode.setValues(vals);
        }

        valueCache.put(bbnnode.getLabel(), values);
        return bbnnode;
    }

    /**
     * Parse string property. The input string is expected to have at least one
     * equal sign. If the right hand side is enclosed with parentheses, it will
     * treat it as lists. If the value is numeric, it tries to convert that to a
     * Double. Otherwise, it stores the value as a string.
     *
     * @param s    The property string
     * @param prop The property table
     */
    protected void parseProperty(String s, Hashtable prop) {
        int idx = s.indexOf('=');
        if (idx == -1) return;
        String name = s.substring(0, idx).trim();
        String value = s.substring(idx + 1).trim();
        if (value.startsWith("(") && value.endsWith(")")) { //$NON-NLS-1$ //$NON-NLS-2$
            value = value.substring(1, value.length() - 1).trim();
            StringTokenizer tokenizer = new StringTokenizer(value, ", "); //$NON-NLS-1$
            LinkedList values = new LinkedList();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                try {
                    Double dbl = new Double(token);
                    values.add(dbl);
                } catch (Exception e) {
                    values.add(token);
                }
            }
            prop.put(name, values);
        } else {
            try {
                Double dbl = new Double(value);
                prop.put(name, dbl);
            } catch (Exception e) {
                prop.put(name, value);
            }
        }
    }

    protected void visitDefinition(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) return;
        LinkedList parents = new LinkedList();
        String curNodeName = null, CPTString = null;

        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equals("FOR")) { //$NON-NLS-1$
                        curNodeName = getElementValue(node);
                    } else if (name.equals("GIVEN")) { //$NON-NLS-1$
                        String parentName = getElementValue(node);
                        BBNNode parentNode = (BBNNode) nodeCache.get(parentName);
                        if (parentNode == null)
                            throw new RuntimeException("Cannot resolve node " + parentName);
                        if (parentNode.isUtility())
                            throw new RuntimeException("Utility nodes can never be parent nodes!");
                        if (parentNode != null) parents.add(parentName);
                    } else if (name.equals("TABLE")) { //$NON-NLS-1$
                        CPTString = getElementValue(node);
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

        // Sanity check
        if (curNodeName == null)
            throw new RuntimeException("Ill-formed <DEFINITION> tag, no names specified!");

        BBNNode curNode = (BBNNode) nodeCache.get(curNodeName);
        if (curNode == null)
            throw new RuntimeException("Ill-formed <DEFINITION> tag, non-existant names specified!");
        if (curNode.isDecision()) return;
        if (CPTString == null)
            throw new RuntimeException("Ill-formed <DEFINITION> tag, no tables specified!");

        // Post processing
        for (Iterator i = parents.iterator(); i.hasNext();) {
            BBNNode parentNode = (BBNNode) nodeCache.get(i.next());
            graph.addEdge(parentNode, curNode);
        }

        if (!curNode.isUtility()) {
            if (rowFirst) {
                parents.addFirst(curNodeName);
            } else {
                parents.add(curNodeName);
            }
        }
        StringTokenizer tok = new StringTokenizer(CPTString);

        // We should validate whether tok contains enough tokens.
        int cardinality = 1;
        for (Iterator i = parents.iterator(); i.hasNext();) {
            String name = (String) i.next();
            List values = (List) valueCache.get(name);
            cardinality *= values.size();
        }
        if (cardinality != tok.countTokens())
            throw new RuntimeException("Ill-formed <DEFINITION> tag, table entries doesn't match!");

        curNode.setCPF(convertCPF(parents, tok, new Hashtable(), new Hashtable()));
    }

    /**
     * Converting XMLBif table to flat CPF
     *
     * @param nodeList
     * @param tok
     * @param curQuery
     * @param CPF
     * @return Hashtable
     */
    private Hashtable convertCPF(LinkedList nodeList, StringTokenizer tok, Hashtable curQuery, Hashtable CPF) {
        String name = (String) nodeList.removeFirst();
        List values = (List) valueCache.get(name);

        for (Iterator i = values.iterator(); i.hasNext();) {
            curQuery.put(name, i.next());
            if (nodeList.size() > 0) {
                convertCPF(nodeList, tok, curQuery, CPF);
            } else {
                double value = Double.parseDouble(tok.nextToken());
                CPF.put(curQuery.clone(), new BBNConstant(value));
            }
        }

        nodeList.addFirst(name);
        return CPF;
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
        XMLBifParser p = new XMLBifParser();
        try {
            p.load(new FileInputStream(args[0]));
            System.out.println(p.graph.toString());
            System.out.println("----------------------------------"); //$NON-NLS-1$
            p.save(System.out, p.graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return boolean
     */
    public boolean isRowFirst() {
        return rowFirst;
    }

    /**
     * Sets the rowFirst.
     *
     * @param rowFirst The rowFirst to set
     */
    public void setRowFirst(boolean rowFirst) {
        this.rowFirst = rowFirst;
    }

}
