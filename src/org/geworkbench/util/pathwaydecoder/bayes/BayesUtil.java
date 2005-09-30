package org.geworkbench.util.pathwaydecoder.bayes;

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

public class BayesUtil {
    public BayesUtil() {
    }

    public Table convertToTable(DSMicroarraySet<DSMicroarray> mArraySet) {
        Table data = new Table(false);
        if (mArraySet != null){
            DSItemList<DSGeneMarker> markerInfos = mArraySet.getMarkers();
            for (DSGeneMarker m : markerInfos) {
                Attribute attr = new Attribute(m.getDescription());
                data.addAttribute(attr);
            }

            for (DSMicroarray ma : mArraySet) {
                Tuple tup = new Tuple();
                DSMarkerValue[] markerValues = ma.getMarkerValues();
                for (int mvCtr = 0; mvCtr < markerValues.length; mvCtr++) {
                    tup.addValue(new Double(markerValues[mvCtr].getValue()));
                }
                data.addTuple(tup);
            }
        }
        return data;
    }

    public Table convertToTable(DSMicroarraySet mArraySet, DSItemList<DSMicroarray> mArrays) {
        Table data = new Table(false);
        if (mArraySet != null && mArrays != null){
            DSItemList<DSGeneMarker> markerInfos = mArraySet.getMarkers();
            for (DSGeneMarker m : markerInfos) {
                Attribute attr = new Attribute(m.getDescription());
                data.addAttribute(attr);
            }

            for (DSMicroarray ma : mArrays) {
                Tuple tup = new Tuple();
                DSMarkerValue[] markerValues = ma.getMarkerValues();
                for (int mvCtr = 0; mvCtr < markerValues.length; mvCtr++) {
                    tup.addValue(new Double(markerValues[mvCtr].getValue()));
                }
                data.addTuple(tup);
            }
        }

        return data;
    }
}
