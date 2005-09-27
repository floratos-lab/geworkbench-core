package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import java.util.Set;
import java.util.List;

/**
 * @author John Watkinson
 */
public interface DSAnnotationContext<T extends DSBioObject> extends DSAnnotationSource<T> {

    public String getName();

    /**
     * When this context is used in conjunction with a {@link DSAnnotationContextManager}, do not use this method.
     * Use {@link DSAnnotationContextManager#renameContext(org.geworkbench.bison.datastructure.biocollections.DSDataSet, String, String)} instead.
     */
    public void setName(String newName);

    //// ANNOTATIONS

    public boolean addAnnotationType(DSAnnotationType annotationType);

    public boolean removeAnnotationType(DSAnnotationType annotationType);

    public int getNumberOfAnnotationTypes();

    public DSAnnotationType getAnnotationType(int index);

    public <Q> void annotateItem(T item, DSAnnotationType<Q> annotationType, Q value);

    public boolean removeAnnotationFromItem(T item, DSAnnotationType annotationType);

    public DSAnnotationType[] getAnnotationTypesForItem(T item);

    //// LABELS

    public boolean addLabel(Object label);

    public boolean addCriterionLabel(Object label, DSCriterion<T> criterion);

    public boolean removeLabel(Object label);

    public boolean isCriterionLabel(Object label);

    public DSCriterion<T> getCriterionForLabel(Object label);

    public int getNumberOfLabels();

    public Object getLabel(int index);

    public void setLabelActive(Object label, boolean active);

    public boolean isLabelActive(Object label);

    public boolean labelItem(T item, Object label);

    public DSPanel<T> getActiveItems();

    public DSPanel<T> getItems(Object label);

    public DSPanel<T> getItemsWithAny(Object... labels);

    public DSPanel<T> getItemsWithAll(Object... labels);

    public List getLabelsForItem(T item);

    public boolean removeLabelFromItem(T item, Object label);

    //// CLASSIFICATIONS

    public boolean addClassification(Object classification);

    public boolean removeClassification(Object classification);

    public int getNumberOfClassifications();

    public Object getClassification(int index);

    public void classifyItem(T item, Object classification);

    public void classifyAllItems(DSPanel<T> items, Object classification);

    public Object getClassificationForItem(T item);

    public void removeClassificationFromItem(T item, Object classification);

    public DSPanel<T> getItemsForClassification(Object classification);

}
