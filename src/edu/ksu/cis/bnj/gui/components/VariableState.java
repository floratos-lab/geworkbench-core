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

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import salvo.jesus.graph.visual.*;

import java.awt.event.MouseEvent;
import java.util.HashSet;

/**
 * @author Roby Joehanes
 */
public abstract class VariableState extends GraphPanelVertexState {

    protected GraphPanel gpanel;
    protected VisualGraph vGraph;
    protected NodeManager owner;
    protected int counter = 0;

    /**
     * @param panel
     */
    public VariableState(NodeManager owner, GraphPanel panel) {
        super(panel);
        gpanel = panel;
        this.owner = owner;
        vGraph = gpanel.getVisualGraph();
    }

    /**
     * Creates a new vertex on the specified coordinate.
     */
    public GraphPanelState mousePressed(MouseEvent e) {
        VisualVertex vVertex;
        VisualEdge vEdge;
        if (GUIUtil.isLeftMouseButton(e)) {
            // Create a new vertex and set its location
            // to the coordinates of the mouse
            BBNGraph graph = (BBNGraph) vGraph.getGraph();
            BBNNode newNode = createNode(graph);
            graph.add(newNode);

            vVertex = vGraph.getVisualVertex(newNode);
            setSelectedNodes(vVertex);
    
            // Notify the VisualGraphComponent of the event
            // Do this before adding the new vertex onto the graph
            informTargetVisualGraphComponentOfMouseEvent(e);
    
            // Set the location of the visual representation of the vertex
            // to the coordinates of the mouse
            vVertex.setLocation(e.getX(), e.getY());
        } else if (GUIUtil.isRightMouseButton(e)) {
            int x = e.getX(), y = e.getY();
            owner.properties(gpanel, x, y);
        }
        return this;
    }

    protected void setSelectedNodes(VisualVertex vVertex) {
        if (owner != null) {
            HashSet selectedNodes = new HashSet();
            selectedNodes.add(vVertex);
            owner.setSelectedNodes(selectedNodes);
        }
    }

    public void resetCounter() {
        counter = 0;
    }

    protected abstract BBNNode createNode(BBNGraph graph);
}
