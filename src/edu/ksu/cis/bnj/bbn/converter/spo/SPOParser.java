package edu.ksu.cis.bnj.bbn.converter.spo;

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
import edu.ksu.cis.kdd.util.StringUtil;
import edu.ksu.cis.kdd.util.TableSet;
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
 * <dt>University of Kentucky's SPO Parser
 * <dd>
 * <p/>
 * <dt>Description:
 * <dd>This class is used to parse XML data of SPO format from University of
 * Kentucky This is a DOM implementation
 * </dl>
 * <p/>
 * <P>This format is courtesy of Dr. Alexander Dekhtyar, Zhao Wenzhong, and Judy Goldsmith
 * of University of Kentucky
 * <p/>
 * <P>Current version: We assume that ALL probability distributions and ALL values are
 * explicitly mentioned. The only exception is that we may omit the nodes with no parents.
 * In this case, we assume uniform distribution across the values assuming that ALL values
 * are mentioned.
 *
 * @author Roby Joehanes
 * @version 0.1.0
 */
public class SPOParser implements Converter {

    protected BBNGraph graph = null;
    protected Hashtable nodeCache = new Hashtable();
    protected TableSet valueCache = new TableSet();
    protected TableSet parentsCache = new TableSet();
    protected TableSet parentValuesCache = new TableSet();
    protected Hashtable cptCache = new Hashtable();
    protected static String ln = System.getProperty("line.separator"); //$NON-NLS-1$
    protected static String SPODTD = "<?xml version=\"1.0\"?>" + ln + ln + //$NON-NLS-1$
            "<spos xmlns=\"spo.xsd\">" + ln + ln; //$NON-NLS-1$


    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#initialize()
     */
    public void initialize() {
        nodeCache = new Hashtable();
        valueCache = new TableSet();
        parentsCache = new TableSet();
        parentValuesCache = new TableSet();
        cptCache = new Hashtable();
    }

