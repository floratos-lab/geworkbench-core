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

import edu.ksu.cis.kdd.util.gui.GUIUtil;
import salvo.jesus.graph.visual.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * State object that represents the normal mode in a GraphPanel.
 * Normal mode being 1) drag vertex and right click on VisualGraphComponents
 * to cause a popup menu to be displayed.
 *
 * @author Jesus M. Salvo Jr.
 *         <p/>
 *         With some adjustments and adding selection box for multiple item selection -- Roby Joehanes
 */
public class NormalState extends GraphPanelNormalState {
    /**
     * VisualVertex object selected during the mousePressed() method
     */
    protected VisualVertex targetvertex;

    /**
     * VisualEdge object selected during the mousePressed() method
     */
    protected VisualEdge targetedge;

    /**
     * Existing cursor prior to changing the cursor.
     */
    protected Cursor previouscursor;

    /**
     * A Cursor object of type Cursor.MOVE_CURSOR
     */
    protected Cursor movecursor;

    /**
     * Stores the previous x-coordinate of targetedvertex when dragging
     * a VisualVertex object.
     */
    protected int previous_x;

    /**
     * Stores the previous y-coordinate of targetedvertex when dragging
     * a VisualVertex object.
     */
    protected int previous_y;

    protected GraphPanel gpanel = null;
    protected NodeManager owner;

    protected static BasicStroke marquee = GUIUtil.getDefaultMarquee();
    protected Rectangle selectionBox;

    /**
     * Creates a GraphPanelNormalState object for the specified GraphPanel object.
     */
    public NormalState(NodeManager owner, GraphPanel gpanel) {
        super(gpanel);
        this.gpanel = gpanel;
        this.owner = owner;
        this.movecursor = new Cursor(Cursor.MOVE_CURSOR);
    }

    /**
     * If there is a VisualGraphComponent at the coordinate specified
     * in the MouseEvent e, then a JPopupMenu will be shown
     * by calling the popup() method whose context is the selected
     * VisualGraphComponent.
     */
    public GraphPanelState mousePressed(MouseEvent e) {
        VisualVertex vVertex;
        VisualGraphComponent component;
        VisualEdge vEdge;
        VisualGraph vGraph = gpanel.getVisualGraph();
        int x = e.getX(), y = e.getY();
        this.previous_x = x;
        this.previous_y = y;

        if (GUIUtil.isLeftMouseButton(e)) {
            this.targetvertex = gpanel.getVisualGraph().getNode(this.previous_x, this.previous_y);
            this.targetedge = gpanel.getVisualGraph().getVisualEdge(this.previous_x, this.previous_y);
            if (owner != null) {
                if (targetvertex == null) {
                    if (!e.isControlDown()) owner.unselectNodes();
                } else {
                    Set selectedNodes = owner.getSelectedNodes();
                    if (selectedNodes == null || (!selectedNodes.contains(targetvertex) && !e.isControlDown())) {
                        selectedNodes = new HashSet();
                        selectedNodes.add(targetvertex);
                        owner.setSelectedNodes(selectedNodes);
                    }
                }
            }
        } else if (GUIUtil.isRightMouseButton(e)) {
            owner.properties(gpanel, x, y);
        }
        informTargetVisualGraphComponentOfMouseEvent(e);
        return this;
    }

