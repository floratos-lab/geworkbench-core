package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * This defines the contract for a manager of contexts for the annotation, labelling and classification of the items
 * of a {@link DSDataSet}.
 * <p>
 * For each data set, there can be one or more {@link DSAnnotationContext DSAnnotationContexts}. Additionally, there
 * is a <i>default</i> context associated with each data set.
 *
 * @author John Watkinson
 */
public interface DSAnnotationContextManager {

    public <T extends DSBioObject> DSAnnotationContext<T>[] getAllContexts(DSDataSet<T> dataSet);

    public <T extends DSBioObject> DSAnnotationContext<T> getContext(DSDataSet<T> dataSet, String name);

    public <T extends DSBioObject> DSAnnotationContext<T> createContext(DSDataSet<T> dataSet, String name);

    public boolean removeContext(DSDataSet dataSet, String name);

    public boolean renameContext(DSDataSet dataSet, String oldName, String newName);

    public <T extends DSBioObject> DSAnnotationContext<T> getDefaultContext(DSDataSet<T> dataSet);
}
