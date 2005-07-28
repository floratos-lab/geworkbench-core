package org.geworkbench.engine.management;

import java.util.HashMap;

/**
 * Date: Apr 25, 2005
 * Time: 5:12:57 PM
 * Company: Columbia University
 * Author: John Watkinson
 * <p/>
 * A HashMap that supports polymorphism. So, if there is an entry for java.lang.Number, but not for java.lang.Float,
 * then passing in java.lang.Float the get statement will return the java.lang.Number entry.
 */
public class TypeMap <T> extends HashMap<Class, T> {

    public TypeMap() {
        super();
    }

    @Override public T get(Object key) {
        Class type = (Class) key;
        T result = super.get(type);
        while ((result == null) && (type != null)) {
            type = type.getSuperclass();
            result = super.get(type);
        }
        return result;
    }

}
