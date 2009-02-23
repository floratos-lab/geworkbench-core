package org.geworkbench.events;

import org.geworkbench.bison.model.analysis.Analysis;
import org.geworkbench.engine.config.events.Event;

/**
 * @author swapneelsheth
 * @version $Id: AnalysisInvokedEvent.java,v 1.3 2009-02-23 18:08:48 chiangy Exp $
 */
public class AnalysisInvokedEvent extends Event {

	private Analysis analysis;
	private String dataSetName;

	/**
	 * 
	 * @param analysis
	 * @param dataSetName
	 */
	public AnalysisInvokedEvent(Analysis analysis, String dataSetName) {
		super(null);
		this.analysis = analysis;
		this.dataSetName = dataSetName;
	}

	/**
	 * 
	 * @return
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * 
	 * @return
	 */
	public String getDataSetName() {
		return dataSetName;
	}

}
