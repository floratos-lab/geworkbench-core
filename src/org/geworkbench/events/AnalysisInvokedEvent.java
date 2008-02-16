package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * @author swapneelsheth
 */
public class AnalysisInvokedEvent extends Event {
	
	private String analysisName;
	private String dataSetName;
	
	public AnalysisInvokedEvent(String analysisName, String dataSetName) {
		super(null);
		this.analysisName = analysisName;
		this.dataSetName = dataSetName;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public String getDataSetName() {
		return dataSetName;
	}

}
