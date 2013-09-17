package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

public class CaArraySuccessEvent extends Event {

	private int total = 0;

	public CaArraySuccessEvent(int total) {
		super(null);
		this.total = total;
	}

	public int getTotalArrays() {
		return total;
	}

}
