package org.geworkbench.engine.management;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;

/**
 * A classloader that allows class definitions in the component to override those of the parent.
 *
 * @author John Watkinson
 */
public class ComponentClassLoader extends URLClassLoader {

    private ClassLoader parent;
    private ComponentResource resource;

    public ComponentClassLoader(URL[] urls, ComponentResource resource) {
        super(urls);
        this.resource = resource;
        parent = getParent();
        if (parent == null) {
            parent = getSystemClassLoader();
        }
    }

    public ComponentClassLoader(URL[] urls, ClassLoader parent, ComponentResource resource) {
        super(urls, parent);
        this.resource = resource;
        this.parent = parent;
        if (parent == null) {
            parent = getSystemClassLoader();
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }

    /**
     * Attempts to load the class in this classloader before deferring to parent.
     */
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> type = findLoadedClass(name);
        if (type == null) {
            try {
                type = findClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore-- type will be null
            }
        }
        if (type == null) {
            try {
                type = parent.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore-- type will be null
            }
        }
        if (type == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(type);
        }
        return type;
    }

    public ComponentResource getComponentResource() {
        return resource;
    }
}