    /**
     * Shows a popup menu if there was a VisualGraphComponent during the
     * mousePressed() event.
     */
    public GraphPanelState mouseReleased(MouseEvent e) {
        // Different platforms return true for isPopupTrigger() on different events.
        // Some return true on mousePressed(), while other on mouseReleased().
        // Therefore, adaptee.vertexlicked or adaptee.edgeclicked may have been set on mousePressed(),
        // but the popup menu may not appear until mouseReleased().
        VisualGraphComponent component;

        if (selectionBox != null && owner != null) {
            // We have something selected
            VisualGraph vGraph = gpanel.getVisualGraph();
            List vertices = vGraph.getVisualVertices();
            if (vertices != null) {
                HashSet selectedItems = new HashSet();
                //System.out.println(selectionBox);
                for (Iterator i = vertices.iterator(); i.hasNext();) {
                    VisualVertex vVertex = (VisualVertex) i.next();
                    Rectangle bounds = vVertex.getBounds();
                    //System.out.println(vVertex+" := "+bounds);
                    if (selectionBox.contains(bounds)) {
                        selectedItems.add(vVertex);
                    }
                }
                if (e.isControlDown()) { // If control button is pressed, it means add the selection
                    Set oldSelected = owner.getSelectedNodes();
                    if (oldSelected != null) selectedItems.addAll(oldSelected);
                }
                owner.setSelectedNodes(selectedItems);
                //System.out.println();
            }
        } else if (targetvertex != null && owner != null && GUIUtil.isLeftMouseButton(e)) {
            Set selectedNodes = owner.getSelectedNodes();
            // Update the selected nodes only if the targetvertex is not already selected
            if (selectedNodes == null || !selectedNodes.contains(targetvertex)) {
                selectedNodes = new HashSet();
                selectedNodes.add(targetvertex);
                owner.setSelectedNodes(selectedNodes);
            }
        }

        selectionBox = null;
        gpanel.repaint();
        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);

