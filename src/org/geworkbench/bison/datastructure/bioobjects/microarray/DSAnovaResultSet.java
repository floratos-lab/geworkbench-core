/**
 * 
 */
package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * @author yc2480
 * @version $Id$
 */
public interface DSAnovaResultSet<T extends DSGeneMarker> extends
		DSSignificanceResultSet<T> {
	
	public double[][] getResult2DArray();

	public double getPValue (T marker);
	public double getAdjPValue (T marker);
	public double getFStatistic (T marker);
	public double getMean(T marker, String label);
	public double getDeviation(T marker, String label);
	
	public void saveToFile(String filename);
	
	public void microarraySetViewSetter(DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view); //for microarraysetview injection
	public String[] significantMarkerNamesGetter(); //for panel injection
}
