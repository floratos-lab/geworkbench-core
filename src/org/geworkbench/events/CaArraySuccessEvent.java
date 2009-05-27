package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

public class CaArraySuccessEvent extends Event {

	int number = 0;
	int total = 0;

	public CaArraySuccessEvent(int number, int total) {
		super(null);
		this.number = number;
		this.total = total;
	}

	public int getTotalArrays() {
		return total;
	}

	public int getCurrentArrayIndex() {
		return number;
	}
}
