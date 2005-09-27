package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * @author John Watkinson
 */
public interface DSAnnotationSource<T extends DSBioObject> {

    public <Q> Q getAnnotationForItem(T item, DSAnnotationType<Q> annotationType);

}
