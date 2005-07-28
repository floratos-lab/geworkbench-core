package org.geworkbench.bison.datastructure.chipchip;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CSChipchip {
    Vector<DSMarkerValue> markers;
    DSGeneMarker factor;
    String name;

    public CSChipchip(int markerCount) {
        markers = new Vector<DSMarkerValue>(markerCount);
        markers.setSize(markerCount);
    }

    public CSChipchip(String name, int markerCount) {
        this.name = name;
        markers = new Vector<DSMarkerValue>(markerCount);
        markers.setSize(markerCount);
    }

    public void setMarkerValue(DSMarkerValue marker, int index) {
        markers.set(index, marker);
    }

    public DSMarkerValue getMarker(int index) {
        return markers.get(index);
    }

    public DSGeneMarker getFactor() {
        return factor;
    }

    public void setFactor(DSGeneMarker factor) {
        this.factor = factor;
    }
}
