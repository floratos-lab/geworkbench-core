package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import affymetrix.fusion.cel.FusionCELData;

/**
 * Stores Probe Intensities as loaded from a CEL file.
 * User: mhall
 * Date: Mar 13, 2006
 * Time: 11:43:22 AM
 */
public class CSProbeIntensityArray extends CSAncillaryDataSet implements DSProbeIntensityArray {

    static Log log = LogFactory.getLog(CSProbeIntensityArray.class);

    File dataFile = null;
    private String chipType;
    private float[][] probeIntensities;
    private float[] minMax;

    public CSProbeIntensityArray(DSDataSet parent, String label) {
        super(parent, label);
    }

    public CSProbeIntensityArray(DSDataSet parent, String label, String chipType, float[][] probeIntensities) {
        super(parent, label);
        this.chipType = chipType;
        this.probeIntensities = probeIntensities;
    }

    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }

    public void processFile() {
        if (dataFile == null) {
            return;
        }
        log.debug("File: " + dataFile.getAbsolutePath());
        FusionCELData loader = new FusionCELData();
        loader.setFileName(dataFile.getAbsolutePath());
        loader.read();
        try {
            chipType = loader.getChipType();
        } catch (Exception e) {
            log.warn("Error reading chip type.");
        }

        probeIntensities = new float[loader.getRows()][loader.getCols()];
        minMax = fillArrayReturnMinMax(probeIntensities, loader);
    }

    public String getChipType() {
        return chipType;
    }

    public void setChipType(String chipType) {
        this.chipType = chipType;
    }

    public float[][] getProbeIntensities() {
        return probeIntensities;
    }

    public void setProbeIntensities(float[][] probeIntensities) {
        this.probeIntensities = probeIntensities;
    }

    public float[] getMinMax() {
        return minMax;
    }

    public void setMinMax(float[] minMax) {
        this.minMax = minMax;
    }

    private float[] fillArrayReturnMinMax(float[][] intensityData, FusionCELData loader) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < intensityData.length; i++) {
            float[] floats = intensityData[i];
            for (int j = 0; j < floats.length; j++) {
                floats[j] = loader.getIntensity(j, i);
                double logv = Math.log10(floats[j]);
                if (logv < min) {
                    min = (float) logv;
                }
                if (logv > max) {
                    max = (float) logv;
                }
            }
        }
        return new float[]{min, max};
    }

}
