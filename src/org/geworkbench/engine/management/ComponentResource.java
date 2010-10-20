package org.geworkbench.engine.management;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a component (or set of components), their configuration, and required libraries.
 * <p/>
 * Component Resource directories must have the component classes in a subdirectory called
 * <tt>classes</tt> and a lib directory in a subdirectory called <tt>lib</tt>. Only
 * <tt>.zip</tt> and <tt>.jar</tt> files will be loaded from the lib directory.
 * <p/>
 * Visual components may have an optional <tt>.cwb.xml</tt> file with component configuration
 * in the <tt>classes</tt> directory at the same level as the class that extends
 * {@link org.geworkbench.engine.config.VisualPlugin}.
 *
 * @author John Watkinson
 * @version $Id$
 */
public class ComponentResource {

    static Log log = LogFactory.getLog(ComponentResource.class);

    public static final String LIB_DIR = "lib";
    public static final String CLASSES_DIR = "classes";
    public static final String CONF_DIR = "conf";

    /**
     * The directory in which the component resides.
     */
    private String dir;

    private URL baseURL;

    /**
     * The class loader for the resource.
     */
    private URLClassLoader classLoader;

    /**
     * Used to search for classes of a certain type within this resource.
     */
    private ClassSearcher classSearcher;

    /**
     * Indicates whether this resource was originally loaded from a gear file or not
     */
    private boolean isFromGear = false;

    private String name;

    /**
     * Creates a new component resource rooted in the given directory.
     *
     * @param dir the directory for the component resource.
     */
    public ComponentResource(String dir, boolean isFromGear) throws IOException {
        this.dir = dir;
        name = new File(dir).getName();
        this.isFromGear = isFromGear;
        classLoader = createClassLoader();
    }

    /**
     * Use this to create the component resource for the built-in components only.
     * @param classLoader the root classloader
     */
    public ComponentResource(URLClassLoader classLoader) throws IOException {
        dir = ".";
        isFromGear = false;
        this.classLoader = classLoader;
        File classesDir = new File(dir + '/' + CLASSES_DIR);
        if (classesDir.exists()) {
            baseURL = classesDir.toURI().toURL();
            log.debug("Adding " + baseURL + " to classpath.");
        }
    }

    private URLClassLoader createClassLoader() throws IOException {
        log.debug("Creating classloader for "+dir);
        // Do classes dir
        List<URL> urls = new ArrayList<URL>();
        File classesDir = new File(dir + '/' + CLASSES_DIR);
        if (classesDir.exists()) {
            baseURL = classesDir.toURI().toURL();
            log.debug("Adding " + baseURL + " to classpath.");
            urls.add(baseURL);
        }
        // add jar file directory under component directory (typically one)
        File componentDirectory = new File(dir);
        File[] jarFiles = componentDirectory.listFiles();
        for (File file: jarFiles) {
            if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".jar")) {
            	urls.add(file.toURI().toURL());
            }
        }

        // Add conf dir if it exists
        File confDir = new File(dir + '/' + CONF_DIR);
        if (confDir.exists()) {
            URL confURL = confDir.toURI().toURL();
            log.debug("Adding " + confURL + " to classpath.");
            urls.add(confURL);
        }

        // Do libs
        File libdir = new File(dir + '/' + LIB_DIR);
        if (libdir.exists()) {
            File[] libFiles = libdir.listFiles();
            for (int i = 0; i < libFiles.length; i++) {
                File file = libFiles[i];
                if (!file.isDirectory()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".xsd") || name.endsWith(".xml") || name.endsWith(".dtd") || name.endsWith(".properties") || name.endsWith(".ccmproperties")|| name.endsWith(".dll")) {
                        // file.toURL() is obsolete
                        /* see http://www.jguru.com/faq/view.jsp?EID=1280051 */
                        urls.add(file.toURI().toURL());
                    }
                }
            }
        }

        // Build classpath
        URL[] classpath = new URL[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            classpath[i] = (URL) urls.get(i);
        }
        return new ComponentClassLoader(classpath, this);
    }

    public boolean isFromGear() {
        return isFromGear;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getDir() {
        return dir;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the class searcher for the component resource.
     */
    public ClassSearcher getClassSearcher() {
        if (classSearcher == null) {
            if (baseURL != null) {
                log.debug("Building class searcher for '" + getName() + "'...");
                classSearcher = new ClassSearcher(new URL[]{baseURL});
            }
        }
        return classSearcher;
    }

}
