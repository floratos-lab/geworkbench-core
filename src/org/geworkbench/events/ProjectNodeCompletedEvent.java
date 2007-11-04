package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author keshav
 * @author kk2457
 * @version $Id: ProjectNodeCompletedEvent.java,v 1.2 2007-11-04 19:13:17 kk2457 Exp $
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

	public ProjectNodeCompletedEvent(EventSource s) {
		super(s);
		// TODO Auto-generated constructor stub
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
