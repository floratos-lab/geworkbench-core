package org.geworkbench.bison.util.colorcontext;

import java.awt.Color;
import java.io.Serializable;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

public class ExpressionPValueColorContext implements ColorContext, Serializable {

	private static final long serialVersionUID = 7733639267835219074L;

	public ExpressionPValueColorContext() {
    }
	
    /**
     * @param mv        The <code>MarkerValue</code> that needs to be drawn.
     * @param intensity color intensity to be used
     * @return The <code>Color</code> to use for drawing.
     */
    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {

//        intensity *= 2;
        intensity = 2 / intensity; 
        double value = mv.getValue();
        org.geworkbench.bison.util.Range range = ((DSRangeMarker) mInfo).getRange();
        double mean = range.norm.getMean(); //(range.max + range.min) / 2.0;
        double foldChange = (value - mean) / (range.norm.getSigma() + 0.00001); //Math.log(change) / Math.log(2.0);
        if (foldChange < -intensity) {
            foldChange = -intensity;
        }
        if (foldChange > intensity) {
            foldChange = intensity;
        }

        double colVal = foldChange / intensity;
        if (foldChange > 0) {
            return new Color(1.0F, (float) (1 - colVal), (float) (1 - colVal));
        } else {
            return new Color((float) (1 + colVal), (float) (1 + colVal), 1.0F);
        }
        
    }

    public void updateContext(DSMicroarraySetView<DSGeneMarker, DSMicroarray> view) {
        ColorContextUtils.computeRange(view);
    }

    public Color getMaxColorValue(float intensity) {
        return Color.red;
    }

    public Color getMinColorValue(float intensity) {
        return Color.blue;
    }

    public Color getMiddleColorValue(float intensity) {
        return Color.white;
    }
}
