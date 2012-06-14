package org.geworkbench.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
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
public class ProjectEvent {

	static Log log = LogFactory.getLog(ProjectEvent.class);
	
	@Deprecated
    public static final String CLEARED = "Project Cleared";
	@Deprecated
    public static final String SELECTED = "Node Selected";

    private final Message value;
    private final DSDataSet<? extends DSBioObject> dataSet;
    private final ProjectTreeNode node;

    public enum Message {CLEAR, SELECT, CCM_UPDATE};

    public ProjectEvent(final Message value, final DSDataSet<? extends DSBioObject> dataSet, final ProjectTreeNode node) {
        this.value = value;
        this.dataSet = dataSet;
        this.node = node;
    }

    /**
     * It is recommended to used the version using enum instead of String
     */
    @Deprecated
    public String getMessage() {
    	switch(value) {
    	case CLEAR: return CLEARED;
    	case SELECT: return SELECTED;
    	default: return "No Message";
    	}
    }
    
    public Message getValue() {
    	return value;
    }

    @SuppressWarnings("rawtypes")
	public DSDataSet getDataSet() {
        return dataSet;
    }

    /**
     * This should be ProjectTreeNode's responsibility, not this class's. 
     */
    @Deprecated
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
