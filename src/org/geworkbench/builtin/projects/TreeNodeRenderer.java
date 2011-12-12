package org.geworkbench.builtin.projects;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.engine.management.TypeMap;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>JTree</code> renderer to be used to render the Project Tree
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class TreeNodeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1879887785935786137L;

    /**
     * <code>Component</code> used for rendering on the <code>ProjectTree</code>
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
    @SuppressWarnings("unchecked")
	@Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        String description = null;
        if (value == tree.getModel().getRoot()) {
            setIcon(Icons.WORKSPACE_ICON);
            setToolTipText("This is the workspace.");
        } else if (value.getClass() == ProjectNode.class) {
            setIcon(Icons.PROJECT_ICON);
            setToolTipText("This is a project folder.");
        } else if(value.getClass() == PendingTreeNode.class){
        	setIcon(Icons.BUSY_STATIC_ICON);
        	setToolTipText(null);
        } else {
            if (value.getClass() == DataSetNode.class) {
                DSDataSet<? extends DSBioObject> df = ((DataSetNode) value).getDataset();
                ImageIcon icon = getIconForType(df.getClass());
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(Icons.MICROARRAYS_ICON);
                }
                description = df.getDescription();
            } else if (value.getClass() == DataSetSubNode.class) {
                DSAncillaryDataSet<? extends DSBioObject> adf = ((DataSetSubNode) value)._aDataSet;               
                ImageIcon icon = getIconForType(adf.getClass());
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(Icons.DATASUBSET_ICON);
                }
                description = adf.getDescription();
            } else if (value.getClass() == ImageNode.class) {
                setIcon(Icons.IMAGE_ICON);
            }
            if (description!=null) {
                setToolTipText(description);
            } else setToolTipText(null);
        }

        return this;
    }

	private static ImageIcon getIconForType(Class<? extends DSNamed> type) {
		ImageIcon icon = iconMap.get(type);
		if (icon == null) {
			return Icons.GENERIC_ICON;
		} else {
			return icon;
		}
	}

	private static TypeMap<ImageIcon> iconMap = new TypeMap<ImageIcon>();

	// Initialize default icons
	static {
		DefaultIconAssignments.initializeDefaultIconAssignments();
	}

	public static void setIconForType(Class<? extends DSNamed> type,
			ImageIcon icon) {
		iconMap.put(type, icon);
	}
}
