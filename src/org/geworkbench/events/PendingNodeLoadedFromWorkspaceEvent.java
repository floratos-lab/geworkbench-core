package org.geworkbench.events;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author kk2457
 * @version $Id: PendingNodeLoadedFromWorkspaceEvent.java,v 1.2 2008-01-03 19:26:21 keshav Exp $
 */
public class PendingNodeLoadedFromWorkspaceEvent extends Event {

	private Log log = LogFactory.getLog(this.getClass());

	private Collection<GridEndpointReferenceType> gridEprs = null;

	public PendingNodeLoadedFromWorkspaceEvent(
			Collection<GridEndpointReferenceType> gridEprs, EventSource s) {
		super(s);
		this.gridEprs = gridEprs;
	}

	public Collection<GridEndpointReferenceType> getGridEprs() {
		return gridEprs;
	}

}
