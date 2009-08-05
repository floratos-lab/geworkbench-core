package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
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
public class GetSequenceEvent extends Event {
    /**
     * The <code>microarraySet</code> that was the input to the filtering
     * operation.
     */
    private DSMicroarraySet sourceMA = null;
    
    /**
     * Information about the filter used.
     */
    private String info = null;

    public GetSequenceEvent(DSMicroarraySet s, String info) {
        super(null);
        this.sourceMA = s;      
        this.info = info;
    }

    /**
     * Gets the <code>microarraySet</code> that was the input to the filtering
     * operation.
     *
     * @return the input dataset
     */
    public DSMicroarraySet getOriginalMASet() {
        return this.sourceMA;
    }

    

    /**
     * Gets the information about the filter used
     *
     * @return
     */
    public String getInformation() {
        return this.info;
    }

}
