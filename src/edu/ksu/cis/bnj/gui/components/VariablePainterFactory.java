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

import edu.ksu.cis.bnj.bbn.BBNNode;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.drawing.Painter;
import salvo.jesus.graph.visual.drawing.VisualVertexPainterFactory;

/**
 * @author Roby Joehanes
 */
public class VariablePainterFactory extends VisualVertexPainterFactory {

    /**
     *
     */
    public VariablePainterFactory() {
        super();
    }

    /**
     * @see salvo.jesus.graph.visual.drawing.PainterFactory#getPainter(salvo.jesus.graph.visual.VisualGraphComponent)
     */
    public Painter getPainter(VisualGraphComponent comp) {
        BBNNode node = (BBNNode) ((VisualVertex) comp).getVertex();

        // We can't use one painter for all nodes of the same type because we'd like to
        // be able to highlight some of them when selected
        if (node.isDecision()) {
            return new DecisionVariablePainter();
        } else if (node.isUtility()) {
            return new UtilityVariablePainter();
        } else {
            return new ChanceVariablePainter();
        }
    }

}
