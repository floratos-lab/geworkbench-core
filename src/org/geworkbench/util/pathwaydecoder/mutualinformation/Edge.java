package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;

/**
 * @version $Id$
 */
public class Edge implements Serializable {
	private static final long serialVersionUID = 742062775524951624L;

	final private String startNode;
	final private String endNode;

	public Edge(String start, String end) {
		this.startNode = start;
		this.endNode = end;
	}

	public String getStartNode() {
		return this.startNode;
	}

	public String getEndNode() {
		return this.endNode;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Edge)) {
			return false;
		}
		Edge e = (Edge) object;
		if (this.equals(e.startNode, e.endNode)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int h = 31 * 17 + startNode.hashCode();
		return 31 * h + endNode.hashCode();
	}

	public boolean equals(String start, String end) {
		if (this.startNode.equals(start) && this.endNode.equals(end)) {
			return true;
		} else {
			return false;
		}
	}
}
