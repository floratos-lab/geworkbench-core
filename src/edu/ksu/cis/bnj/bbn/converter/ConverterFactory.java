package edu.ksu.cis.bnj.bbn.converter;

/*
 * 
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.kdd.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Roby Joehanes
 */
public class ConverterFactory {
    private static TableSet formatConfigTable = null;
    private static Hashtable desc2format = null;
    private static final String interfaceName = "edu.ksu.cis.bnj.bbn.converter.Converter"; //$NON-NLS-1$

    static {
        loadConfig();
    }

    public static TableSet loadConfig() {
        if (formatConfigTable != null) return formatConfigTable;
        try {
            formatConfigTable = Settings.getConverterTable();
            desc2format = new Hashtable();
            for (Enumeration e = formatConfigTable.keys(); e.hasMoreElements();) {
                String ext = (String) e.nextElement();
                HashSet set = (HashSet) formatConfigTable.get(ext);
                for (Iterator i = set.iterator(); i.hasNext();) {
                    ConverterData data = (ConverterData) i.next();
                    desc2format.put(data.getDescription(), data);
                }
            }
            return formatConfigTable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TableSet reloadConfig() {
        formatConfigTable = null;
        return loadConfig();
    }

    /**
     * <tt>file</tt> = The file name to be converted (MUST BE WITH FULL PATH)<BR>
     * <tt>sourceFormat</tt> = treat all files in <TT>files</tt> as <tt>sourceFormat</tt>. Null means autodetect<br>
     * <tt>targetFormat</tt> = target format (as described in formatConfigTable) <br>
     * <tt>outputdir</tt> = output directory<BR>
     */
    public static void convert(String file, String sourceFormat, String targetFormat, String outputDir) {
        ConverterData destData = (ConverterData) desc2format.get(targetFormat);
        if (destData == null) {
            Set s = formatConfigTable.get(targetFormat);
            if (s == null)
                throw new RuntimeException("Cannot find destination format " + targetFormat);
            if (s.size() > 1)
                throw new RuntimeException("Two or more formats found on " + targetFormat);
            destData = (ConverterData) s.iterator().next();
        }

        if (outputDir != null) {
            File outdir = new File(outputDir);
            if (!(outdir.exists() && outdir.isDirectory()))
                throw new RuntimeException("Invalid output directory " + outputDir);
            if (!outputDir.endsWith(File.separator)) outputDir += File.separator;
        } else
            outputDir = ""; //$NON-NLS-1$

        ConverterData srcData = new ConverterData();
        BBNGraph graph = load(file, sourceFormat, srcData);

        String inExt = "." + srcData.getExtension(); //$NON-NLS-1$
        String outExt = "." + destData.getExtension(); //$NON-NLS-1$
        String outFile;
        int pathIndex = file.lastIndexOf(File.separator);
        if (pathIndex != -1) {
            file = file.substring(pathIndex + 1);
        }

        outFile = outputDir + file.substring(0, file.lastIndexOf(".")) + outExt; //$NON-NLS-1$

        save(graph, outFile, targetFormat);
    }

    public static BBNGraph load(String filename) {
        return load(filename, null, null);
    }

    public static BBNGraph load(String filename, String sourceFormat) {
        return load(filename, sourceFormat, null);
    }

    /**
     * Load BBN Graph from a filename
     *
     * @param filename
     * @param sourceFormat
     * @param detectedFormat
     * @return BBNGraph
     */
    public static BBNGraph load(String filename, String sourceFormat, ConverterData detectedFormat) {
        // Sanity check and initialization
        File f = new File(filename);
        if (!f.exists()) throw new RuntimeException("Error: File doesn't exist!");

        HashSet srcSet = null;
        ConverterData srcData = null;
        if (sourceFormat == null) {
            srcSet = new HashSet();
            srcSet.addAll(desc2format.values());
        } else {
            srcSet = formatConfigTable.get(sourceFormat);
            if (srcSet == null) srcData = (ConverterData) desc2format.get(sourceFormat);
        }
        if (srcSet == null && srcData == null) throw new RuntimeException("Cannot find source format " + sourceFormat);

        BBNGraph graph = null;
        Converter src = null;

        // Assuming that one converter can only do one format...
        for (Iterator i = srcSet.iterator(); i.hasNext();) {
            srcData = (ConverterData) i.next();
            src = getConverter(srcData);
            try {
                src.initialize();
                graph = src.load(new FileInputStream(filename));
                if (graph != null) break; // Stop upon the first success
            } catch (Exception e) {
                if (Settings.DEBUG) {
                    if (src != null) e.printStackTrace();
                    System.out.println("Loader [" + srcData + "] failed, trying the next one...");
                    e.printStackTrace();
                }
                continue;
            }
        }

        if (graph == null)
            throw new RuntimeException("Unable to load file");
        if (detectedFormat != null) detectedFormat.copy(srcData);
        src = null;
        srcSet = null;
        System.gc();
        return graph;
    }

    public static void save(BBNGraph graph, String filename, String targetFormat) {
        try {
            save(graph, new FileOutputStream(filename), targetFormat);
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            if (Settings.DEBUG) e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void save(BBNGraph graph, OutputStream w, String targetFormat) {
        ConverterData destData = (ConverterData) desc2format.get(targetFormat);
        if (destData == null) {
            Set s = formatConfigTable.get(targetFormat);
            if (s == null)
                throw new RuntimeException("Cannot find destination format " + targetFormat);
            if (s.size() > 1)
                throw new RuntimeException("Two or more formats found on " + targetFormat);
            destData = (ConverterData) s.iterator().next();
        }

        Converter dest = getConverter(destData);
        Converter src = null;
        LinkedList srcList = new LinkedList();

        try {
            dest.initialize();
            dest.save(w, graph);
            w.flush();
            System.gc();
        } catch (Exception e) {
            if (Settings.DEBUG) e.printStackTrace();
            throw new RuntimeException("Cannot save the file");
        }
    }

    public static Converter getConverter(String formatDescription) {
        return getConverter((ConverterData) desc2format.get(formatDescription));
    }

    protected static Converter getConverter(ConverterData data) {
        String className = data.getPackageName() + "." + data.getClassName(); //$NON-NLS-1$
        if (className.startsWith("null.") || className.endsWith(".null")) //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException("Error in the configuration file");

        try {
            return (Converter) FileClassLoader.loadAndInstantiate(className, null, new String[]{interfaceName});
        } catch (Exception e) {
            if (Settings.DEBUG)
                System.out.println("Warning: Cannot load reflection object" + className);
            return null;
        }
    }


    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i"); //$NON-NLS-1$
        String outputFormat = params.getString("-f"); //$NON-NLS-1$
        String outputFile = params.getString("-o"); //$NON-NLS-1$
        boolean quiet = params.getBool("-q"); //$NON-NLS-1$

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.converter.ConverterFactory -i:inputfile [-o:outputfile] [-f:saveformat] [-q]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn, libb, dsl, ent, dsc}");
            System.out.println("-q: quiet mode");
            System.out.println("If outputfile is not specified, it will default to the standard output.");
            return;
        }


        OutputStream out = System.out;

        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Runtime r = Runtime.getRuntime();
        long freemem = r.freeMemory();
        BBNGraph g = null;
        try {
            g = BBNGraph.load(inputFile);
            freemem = freemem - r.freeMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!quiet) {
            System.out.println("Memory needed = " + freemem);
        }

        if (g != null) {
            if (outputFormat == null) outputFormat = "xml";
            try {
                save(g, out, outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isKnownFormat(String formatExt) {
        return formatExt != null && formatConfigTable.get(formatExt) != null;
    }
}
