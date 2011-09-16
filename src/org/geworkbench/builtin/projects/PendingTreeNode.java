package org.geworkbench.builtin.projects;

import java.io.Serializable;

import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * @uthor kumar
 * @author keshav
 * @version $Id$
 */
public class PendingTreeNode extends ProjectTreeNode implements Serializable {

	private static final long serialVersionUID = 6783438582542203187L;

	// this is necessary only because when the underlying DSDataSet is serialized, it looses it identity as pending node
	static class PendingNode extends CSDataSet<DSBioObject> {
		private static final long serialVersionUID = -3552145129303064083L;
	}
	
	/**
	 * Constructor.
	 */
	public PendingTreeNode(String label, String history,
			GridEndpointReferenceType gridEpr) {
		DSDataSet<?> dataset = new PendingNode();
		dataset.setLabel(label);
		dataset.addDescription(history);
		dataset.addObject(GridEndpointReferenceType.class, gridEpr);
		super.setUserObject(dataset);
	}

	public DSDataSet<?> getDSDataSet() {
		return (DSDataSet<?>)getUserObject();
	}
	/**
	 * 
	 * Get underlying GridEndpointReferenceType.
	 */
	public GridEndpointReferenceType getGridEpr() {
		DSDataSet<?> dataset = getDSDataSet();
		return (GridEndpointReferenceType)dataset.getObject(GridEndpointReferenceType.class);
	}
}
