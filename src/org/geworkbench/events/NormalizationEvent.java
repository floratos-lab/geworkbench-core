package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Application event thrown in order to communicate the results of a normalization
 * operation.
 */
public class NormalizationEvent extends Event {
    /**
     * The microarray that was the input to the normalization operation.
     */
    private final DSMicroarraySet<DSMicroarray> sourceMA;
    /**
     * The microarray that was the result of the normalization operation.
     */
    private final DSMicroarraySet<DSMicroarray> resultMA;
    /**
     * Information about the normalizer used.
     */
    private String normalizerInfo = null;

    @SuppressWarnings("unchecked")
	public NormalizationEvent(final DSMicroarraySet<?> s, final DSMicroarraySet<?> r, final String info) {
        super(null);
        sourceMA = (DSMicroarraySet<DSMicroarray>) s;
        resultMA = (DSMicroarraySet<DSMicroarray>) r;
        normalizerInfo = info;
    }

    public DSMicroarraySet<DSMicroarray> getOriginalMASet() {
        return sourceMA;
    }

    public DSMicroarraySet<DSMicroarray> getNormalizedMASet() {
        return resultMA;
    }

    public String getInformation() {
        return normalizerInfo;
    }

}
