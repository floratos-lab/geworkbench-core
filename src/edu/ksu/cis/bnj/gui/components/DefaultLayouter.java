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

import edu.ksu.cis.bnj.bbn.BBNNode;
import salvo.jesus.graph.visual.VisualGraph;
import salvo.jesus.graph.visual.VisualVertex;

import java.util.Hashtable;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class DefaultLayouter extends AbstractLayouter {

    /**
     *
     */
    public DefaultLayouter(VisualGraph v) {
        super(v);
    }

    /**
     * @see salvo.jesus.graph.visual.layout.GraphLayoutManager#layout()
     */
    public void layout() {
        List visualVertices;
        VisualVertex vVertex;
        BBNNode node;
        int i, size;

        initialized = true;
        visualVertices = this.vGraph.getVisualVertices();
        size = visualVertices.size();
        for (i = 0; i < size; i++) {
            vVertex = (VisualVertex) visualVertices.get(i);
            node = (BBNNode) vVertex.getVertex();
            Hashtable nodeProperty = node.getProperty();
            if (nodeProperty != null) {
                List l = (List) node.getProperty().get("position"); // $NON-NLS-1$
                try {
                    double x = ((Double) l.get(0)).doubleValue();
                    double y = ((Double) l.get(1)).doubleValue();
                    vVertex.setLocation(x, y);
                } catch (Exception e) {
                }
            }
        }
        this.vGraph.repaint();
    }
}
