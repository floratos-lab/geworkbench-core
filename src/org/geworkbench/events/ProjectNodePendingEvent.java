package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * A project node pending event to be used to trigger "events in progress".
 * 
 * @author keshav
 * @version $Id: ProjectNodePendingEvent.java,v 1.2 2007-11-04 19:13:18 kk2457 Exp $
 */
public class ProjectNodePendingEvent extends Event {

	private String name = null;

	private GridEndpointReferenceType gridEndpointReferenceType = null;

	public ProjectNodePendingEvent(String name,
			GridEndpointReferenceType gridEndpointReferenceType) {
		super(null);// FIXME this is bad

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

}
