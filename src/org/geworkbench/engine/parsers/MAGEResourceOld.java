package org.geworkbench.engine.parsers;

import gov.nih.nci.mageom.bean.BioAssay.BioAssayImpl;
import gov.nih.nci.mageom.bean.Experiment.ExperimentImpl;
import gov.nih.nci.mageom.domain.Experiment.Experiment;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 * @todo - Phase this class out in favor of the new MAGEResource
 */

/**
 * Encapsulates the data for a microarray as captured by a MAGE BioAssayData
 * object.
 */
public class MAGEResourceOld {
    public final static int INVALID_TYPE = 0;
    public final static int AFFY_TYPE = 1;
    public final static int GENEPIX_TYPE = 2;
    /**
     * A remote bioassay
     */
    protected BioAssayImpl[] m_baImpls = null;
    /**
     * The host <code>Experiment</code> for the microarray <code>baData</code>.
     */
    protected Experiment experiment = null;

    public MAGEResourceOld(BioAssayImpl[] baImpls, Experiment exp) {
        m_baImpls = baImpls;
        experiment = exp;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public BioAssayImpl[] getBioAssays() {
        return m_baImpls;
    }

    /**
     * Identifies the array technology (e.g., Affy, GenePix, etc.) underlying
     * the array data encapsulated by the resource.
     *
     * @return The array type.
     */
    public int getArrayType() {
        if (m_baImpls == null || m_baImpls.length == 0) {
            System.out.println("No bioassay.");
            return INVALID_TYPE;
        }

        if (experiment == null) {
            System.out.println("No experiment.");
            return INVALID_TYPE;
        }

        String platform = ((ExperimentImpl) experiment).getPlatformType();
        System.out.println("Platform: " + platform);
        if (isAffymetrix(platform))
            return AFFY_TYPE;
        else if (isGenepix(platform))
            return GENEPIX_TYPE;
        else
            return INVALID_TYPE;
    }

    /**
     * Returns the <code>BioAssayImpl[]</code> object encapsulated by the resource.
     *
     * @return
     */
    public BioAssayImpl[] getData() {
        return m_baImpls;
    }

    /**
     * Checks if the experiment platform is Affymetrix.
     *
     * @param platform String returned by a call to
     *                 <code>ExperimentImpl.getPlatformType</code>.
     * @return <code>true</code> if the experiment platform is Affy.
     *         <code>false</code> otherwise.
     */
    private boolean isAffymetrix(String platform) {
        if (platform != null && (platform.equalsIgnoreCase("Affymetrix") || platform.equalsIgnoreCase("Affy-MAS 5.0")))
            return true;
        else
            return false;
    }

    /**
     * Checks if the experiment platform is Genepix.
     *
     * @param platform String returned by a call to
     *                 <code>ExperimentImpl.getPlatformType</code>.
     * @return <code>true</code> if the experiment platform is Genepix.
     *         <code>false</code> otherwise.
     */
    private boolean isGenepix(String platform) {
        if (platform != null && (platform.equalsIgnoreCase("Long Oligo - Spotted") || (platform.equalsIgnoreCase("cDNA - Spotted"))))
            return true;
        else
            return false;
    }

}

