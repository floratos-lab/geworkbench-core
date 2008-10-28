package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * 
 * After the data has been changed by other threads, we can throw this event. As
 * an example, we throw this event in the ProjectPanel when finished loading a
 * workspace to effectively stop block others from accessing the data. In this
 * example, this is received by the ColorMosaicPanel.
 * <p>
 * This event is used in combination with the DirtyDataEvent. In the example
 * above, the DirtyDataEvent is published from the ProjectPanel to alert
 * recipients that the data in question is dirty.
 * 
 * @author yc2480
 * @version $Id: CleanDataEvent.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 */
public class CleanDataEvent extends Event {

	public CleanDataEvent() {
		super(null);
	}

}
