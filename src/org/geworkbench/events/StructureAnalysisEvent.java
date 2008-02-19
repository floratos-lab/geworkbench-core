package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.structure.DSProteinStructure;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Application event thrown in order to communicate the results of comparative
 * modeling analysis.
 */
public class StructureAnalysisEvent extends Event {
    /**
     * The pdb that was the input to the structure analysis operation.
     */
    private DSProteinStructure sourceMA = null;
    /**
     * The pdb that was the result of the structure analysis operation.
     */
    //    private DSProteinStructure resultMA = null;
    private String resultMA = null;
    /**
     * Information about the structure analysis used.
     */
    private String structureanalysisInfo = null;

    public StructureAnalysisEvent(DSProteinStructure s, String r, String info) {
        super(null);
        sourceMA = s;
        resultMA = r;
        structureanalysisInfo = info;
    }

    public DSProteinStructure getDataSet() {
        return sourceMA;
    }

    public String getAnalyzedStructure() {
        return resultMA;
    }

    public String getInformation() {
        return structureanalysisInfo;
    }

}
