/**
 * 
 */
package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.management.Script;

/**
 * @author yc2480
 * @version $id$
 */
public interface DSAnovaResultSet<T extends DSGeneMarker> extends
		DSSignificanceResultSet {

	public Double getSignificance(T marker);

	public void setSignificance(T marker, double signficance);

	public DSPanel<T> getSignificantMarkers();

	public double getCriticalPValue();

	public String[] getLabels(int index);

	public DSMicroarraySet getParentDataSet();

	public void sortMarkersBySignificance();
	
	public double[][] getResult2DArray();

	public double getPValue (T marker);
	public double getAdjPValue (T marker);
	public double getFStatistic (T marker);
	public double getMean(T marker, String label);
	public double getDeviation(T marker, String label);
	
	@Script
	public void saveToFile(String filename);

}
