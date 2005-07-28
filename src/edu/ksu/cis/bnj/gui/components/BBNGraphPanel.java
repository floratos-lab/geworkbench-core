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
import edu.ksu.cis.bnj.gui.event.*;
import salvo.jesus.graph.visual.ChangeStateEvent;
import salvo.jesus.graph.visual.GraphPanel;
import salvo.jesus.graph.visual.GraphScrollPane;
import salvo.jesus.graph.visual.VisualGraph;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roby Joehanes
 */
public class BBNGraphPanel extends GraphScrollPane implements NodeManagerListener {

    protected GraphPanel graphPanel;
    protected VisualGraph vGraph;
    protected VariablePainterFactory varPainterFactory = new VariablePainterFactory();
    protected EdgePainterFactory edgePainterFactory = new EdgePainterFactory();
    protected ChanceVariableState randomVarState;
    protected DecisionVariableState decisionVarState;
    protected UtilityVariableState utilityVarState;
    protected EdgeState edgeState;
    protected NormalState normalState;
    protected AutoLayouter autoLayouter;
    protected DefaultLayouter defaultLayouter;
    protected BNJMainPanel owner = null;
    protected NodeManager nodeManager;
    protected static Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

    public BBNGraphPanel() {
        super();
        init();
    }

    public BBNGraphPanel(BNJMainPanel owner) {
        super();
        this.owner = owner;
        init();
    }

    protected void init() {
        graphPanel = (GraphPanel) getViewport().getView();
        vGraph = super.getVisualGraph();
        nodeManager = new NodeManager(vGraph);
        nodeManager.addListener(this);
        vGraph.setVisualEdgePainterFactory(edgePainterFactory);
        vGraph.setVisualVertexPainterFactory(varPainterFactory);
        defaultLayouter = new DefaultLayouter(vGraph);
        autoLayouter = new AutoLayouter(vGraph);
        normalState = new NormalState(nodeManager, graphPanel);
        edgeState = new EdgeState(nodeManager, graphPanel);
        randomVarState = new ChanceVariableState(nodeManager, graphPanel);
        decisionVarState = new DecisionVariableState(nodeManager, graphPanel);
        utilityVarState = new UtilityVariableState(nodeManager, graphPanel);
        changeToNormalState();
        JScrollBar sb = getVerticalScrollBar();
        sb.setUnitIncrement(16); // Faster scroll, please
        sb.setBlockIncrement(16);
        sb = getHorizontalScrollBar();
        sb.setUnitIncrement(16); // Faster scroll, please
        sb.setBlockIncrement(16);
    }

    public void setGraph(BBNGraph g) {
        vGraph.setGraph(g);
        defaultLayout();
        randomVarState.resetCounter();
        decisionVarState.resetCounter();
        utilityVarState.resetCounter();
    }

    public BBNGraph getGraph() {
        return nodeManager.getGraph();
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void autoLayout() {
        Cursor prevCursor = owner == null ? null : owner.getCursor();
        if (owner != null) owner.setCursor(waitCursor);
        vGraph.setGraphLayoutManager(autoLayouter);
        vGraph.layout();
        if (owner != null) owner.setCursor(prevCursor);
    }

    public void defaultLayout() {
        vGraph.setGraphLayoutManager(defaultLayouter);
        vGraph.layout();
    }

    public void changeToNormalState() {
        processChangeStateEvent(new ChangeStateEvent(this, normalState));
    }

    public void changeToEdgeState() {
        processChangeStateEvent(new ChangeStateEvent(this, edgeState));
    }

    public void changeToChanceVarState() {
        processChangeStateEvent(new ChangeStateEvent(this, randomVarState));
    }

    public void changeToDecisionVarState() {
        processChangeStateEvent(new ChangeStateEvent(this, decisionVarState));
    }

    public void changeToUtilityVarState() {
        processChangeStateEvent(new ChangeStateEvent(this, utilityVarState));
    }

    public void setModified(boolean b) {
        nodeManager.setModified(b);
    }

    public boolean isModified() {
        return nodeManager.isModified();
    }

    public void selectedNodesChanged(NodeSelectionEvent event) {
    }

    public void selectedNodesDeleted(NodeDeletedEvent deletedNodes) {
    }

    public void nodePropertyChanged(NodePropertiesEvent evt) {
    }

    public void graphChanged(GraphChangedEvent event) {
        defaultLayout();
    }

    public void nodeAdded(NodeAddedEvent event) {
    }

    public void edgeAdded(EdgeAddedEvent event) {
    }

    public void edgeRemoved(EdgeDeletedEvent event) {
    }
}
