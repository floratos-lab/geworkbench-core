/*
 * Created on Mar 5, 2003
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

import salvo.jesus.graph.visual.VisualEdge;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.layout.GraphLayoutManager;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Roby Joehanes
 */
public abstract class AbstractLayouter implements GraphLayoutManager {

    protected boolean initialized = false;

    protected VisualGraph vGraph;

    /**
     *
     */
    public AbstractLayouter(VisualGraph v) {
        vGraph = v;
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#addEdge(salvo.jesus.graph.visual.VisualEdge)
     */
    public void addEdge(VisualEdge edge) {
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#addVertex(salvo.jesus.graph.visual.VisualVertex)
     */
    public void addVertex(VisualVertex node) {

    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#drawLayout()
     */
    public void drawLayout() {
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#isInitialized()
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#removeEdge(salvo.jesus.graph.visual.VisualEdge)
     */
    public void removeEdge(VisualEdge edge) {
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#removeVertex(salvo.jesus.graph.visual.VisualVertex)
     */
    public void removeVertex(VisualVertex node) {
    }

    public void paintEdge(Graphics2D g2d, VisualEdge vEdge) {
        this.routeEdge(g2d, vEdge);
    }

    public void routeEdge(Graphics2D g2d, VisualEdge vEdge) {
        GeneralPath gPath = vEdge.getGeneralPath();

        g2d.setColor(vEdge.getOutlinecolor());
        Rectangle2D vaBounds = vEdge.getVisualVertexA().getBounds2D();
        Rectangle2D vbBounds = vEdge.getVisualVertexB().getBounds2D();

        Point2D.Float fromcenter = new Point2D.Float(new Double(vaBounds.getCenterX()).floatValue(), new Double(vaBounds.getCenterY()).floatValue());
        Point2D.Float tocenter = new Point2D.Float(new Double(vbBounds.getCenterX()).floatValue(), new Double(vbBounds.getCenterY()).floatValue());

        gPath.reset();
        gPath.moveTo((float) (fromcenter.x), (float) (fromcenter.y));
        gPath.lineTo((float) (tocenter.x), (float) (tocenter.y));
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#layout()
     */
    public abstract void layout();

}
