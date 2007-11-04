package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author keshav
 * @author kk2457
 * @version $Id: ProjectNodeCompletedEvent.java,v 1.1 2007-11-04 18:28:12 keshav Exp $
 */
public class ProjectNodeCompletedEvent extends Event {

	private String name = null;

	private GridEndpointReferenceType gridEndpointReferenceType = null;

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

}
