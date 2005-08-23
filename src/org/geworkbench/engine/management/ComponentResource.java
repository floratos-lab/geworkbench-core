package org.geworkbench.engine.management;

import java.net.URLClassLoader;

/**
 * Represents a component (or set of components), their configuration, and required libraries.
 * <p>
 * Component Resource directories must have the component classes in a subdirectory called
 * <tt>classes</tt> and a lib directory in a subdirectory called <tt>lib</tt>. Only
 * <tt>.zip</tt> and <tt>.jar</tt> files will be loaded from the lib directory.
 * <p>
 * Visual components may have an optional <tt>.cwb.xml</tt> file with component configuration
 * in the <tt>classes</tt> directory at the same level as the class that extends
 * {@link org.geworkbench.engine.config.VisualPlugin}.
 *
 * @author John Watkinson
 */
public class ComponentResource {

    /**
     * The directory in which the component resides.
     */
    private String dir;

    /**
     * The class loader for the resource.
     */
    private URLClassLoader classLoader;

    /**
     * Creates a new component resource rooted in the given directory.
     * @param dir the directory for the component resource.
     */
    public ComponentResource(String dir) {
        this.dir = dir;
    }

    // todo - finish this class
}
