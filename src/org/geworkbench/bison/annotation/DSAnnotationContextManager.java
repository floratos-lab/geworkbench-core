package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * This defines the contract for a manager of contexts for the annotation, labelling and classification of the items
 * of a {@link DSItemList}.
 * <p>
 * For each data set, there can be one or more {@link DSAnnotationContext DSAnnotationContexts}. Additionally, there
 * is a <i>default</i> context associated with each data set.
 *
 * @author John Watkinson
 */
public interface DSAnnotationContextManager {

    public <T extends DSNamed> DSAnnotationContext<T>[] getAllContexts(DSItemList<T> itemList);

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, String name);

    public <T extends DSNamed> DSAnnotationContext<T> createContext(DSItemList<T> itemList, String name);

    public <T extends DSNamed> int getNumberOfContexts(DSItemList<T> itemList);

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, int index);

    public boolean removeContext(DSItemList itemList, String name);

    public boolean renameContext(DSItemList itemList, String oldName, String newName);

    public <T extends DSBioObject> DSAnnotationContext<T> getDefaultContext(DSItemList<T> itemList);
}
