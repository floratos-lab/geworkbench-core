package org.geworkbench.bison.parsers.resources;

import MAGE.BioAssay;
import MAGE.Experiment;

public class MAGEResource {

    /**
     * A collection of remote bioassays
     */
    private BioAssay[] assays = null;

    /**
     * The host <code>Experiment</code> for the microarray <code>baData</code>.
     */
    private Experiment experiment = null;

    public MAGEResource(BioAssay[] ba, Experiment exp) {
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
