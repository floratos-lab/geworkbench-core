package org.geworkbench.builtin.projects;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: First Genetic Trust Inc.
 * </p>
 * <p/> <code>ProjectTree</code> node which represents a generic node in the
 * Project panel component
 * 
 * @author First Genetic Trust
 * @version 1.0
 */
public class ProjectTreeNode extends DefaultMutableTreeNode implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6368086703128743579L;

	protected String description = "";

	/**
	 * Default Constructor, required by its sub-class's constructor
	 */
	protected ProjectTreeNode() {
	}

	/**
	 * Constructor
	 * 
	 * The only time you construct an instance of this class, instead of its sub-class, is the root node. */
	public ProjectTreeNode(String rootLabel) {
		setUserObject(rootLabel);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}