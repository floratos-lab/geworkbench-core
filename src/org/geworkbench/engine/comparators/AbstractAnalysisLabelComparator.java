package org.geworkbench.engine.comparators;

import java.util.Comparator;

import org.geworkbench.analysis.AbstractAnalysis;

/**
 * Comparator for AbstraactAnalyses.
 * 
 * @author tg2321
 * @version $Id$
 */
public class AbstractAnalysisLabelComparator implements Comparator<AbstractAnalysis>{

	public int compare(AbstractAnalysis abstractAnalysis1, AbstractAnalysis abstractAnalysis2){
		String label1 = abstractAnalysis1.getLabel();
		String label2 = abstractAnalysis2.getLabel();
		return label1.compareToIgnoreCase(label2);
	}
}