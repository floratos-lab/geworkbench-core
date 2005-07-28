package org.geworkbench.bison.datastructure.chipchip;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.util.HashMap;
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
public class CSChipchipSet {
    Vector<CSChipchip> chipchips = new Vector<CSChipchip>();
    HashMap<DSGeneMarker, DSGeneMarker> markerStats = new HashMap<DSGeneMarker, DSGeneMarker>();

    public CSChipchipSet() {
        markerStats = new HashMap<DSGeneMarker, DSGeneMarker>();
    }

    public void addChipchip(CSChipchip chipchip) {
        this.chipchips.add(chipchip);
    }

    public void addGenericMarker(DSGeneMarker marker) {
        //        this.markerStats.set(marker.getSerial(), marker);
        this.markerStats.put(marker, marker);
    }

    public CSChipchip getChipchip(int index) {
        return chipchips.get(index);
    }

    public int getMarkerCount() {
        return markerStats.size();
    }

    public DSGeneMarker getMarker(DSGeneMarker marker) {
        return markerStats.get(marker);
    }

    public boolean contains(DSGeneMarker marker) {
        return markerStats.containsKey(marker);
    }

    DSMarkerValue getMarkerValue(CSChipchip chip, DSGeneMarker marker) {
        //DSMarker geneMarkerTmp = new CSMarker(marker);
        DSGeneMarker geneMarker = getMarker(marker);
        if (geneMarker != null) {
            return chip.getMarker(geneMarker.getSerial());
        } else {
            return null;
        }
    }

    public Vector<CSChipchip> getChipchips() {
        return chipchips;
    }
}
