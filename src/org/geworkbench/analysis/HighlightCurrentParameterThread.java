package org.geworkbench.analysis;


/**
 * We use this class as a call back function
 * 
 * @author yc2480
 * $id$
 */
public class HighlightCurrentParameterThread extends Thread {
	ReHighlightable aPanel = null;

	/**
	 * Pass in the analysis panel needs to be refreshed.
	 * @param analysisPanel
	 */
	public HighlightCurrentParameterThread(ReHighlightable aPanel) {
		super();
		this.aPanel = aPanel;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		aPanel.refreshHighLight();
	}
}
