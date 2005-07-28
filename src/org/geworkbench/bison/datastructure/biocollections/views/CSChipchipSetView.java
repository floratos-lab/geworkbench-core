package org.geworkbench.bison.datastructure.biocollections.views;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSChipchip;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */

//This class is temporary.... to change
public class CSChipchipSetView extends CSMicroarraySetView<DSGeneMarker, DSChipchip> {

    public CSChipchipSetView() {
    }

    public double getValue(int markerIndex, int arrayIndex) {
        DSMicroarray ma = get(arrayIndex);
        DSGeneMarker marker = markers().get(markerIndex);
        return ma.getMarkerValue(marker).getValue();
    }

    public double getValue(DSGeneMarker marker, int arrayIndex) {
        //        DSMicroarray ma = get(arrayIndex);
        //        DSMarker markerValue = markers().get(marker);
        //        return ma.getMarkerValue(markerValue).getValue();
        return ((CSMicroarraySet) dataSet).getValue(marker, arrayIndex);
    }

    public double[] getRow(int index) {
        double[] rowVals = new double[this.size()];
        for (int itemCtr = 0; itemCtr < rowVals.length; itemCtr++) {
            rowVals[itemCtr] = getValue(index, itemCtr);
        }
        return rowVals;
    }

    public double[] getRow(DSGeneMarker marker) {
        DSGeneMarker markerValue = markers().get(marker);
        return getRow(markerValue.getSerial());
    }
}
