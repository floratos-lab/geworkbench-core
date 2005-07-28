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

import edu.ksu.cis.bnj.bbn.converter.ConverterFactory;
import edu.ksu.cis.kdd.util.graph.Edge;
import edu.ksu.cis.kdd.util.graph.Graph;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * A Wrapper for BBN Graph
 *
 * @author Roby Joehanes
 */
public class BBNGraph extends Graph {

    public static final String XML_FORMAT = "xml";
    public static final String BIF_FORMAT = "bif";
    public static final String HUGIN_FORMAT = "net";
    public static final String XBN_FORMAT = "xbn";
    public static final String DSC_FORMAT = "dsc";
    public static final String DSL_FORMAT = "dsl";
    public static final String LIBB_FORMAT = "libb";
    public static final String ERGO_FORMAT = "ent";

    /**
     * Return the set of evidence nodes.
     *
     * @return Set
     */
    public Set getEvidenceNodes() {
        HashSet nodes = new HashSet();

        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            if (n.isEvidence()) nodes.add(n);
        }
        return nodes;
    }

    /**
     * Get a table of node name -> evidence value
     *
     * @return Hashtable
     */
    public Hashtable getEvidenceTable() {
        Hashtable tbl = new Hashtable();

        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            if (n.isEvidence()) tbl.put(n.getLabel(), n.getEvidenceValue());
        }

        return tbl;
    }

    /**
     * Make all evidence nodes lose their evidence value
     */
    public void clearEvidenceNodes() {
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            if (n.isEvidence()) n.setEvidenceValue(null);
        }
    }

    /**
     * Set the evidence values based on the table. The
     * table is from node name -> evidence value.
     *
     * @param tbl
     */
    public void setEvidenceTable(Hashtable tbl) {
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            BBNNode n = (BBNNode) getNode(name);
            n.setEvidenceValue(tbl.get(name));
        }
    }

    /**
     * Return the set of query nodes.
     *
     * @return Set
     */
    public Set getQueryNodes() {
        HashSet nodes = new HashSet();

        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            if (!n.isEvidence()) nodes.add(n);
        }
        return nodes;
    }

    /**
     * Reset all node caches.
     *
     * @see edu.ksu.cis.bnj.bbn.BBNNode#precache
     */
    public void resetNodeCaches() {
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            n.resetCache();
        }
    }

    /**
     * Equality check. Simple name and nodes check
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof BBNGraph)) return false;
        BBNGraph n = (BBNGraph) o;
        return nodeTable.equals(n.nodeTable) && getName().equals(n.getName());
    }

    public void setEvidenceNodes(Hashtable tbl) {
        // Sanity check
        if (tbl == null || tbl.size() == 0) return;
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            String nodeName = (String) e.nextElement();
            String nodeValue = (String) tbl.get(nodeName);
            BBNNode node = (BBNNode) nodeTable.get(nodeName);
            if (node == null)
                throw new RuntimeException("Node " + nodeName + " is not found!");
            BBNValue value = node.getValues();
            if (value == null || !value.contains(nodeValue))
                throw new RuntimeException("Node " + nodeName + " doesn't contains value " + nodeValue);
        }
        // If they're all sane, then we update the evidences
        for (Enumeration e = tbl.keys(); e.hasMoreElements();) {
            String nodeName = (String) e.nextElement();
            String nodeValue = (String) tbl.get(nodeName);
            BBNNode node = (BBNNode) nodeTable.get(nodeName);
            node.setEvidenceValue(nodeValue);
        }
    }

    /**
     * For Debugging
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$
        Set nodes = getNodes();

        buf.append(super.toString());

        for (Iterator i = nodes.iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            //buf.append(n.toVerboseString()+ln);
        }

        return buf.toString();
    }

    /**
     * Convenience function for loading graph out of a file.
     *
     * @param filename
     * @return BBNGraph
     */
    public static BBNGraph load(String filename) {
        return ConverterFactory.load(filename);
    }

    /**
     * Convenience function for loading graph out of a file.
     *
     * @param filename
     * @param format   Format string
     * @return BBNGraph
     */
    public static BBNGraph load(String filename, String format) {
        return ConverterFactory.load(filename, format);
    }

    /**
     * Convenience function to save graph to a file with a specified format
     *
     * @param filename
     * @param format
     */
    public void save(String filename, String format) {
        ConverterFactory.save(this, filename, format);
    }

    /**
     * Convenience function to save graph to an xmlbif format
     *
     * @param filename
     */
    public void save(String filename) {
        save(filename, "xml"); //$NON-NLS-1$
    }

    /**
     * Convenience function to load evidences out of an XEB file.
     *
     * @param filename
     */
    public void loadEvidence(String filename) {
        try {
            Hashtable tbl = EvidenceParser.load(new FileInputStream(filename));
            setEvidenceNodes(tbl);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience function to save evidences out to an XEB file.
     *
     * @param filename
     */
    public void saveEvidence(String filename) {
        Hashtable tbl = new Hashtable();

        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            BBNNode n = (BBNNode) i.next();
            if (n.isEvidence())
                tbl.put(n.getLabel(), n.getEvidenceValue().toString());
        }
        try {
            if (tbl.size() > 0)
                EvidenceParser.save(new FileOutputStream(filename), tbl);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    /**
     * Perform a deep clone on the graph. (Expensive operation)
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        BBNGraph graph = new BBNGraph();
        graph.name = name;

        Hashtable nodeTable = new Hashtable();
        Set nodes = getNodes();
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            BBNNode newNode = (BBNNode) node.clone();
            nodeTable.put(node.getLabel(), newNode);
            graph.addNode(newNode);
        }

        Set edges = getEdges();
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge e = (Edge) i.next();
            BBNNode src = (BBNNode) e.getSource();
            BBNNode dest = (BBNNode) e.getDestination();
            src = (BBNNode) nodeTable.get(src.getLabel());
            dest = (BBNNode) nodeTable.get(dest.getLabel());
            assert (src != null && dest != null);
            graph.addEdge(src, dest);
        }

        return graph;
    }

    /**
     * isModified keeps track of any changes that may have been made
     * to this graph.
     *
     * @author jplummer
     */
    public boolean isModified() {
        // TODO
        return false;
    }
}
