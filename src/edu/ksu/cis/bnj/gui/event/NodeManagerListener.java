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


/**
 * @author Roby Joehanes
 */
public interface NodeManagerListener {
    public void selectedNodesChanged(NodeSelectionEvent event);

    public void selectedNodesDeleted(NodeDeletedEvent event);

    public void nodePropertyChanged(NodePropertiesEvent event);

    public void graphChanged(GraphChangedEvent event);

    // no need for nodeRemoved because the only nodes we can remove is the ones get selected
    public void nodeAdded(NodeAddedEvent event);

    public void edgeAdded(EdgeAddedEvent event);

    public void edgeRemoved(EdgeDeletedEvent event);
}
