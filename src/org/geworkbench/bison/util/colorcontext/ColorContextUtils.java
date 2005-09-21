package org.geworkbench.bison.util.colorcontext;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.util.CSCriterionManager;
import org.geworkbench.bison.util.Range;

/**
 * @author John Watkinson
 */
public class ColorContextUtils {

    public static void computeRange(DSMicroarraySetView<DSGeneMarker, DSMicroarray> view) {
        DSMicroarraySet<DSMicroarray> microarraySet = view.getMicroarraySet();
        // DSCriteria criteria = CSCriterionManager.getCriteria(microarraySet);
        // view.setItemPanel(criteria.getSelectedCriterion());
        // view.useItemPanel(true);
        for (DSGeneMarker marker : microarraySet.getMarkers()) {
            ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).reset(marker.getSerial(), 0, 0);
        }
        if (view.items().size() == 1) {
            // @todo - watkin - move this range calculation in to a different ColorContext, maybe?
            DSMicroarray ma = view.items().get(0);
            Range range = new org.geworkbench.bison.util.Range();
            for (DSGeneMarker marker : microarraySet.getMarkers()) {
                DSMutableMarkerValue mValue = (DSMutableMarkerValue) ma.getMarkerValue(marker.getSerial());
                double value = mValue.getValue();
                range.min = Math.min(range.min, value);
                range.max = Math.max(range.max, value);
                range.norm.add(value);
            }
            for (DSGeneMarker marker : microarraySet.getMarkers()) {
                Range markerRange = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).getRange();
                markerRange.min = range.min;
                markerRange.max = range.max;
                markerRange.norm = range.norm;
            }
        } else {
            for (DSGeneMarker marker : microarraySet.getMarkers()) {
                for (DSMicroarray ma : view.items()) {
                    DSMutableMarkerValue mValue = (DSMutableMarkerValue) ma.getMarkerValue(marker.getSerial());
                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).check(mValue, false);
                }
            }
        }
    }

}
