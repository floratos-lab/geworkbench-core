package org.geworkbench.util.colorcontext;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Decides what color should be used when dispaying a color mosaic representation
 * of a marker value.
 */
public interface ColorContext {

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity);

    /**
     * @param mv        The <code>MarkerValue</code> that needs to be drawn.
     * @param mInfo     The <code>MarkerInfo</code> corresponding to the
     *                  <code>MarkerValue</code>
     * @param intensity to be used
     * @return The <code>Color</code> to use for drawing.
     */
    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity);
}
