/*
 * Created on Oct 21, 2003
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
import edu.ksu.cis.kdd.util.Settings;
import salvo.jesus.graph.visual.VisualVertex;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Hashtable;

/**
 * @author Roby Joehanes
 */
public class BBNTreeRenderer extends DefaultTreeCellRenderer {
    protected Hashtable settings = Settings.getWindowSettings("MAIN"); // $NON-NLS-1$
    protected ImageIcon randomNodeIcon = (ImageIcon) settings.get("RandomTreeNode"); // $NON-NLS-1$
    protected ImageIcon decisionNodeIcon = (ImageIcon) settings.get("DecisionTreeNode"); // $NON-NLS-1$
    protected ImageIcon utilityNodeIcon = (ImageIcon) settings.get("UtilityTreeNode"); // $NON-NLS-1$
    protected ImageIcon evidenceNodeIcon = (ImageIcon) settings.get("EvidenceValueIcon"); // $NON-NLS-1$
    protected ImageIcon valueNodeIcon = (ImageIcon) settings.get("NormalValueIcon"); // $NON-NLS-1$
    protected ImageIcon graphIcon = (ImageIcon) settings.get("GraphIcon"); // $NON-NLS-1$

    public BBNTreeRenderer() {
        super();
        setLeafIcon(null);
        setOpenIcon(null);
        setClosedIcon(null);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object treeNode, boolean isSelected, boolean isExpanded, boolean isLeaf, int rowNum, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, treeNode, isSelected, isExpanded, isLeaf, rowNum, hasFocus);
        
        // Detect node type
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
        Object userObject = node.getUserObject();
        if (node.isRoot()) {
            setIcon(graphIcon);
        } else {
            if (userObject instanceof VisualVertex) {
                BBNNode bbnNode = (BBNNode) ((VisualVertex) userObject).getVertex();
                ImageIcon selectedIcon = null;
                if (bbnNode.isDecision()) {
                    selectedIcon = decisionNodeIcon;
                } else if (bbnNode.isUtility()) {
                    selectedIcon = utilityNodeIcon;
                } else {
                    selectedIcon = randomNodeIcon;
                }
                setIcon(selectedIcon);
            } else {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                VisualVertex parentObject = (VisualVertex) parent.getUserObject();
                BBNNode bbnNode = (BBNNode) ((VisualVertex) parentObject).getVertex();

                if (bbnNode.isEvidence() && bbnNode.getEvidenceValue().equals(userObject)) {
                    setIcon(evidenceNodeIcon);
                } else {
                    setIcon(valueNodeIcon);
                }
                return this;
            }
        }
        return this;
    }
}
