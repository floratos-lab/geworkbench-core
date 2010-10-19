package org.geworkbench.builtin.projects;

import java.io.Serializable;

/**
 * The node representing 'project' in the project panel.
 * 
 * This class does not have any different behavior from ProjectTreeNode, 
 * but is identified by ProjectPanel, ProjectSelection, and TreeNodeRenderer
 * as a distinct type of ProjectTreeNode.
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class ProjectNode extends ProjectTreeNode implements Serializable {
	private static final long serialVersionUID = -3970450782174530670L;

	public ProjectNode(Object name) {
		super(name);
	}
}
