package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
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
