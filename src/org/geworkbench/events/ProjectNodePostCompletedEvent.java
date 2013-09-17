package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.builtin.projects.ProjectTreeNode;
import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

public class ProjectNodePostCompletedEvent extends Event {

	private ProjectTreeNode parent=null;
	private DSAncillaryDataSet<? extends DSBioObject> ancillaryDataSet = null;
	
	public ProjectNodePostCompletedEvent(String name,
			GridEndpointReferenceType gridEndpointReferenceType, DSAncillaryDataSet<? extends DSBioObject> ancillaryDataSet, ProjectTreeNode parent) {
		super(null);
		this.ancillaryDataSet = ancillaryDataSet;
		this.parent = parent;
		// TODO Auto-generated constructor stub
	}

	public ProjectTreeNode getParentNode(){
		return parent;
	}

	public DSAncillaryDataSet<? extends DSBioObject> getAncillaryDataSet() {
		return ancillaryDataSet;
	}

}
