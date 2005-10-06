package org.geworkbench.engine.management;

import org.geworkbench.bison.ReferenceImplMap;

/**
 * Factory implementation to obtain concrete instances of <code>bison</code>
 * datatypes
 * @author manjunath at genomecenter dot columbia dot edu
 */

public class BisonFactory {

    public static <T> T createInstance(Class<T> klass){
        T instance = null;
        try {
            Class<T> implType = ReferenceImplMap.getDefaultImplementationMap().
                               getDefaultImplementation(klass);
            instance = implType.newInstance();
        } catch (InstantiationException ie) {
        } catch (IllegalAccessException iae) {}

        return instance;
    }

    public static Object createInstance(String clazz){
        Object instance = null;
        try {
            Class klass = Class.forName(clazz);
            Class implType = ReferenceImplMap.getDefaultImplementationMap().
                               getDefaultImplementation(klass);
            instance = implType.newInstance();
        } catch (ClassNotFoundException cnfe){
        } catch (InstantiationException ie){
        } catch (IllegalAccessException iae){}

        return instance;
    }
}
