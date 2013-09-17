package org.geworkbench.events;

import java.util.Map;

import org.geworkbench.analysis.AbstractGridAnalysis;
import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author kk2457
 * @version $Id$
 */
public class PendingNodeLoadedFromWorkspaceEvent extends Event {

	private final Map<GridEndpointReferenceType, AbstractGridAnalysis> gridEprs;

	public PendingNodeLoadedFromWorkspaceEvent(
			final Map<GridEndpointReferenceType, AbstractGridAnalysis> gridEprs) {
		super(null);
		this.gridEprs = gridEprs;
	}

	public Map<GridEndpointReferenceType, AbstractGridAnalysis> getGridEprs() {
		return gridEprs;
	}

}
