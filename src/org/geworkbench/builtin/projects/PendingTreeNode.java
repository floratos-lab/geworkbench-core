package org.geworkbench.builtin.projects;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class PendingTreeNode extends DefaultMutableTreeNode  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6783438582542203187L;
	private String description = "";

    /**
     * Default Constructor
     */
    public PendingTreeNode() {
    }

    /**
     * Constructor
     *
     * @param nodeName <code>Object</code> to be set as User Object
     */
    public PendingTreeNode(Object nodeName) {
        setUserObject(nodeName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	
	
}
