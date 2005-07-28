/*
 * Created on Feb 26, 2003
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
package edu.ksu.cis.bnj.bbn.inference.ls;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.kdd.util.graph.Edge;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Roby Joehanes
 */
public class CliqueTree extends BBNGraph {

    protected BBNGraph owner;

    public CliqueTree(BBNGraph g) {
        owner = g;
        Hashtable nodeCache = new Hashtable();
        edu.ksu.cis.kdd.util.graph.CliqueTree tree = new edu.ksu.cis.kdd.util.graph.CliqueTree(g);  // most unfortunate naming...
        Set evNodes = g.getEvidenceNodes();

        // Add the nodes
        for (Iterator i = tree.getOrderedCliques().iterator(); i.hasNext();) {
            edu.ksu.cis.kdd.util.graph.Clique clique = (edu.ksu.cis.kdd.util.graph.Clique) i.next();

            Clique clq = new Clique(clique);
            addNode(clq);
            clq.filterEvidenceNodes(evNodes);
            nodeCache.put(clique.toString(), clq);
        }

        // Add the edges
        for (Iterator i = tree.getEdges().iterator(); i.hasNext();) {
            Edge edge = (Edge) i.next();
            Clique src = (Clique) nodeCache.get(edge.getSource().toString());
            Clique dest = (Clique) nodeCache.get(edge.getDestination().toString());
            addEdge(src, dest);
        }

    }
}
