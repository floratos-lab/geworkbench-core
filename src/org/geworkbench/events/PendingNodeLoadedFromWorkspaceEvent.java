package org.geworkbench.events;

import java.util.Collection;

import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author kk2457
 * @version $Id$
 */
public class PendingNodeLoadedFromWorkspaceEvent extends Event {

	private Collection<GridEndpointReferenceType> gridEprs = null;

	public PendingNodeLoadedFromWorkspaceEvent(
			Collection<GridEndpointReferenceType> gridEprs) {
		super(null);
		this.gridEprs = gridEprs;
	}

	public Collection<GridEndpointReferenceType> getGridEprs() {
		return gridEprs;
	}

}
