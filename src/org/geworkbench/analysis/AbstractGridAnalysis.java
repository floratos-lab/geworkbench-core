package org.geworkbench.analysis;

import java.util.Map;

/**
 * Analyses that have a corresponding (ca)Grid component should extend this
 * abstract class to have the grid analyses exposed.
 * 
 * @author keshav
 * @version $Id: AbstractGridAnalysis.java,v 1.5 2007-06-13 16:13:36 keshav Exp $
 */
public abstract class AbstractGridAnalysis extends AbstractAnalysis {

	/**
	 * This method should be implmented to obtain the user input (which is
	 * stored as BISON parameters), convert it to caGrid parameters, and return
	 * a map with caGrid parameter values keyed by parameter names.
	 * 
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> getBisonParameters();

	/**
	 * Analyses extending this class will implement this method, setting the
	 * name of the service. This name should be equivalent to one of the names
	 * in the "grid services cache". See {@link GridServiceRunner}.
	 * 
	 * @return String
	 */
	public abstract String getAnalysisName();

}
