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
 * Application event thrown in order to communicate the results of annotation analysis
 */
public class AnnotationAnalysisEvent extends Event {
    /**
     * The pdb that was the input to the annotation analysis operation.
     */
    private DSProteinStructure sourceMA = null;
    /**
     * The pdb that was the result of the annotation analysis operation.
     */
    private String resultMA = null;
    /**
     * Information about the annotation analysis used.
     */
    private String annotationanalysisInfo = null;

    public AnnotationAnalysisEvent(DSProteinStructure s, String r, String info) {
        super(null);
        sourceMA = s;
        resultMA = r;
        annotationanalysisInfo = info;
    }

    public DSProteinStructure getDataSet() {
        return sourceMA;
    }

    public String getAnalyzedStructure() {
        return resultMA;
    }

    public String getInformation() {
        return annotationanalysisInfo;
    }

}
