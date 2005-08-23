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
    double threshold;
    int thresholdType;
    int missingValues;
    private DSPanel<DSGeneMarker> markerPanel;

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
        // Collect the parameters needed for the execution of the normalizer

        markerPanel = ((HouseKeepingGeneNormalizerPanel) aspp).getPanel();
        int houseKeepgeneNumber = markerPanel.size();
        for (int i = 0; i < markerPanel.size(); i++) {
            System.out.println("Selected" + markerPanel.get(i));
        }
        double[] ratioArray = getRatioArrary(maSet, markerPanel, BASEARRAY);

        int arrayCount = maSet.size();

        int markerCount = maSet.getMarkers().size();
        DSMicroarray microarray = null;
        DSMutableMarkerValue markerValue = null;
        for (int i = 0; i < arrayCount; i++) {
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
        DSMicroarray mArray = null;
        int arrayCount = maSet.size();
        DSMutableMarkerValue markerValue = null;
        double [][] arrays = new double[markerCount][arrayCount];

        double ratio[] = new double[arrayCount];
        for (int j = 0; j < markerCount; j++) {
            CSGeneMarker csgMarker = (CSGeneMarker) markerPanel.get(j);
            double[] expressProfile = maSet.getRow(csgMarker);
            for(int k=0; k<arrayCount; k++){
                arrays[j][k] = maSet.getValue(csgMarker, k);
                System.out.println(arrays[j][k]+" array");
            }
            //arrays[j] =  maSet.getRow(csgMarker);
        }
//        for (int i = 0; i < arrayCount; i++) {
//            ratio[i] = 0d;
//            mArray = (DSMicroarray) maSet.get(i);
//            for (int j = 0; j < markerCount; j++) {
//                CSGeneMarker csgMarker = (CSGeneMarker) markerPanel.get(j);
//                markerValue = mArray.getMarkerValue(csgMarker);
//                if (!markerValue.isMissing()) {
//                    ratio[i] += markerValue.getValue();
//                    System.out.println(csgMarker.getLabel() + markerValue.getValue() + ratio[i]);
//                }
//
//            }
//
//        }

        for (int i = 0; i < arrayCount; i++) {
            ratio[i] = 0d;

            for (int j = 0; j < markerCount; j++) {

                    ratio[i] += arrays[j][i];
                    System.out.println(arrays[j][i]+"csgMarker.getLabel()" + ratio[i]);
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
