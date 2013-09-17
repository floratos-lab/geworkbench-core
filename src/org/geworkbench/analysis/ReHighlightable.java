package org.geworkbench.analysis;

/**
 * @author yc2480
 * @version $ID$
 */
public interface ReHighlightable {

	/**
	 * When called, Component (ex:AnalysisPanel and NormalizationPanel) should
	 * refresh the high light for the parameter set list after parameter changed
	 * in parameter panels.
	 */
	abstract void refreshHighLight();
}
