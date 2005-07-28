package org.geworkbench.engine.parsers.sequences;

import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.util.sequences.SequenceDB;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.resource.Resource;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * <p>Title: SequenceFileFormat</p>
 * <p>Description: SequenceFileFormat</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author Saroja Hanasoge
 * @version 1.0
 */

public class SequenceFileFormat extends DataSetFileFormat {

    String[] fastaExtensions = {"FA", "fasta", "fa", "txt"};
    SequenceResource resource = new SequenceResource();
    FASTAFilter fastaFilter = null;

    public SequenceFileFormat() {
        formatName = "FASTA File";
        fastaFilter = new FASTAFilter();
        //Arrays.sort(fastaExtensions);
    }

    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFileName(file.getName());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }

    public String[] getFileExtensions() {
        return fastaExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet getDataFile(File file) {
        SequenceDB sequenceDB = null;
        try {
            sequenceDB = SequenceDB.createFASTAfile(file);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return sequenceDB;
    }

    public DSDataSet getDataFile(File[] files) {

        return null;
    }


    public DSMicroarraySet getMArraySet(File file) {
        return null;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
    }

    public FileFilter getFileFilter() {
        return fastaFilter;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    public class FASTAFilter extends FileFilter {

        public String getDescription() {
            return "FASTA Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < fastaExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(fastaExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
