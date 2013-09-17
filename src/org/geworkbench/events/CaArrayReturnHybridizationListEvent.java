package org.geworkbench.events;

import org.geworkbench.builtin.projects.remoteresources.carraydata.CaArray2Experiment;
import org.geworkbench.engine.config.events.Event;

public class CaArrayReturnHybridizationListEvent extends Event {
	
	CaArray2Experiment experiment;

	public CaArrayReturnHybridizationListEvent(CaArray2Experiment experiment) {
		super(null);
		this.experiment = experiment;
	}

	public CaArray2Experiment getCaArray2Experiment() {
		return experiment;
	}
}
