package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.builtin.projects.ProjectTreeNode;
import org.geworkbench.builtin.projects.DataSetSubNode;
import org.geworkbench.builtin.projects.DataSetNode;

import javax.swing.tree.TreeNode;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class ProjectEvent extends Event {

    public static final String CLEARED = "Project Cleared";
    public static final String SELECTED = "Node Selected";

    private final String value;
    private final DSDataSet<? extends DSBioObject> dataSet;
    private final ProjectTreeNode node;

    public ProjectEvent(final String message, final DSDataSet<? extends DSBioObject> dataSet, final ProjectTreeNode node) {
        super(null);
        this.value = message;
        this.dataSet = dataSet;
        this.node = node;
    }

    public String getMessage() {
        return value;
    }

    @SuppressWarnings("rawtypes")
	public DSDataSet getDataSet() {
        return dataSet;
    }

    @SuppressWarnings("rawtypes")
	public DSDataSet getParent() {
		if (node instanceof DataSetSubNode) {
			DataSetSubNode subNode = (DataSetSubNode) node;
			TreeNode parent = subNode.getParent();
			if (parent instanceof DataSetNode) {
				return ((DataSetNode) parent).getDataset();
			}
		}

        return null;
    }
    
    public ProjectTreeNode getTreeNode() { return node; }

}
