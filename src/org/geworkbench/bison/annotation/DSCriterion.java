package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * @author John Watkinson
 */
public interface DSCriterion<T extends DSBioObject> {

    public boolean applyCriterion(T item, DSAnnotationSource<T> annotationContext);

}
