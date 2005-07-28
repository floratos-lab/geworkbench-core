/*
 * Created on Oct 20, 2003
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

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.gui.event.*;
import edu.ksu.cis.kdd.util.Settings;
import salvo.jesus.graph.*;
import salvo.jesus.graph.visual.VisualEdge;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualVertex;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class NodeManager implements GraphListener {

    protected VisualGraph vGraph;
    protected boolean modified = false;
    protected Set selectedNodes;
    protected List listeners = new ArrayList();
    protected Hashtable settings = Settings.getWindowSettings("MAIN"); // $NON-NLS-1$
    protected JPopupMenu propertiesPopup = null, nodeDeletePopup = null, edgeDeletePopup = null, normalPopup = null;
    protected VisualEdge selectedEdge = null;

    public NodeManager(VisualGraph g) {
        vGraph = g;
    }

    public void setGraph(BBNGraph g) {
        Graph oldGraph = vGraph.getGraph();
        if (oldGraph != null) oldGraph.removeListener(this);
        vGraph.setGraph(g); // let vGraph be the FIRST listener before us! This is so that we can invoke vGraph.getVisualVertex correctly
        g.addListener(this);
        modified = false;
        fireGraphChangedEvent(new GraphChangedEvent(this, vGraph));
    }

    public BBNGraph getGraph() {
        // Store the coordinates back.
        List vVertices = vGraph.getVisualVertices();
        if (vVertices != null) {
            for (Iterator i = vVertices.iterator(); i.hasNext();) {
                VisualVertex vVertex = (VisualVertex) i.next();
                BBNNode node = (BBNNode) vVertex.getVertex();
                Hashtable nodeProperty = node.getProperty();
                if (nodeProperty == null) {
                    nodeProperty = new Hashtable();
                    node.setProperty(nodeProperty);
                }
                Rectangle bounds = vVertex.getBounds();
                LinkedList positions = new LinkedList();
                positions.add(new Double(bounds.x));  // Add x and y
                positions.add(new Double(bounds.y));
                nodeProperty.put("position", positions); // $NON-NLS-1$
            }
        }
        BBNGraph graph = (BBNGraph) vGraph.getGraph();
        return graph;
    }

    public void fireNodeSelectionEvent(NodeSelectionEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.selectedNodesChanged(event);
        }
    }

    public void fireNodeDeletionEvent(NodeDeletedEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.selectedNodesDeleted(event);
        }
    }

    public void fireNodeDeletionEvent(EdgeDeletedEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.edgeRemoved(event);
        }
    }

    public void fireNodePropertiesEvent(NodePropertiesEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.nodePropertyChanged(event);
        }
    }

    public void fireGraphChangedEvent(GraphChangedEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.graphChanged(event);
        }
    }

    public void fireNodeAddedEvent(NodeAddedEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            NodeManagerListener l = (NodeManagerListener) i.next();
            l.nodeAdded(event);
        }
    }

    public void setSelectedNodes(Set nodeList) {
        if (selectedNodes != null) {
            for (Iterator i = selectedNodes.iterator(); i.hasNext();) {
                VisualVertex vVertex = (VisualVertex) i.next();
                ((VariablePainter) vVertex.getPainter()).setColorToNormal();
            }
        }
        selectedNodes = nodeList;
        if (nodeList != null) {
            for (Iterator i = selectedNodes.iterator(); i.hasNext();) {
                VisualVertex vVertex = (VisualVertex) i.next();
                ((VariablePainter) vVertex.getPainter()).setColorToHighlight();
            }
        }
        Set s = selectedNodes == null ? null : Collections.unmodifiableSet(selectedNodes);
        fireNodeSelectionEvent(new NodeSelectionEvent(this, s));
        vGraph.repaint();
    }

    public void setSelectedNode(VisualVertex vVertex) {
        Set newSet = new HashSet();
        newSet.add(vVertex);
        setSelectedNodes(newSet);
    }

    public void unselectNodes() {
        setSelectedNodes(null);
    }

    public Set getSelectedNodes() {
        return selectedNodes == null ? null : Collections.unmodifiableSet(selectedNodes);
    }

    public void deleteSelectedNodes() {
        if (selectedNodes != null) {
            Set oldSelectedNodes = selectedNodes;
            for (Iterator i = selectedNodes.iterator(); i.hasNext();) {
                VisualVertex vVertex = (VisualVertex) i.next();
                try {
                    vGraph.remove(vVertex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            selectedNodes = null;
            fireNodeDeletionEvent(new NodeDeletedEvent(this, oldSelectedNodes));
            vGraph.repaint();
        }
    }

    public void deleteSelectedEdge() throws Exception {
        vGraph.removeEdge(selectedEdge);
        // TODO: Fire deleted edges event
        selectedEdge = null;
    }

    public Object properties(Component c, int x, int y) {
        VisualVertex vVertex = vGraph.getNode(x, y);
        if (vVertex != null) {
            if (selectedNodes == null || !selectedNodes.contains(vVertex)) {
                setSelectedNode(vVertex);
            }
            nodeProperties(c, x, y);
        } else {
            VisualEdge vEdge = vGraph.getVisualEdge(x, y);
            if (vEdge != null)
                edgeProperties(c, vEdge, x, y);
            else
                getNormalPopup().show(c, x, y);
        }
        return null;
    }

    public Object nodeProperties(Component c, int x, int y) {
        JPopupMenu popup = null;
        if (selectedNodes == null || selectedNodes.size() == 0) {
            popup = getNormalPopup();
        } else if (selectedNodes.size() == 1) {
            popup = getNodePropertiesPopup();
        } else {
            popup = getNodeDeletePopup();
        }
        if (popup != null) popup.show(c, x, y);

        return null;
    }

    public Object edgeProperties(Component c, VisualEdge vEdge, int x, int y) {
        JPopupMenu popup = getEdgeDeletePopup();
        selectedEdge = vEdge;
        popup.show(c, x, y);
        return null;
    }

    public void addListener(NodeManagerListener l) {
        if (l != null) listeners.add(l);
    }

    protected JPopupMenu getNormalPopup() {
        if (normalPopup == null) {
            normalPopup = (JPopupMenu) settings.get("NormalPopup"); // $NON-NLS-1$
        }
        return normalPopup;
    }

    protected JPopupMenu getNodePropertiesPopup() {
        if (propertiesPopup == null) {
            propertiesPopup = (JPopupMenu) settings.get("NodeDeleteWithPropertiesPopup"); // $NON-NLS-1$
        }
        return propertiesPopup;
    }

    protected JPopupMenu getNodeDeletePopup() {
        if (nodeDeletePopup == null) {
            nodeDeletePopup = (JPopupMenu) settings.get("NodeDeletePopup"); // $NON-NLS-1$
        }
        return nodeDeletePopup;
    }

    protected JPopupMenu getEdgeDeletePopup() {
        if (edgeDeletePopup == null) {
            edgeDeletePopup = (JPopupMenu) settings.get("EdgeDeletePopup"); // $NON-NLS-1$
        }
        return edgeDeletePopup;
    }

    public void removeListener(NodeManagerListener l) {
        if (l != null) listeners.remove(l);
    }

    public void beforeVertexAdded(GraphAddVertexEvent event) throws Exception {
    }

    public void afterVertexAdded(GraphAddVertexEvent event) {
        modified = true;
        fireNodeAddedEvent(new NodeAddedEvent(this, vGraph.getVisualVertex(event.getVertex())));
    }

    public void beforeVertexRemoved(GraphRemoveVertexEvent event) throws Exception {
    }

    public void afterVertexRemoved(GraphRemoveVertexEvent event) {
        modified = true;
    }

    public void beforeEdgeAdded(GraphAddEdgeEvent event) throws Exception {
    }

    public void afterEdgeAdded(GraphAddEdgeEvent event) {
        modified = true;
    }

    public void beforeEdgeRemoved(GraphRemoveEdgeEvent event) throws Exception {
    }

    public void afterEdgeRemoved(GraphRemoveEdgeEvent event) {
        modified = true;
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public boolean isModified() {
        return modified;
    }

}
