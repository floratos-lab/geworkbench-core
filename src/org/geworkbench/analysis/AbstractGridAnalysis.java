package org.geworkbench.analysis;

import java.util.Map;

/**
 * Analyses that have a corresponding (ca)Grid component should extend this
 * abstract class to have the grid analyses exposed.
 * 
 * @author keshav
 * @version $Id: AbstractGridAnalysis.java,v 1.4 2007-04-16 20:07:12 keshav Exp $
 */
public abstract class AbstractGridAnalysis extends AbstractAnalysis {

	/**
	 * 
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> getBisonParameters();

	/**
	 * 
	 * @return String
	 */
	public abstract String getAnalysisName();

}
