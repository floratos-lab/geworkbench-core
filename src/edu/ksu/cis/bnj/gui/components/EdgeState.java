package edu.ksu.cis.bnj.gui.components;

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

import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import salvo.jesus.graph.visual.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;


/**
 * State object that represents the edge mode in a GraphPanel.
 * Edge mode being the ability to add an edge interactively by dragging
 * the mouse from a vertex and releasing the mouse on another vertex.
 *
 * @author Jesus M. Salvo Jr.
 *         <p/>
 *         With a little modification here and there to eliminate stack trace errors.
 *         And also hooks here and there -- Roby Joehanes
 */
public class EdgeState extends GraphPanelEdgeState {
    /**
     * Reference to the VisualVertex object selected during the mousePressed() method.
     * This identifies the source vertex of an edge being created.
     */
    protected VisualVertex sourcevertex;

    /**
     * Line2D object that is drawn when an edge is being interactively created.
     */
    protected Line2D.Double probableedgeline;

    /**
     * A cross-hair Cursor object
     */
    protected Cursor edgecursor;

    /**
     * Existing cursor prior to changing the cursor to a cross-hair
     */
    protected Cursor previouscursor;

    protected GraphPanel gpanel;
    protected NodeManager owner;
    protected static BasicStroke marquee = GUIUtil.createMarquee(new float[]{2.0f, 2.0f});

    /**
     * Creates a GraphPanelEdgeState object for the specified GraphPanel object.
     */
    public EdgeState(NodeManager owner, GraphPanel gpanel) {
        super(gpanel);
        this.gpanel = gpanel;
        this.owner = owner;
        this.edgecursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    }

    /**
     * Identifies the source vertex of a new Edge being created.
     */
    public GraphPanelState mousePressed(MouseEvent e) {
        VisualVertex vVertex;
        VisualGraphComponent component;
        VisualEdge vEdge;
        VisualGraph vGraph = gpanel.getVisualGraph();
        if (GUIUtil.isLeftMouseButton(e)) {
            this.sourcevertex = gpanel.getVisualGraph().getNode(e.getX(), e.getY());
        } else if (GUIUtil.isRightMouseButton(e)) {
            int x = e.getX(), y = e.getY();
            owner.properties(gpanel, x, y);
        }
        informTargetVisualGraphComponentOfMouseEvent(e);
        return this;

    }

    /**
     * Signifies the end of a drag. If there was a vertex clicked during
     * the start of the drag (during the mousePressed() event) and there is
     * a vertex at the end of the drag, then an edge is added to the graph,
     * and the mouse cursor is returned to its original.
     */
    public GraphPanelState mouseReleased(MouseEvent e) {
        // Edge mode. If there was a vertex clicked during the mousePressed() event
        // and there is a vertex at this (mouseReleased()) event, then add an
        // edge to the graph
        VisualVertex sinkvertex = null;

        this.probableedgeline = null;
        if (this.sourcevertex != null) {
            sinkvertex = gpanel.getVisualGraph().getNode(e.getX(), e.getY());
            if (sinkvertex != null)
                try {
                    gpanel.getVisualGraph().addEdge(this.sourcevertex, sinkvertex);
                } catch (Exception ex) {
                    // Roby Joehanes's patch
                    DialogFactory.getOKDialog(null, DialogFactory.ERROR, "Error!", "Cannot add an edge! It may cause a loop.");
                    //ex.printStackTrace();
                }
            this.sourcevertex = null;
        }
        if (this.previouscursor != null) {
            this.gpanel.setCursor(this.previouscursor);
            this.previouscursor = null;
        }
        gpanel.repaint();

        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);

        return this;
    }

    /**
     * If there was a vertex clicked during the start of the drag
     * (during the mousePressed() event), draw a line from the
     * source vertex to the current coordinate.
     */
    public GraphPanelState mouseDragged(MouseEvent e) {
        // Edge mode. Draw a line between the vertex that was clicked
        // on the mousePressed() event and the current coordinate of the mouse.
        if (this.sourcevertex != null) {
            if (probableedgeline == null)
                probableedgeline = new Line2D.Double();
            probableedgeline.setLine(this.sourcevertex.getBounds().getCenterX(), this.sourcevertex.getBounds().getCenterY(), (double) e.getX(), (double) e.getY());

            gpanel.repaint();
        }

        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);

        return this;
    }

    /**
     * This method sets the cursor to a crosshair whenever the cursor
     * enters a VisualVertex object. The cursor is reset to its original
     * when the mouse cursor leaves a VisualVertex object.
     */
    public GraphPanelState mouseMoved(MouseEvent e) {
        VisualGraphComponent vVertex = this.gpanel.getVisualGraph().getNode(e.getX(), e.getY());
        VisualGraphComponent vEdge = this.gpanel.getVisualGraph().getVisualEdge(e.getX(), e.getY());
        VisualGraphComponent component;

        if (vVertex != null) {
            if (this.previouscursor == null)
                this.previouscursor = this.gpanel.getCursor();
            this.gpanel.setCursor(this.edgecursor);
        } else if (this.previouscursor != null) {
            this.gpanel.setCursor(this.previouscursor);
            this.previouscursor = null;
        }

        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);

        return this;
    }

    /**
     * Call VisualGraph.paint() method, passing the Graphics2D context
     * and the probable edge line to be drawn
     */
    public void paint(Graphics2D g2d) {
        this.gpanel.getVisualGraph().paint(g2d);
        Stroke originalstroke;
        if (probableedgeline != null) {
            originalstroke = g2d.getStroke();
            g2d.setStroke(marquee);
            g2d.draw(probableedgeline);
            g2d.setStroke(originalstroke);
        }
    }

}

