package org.geworkbench.builtin.projects;

import java.io.Serializable;

import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * @uthor kumar
 * @author keshav
 * @version $Id: PendingTreeNode.java,v 1.2 2008-01-03 19:26:21 keshav Exp $
 */
public class PendingTreeNode extends ProjectTreeNode implements Serializable {

	private static final long serialVersionUID = 6783438582542203187L;

	private GridEndpointReferenceType gridEpr;

	/**
	 * Default Constructor
	 */
	public PendingTreeNode() {
		throw new RuntimeException(
				"Cannot instantiate using the no-arg constructor.  No-arg constructor provided for standard java-bean convention purposes.");
	}

	/**
	 * Constructor
	 * 
	 * @param nodeName
	 *            <code>Object</code> to be set as User Object
	 */
	public PendingTreeNode(Object nodeName, String description,
			GridEndpointReferenceType gridEpr) {
		super.setUserObject(nodeName);
		super.description = description;
		this.gridEpr = gridEpr;
	}

	/**
	 * 
	 * @return
	 */
	public GridEndpointReferenceType getGridEpr() {
		return gridEpr;
	}
}
