/**
 * 
 */
package org.geworkbench.bison.model.clusters;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * An serial implementation of cluster set so that no recursive operation is
 * needed to traverse. This is particularly necessary to support serialization
 * of CSHierClusterDataSet of large depth.
 * 
 * @author zji
 * @version $Id$
 * 
 */
public class NonRecursiveClusterSet implements Serializable {
	private static final long serialVersionUID = 5767070276541277929L;

	private static Log log = LogFactory.getLog(NonRecursiveClusterSet.class);

	private static class ClusterNode implements Serializable {
		private static final long serialVersionUID = -1600384841523572174L;

		public ClusterNode(Object value, int depth, int parent) {
			this.value = value;
			this.parent = parent;
			this.depth = depth;
		}

		int depth;
		Object value;
		int parent;
	}

	/*
	 * a 'flattened' implementation of cluster using a variable-sized array
	 * instead of tree structure to avoid recursive traverse.
	 */
	private List<ClusterNode> list = null;

	private static Object getValue(DefaultHierCluster node,
			Class<?> clusterClass) throws IOException {
		Object value = null;
		if (clusterClass.equals(MarkerHierCluster.class)) {
			MarkerHierCluster m = (MarkerHierCluster) node;
			value = m.getMarkerInfo();
		} else if (clusterClass.equals(MicroarrayHierCluster.class)) {
			MicroarrayHierCluster m = (MicroarrayHierCluster) node;
			value = m.getMicroarray();
		} else {
			log.error("Invalid HierCluster class " + clusterClass);
			throw new IOException("Invalid HierCluster class " + clusterClass);
		}
		return value;
	}

	private static void setValue(DefaultHierCluster node,
			Class<?> clusterClass, Object value) throws IOException {
		if (clusterClass.equals(MarkerHierCluster.class)) {
			MarkerHierCluster m = (MarkerHierCluster) node;
			m.setMarkerInfo((DSGeneMarker) value);
		} else if (clusterClass.equals(MicroarrayHierCluster.class)) {
			MicroarrayHierCluster m = (MicroarrayHierCluster) node;
			m.setMicroarray((DSMicroarray) value);
		} else {
			log.error("Invalid HierCluster class " + clusterClass);
			throw new IOException("Invalid HierCluster class " + clusterClass);
		}
	}

	/* The only constructor. */
	public NonRecursiveClusterSet(HierCluster hierCluster, Class<?> clusterClass)
			throws IOException {
		// support null is important because clustering may be done for only one
		// of the two types: marker clustering or microarray clustering
		if (hierCluster == null)
			return;

		list = new ArrayList<ClusterNode>();

		Set<DefaultHierCluster> visited = new HashSet<DefaultHierCluster>();
		Stack<DefaultHierCluster> stack = new Stack<DefaultHierCluster>();
		Stack<Integer> parentIndexStack = new Stack<Integer>();

		DefaultHierCluster end = new DefaultHierCluster();
		stack.push(end);
		int parentIndex = -1;
		parentIndexStack.push(parentIndex);

		DefaultHierCluster thisnode = (DefaultHierCluster) hierCluster;

		// first round create the list without correct parent information
		while (thisnode != end) {
			List<Cluster> children = thisnode.children;

			if (children.size() == 0) {
				ClusterNode clusterNode = new ClusterNode(getValue(thisnode,
						clusterClass), thisnode.getDepth(), parentIndex);
				list.add(clusterNode);
			}

			// decide what node to work on now
			boolean found = false;
			for (Cluster cluster : children) {
				DefaultHierCluster child = (DefaultHierCluster) cluster;
				if (!visited.contains(child)) {
					stack.push(thisnode);
					if (!visited.contains(thisnode)) {
						visited.add(thisnode);
						ClusterNode clusterNode = new ClusterNode(getValue(
								thisnode, clusterClass), thisnode.getDepth(),
								parentIndexStack.peek());
						list.add(clusterNode);
						parentIndex = list.size() - 1;
					}
					parentIndexStack.push(parentIndex);

					thisnode = child;
					found = true;
					break;
				}
			}
			if (!found) {
				visited.add(thisnode);
				thisnode = stack.pop();
				parentIndex = parentIndexStack.pop();
			}
		}
	}

	/**
	 * Create a HierCluster instance from this NonRecursiveClusterSet.
	 * 
	 * @param clusterClass
	 * @return the root
	 * @throws IOException
	 */
	public HierCluster convertToHierCluster(Class<?> clusterClass)
			throws IOException {
		if (list == null)
			return null;

		HierCluster root = null;
		DefaultHierCluster[] clusterArray = null;
		if (clusterClass.equals(MarkerHierCluster.class)) {
			clusterArray = new MarkerHierCluster[list.size()];
			for (int i = 0; i < clusterArray.length; i++)
				clusterArray[i] = new MarkerHierCluster();
		} else if (clusterClass.equals(MicroarrayHierCluster.class)) {
			clusterArray = new MicroarrayHierCluster[list.size()];
			for (int i = 0; i < clusterArray.length; i++)
				clusterArray[i] = new MicroarrayHierCluster();
		} else {
			log.error("Invalid HierCluster class " + clusterClass);
			throw new IOException("Invalid HierCluster class " + clusterClass);
		}

		for (int i = 0; i < clusterArray.length; i++) {
			ClusterNode node = list.get(i);
			int parent = node.parent;
			Object value = node.value;

			setValue(clusterArray[i], clusterClass, value);
			clusterArray[i].setDepth(node.depth);
			if (parent == -1) {
				root = clusterArray[i]; // index must be zero in this case.
			} else {
				clusterArray[parent].addNode(clusterArray[i]);
			}
		}

		return root;
	}

}
