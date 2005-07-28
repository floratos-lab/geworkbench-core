/*
 * Created on Oct 18, 2003
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

package edu.ksu.cis.bnj.gui.components;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.BBNValue;
import edu.ksu.cis.bnj.gui.event.*;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualVertex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class BBNTree extends JTree implements ActionListener, MouseListener, NodeManagerListener {

    protected DefaultTreeModel treeModel = null;
    protected DefaultMutableTreeNode rootModel = null;
    protected VisualGraph graph = null;
    protected BNJMainPanel owner = null;
    protected Hashtable nodeToModel = new Hashtable();
    protected Hashtable modelToNode = new Hashtable();
    protected NodeManager nodeManager;
    protected boolean ignoreEvent = false;
    protected boolean graphEditable = true;

    public BBNTree(NodeManager nm) {
        this(null, nm);
    }

    public BBNTree(BNJMainPanel owner, NodeManager nm) {
        super();
        this.owner = owner;
        nodeManager = nm;
        nm.addListener(this);
        init();
    }

    protected void init() {
        treeModel = getNewModel();
        rootModel = (DefaultMutableTreeNode) treeModel.getRoot();
        setModel(treeModel);
        putClientProperty("JTree.lineStyle", "Angled"); // $NON-NLS-1$ // $NON-NLS-2$
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        Insets inset = new Insets(0, 5, 0, 0);
        setBorder(new EmptyBorder(inset));
        BBNTreeRenderer renderer = new BBNTreeRenderer();
        setCellRenderer(renderer);
        addMouseListener(this);
    }

    protected DefaultTreeModel getNewModel() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Untitled");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        return treeModel;
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void nodeAdded(NodeAddedEvent event) {
        addNodeToTree(event.getNode());
        sortNodes();
        treeModel.nodeStructureChanged(rootModel);
    }

    public void nodeRemoved(VisualVertex node) {
        DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) nodeToModel.get(node);
        nodeToModel.remove(node);
        modelToNode.remove(nodeModel);
        treeModel.removeNodeFromParent(nodeModel);
    }

    protected void sortNodes() {
        TreeSet nodeNames = new TreeSet(); // Auto-sorting set. Really handy.
        Hashtable nodeNameToModel = new Hashtable();
        int childNum = treeModel.getChildCount(rootModel);
        for (int i = 0; i < childNum; i++) {
            DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) treeModel.getChild(rootModel, i);
            String name = nodeModel.toString();
            nodeNames.add(name);
            nodeNameToModel.put(name, nodeModel);
        }
        rootModel.removeAllChildren();
        for (Iterator i = nodeNames.iterator(); i.hasNext();) {
            String nodeName = (String) i.next();
            DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) nodeNameToModel.get(nodeName);
            rootModel.add(nodeModel);
        }
    }

    protected void populateTree() {
        BBNGraph bbnGraph = (BBNGraph) graph.getGraph();
        String graphName = bbnGraph.getName();
        nodeToModel = new Hashtable();
        modelToNode = new Hashtable();
        if (graphName == null || graphName.length() == 0) graphName = "Untitled";
        rootModel = new DefaultMutableTreeNode(graphName);
        treeModel = new DefaultTreeModel(rootModel);
        List nodes = graph.getVisualVertices();

        // We do this in two pass because we want the tree display sorted.
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            VisualVertex node = (VisualVertex) i.next();
            addNodeToTree(node);
        }

        sortNodes();
        setModel(treeModel);
    }

    public void expandAll() {
        expandPath(new TreePath(rootModel));
        Set keyset = modelToNode.keySet();
        if (keyset != null) {
            for (Iterator i = keyset.iterator(); i.hasNext();) {
                DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) i.next();
                expandPath(new TreePath(nodeModel.getPath()));
            }
        }
    }

    public void collapseAll() {
        Set keyset = modelToNode.keySet();
        if (keyset != null) {
            for (Iterator i = keyset.iterator(); i.hasNext();) {
                DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) i.next();
                collapsePath(new TreePath(nodeModel.getPath()));
            }
        }
        collapsePath(new TreePath(rootModel));
    }

    protected void addNodeToTree(VisualVertex vVertex) {
        BBNNode node = (BBNNode) vVertex.getVertex();
        String nodeName = node.getLabel();
        DefaultMutableTreeNode nodeModel = new DefaultMutableTreeNode(vVertex);
        BBNValue values = node.getValues();
        nodeToModel.put(vVertex, nodeModel);
        modelToNode.put(nodeModel, vVertex);
        if (values instanceof BBNDiscreteValue) {
            BBNDiscreteValue dval = (BBNDiscreteValue) values;
            for (Iterator j = dval.iterator(); j.hasNext();) {
                Object value = j.next();
                DefaultMutableTreeNode valueModel = new DefaultMutableTreeNode(value); // Value should be left as it is... User must implement toString to display properly
                nodeModel.add(valueModel);
            }
        }
        //treeModel.insertNodeInto(nodeModel, rootNode, rootNode.getChildCount());
        rootModel.add(nodeModel);
    }

    public void mouseClicked(MouseEvent e) {
        if (!graphEditable) return;
        TreePath[] paths = getSelectionPaths();
        if (paths == null) {
            nodeManager.unselectNodes();
            return;
        }
        int maxPaths = paths.length;
        Set selectedNodes = new HashSet();
        boolean isDoubleClick = e.getClickCount() > 1;
        boolean needRepaint = false;
        for (int i = 0; i < maxPaths; i++) {
            Object[] pathObjects = paths[i].getPath();
            int pathLength = pathObjects.length;
            if (pathObjects == null || pathLength < 2) continue; // root or nothing selected, then bail out
            DefaultMutableTreeNode selectedModel = (DefaultMutableTreeNode) pathObjects[1];
            VisualVertex node = (VisualVertex) modelToNode.get(selectedModel);
            selectedNodes.add(node);
            if (isDoubleClick && pathLength == 3) {
                BBNNode bbnNode = (BBNNode) node.getVertex();
                Object evidenceValue = bbnNode.getEvidenceValue();
                Object selectedEvidence = ((DefaultMutableTreeNode) pathObjects[2]).getUserObject();
                if (evidenceValue == null || !evidenceValue.equals(selectedEvidence)) {
                    //System.out.println("Set evidence to "+selectedEvidence);
                    bbnNode.setEvidenceValue(selectedEvidence);
                } else {
                    //System.out.println("Set evidence to null");
                    bbnNode.setEvidenceValue(null); // Toggle it
                }
                needRepaint = true;
                nodeManager.fireNodePropertiesEvent(new NodePropertiesEvent(this, NodePropertiesEvent.EVIDENCE_CHANGED, node));
            }
        }
        if (needRepaint) {
            revalidate();
            repaint();
        }
        if (maxPaths > 0) {
            ignoreEvent = true;
            nodeManager.setSelectedNodes(selectedNodes);
            ignoreEvent = false;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void selectedNodesChanged(NodeSelectionEvent event) {
        if (ignoreEvent) return;  // a hack to avoid double-feedback
        Set newSelectedNodes = event.getSelection();
        TreeSelectionModel selection = getSelectionModel();
        selection.clearSelection();
        if (newSelectedNodes == null) return;
        for (Iterator i = newSelectedNodes.iterator(); i.hasNext();) {
            VisualVertex vVertex = (VisualVertex) i.next();
            DefaultMutableTreeNode nodeModel = (DefaultMutableTreeNode) nodeToModel.get(vVertex);
            selection.addSelectionPath(new TreePath(nodeModel.getPath()));
        }
    }

    public void selectedNodesDeleted(NodeDeletedEvent event) {
        Set deletedNodes = event.getDeletedNodes();
        for (Iterator i = deletedNodes.iterator(); i.hasNext();) {
            VisualVertex vVertex = (VisualVertex) i.next();
            nodeRemoved(vVertex);
        }
    }

    public void nodePropertyChanged(NodePropertiesEvent evt) {
        if (evt.getSource() == this) return;
        switch (evt.getType()) {
            case NodePropertiesEvent.LABEL_CHANGED:
            case NodePropertiesEvent.EVIDENCE_CHANGED:
                revalidate();
                repaint();
                break;
            case NodePropertiesEvent.STATES_CHANGED:
                VisualVertex vertex = evt.getNode();
                DefaultMutableTreeNode model = (DefaultMutableTreeNode) nodeToModel.get(vertex);
                if (model != null) {
                    model.removeAllChildren();
                    BBNDiscreteValue dval = (BBNDiscreteValue) ((BBNNode) vertex.getVertex()).getValues();
                    if (dval != null) {
                        for (Iterator i = dval.iterator(); i.hasNext();) {
                            Object value = i.next();
                            DefaultMutableTreeNode valueModel = new DefaultMutableTreeNode(value); // Value should be left as it is... User must implement toString to display properly
                            model.add(valueModel);
                        }
                    }
                    treeModel.nodeStructureChanged(model);
                }
                break;
        }
    }

    public void graphChanged(GraphChangedEvent event) {
        this.graph = event.getGraph();
        populateTree();
    }

    public void edgeAdded(EdgeAddedEvent event) {
    }

    public void edgeRemoved(EdgeDeletedEvent event) {
    }

    /**
     * Whether or not the user are allowed to edit the evidence of this tree (by default = allowed)
     *
     * @param b
     */
    public void setGraphEditable(boolean b) {
        graphEditable = b;
    }

    public boolean isGraphEditable() {
        return graphEditable;
    }
}