    /**
     * This routine is supposed to save the BBN Graph into SPO format.
     *
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#save(java.io.Writer, edu.ksu.cis.bnj.bbn.BBNGraph)
     */
    public void save(OutputStream stream, BBNGraph graph) {
        Writer w = new OutputStreamWriter(stream);
        nodeCache = new Hashtable();
        valueCache = new TableSet();
        try {
            w.write(SPODTD);
            List nodes = graph.topologicalSort();
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                BBNNode node = (BBNNode) i.next();
                List parents = node.getParents();
                if (parents == null || parents.size() == 0) {
                    w.write(StringUtil.mangleXMLString(processNode(node, null, 0)) + ln);
                } else {
                    w.write(StringUtil.mangleXMLString(processNode(node, parents)) + ln);
                }
            }
            w.write("</spos>" + ln); //$NON-NLS-1$
            w.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String processNode(BBNNode node, Hashtable parentValues, int counter) {
        StringBuffer buf = new StringBuffer();
        String tab = "  ";
        String nodeName = node.getLabel();
        buf.append("<spo path = \"" + nodeName + counter + "\">" + ln); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(tab + "<table>" + ln); //$NON-NLS-1$
        buf.append(tab + tab + "<variable>" + ln); //$NON-NLS-1$
        buf.append(tab + tab + tab + "<name>" + nodeName + "</name>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(tab + tab + "</variable>" + ln); //$NON-NLS-1$
        BBNDiscreteValue values = (BBNDiscreteValue) node.getValues();
        for (Iterator i = values.iterator(); i.hasNext();) {
            Object value = i.next();
            buf.append(tab + tab + "<row> <val>" + value + "</val> <P>"); //$NON-NLS-1$ //$NON-NLS-2$
            // tricky part: Extracting the probability
            Hashtable queryTable = null;
            if (parentValues != null && parentValues.size() > 0) {
                queryTable = (Hashtable) parentValues.clone();
            } else {
                queryTable = new Hashtable();
            }
            queryTable.put(nodeName, value);
            buf.append(node.query(queryTable) + "</P> </row>" + ln); //$NON-NLS-1$
        }

        buf.append(tab + "</table>" + ln); //$NON-NLS-1$
        if (parentValues != null && parentValues.size() > 0) {
            buf.append(tab + "<conditional>" + ln); //$NON-NLS-1$
            for (Enumeration e = parentValues.keys(); e.hasMoreElements();) {
                Object parentName = e.nextElement();
                Object parentValue = parentValues.get(parentName);
                buf.append(tab + tab + "<elem> <name>" + parentName + //$NON-NLS-1$
                        "</name> <val>" + parentValue + "</val> </elem>" + ln); //$NON-NLS-1$ //$NON-NLS-2$
            }
            buf.append(tab + "</conditional>" + ln); //$NON-NLS-1$
        }
        buf.append("</spo>" + ln); //$NON-NLS-1$
        return buf.toString();
    }

    protected String processNode(BBNNode node, List parents) {
        StringBuffer buf = new StringBuffer();
        pathCounter = 0;
        if (parents instanceof LinkedList) {
            processNode(node, (LinkedList) parents, new Hashtable(), buf);
        } else {
            LinkedList newParents = new LinkedList();
            newParents.addAll(parents);
            processNode(node, (LinkedList) newParents, new Hashtable(), buf);
        }
        return buf.toString();
    }

    private int pathCounter = 0;

    protected void processNode(BBNNode node, LinkedList parents, Hashtable curParentValues, StringBuffer buf) {
        BBNNode parent = (BBNNode) parents.removeFirst();
        BBNDiscreteValue values = (BBNDiscreteValue) parent.getValues();
        for (Iterator i = values.iterator(); i.hasNext();) {
            Object value = i.next();
            curParentValues.put(parent.getLabel(), value);
            if (parents.size() == 0) {
                String spoContent = processNode(node, curParentValues, pathCounter);
                buf.append(spoContent);
                buf.append(ln);
                pathCounter++;
            } else {
                processNode(node, parents, curParentValues, buf);
            }
        }

        parents.addFirst(parent);
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.converter.Converter#load(java.io.Reader)
     */
    public BBNGraph load(InputStream stream) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false); // we're not interested in checking the file thoroughly
        factory.setNamespaceAware(false);
        graph = new BBNGraph();

        //Parse the document
        try {
            parser = factory.newDocumentBuilder();
            doc = parser.parse(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        visitDocument(doc);

        // Post process the graph, adding all the values
        for (Iterator i = graph.getNodes().iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            String nodeName = node.getLabel();
            BBNDiscreteValue values = new BBNDiscreteValue();
            assert (values != null && values.size() != 0);
            values.addAll(valueCache.get(nodeName));
            node.setValues(values);
            BBNCPF cpf = node.getCPF();
            if (cpf == null) {
                List parents = node.getParents();
                if (parents != null && parents.size() > 0)
                    throw new RuntimeException("Don't know what to do: Probability not mentioned, but we have parents in node " + nodeName);
                // If the node has no CPF AND no parents,
                // assume uniform probability distributions.
                List nodeCollection = new LinkedList();
                nodeCollection.add(nodeName);
                cpf = new BBNCPF(nodeCollection);
                BBNPDF p = new BBNConstant(1.0 / values.size());
                for (Iterator j = values.iterator(); j.hasNext();) {
                    String value = j.next().toString();
                    Hashtable queryTable = new Hashtable();
                    queryTable.put(nodeName, value);
                    cpf.put(queryTable, p);
                }
            }
        }
        System.gc();
        return graph;
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
                    if (name.equalsIgnoreCase("SPOS")) { //$NON-NLS-1$
                        // Ignore this header tag
                        visitDocument(node);
                    } else if (name.equalsIgnoreCase("SPO")) { //$NON-NLS-1$
                        visitSPO(node);
                        // if multi-model, add graph to a list, I'd rather not do that now
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
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    protected void visitSPO(Node parent) {
        // We don't need the name of the path, so discard the parameters

        List parentNodes = new LinkedList();
        Hashtable parentValuesTable = new Hashtable();
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        // Split into two loops to catch the conditional part first
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("CONDITIONAL")) { //$NON-NLS-1$
                        parentNodes = visitConditional(node, parentValuesTable);
                    } else if (name.equalsIgnoreCase("CONTEXT")) { //$NON-NLS-1$
                        // Ignore context, it's just an annotation
                    }
                    break;
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }

        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("TABLE")) { //$NON-NLS-1$
                        visitTable(node, parentNodes, parentValuesTable);
                    }
                    break;
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

    protected void visitTable(Node parent, List parentNodes, Hashtable parentTable) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        List vars = null;

        // scan variables first
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("VARIABLE")) { //$NON-NLS-1$
                        vars = visitVariables(node);
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

        if (vars == null || vars.size() != 1)
            throw new RuntimeException("Error: We cannot handle variable tag more than 1 in Bayes Nets");
        BBNNode varNode = (BBNNode) vars.get(0);
        String var = varNode.getLabel();
        // Check whether the inheritance is legal
        //System.out.println(parentsCache+"<==>"+var);
        Set cachedParents = parentsCache.get(var);
        if (cachedParents == null) {
            parentsCache.putAll(var, parentNodes);
            parentValuesCache.put(var, parentTable);
            for (Iterator i = parentNodes.iterator(); i.hasNext();) {
                BBNNode parentNode = (BBNNode) i.next();
                graph.addEdge(parentNode, varNode);   // add the edge
            }
        } else {
            if (cachedParents.size() != parentNodes.size() || !cachedParents.containsAll(parentNodes))
                throw new RuntimeException("Error: Redefined parents for node " + var);
            Set cachedValues = parentValuesCache.get(var);
            if (cachedValues.contains(parentTable))
                throw new RuntimeException("Error: Redefined parent values for node " + var);
        }

        Hashtable probTable = new Hashtable();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("ROW")) { //$NON-NLS-1$
                        // Visit rows
                        visitRows(node, var, probTable);
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

        if (probTable.size() == 0)
            throw new RuntimeException("Error: Must define probability values!");

        // Table post-processing
        BBNCPF cpt = (BBNCPF) cptCache.get(var);
        if (cpt == null) {
            Set nodeSet = new HashSet();
            nodeSet.add(var);
            nodeSet.addAll(parentTable.keySet());
            cpt = new BBNCPF(nodeSet);
            cptCache.put(var, cpt);
            BBNNode node = (BBNNode) nodeCache.get(var);
            node.setCPF(cpt);
        }
        for (Enumeration e = probTable.keys(); e.hasMoreElements();) {
            String val = (String) e.nextElement();
            Double p = (Double) probTable.get(val);
            Hashtable entry = (Hashtable) parentTable.clone();
            entry.put(var, val);
            cpt.put(entry, new BBNConstant(p.doubleValue()));
        }
    }

    protected List visitConditional(Node parent, Hashtable condTable) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("ELEM")) { //$NON-NLS-1$
                        visitElem(node, condTable);
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

        // Is a node created for the conditional part
        LinkedList parentNodes = new LinkedList();
        for (Enumeration e = condTable.keys(); e.hasMoreElements();) {
            String nodeName = (String) e.nextElement();
            String nodeValue = (String) condTable.get(nodeName);
            parentNodes.add(createNode(nodeName));
            valueCache.put(nodeName, nodeValue);
        }
        return parentNodes;
    }

    protected void visitElem(Node parent, Hashtable elemTable) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        String eName = null, eVal = null;
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("NAME")) { //$NON-NLS-1$
                        eName = getElementValue(node);
                    } else if (name.equalsIgnoreCase("VAL")) { //$NON-NLS-1$
                        eVal = getElementValue(node);
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
        assert(eName != null && eVal != null);
        elemTable.put(eName, eVal);
    }

