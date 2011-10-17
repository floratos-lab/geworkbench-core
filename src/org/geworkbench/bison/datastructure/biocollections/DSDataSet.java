package org.geworkbench.bison.datastructure.biocollections;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSDescribable;
import org.geworkbench.bison.datastructure.properties.DSExtendable;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * Implementing classes store sets of biological data (such as a set of microarrays or sequences).
 */
public interface DSDataSet <T extends DSBioObject> extends DSDescribable, DSNamed, Serializable, DSExtendable, DSItemList<T> {

    /**
     * The name of data set.
     *
     * @return the data set name.
     */
    public String getDataSetName();

    /**
     * The underlying file associated with this data set.
     *
     * @return the file for this data set.
     * @todo - watkin - This should be phased out and replaced with the resource-management methods.
     */
    public File getFile();

    public void setFile(File file);

    /**
     * Writes the data set to the provided filename.
     *
     * @param fileName the filename (path) to which to write this file.
     * @todo - watkin - This should be phased out and replaced with the resource-management methods.
     */
    public void writeToFile(String fileName);

    /**
     * Gets an object by the specified type.
     *
     * @param objectType the object specifying the type of the required object.
     * @return the requested object
     * @todo - watkin - This and {@link #addObject(Object, Object)} are very confusing and not type-safe.
     */
    public Object getObject(Class<?> objectType);

    /**
     * Stores an arbitrary object by type.
     *
     * @param objectType the object specifying the type.
     * @param anObject   the object to store.
     * @todo - watkin - This and {@link #getObject(Object)} are very confusing and not type-safe.
     */
    public void addObject(Class<?> objectType, Object anObject);

    /**
     * Gets the activation status of the data set.
     *
     * @return <code>true</code> if active, <code>true</code> otherwise.
     */
    public boolean isActive();

    /**
     * Gets a string describing the compatibility of this data set.
     *
     * @return the compatibility label.
     */
    public String getCompatibilityLabel();

    /**
     * Returns the experiment information associated with the data set.
     * What constitutes "experiment information" varies, depending on the
     * input source where the data set came from. E.g., for Affy input
     * files it is the file preamble (the part just before the data lines which
     * provides various experiment execution parameters) that becomes the experiment
     * information.
     *
     * @return the experiment information string.
     */
    public String getExperimentInformation();

    /**
     * Sets the experiment information.
     *
     * @param experimentInformation the experiment information.
     */
    public void setExperimentInformation(String experimentInformation);

	public ArrayList<Integer> getColumnOrder();
	public void setColumnOrder(ArrayList<Integer> columnOrder);

	public void deactivate();

	public void activate();
}
