package org.geworkbench.engine.comparators;

import java.util.Comparator;

import org.geworkbench.analysis.AbstractAnalysis;


/**
 * 
 * @author tg2321
 * @version
 */
public class AbstractAnalysisLabelComparator implements Comparator<Object>{
	/**
	 * Comparator for AbstraactAnalyses.
	 */

	public int compare(Object abstractAnalysis1, Object abstractAnalysis2){
		String label1 = ( (AbstractAnalysis) abstractAnalysis1).getLabel().toLowerCase();
		String label2 = ( (AbstractAnalysis) abstractAnalysis2).getLabel().toLowerCase();
		return label1.compareTo(label2);
	}
}