    protected List visitVariables(Node parent) {
        LinkedList varList = new LinkedList();
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("NAME")) { //$NON-NLS-1$
                        String varName = getElementValue(node);
                        BBNNode varNode = createNode(varName);
                        varList.add(varNode);
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
        return varList;
    }

    protected void visitRows(Node parent, String var, Hashtable probTable) {
        String val = null;
        double p = Double.NaN;

        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase("VAL")) { //$NON-NLS-1$
                        if (val != null)
                            throw new RuntimeException("Cannot handle more than 1 values");
                        val = getElementValue(node);
                    } else if (name.equalsIgnoreCase("P")) { //$NON-NLS-1$
                        p = Double.parseDouble(getElementValue(node));
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
        if (p == Double.NaN)
            throw new RuntimeException("Error: Must define the probability value inside the <row> tag!");
        if (val == null)
            throw new RuntimeException("Error: Value must be defined!");
        probTable.put(val, new Double(p));

        valueCache.put(var, val);
    }

    /**
     * Create a node with the specified name if the node hasn't been created before.
     *
     * @param name
     */
    protected BBNNode createNode(String name) {
        BBNNode node = (BBNNode) nodeCache.get(name);
        if (node == null) {
            node = new BBNNode();
            node.setName(name);
            graph.addNode(node);
            nodeCache.put(name, node);
        }
        return node;
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
     * Driver for testing this parser
     *
     * @param args
     */
    public static void main(String[] args) {
        SPOParser p = new SPOParser();
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
