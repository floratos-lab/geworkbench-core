package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author keshav
 * @author kk2457
 * @version $Id: ProjectNodeCompletedEvent.java,v 1.3 2008-01-03 19:26:21 keshav Exp $
 */
public class ProjectNodeCompletedEvent extends Event {

	private String name = null;

	private GridEndpointReferenceType gridEndpointReferenceType = null;
	
	private DSDataSet dataSet = null;
	
	DSAncillaryDataSet ancillaryDataSet = null;
	
	public ProjectNodeCompletedEvent(String name,
			GridEndpointReferenceType gridEndpointReferenceType) {
		super(null);
		this.name = name;
		this.gridEndpointReferenceType = gridEndpointReferenceType;
	}

	public GridEndpointReferenceType getGridEndpointReferenceType() {
		return gridEndpointReferenceType;
	}

	public void setGridEndpointReferenceType(
			GridEndpointReferenceType gridEndpointReferenceType) {
		this.gridEndpointReferenceType = gridEndpointReferenceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DSAncillaryDataSet getAncillaryDataSet() {
		return ancillaryDataSet;
	}

	public void setAncillaryDataSet(DSAncillaryDataSet ancillaryDataSet) {
		this.ancillaryDataSet = ancillaryDataSet;
	}

	public DSDataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DSDataSet dataSet) {
		this.dataSet = dataSet;
	}


}
