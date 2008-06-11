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
 * @version $Id: PendingNodeCancelledEvent.java,v 1.1 2008-06-11 21:12:00 chiangy Exp $
 */
public class PendingNodeCancelledEvent extends Event {

	private Log log = LogFactory.getLog(this.getClass());

	private GridEndpointReferenceType gridEpr = null;

	public PendingNodeCancelledEvent(GridEndpointReferenceType gridEpr) {
		super(null);
		this.gridEpr = gridEpr;
	}

	public GridEndpointReferenceType getGridEpr() {
		return gridEpr;
	}

}
