package org.geworkbench.bison.util.colorcontext;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Default implementation of a color context. Assigns positive marker values
 * in the red spectrum and negative values to the green spectrum.
 */

public class DefaultColorContext implements org.geworkbench.bison.util.colorcontext.ColorContext {

    private final Color MISSING_VALUE_COLOR = Color.GRAY;

    public DefaultColorContext() {
    }

    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        if (mv == null || mv.isMissing())
            return MISSING_VALUE_COLOR;
        double value = mv.getValue();
        intensity *= 1000f;
        if (mv instanceof DSGenepixMarkerValue)
            value *= 1000f;
        int max = 999999;
        Color color = null;
        float v = (float) ((value) / (2 * max)) * intensity;
        if (v > 0) {
            v = Math.min(1.0F, v);
            color = new Color(v, 0F, 0F);
        } else {
            v = Math.min(1.0F, -v);
            color = new Color(0F, v, 0F);
        }
        return color;
    }

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        return null;
    }
}
