package org.geworkbench.analysis;

import java.util.Map;

/**
 * Analyses that have a corresponding (ca)Grid component should extend this
 * abstract class to have the grid analyses exposed.
 * 
 * @author keshav
 * @version $Id: AbstractGridAnalysis.java,v 1.3 2007-03-27 19:32:37 keshav Exp $
 */
public abstract class AbstractGridAnalysis extends AbstractAnalysis {

	/**
	 * 
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> getBisonParameters();

}
