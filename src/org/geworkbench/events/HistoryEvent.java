package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.engine.config.events.Event;

/**
 * @author zji
 * @version $Id: HistoryEvent.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 */
public class HistoryEvent extends Event {
	
	private final DSDataSet<? extends DSBioObject> dataset;

	public HistoryEvent(final DSDataSet<? extends DSBioObject> dataset) {
		super(null);
		this.dataset = dataset;
	}

	public DSDataSet<? extends DSBioObject> getDataSet() {
		return dataset;
	}

}
