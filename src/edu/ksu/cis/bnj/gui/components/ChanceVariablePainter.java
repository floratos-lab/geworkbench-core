package edu.ksu.cis.bnj.gui.components;

/*
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

import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.VisualVertex;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Random Variable Vertex Painter
 *
 * @author Roby Joehanes
 */
public class ChanceVariablePainter extends VariablePainter {

    /**
     * Constructor for VertexPainter.
     */
    public ChanceVariablePainter() {
        super();
    }

    /**
     * @see salvo.jesus.graph.visual.drawing.Painter#paint(salvo.jesus.graph.visual.VisualGraphComponent, java.awt.Graphics2D)
     */
    public void paint(VisualGraphComponent vgc, Graphics2D g2d) {
        VisualVertex vVertex = (VisualVertex) vgc;
        Rectangle bounds = vVertex.getBounds();
        super.paint(vVertex, g2d, new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
    }

}
