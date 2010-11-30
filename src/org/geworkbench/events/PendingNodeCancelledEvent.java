package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.ginkgo.labs.ws.GridEndpointReferenceType;

/**
 * 
 * @author kk2457
 * @version $Id$
 */
public class PendingNodeCancelledEvent extends Event {

	private GridEndpointReferenceType gridEpr = null;

	public PendingNodeCancelledEvent(GridEndpointReferenceType gridEpr) {
		super(null);
		this.gridEpr = gridEpr;
	}

	public GridEndpointReferenceType getGridEpr() {
		return gridEpr;
	}

}
