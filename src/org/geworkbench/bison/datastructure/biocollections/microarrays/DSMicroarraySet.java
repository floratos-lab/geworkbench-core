package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

/**
 * Defines a generic set of Microarrays.
 * <p/>
 * DSDataSet. The objects in a matrix dataset will themselves contain vectors of values. -- AM
 * todo - watkin - this should have two generic type variables, one for microarray, one for marker.
 * Currently, it is just generic for microarrays.
 * 
 * @version $Id$
 */
public interface DSMicroarraySet extends DSDataSet<DSMicroarray> {

	// type of marker value objects
    public final static int DO_NOT_CREATE_VALUE_OBJECT = 0;
    public final static int expPvalueType = 3; // added by xuegong
    public final static int genepixGPRType = 5;
    public final static int affyTxtType = 6; // Txt MAS 4.0/5.0 file type

    public double getValue(int markerIndex, int microarrayIndex);

    public double getValue(DSGeneMarker marker, int maIndex);

    public double getMeanValue(DSGeneMarker marker, int maIndex);

    public double[] getRow(DSGeneMarker marker);

    /**
     * Merges another <code>MicroarraySet</code> into this one
     *
     * @param newMaSet MicroarraySet
     */
    void mergeMicroarraySet(DSMicroarraySet newMaSet) throws Exception;

    /**
     * get the marker list
     *
     * @return DSItemList
     */
    public DSItemList<DSGeneMarker> getMarkers();

    void setCompatibilityLabel(String compatibilityLabel);

    public void initializeMarkerVector(int markerCount);
    
    public String getAnnotationFileName();
    public void setAnnotationFileName(String annotationFileName);

    public void sortMarkers(int mrkNo);
    // marker indices in sorted marker list for data file loading
    public int[] getNewMarkerOrder();

    // marker order in marker selector panel: gene, probe, original
    public String getSelectorMarkerOrder();
    public void setSelectorMarkerOrder(String order);
    public void writeToTabDelimFile(String fileName);
}
