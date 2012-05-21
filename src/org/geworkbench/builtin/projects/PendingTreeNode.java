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

	static class PendingNode extends CSDataSet<DSBioObject> {
		private static final long serialVersionUID = -3552145129303064083L;

		final GridEndpointReferenceType gridEpr;
		final String analysisClassName;
		
		public PendingNode(String label, String history,
				GridEndpointReferenceType gridEpr, String analysisClassName) {
			setLabel(label);
			setDescription(history);
			this.gridEpr = gridEpr;
			this.analysisClassName = analysisClassName;
		}
	}
	
	/**
	 * Constructor.
	 */
	public PendingTreeNode(String label, String history,
			GridEndpointReferenceType gridEpr, String analysisClassName) {

		DSDataSet<?> dataset = new PendingNode(label, history, gridEpr,
				analysisClassName);

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
		return ((PendingNode)dataset).gridEpr;
	}
}
