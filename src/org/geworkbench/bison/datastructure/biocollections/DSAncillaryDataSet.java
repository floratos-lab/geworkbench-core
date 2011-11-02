package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * Extends the notion of a data set by adding access to parent dataset.
 *
 */
public interface DSAncillaryDataSet<T extends DSBioObject> extends DSDataSet<T> {

    /**
     * Gets the parent data set for this ancillary data set.
     * @return
     */
    public DSDataSet<T> getParentDataSet();
}