        // Do not forget to remove any reference to the vertex and edge that was clicked
        // during the mousePressed.
        this.targetvertex = null;
        this.targetedge = null;
        this.previous_x = e.getX();
        this.previous_y = e.getY();
        return this;
    }

    /**
     * If there was a VisualVertex object selected during the mousePressed() method,
     * then drag the VisualVertex object to the new location specfied by the
     * MouseEvent e.
     */
    public GraphPanelState mouseDragged(MouseEvent e) {
        if (GUIUtil.isLeftMouseButton(e)) {
            int x = e.getX(), y = e.getY();
            if (this.targetvertex != null) {
                this.dragVertex(x, y);
            } else {
                if (selectionBox == null)
                    selectionBox = new Rectangle();
                int x1, y1, x2, y2;
                if (x < previous_x) {
                    x1 = x;
                    x2 = previous_x - x;
                } else {
                    x1 = previous_x;
                    x2 = x - previous_x;
                }
                if (y < previous_y) {
                    y1 = y;
                    y2 = previous_y - y;
                } else {
                    y1 = previous_y;
                    y2 = y - previous_y;
                }
                selectionBox.setRect(x1, y1, x2, y2);
                gpanel.repaint();
            }
        }
        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);
        return this;
    }

    public void paint(Graphics2D g2d) {
        super.paint(g2d);
        Stroke originalstroke;

        if (selectionBox != null) {
            originalstroke = g2d.getStroke();
            g2d.setStroke(marquee);
            g2d.draw(selectionBox);
            g2d.setStroke(originalstroke);
        }
    }

    /**
     * This method is automatically called by the mouseDragged() method
     * if there was a VisualVertex selected during the mousePressed() method,
     * to drag the selected VisualVertex object to the specifid coordinate.
     *
     * @param x New x coordinate
     * @param y New y coordinate
     */
    private void dragVertex(int x, int y) {
        Rectangle vertexrect;
        int deltax, deltay;

        vertexrect = this.targetvertex.getBounds();

        // Do not allow coordinates to be negative, as there is no way
        // to adjust the scrollbars of JScrollPane to set the viewport to negative
        // by default.
        deltax = vertexrect.x + x - this.previous_x < 0 ? -vertexrect.x : x - this.previous_x;
        deltay = vertexrect.y + y - this.previous_y < 0 ? -vertexrect.y : y - this.previous_y;

        // Now drag the vertex to its new location.
        if (owner != null) {
            Set selectedNodes = owner.getSelectedNodes();
            if (selectedNodes != null && selectedNodes.size() > 0) {
                for (Iterator i = selectedNodes.iterator(); i.hasNext();) {
                    VisualVertex vVertex = (VisualVertex) i.next();
                    vVertex.setLocationDelta(deltax, deltay);
                }
            } else {
                this.targetvertex.setLocationDelta(deltax, deltay);
            }
        } else {
            this.targetvertex.setLocationDelta(deltax, deltay);
        }

        // Make the current coordinate the "previous" coordinate for
        // the next mouseDragged event
        this.previous_x = this.previous_x + deltax;
        this.previous_y = this.previous_y + deltay;

        gpanel.getVisualGraph().repaint();
    }

    /**
     * Creates and shows a JPopMenu object, whose context is the
     * VisualGraphComponent selected during the mousePressed() event.
     *
     * @param x The x-coordinate where the popup menu will be shown.
     * @param y The y-coordinate where the popup menu will be shown.
     */
    private void popup(int x, int y) {
        JPopupMenu popup;
        JMenuItem propertiesmenuitem;
        JMenuItem deletemenuitem;

        // Show a popup menu for a vertex
        if (this.targetvertex != null) {
            popup = new JPopupMenu();
            propertiesmenuitem = new JMenuItem("Vertex Properties...");
            deletemenuitem = new JMenuItem("Delete Vertex");

            popup.add(propertiesmenuitem);
            popup.addSeparator();
            popup.add(deletemenuitem);

            // Specify what action to take when the Properties menu items is selected
            propertiesmenuitem.addActionListener(new ActionListener() {
                // We must keep a reference to object referenced by targetvertex,
                // because after the popup() method is called, targertvertex is set to null
                // and there will no way for the dialog to know which context is it
                // displaying information for.
                VisualVertex selectedvertex = targetvertex;

                public void actionPerformed(ActionEvent actionevent) {
                    // new VisualGraphComponentPropertiesDialog( gpanel, this.selectedvertex );
                    gpanel.repaint();
                }
            });

            // For the Delete menu item, delete the vertex from the graph
            deletemenuitem.addActionListener(new ActionListener() {
                // We must keep a reference to object referenced by targetvertex,
                // because after the popup() method is called, targertvertex is set to null
                // and there will no way for us to know which vertex to remove.
                VisualVertex nodetoremove = targetvertex;

                public void actionPerformed(ActionEvent actionevent) {
                    if (nodetoremove != null) {
                        try {
                            gpanel.getVisualGraph().remove(this.nodetoremove);
                            gpanel.repaint();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return;
                        }
                    }
                }
            });

            popup.show(this.gpanel, x, y);
        }

        // Show a popup menu for an edge
        else if (this.targetedge != null) {
            popup = new JPopupMenu();
            propertiesmenuitem = new JMenuItem("Edge Properties...");
            deletemenuitem = new JMenuItem("Delete Edge");

            popup.add(propertiesmenuitem);
            popup.addSeparator();
            popup.add(deletemenuitem);

            // Specify what action to take when the Properties menu items is selected
            propertiesmenuitem.addActionListener(new ActionListener() {
                // We must keep a reference to object referenced by targetedge,
                // because after the popup() method is called, targertedge is set to null
                // and there will no way for the dialog to know which context is it
                // displaying information for.
                VisualEdge selectededge = targetedge;

                public void actionPerformed(ActionEvent actionevent) {
                    //new VisualGraphComponentPropertiesDialog( gpanel, selectededge );
                    gpanel.repaint();
                }
            });

            // For the Delete menu item, delete the vertex from the graph
            deletemenuitem.addActionListener(new ActionListener() {
                // We must keep a reference to object referenced by targetedge,
                // because after the popup() method is called, targertedge is set to null
                // and there will no way for us to know which edge to delete.
                VisualEdge edgetoremove = targetedge;

                public void actionPerformed(ActionEvent actionevent) {
                    if (edgetoremove != null) {
                        try {
                            gpanel.getVisualGraph().removeEdge(edgetoremove);
                            gpanel.repaint();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return;
                        }
                    }
                }
            });
            popup.show(this.gpanel, x, y);
        }
    }

    /**
     * This method sets the cursor to a MOVE_CURSOR whenever the cursor
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
            this.gpanel.setCursor(this.movecursor);
        } else {
            this.gpanel.setCursor(this.previouscursor);
            this.previouscursor = null;
        }

        // Notify the VisualGraphComponent of the event
        informTargetVisualGraphComponentOfMouseEvent(e);

        return this;

    }
}
