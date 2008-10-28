package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * Before we want to change data that is used by other threads, we can throw
 * this event. As an example, we throw this event in the ProjectPanel when
 * loading a workspace to effectively block others from accessing the data. In
 * this example, this is received by the ColorMosaicPanel.
 * <p>
 * This event is used in combination with the CleanDataEvent. In the example
 * above, the CleanDataEvent is published from the ProjectPanel to alert
 * recipients that the data in question is no longer dirty.
 * 
 * @author yc2480
 * @version $Id: DirtyDataEvent.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 */
public class DirtyDataEvent extends Event {

	/**
	 * 
	 */
	public DirtyDataEvent() {
		super(null);
	}

}
