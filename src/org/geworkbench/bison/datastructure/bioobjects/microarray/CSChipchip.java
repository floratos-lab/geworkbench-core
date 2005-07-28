package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

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
public class CSChipchip extends CSMicroarray implements DSChipchip {
    DSGeneMarker transcriptionFactor = new CSGeneMarker();

    public CSChipchip(int markerNo) {
        super(markerNo);
    }

    public CSChipchip(int id, int markerNo) {
        super(markerNo);
        this.serial = id;
    }

    public DSGeneMarker getTranscriptionFactor() {
        return transcriptionFactor;
    }

    public void setTranscriptionFactor(DSGeneMarker transcriptionFactor) {
        this.transcriptionFactor = transcriptionFactor;
    }
}
