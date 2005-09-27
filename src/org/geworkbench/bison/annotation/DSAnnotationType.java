package org.geworkbench.bison.annotation;

/**
 * @author John Watkinson
 */
public interface DSAnnotationType<T> {

    public Object getLabel();

    public Class<T> getType();
    
}
