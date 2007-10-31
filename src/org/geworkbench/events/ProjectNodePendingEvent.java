package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * A project node pending event to be used to trigger "events in progress".
 * 
 * @author keshav
 * @version $Id: ProjectNodePendingEvent.java,v 1.1 2007-10-31 04:49:25 keshav Exp $
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

}
