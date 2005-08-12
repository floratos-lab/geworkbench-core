package org.geworkbench.engine.parsers;

import org.geworkbench.engine.parsers.InputFileFormatException;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.parsers.resources.GenepixResource;
import org.geworkbench.bison.parsers.resources.Resource;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Handles the parsing of Affymetrix .txt files (MAS 5.0).
 * Translates GenePix (gpr) into
 * <code>MicroarraySet</code> objects.
 */

public class GenePixFileFormat extends DataSetFileFormat {
    /**
     * The file extensions expected for Genepix files.
     */
    String[] genepixExtensions = {"gpr"};
    GenepixResource resource = new GenepixResource();
    DSMicroarraySet microarraySet = null;
    /**
     * <code>FileFilter</code> for gating Genepix files, based on their extension.
     */
    GenePixFileFilter genepixFileFilter = null;

    /**
     * Default constructor. Will be invoked by the framework when the
     * <code>&lt;plugin&gt;</code> line for this format is encountered in the
     * application configuration file.
     */
    public GenePixFileFormat() {
        formatName = "GenePix files";   // Setup the display name for the format.
        genepixFileFilter = new GenePixFileFilter();
    }

    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFile(file);
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }

    /**
     * @return The file extensions defined for Genepix data files.
     */
    public String[] getFileExtensions() {
        return genepixExtensions;
    }

    // Fixme.
    // In here we should check (among other things) that:
    // * There are no duplicate markers (ie., no 2 markers have the same name).
    // * The values of the data points respect their expected type.
    public boolean checkFormat(File f) {
        return true;
    }

    /**
     * Return the <code>FileFilter</code> defined for Genepix files.
     *
     * @return
     */
    public FileFilter getFileFilter() {
        return genepixFileFilter;
    }

    /**
     * @param file Input data file, expected to be in the Genepix format.
     * @return A <code>MicroarraySet</code> containing the data in the
     *         input file.
     * @throws org.geworkbench.engine.parsers.InputFileFormatException When the input file deviates from the
     *                                  Genepix format.
     */
    public DSMicroarraySet getMArraySet(File file) throws org.geworkbench.engine.parsers.InputFileFormatException {
        // Check that the file is OK before starting allocating space for it.
        if (!checkFormat(file))
            throw new org.geworkbench.engine.parsers.InputFileFormatException("GenepixFileFormat::getMArraySet - " + "Attempting to open a file that does not comply with the " + "Genepix format.");
        try {
            microarraySet = new CSExprMicroarraySet((GenepixResource) getResource(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return microarraySet;
    }

    /**
     * getDataFile
     *
     * @param file File
     * @return DataSet
     */
    public DSDataSet getDataFile(File file) {
        DSDataSet ds = null;
        try {
            ds = (DSDataSet) getMArraySet(file);
        } catch (InputFileFormatException ife) {
            ife.printStackTrace();
        }
        return ds;
    }

    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet getDataFile(File[] files) {
        return null;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class GenePixFileFilter extends FileFilter {
        public String getDescription() {
            return "Genepix Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < genepixExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(genepixExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
