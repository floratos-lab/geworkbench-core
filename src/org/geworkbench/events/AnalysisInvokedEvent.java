package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;


/**
 * @author swapneelsheth
 */
public class AnalysisInvokedEvent extends Event {
	
	private String analysisName;
	
	public AnalysisInvokedEvent(String analysisName) {
		super(null);
		this.analysisName = analysisName;
	}

	public String getAnalysisName() {
		return analysisName;
	}

}
