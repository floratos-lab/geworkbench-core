package org.geworkbench.builtin.projects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.skin.Skin;

/**
 * @author John Watkinson
 */
public class SaveTree implements Serializable {
	
	transient private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2534917305724421302L;

    List<DataSetSaveNode> nodes;
    private DSDataSet selected;

    public SaveTree(ProjectPanel panel, DSDataSet selected) {
        this.selected = selected;
        nodes = new ArrayList<DataSetSaveNode>();
        selected = panel.getDataSet();
        DefaultTreeModel model = panel.projectTreeModel;
        ProjectTreeNode root = (ProjectTreeNode) model.getRoot();
        int n = root.getChildCount();
        for (int i = 0; i < n; i++) {
            ProjectTreeNode project = (ProjectTreeNode) root.getChildAt(i);
            DataSetSaveNode saveProject = new DataSetSaveNode(project.toString());
            nodes.add(saveProject);
            addChildren(project, saveProject);
        }
    }

    // Recursively build tree
    private void addChildren(ProjectTreeNode node, DataSetSaveNode saveNode) {
        Skin skin = (Skin) GeawConfigObject.getGuiWindow();
        int n = node.getChildCount();
        for (int i = 0; i < n; i++) {
            ProjectTreeNode treeNode = (ProjectTreeNode) node.getChildAt(i);
            DSDataSet dataSet = null;
            if (treeNode instanceof DataSetNode) {
                DataSetNode childNode = (DataSetNode) treeNode;
                dataSet = childNode.dataFile;
            } else if (treeNode instanceof DataSetSubNode) {
                DataSetSubNode childNode = (DataSetSubNode) treeNode;
                dataSet = childNode._aDataSet;
                if (dataSet.getID() == null){
                	dataSet.setID(RandomNumberGenerator.getID());
                }
            }
           
            DataSetSaveNode childSave = new DataSetSaveNode(dataSet);
            childSave.setCommandSelected(skin.getCommandLastSelected(dataSet));
            childSave.setSelectionSelected(skin.getSelectionLastSelected(dataSet));
            childSave.setVisualSelected(skin.getVisualLastSelected(dataSet));
            saveNode.addChild(childSave);
            addChildren(treeNode, childSave);
        }
    }

    public List<DataSetSaveNode> getNodes() {
        return nodes;
    }

    public DSDataSet getSelected() {
        return selected;
    }
}
