package org.geworkbench.builtin.projects;

import org.geworkbench.builtin.projects.ImageNode;
import org.geworkbench.builtin.projects.MicroarraySetNode;
import org.geworkbench.builtin.projects.ProjectNodeOld;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>JTree</code> renderer to be used to render the Project Tree
 * @todo Phase out.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class TreeNodeRendererOld extends DefaultTreeCellRenderer {
    /**
     * Current <code>MicroarraySetNode</code> selection
     */
    public MicroarraySetNode microarraySetNodeSelection = null;
    /**
     * Current <code>ProjectNodeOld</code> selection
     */
    public ProjectNodeOld projectNodeSelection = null;
    /**
     * Current <code>ImageNode</code> selection
     */
    public ImageNode imageNodeSelection = null;
    /**
     * <code>ImageIcon</code> for display on Project Nodes
     */
    ImageIcon projectIcon = null;
    /**
     * <code>ImageIcon</code> for display on Microarray Nodes
     */
    ImageIcon microarrayIcon = null;
    /**
     * <code>ImageIcon</code> for display on Phenotype Nodes
     */
    ImageIcon phenotypeIcon = null;
    /**
     * <code>ImageIcon</code> for display on Image Nodes
     */
    ImageIcon imageIcon = null;

    /**
     * Default Constructor
     */
    public TreeNodeRendererOld() {
        projectIcon = new ImageIcon(getClass().getResource("project16x16.gif"));
        microarrayIcon = new ImageIcon(getClass().getResource("chip16x16.gif"));
        phenotypeIcon = new ImageIcon(getClass().getResource("Phenotype16x16.gif"));
        imageIcon = new ImageIcon(getClass().getResource("image16x16.gif"));
    }

    /**
     * Tests if all Node selections are null
     *
     * @return
     */
    public boolean areNodeSelectionsCleared() {
        if ((microarraySetNodeSelection == null) && (projectNodeSelection == null) && (imageNodeSelection == null))
            return true;
        return false;
    }

    /**
     * Clears all Node selections
     */
    public void clearNodeSelections() {
        microarraySetNodeSelection = null;
        projectNodeSelection = null;
        imageNodeSelection = null;
    }

    /**
     * <code>Component</code> used for remdering on the <code>ProjectTree</code>
     * based on type and selection
     *
     * @param tree     the <code>ProjectTree</code>
     * @param value    the node to be rendered
     * @param sel      if selected
     * @param expanded if node expanded
     * @param leaf     if leaf
     * @param row      row in the tree model
     * @param hasFocus if the node has focus
     * @return the <code>Component</code> to be used for rendering
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setForeground(Color.lightGray);
        if (value.getClass() == ProjectNodeOld.class) {
            setIcon(projectIcon);
            setToolTipText("This is a project folder.");
            if (value == projectNodeSelection) {
                setForeground(Color.black);
            }

        } else if (value.getClass() == MicroarraySetNode.class) {
            setIcon(microarrayIcon);
            setToolTipText("This is a Microarray set.");
            if (value == microarraySetNodeSelection) {
                setForeground(Color.black);
            }

        } else if (value.getClass() == ImageNode.class) {
            setIcon(imageIcon);
            setToolTipText("This is a image selection.");
            if (value == imageNodeSelection) {
                setForeground(Color.black);
            }

        }

        return this;
    }

}