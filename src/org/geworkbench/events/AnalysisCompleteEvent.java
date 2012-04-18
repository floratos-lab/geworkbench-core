/**
 * 
 */
package org.geworkbench.events;

/**
 * @author zji
 *
 */
public class AnalysisCompleteEvent {

	/**
	 * @param invokeEvent
	 */
	public AnalysisCompleteEvent(final AnalysisInvokedEvent invokeEvent) {
		super();
		this.invokeEvent = invokeEvent;
	}

	/**
	 * @return the invokeEvent
	 */
	public AnalysisInvokedEvent getInvokeEvent() {
		return invokeEvent;
	}

	final private AnalysisInvokedEvent invokeEvent;

}
