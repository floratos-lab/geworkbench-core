package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * This interface defines a criterion that can be applied
 *
 * @author John Watkinson
 */
public interface DSCriterion<T extends DSBioObject> {

    /**
     * Applies the criterion to an item.
     * @param item the item to which to apply the criterion.
     * @param annotationSource the source of annotation data for the item.
     * @return <tt>true</tt> if the item fulfills the criterion, <tt>false</tt> otherwise.
     */
    public boolean applyCriterion(T item, DSAnnotationSource<T> annotationSource);

}
