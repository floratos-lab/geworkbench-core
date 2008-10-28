package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;

@SuppressWarnings("unchecked")
/**
 * @author zji
 * @version $Id: HistoryEvent.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 */
public class HistoryEvent extends Event {
	
	private DSDataSet dataset = null;

	public HistoryEvent(DSDataSet dataset) {
		super(null);
		this.dataset = dataset;
	}

	public DSDataSet getDataSet() {
		return dataset;
	}

}
