package org.geworkbench.engine.parsers;

import gov.nih.nci.mageom.domain.BioAssay.BioAssay;
import gov.nih.nci.mageom.domain.Experiment.Experiment;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public class CaArrayResource {

    /**
     * A collection of remote bioassays
     */
    private BioAssay[] assays = null;

    /**
     * The host <code>Experiment</code> for the microarray <code>baData</code>.
     */
    private Experiment experiment = null;

    public CaArrayResource(BioAssay[] ba, Experiment exp) {
        assays = ba;
        experiment = exp;
    }

    public BioAssay[] getBioAssays() {
        return assays;
    }

    public Experiment getExperiment() {
        return experiment;
    }
}
