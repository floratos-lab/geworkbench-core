package org.geworkbench.bison.util.colorcontext;

import java.awt.Color;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

public class AbsoluteWhiteColorContext extends DefaultColorContext {
	private static final long serialVersionUID = -4851762788585835541L;

    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        Color color = super.getMarkerValueColor(mv, mInfo, intensity);
        if (color == MISSING_VALUE_COLOR) return MISSING_VALUE_COLOR;

        int r = color.getRed(), g = color.getGreen();
        if (mv.getValue() > 0) color = new Color(255, 255-r, 255-r);
        else color = new Color(255-g, 255, 255-g);
        return color;
    }

    public Color getMiddleColorValue(float intensity) {
        return Color.white;
    }
}
