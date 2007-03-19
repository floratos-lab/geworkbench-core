package org.geworkbench.analysis;

import java.util.Map;

/**
 * Analyses that have a corresponding (ca)Grid component should extend this
 * abstract class to have the grid analyses exposed.
 * 
 * @author keshav
 * @version $Id: AbstractGridAnalysis.java,v 1.2 2007-03-19 18:13:37 keshav Exp $
 */
public abstract class AbstractGridAnalysis extends AbstractAnalysis {

	/**
	 * Returns the bison parameters, to be used for conversion in to cagrid
	 * parameter types.
	 * 
	 * @return Map<String, String>
	 */
	public abstract Map<String, String> getBisonParameters();

}
