package org.geworkbench.components.normalization;

import org.geworkbench.analysis.AbstractAnalysis;
import org.geworkbench.bison.datastructure.biocollections.microarrays.
        DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.
        DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.model.analysis.AlgorithmExecutionResults;
import org.geworkbench.bison.model.analysis.NormalizingAnalysis;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import javax.swing.JOptionPane;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;

/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Columbia University.</p>
 * @author Xiaoqing Zhang
 * @version 1.0
 */

/**
 * Normalization the expression values based on the assumption that the expression level of housekeeping genes
 * are constant in different experiments.
 */
public class HouseKeepingGeneNormalizer extends AbstractAnalysis implements
        NormalizingAnalysis {
    // Static fields used to designate the available user option within the
    // normalizer's parameters panel.
    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 1;
    public static final int IGNORE = 0;
    public static final int REPLACE = 1;
    public static final int BASEARRAY = 0;
    public static StringBuffer errorMessage = new StringBuffer();
    double threshold;
    int thresholdType;
    int missingValues;
    private DSPanel<DSGeneMarker> markerPanel;
    private boolean[] ExistedMarkers;
    private boolean haveNonExistMarker = false;

    public HouseKeepingGeneNormalizer() {
        setLabel("HouseKeeping Genes Normalizer");
        setDefaultPanel(new HouseKeepingGeneNormalizerPanel());
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getAnalysisType() {
        return HOUSEKEEPINGGENES_VALUE_NORMALIZER_TYPE;
    }

    public AlgorithmExecutionResults execute(Object input) {
        if (input == null) {
            return new AlgorithmExecutionResults(false, "Invalid input.", null);
        }
        assert input instanceof DSMicroarraySet;
        DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet) input;
        errorMessage = new StringBuffer();
        // Collect the parameters needed for the execution of the normalizer

        markerPanel = ((HouseKeepingGeneNormalizerPanel) aspp).getPanel();
        int houseKeepgeneNumber = markerPanel.size();
        ExistedMarkers = new boolean[houseKeepgeneNumber];
        double[] ratioArray = getRatioArrary(maSet, markerPanel, BASEARRAY);
        if (haveNonExistMarker) {
            JOptionPane.showMessageDialog(null,
                                          errorMessage.toString() + "cannot be found in the dataset",
                                          "Warning", JOptionPane.ERROR_MESSAGE);
        }
        int arrayCount = maSet.size();
        int markerCount = maSet.getMarkers().size();
        DSMicroarray microarray = null;
        DSMutableMarkerValue markerValue = null;
        for (int i = 0; i < arrayCount; i++) {
            if (Double.isNaN(ratioArray[i])) {
                continue;
            }
            for (int j = 0; j < markerCount; j++) {
                microarray = maSet.get(i);
                markerValue = (DSMutableMarkerValue) microarray.getMarkerValue(
                        j);
                if (!markerValue.isMissing()) {
                    markerValue.setValue(markerValue.getValue() * ratioArray[i]);

                }
            }

        }
        return new AlgorithmExecutionResults(true, "No errors", input);
    }

    /**
     * getRatioArrary
     *
     * @param maSet DSMicroarraySet
     * @param markerPanel DSPanel
     * @param baseArray int
     * @return double[]
     */
    public double[] getRatioArrary(DSMicroarraySet maSet, DSPanel markerPanel,
                                   int baseArray) {
        int arrayCount = maSet.size();
        double[] rawValues = getHouseKeepingGenesValue(maSet, markerPanel);
        double[] ratioArray = new double[arrayCount];
        for (int i = 0; i < arrayCount; i++) {
            ratioArray[i] = rawValues[BASEARRAY] / rawValues[i];
        }
        return ratioArray;
    }


    /**
     * sum the housekeeping genes expression values together.
     *
     * @param maSet DSMicroarraySet
     * @param markerPanel DSPanel
     * @return double[]
     */

    public double[] getHouseKeepingGenesValue(DSMicroarraySet maSet,
                                              DSPanel markerPanel) {
        int markerCount = markerPanel.size();

        double baseTotal = 0d;
        // DSMicroarray mArray = null;
        int arrayCount = maSet.size();
        DSMutableMarkerValue markerValue = null;
        double[][] arrays = new double[markerCount][arrayCount];
        double ratio[] = new double[arrayCount];

        //Because of a bug in CSMicroarray, getMarkerValue(DSGeneMarker mInfo)
        //does not return correct value, it always return the first marker when the CSGeneMarker
        // does not have a serial number >0. so instead, CSMicroarraySet.getRow(DSGeneMakrer)
        //is used here.

        for (int j = 0; j < markerCount; j++) {
            ExistedMarkers[j] = true;
            //CSGeneMarker csgMarker = (CSGeneMarker) markerPanel.get(j);






            int originIndex =0;
            CSGeneMarker cgMarker = (CSGeneMarker)maSet.getMarkers().get(0);
            System.out.println(maSet.getValue(cgMarker, 0) + " " + maSet.getValue(originIndex, 0));
            System.out.println(cgMarker.getLabel() + "|" + cgMarker.getGeneId() + "|" + cgMarker.getSerial());
            CSMicroarray csmicroarray = (CSMicroarray)maSet.get(0);

            System.out.println(csmicroarray.getMarkerValue(cgMarker) + " " + csmicroarray.getMarkerValue(originIndex));

             DSItemList csm = maSet.getMarkers();
            for (Object obj:csm){
                CSGeneMarker csgMarker = (CSGeneMarker)obj;
                System.out.println(csgMarker.getLabel() + "|" + csgMarker.getGeneId() + csgMarker.getSerial());
                System.out.println(maSet.getValue(csgMarker, 0) + " " + maSet.getValue(originIndex++, 0));
            }
            CSGeneMarker csgMarker = (CSGeneMarker)maSet.getMarkers().get(0);

            double[] expressProfile = maSet.getRow(csgMarker);

            System.out.println(csgMarker.getLabel() + "|" + expressProfile[0] + csgMarker.getGeneId() + csgMarker.getSerial());
            for (int k = 0; k < arrayCount; k++) {
                arrays[j][k] = maSet.getValue(csgMarker, k);

                if (Double.isNaN(arrays[j][k])) {
                    errorMessage.append(csgMarker.getLabel() + " ");
                    ExistedMarkers[j] = false;
                    haveNonExistMarker = true;
                    break;
                }
            }
        }

        for (int i = 0; i < arrayCount; i++) {
            ratio[i] = 0d;

            for (int j = 0; j < markerCount; j++) {
                if (ExistedMarkers[j]) {
                    ratio[i] += arrays[j][i];
                }
            }
        }

        return ratio;
    }

    public String getType() {
        return "HouseKeepingGene Normalizer";
    }

    private void jbInit() throws Exception {
    }

}
