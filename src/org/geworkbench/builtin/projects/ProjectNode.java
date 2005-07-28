package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class ProjectNode extends org.geworkbench.builtin.projects.ProjectNodeOld implements Serializable {
    DataSetNode dataSetNodeSelection = null;
    DataSetSubNode dataSetSubNodeSelection = null;

    ProjectNode(Object nodeName) {
        super(nodeName);
        setUserObject(nodeName);
    }

    public DSDataSet getSelectedDataSet() {
        return dataSetNodeSelection.dataFile;
    }

    public void addNode(TreeNode node, Object object) {
        //this.add((MutableTreeNode)node);
        DataSetNode maNode = new DataSetNode((DSDataSet) object);
        int childCount = dataSetNodeSelection.getChildCount();
        // System.out.println(childCount);
        try {
            dataSetNodeSelection.insert((MutableTreeNode) node, childCount);
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            aiobe.printStackTrace();
        }
    }
}
