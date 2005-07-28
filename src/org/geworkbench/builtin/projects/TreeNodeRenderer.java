package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class TreeNodeRenderer extends DefaultTreeCellRenderer {

    public static ImageIcon _projectIcon = new ImageIcon(TreeNodeRenderer.class.getResource("project16x16.gif"));
    public static ImageIcon _dataSetIcon = new ImageIcon(TreeNodeRenderer.class.getResource("chip16x16.gif"));
    public static ImageIcon _phenotypeIcon = new ImageIcon(TreeNodeRenderer.class.getResource("Phenotype16x16.gif"));
    public static ImageIcon _dataSubSetIcon = new ImageIcon(TreeNodeRenderer.class.getResource("Phenotype16x16.gif"));
    public static ImageIcon _imageIcon = new ImageIcon(TreeNodeRenderer.class.getResource("image16x16.gif"));

    ProjectSelection _selection = null;

    public TreeNodeRenderer(ProjectSelection selection) {
        _selection = selection;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setForeground(Color.lightGray);
        if (value.getClass() == ProjectNode.class) {
            setIcon(_projectIcon);
            setToolTipText("This is a project folder.");
        } else {
            if (value.getClass() == DataSetNode.class) {
                DSDataSet df = ((DataSetNode) value).dataFile;
                ImageIcon icon = df.getIcon();
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(_dataSetIcon);
                }
                String[] descriptions = df.getDescriptions();
                if (descriptions.length > 0) {
                    setToolTipText(descriptions[0]);
                } else {
                    setToolTipText("This is an undefined Data set");
                }
            } else if (value.getClass() == DataSetSubNode.class) {
                DSAncillaryDataSet adf = ((DataSetSubNode) value)._aDataSet;
                ImageIcon icon = adf.getIcon();
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(_dataSubSetIcon);
                }
                String[] descriptions = adf.getDescriptions();
                if (descriptions.length > 0) {
                    setToolTipText("This is a Ancillary Data set: " + descriptions[0]);
                } else {
                    setToolTipText("This is an undefined Ancillary Data set");
                }
            } else if (value.getClass() == ImageNode.class) {
                setIcon(_imageIcon);
            }
        }
        if (value == _selection.getSelectedNode()) {
            setForeground(Color.black);
        }
        return this;
    }
}
