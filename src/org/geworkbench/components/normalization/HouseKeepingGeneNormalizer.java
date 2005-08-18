package org.geworkbench.components.normalization;

import org.geworkbench.analysis.AbstractAnalysis;
import org.geworkbench.bison.datastructure.biocollections.microarrays.
        DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.
        DSMutableMarkerValue;
import org.geworkbench.engine.model.analysis.AlgorithmExecutionResults;
import org.geworkbench.engine.model.analysis.NormalizingAnalysis;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

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
        return THRESHOLD_NORMALIZER_TYPE;
    }

    public AlgorithmExecutionResults execute(Object input) {
        if (input == null) {
            return new AlgorithmExecutionResults(false, "Invalid input.", null);
        }
        assert input instanceof DSMicroarraySet;
        DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet) input;
        // Collect the parameters needed for the execution of the normalizer
//        threshold = ((HouseKeepingGeneNormalizerPanel) aspp).getCutoffValue();
//        thresholdType = ((HouseKeepingGeneNormalizerPanel) aspp).getCutoffType();
//        missingValues = ((HouseKeepingGeneNormalizerPanel) aspp).getMissingValueTreatment();
        markerPanel = ((HouseKeepingGeneNormalizerPanel) aspp).getMarkerPanel();
        for (int i = 0; i < markerPanel.size(); i++) {
            System.out.println(markerPanel.get(i));
        }

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
                    if ((thresholdType == MINIMUM) &&
                        (markerValue.getValue() < threshold)) {
                        markerValue.setValue(threshold);
                    } else if ((thresholdType == MAXIMUM) &&
                               (markerValue.getValue() > threshold)) {
                        markerValue.setValue(threshold);
                    }

                } else if (missingValues == REPLACE) {
                    markerValue.setValue(threshold);
                    markerValue.setMissing(false);
                } else if (missingValues == IGNORE) {
                    //Do nothing
                }

            }

        }

        return new AlgorithmExecutionResults(true, "No errors", input);
    }

    public String getType() {
        return "Threshold Normalizer";
    }

    private void jbInit() throws Exception {
    }

}
