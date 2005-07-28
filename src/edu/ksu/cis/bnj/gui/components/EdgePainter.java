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

import edu.ksu.cis.kdd.util.Settings;
import salvo.jesus.graph.visual.VisualEdge;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.drawing.VisualDirectedEdgePainterImpl;

import java.awt.*;
import java.util.Hashtable;

/**
 * Custom Edge Painter
 *
 * @author Roby Joehanes
 */
public class EdgePainter extends VisualDirectedEdgePainterImpl {

    private static Hashtable settings = Settings.getWindowSettings("MAIN"); //$NON-NLS-1$
    protected Color edgeColor = (Color) settings.get("EdgeColor"); //$NON-NLS-1$

    /**
     * Constructor for BNJEdgePainter.
     */
    public EdgePainter() {
        super();
    }

    /**
     * @see salvo.jesus.graph.visual.drawing.VisualEdgePainter#paintText(java.awt.Graphics2D, java.awt.Font, java.awt.Color, java.lang.String, float, float)
     */
    public void paintText(Graphics2D arg0, Font arg1, Color arg2, String arg3, float arg4, float arg5) {
        // Do not paint the text, so leave this method empty!
    }

    /**
     * @see salvo.jesus.graph.visual.drawing.Painter#paint(salvo.jesus.graph.visual.VisualGraphComponent, java.awt.Graphics2D)
     */
    public void paint(VisualGraphComponent vgc, Graphics2D g2d) {
        VisualEdge vEdge = (VisualEdge) vgc;
        vEdge.setOutlinecolor(edgeColor);
        vEdge.setFillcolor(edgeColor);
        super.paint(vgc, g2d);
    }

}
