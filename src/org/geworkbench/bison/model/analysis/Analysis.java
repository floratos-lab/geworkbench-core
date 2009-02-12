package org.geworkbench.bison.model.analysis;

import java.io.Serializable;
import java.util.Map;

import org.geworkbench.bison.datastructure.properties.DSDescribable;
import org.geworkbench.bison.datastructure.properties.DSIdentifiable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <ul>
 * <li>
 * A computational service has a name and a description (attributes made
 * available through realizing the <code>Describable</code> and
 * <code>Identifiable</code> interfaces).
 * </li>
 * <li>
 * Optionally, a set of parameters may be associated with a
 * computational service (e.g., the agglomeration method and the distance
 * metric to be used with hierarchical clustering). Values to these
 * parameters need to be provided by the user before the service is
 * invoked.
 * </li>
 * <li>
 * Parameters may have implicit or explicit restrictions on the type of
 * values that they can assume. As a result, user-provided values may need
 * to be validated before they can be accepted and used
 * </li>
 * </ul>
 *
 * @author First Genetic Trust Inc.
 * @author keshav
 * @author yc2480
 * @version $Id: Analysis.java,v 1.2 2009-02-12 22:28:15 keshav Exp $
 */
public interface Analysis extends DSDescribable, DSIdentifiable {
    /**
     * returns a GUI for entering analysis-specific parameter values.
     *
     * @return parameterPanel
     */
    ParameterPanel getParameterPanel();

    /**
     * validates the current parameter setting
     *
     * @return validation status
     */
    ParamValidationResults validateParameters();

    /**
     * invokes the actual service
     *
     * @param input input dataset
     * @return Results of algorithm execution
     */
    AlgorithmExecutionResults execute(Object input);
     
    public Map<Serializable, Serializable> getParameters();
    
    public void setParameters(Map<Serializable, Serializable> parameters);

	public void saveParameters(String filename);
	
	public void deleteParameters(String filename);

	public void refreshGUI();
}
