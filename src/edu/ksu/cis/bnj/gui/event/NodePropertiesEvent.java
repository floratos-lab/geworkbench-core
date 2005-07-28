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

package edu.ksu.cis.bnj.gui.event;

import salvo.jesus.graph.visual.VisualVertex;

import java.util.EventObject;

/**
 * Event for node properties change
 *
 * @author Roby Joehanes
 */
public class NodePropertiesEvent extends EventObject {

    public static final int NAME_CHANGED = 0;
    public static final int LABEL_CHANGED = 1;
    public static final int STATES_CHANGED = 2;
    public static final int EVIDENCE_CHANGED = 3;
    public static final int CPF_CHANGED = 4;

    protected int type;
    protected VisualVertex node;

    /**
     * @param source
     */
    public NodePropertiesEvent(Object source, int type, VisualVertex v) {
        super(source);
        this.type = type;
        node = v;
    }

    public int getType() {
        return type;
    }

    public VisualVertex getNode() {
        return node;
    }
}
