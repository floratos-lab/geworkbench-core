package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

/**
 * @author John Watkinson
 */
public interface DSSignificanceResultSet <T extends DSGeneMarker> extends DSAncillaryDataSet {

    /**
     * Gets the significance value for the marker.
     */
    public double getSignificance(T marker);

    public DSMicroarraySet getParentDataSet();

}
