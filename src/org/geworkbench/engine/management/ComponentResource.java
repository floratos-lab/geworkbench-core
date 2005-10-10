package org.geworkbench.engine.management;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
 */
public class ComponentResource {

    static Log log = LogFactory.getLog(ComponentResource.class);

    private static final String LIB_DIR = "lib";
    private static final String GEAR_DIR = "gears";
    private static final String GEAR_EXPLODE_DIR = "gears_exploded";
    private static final String CLASSES_DIR = "classes";

    /**
     * The directory in which the component resides.
     */
    private String dir;

    /**
     * The class loader for the resource.
     */
    private URLClassLoader classLoader;

    /**
     * Used to search for classes of a certain type within this resource.
     */
    private ClassSearcher classSearcher;

    /**
     * Creates a new component resource rooted in the given directory.
     *
     * @param dir the directory for the component resource.
     */
    public ComponentResource(String dir) throws IOException {
        this.dir = dir;
        classLoader = createClassLoader();
    }

    private URLClassLoader createClassLoader() throws IOException {
        log.debug("Creating classloader for "+dir);
        // Do classes dir
        File classesDir = new File(dir + '/' + CLASSES_DIR);
        List<URL> urls = new ArrayList<URL>();
        if (classesDir.exists()) {
            URL baseURL = classesDir.toURI().toURL();
            log.debug("Adding " + baseURL + " to classpath.");
            urls.add(baseURL);
            // Create ClassSearcher based on classes path
            classSearcher = new ClassSearcher(new URL[]{baseURL});
        }

        // Do libs
        File libdir = new File(dir + '/' + LIB_DIR);
        if (libdir.exists()) {
            File[] libFiles = libdir.listFiles();
            for (int i = 0; i < libFiles.length; i++) {
                File file = libFiles[i];
                if (!file.isDirectory()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".jar") || name.endsWith(".zip")) {
                        log.debug("Adding " + file.toURL() + " to classpath.");
                        urls.add(file.toURL());
                    }
                }
            }
        }

        // Do gears
        File explode = new File(dir + '/' + GEAR_EXPLODE_DIR);
        if (!explode.exists()) {
            explode.mkdir();
        }
        File geardir = new File(dir + '/');
        if (geardir.exists()) {
            File[] libFiles = geardir.listFiles();
            for (int i = 0; i < libFiles.length; i++) {
                File file = libFiles[i];
                if (!file.isDirectory()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".gear")) {
                        log.debug("Found gear file " + name);
                        // Make a dir for this gear file to be extracted into
                        File thisdir = new File(explode, name.split("\\.")[0]);
                        if (thisdir.exists()) {
                            deleteDirectory(thisdir);
                        }
                        thisdir.mkdir();
                        List files = unZip(file.getAbsolutePath(), explode.getAbsolutePath());
                        for (int q = 0; q < files.size(); q++) {
                            File file1 = (File) files.get(q);

                            if (file1.getName().toLowerCase().equals(CLASSES_DIR) || file1.getName().toLowerCase().equals(LIB_DIR)) {
                                log.debug("Adding " + file1.toURL() + " to classpath.");
                                urls.add(file1.toURL());
                            }
                        }
                    }
                }
            }
        }

        // Build classpath
        URL[] classpath = new URL[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            classpath[i] = (URL) urls.get(i);
        }
        return new ComponentClassLoader(classpath);
    }

    public static List unZip(String inFile, String outDir) throws IOException {
        Enumeration entries;
        ZipFile zipFile;

        List files = new ArrayList();

        zipFile = new ZipFile(inFile);

        entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                log.trace("Extracting directory: " + entry.getName());
                // This is not robust, could be improved
                File newdir = new File(outDir, entry.getName());
                newdir.mkdir();
                files.add(newdir);
            } else {
                log.trace("Extracting file: " + entry.getName());
                File file = new File(outDir, entry.getName());
                File path = file.getParentFile();
                // Make directory if needed
                path.mkdir();
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(file)));
                files.add(file);
            }
        }

        zipFile.close();
        return files;
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

}
