package org.geworkbench.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.filechooser.FileFilter;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 *  @xuegong wang
 *  @version $Id$
 */
public class ExpressionFileFormat extends DataSetFileFormat {

    private String[] maExtensions = {"exp"};
    private ExpressionResource resource = new ExpressionResource();
    private AffyFilter maFilter = null;

    public ExpressionFileFormat() {
        formatName = "Affymetrix File Matrix";
        maFilter = new AffyFilter();
        Arrays.sort(maExtensions);
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
        return maExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet<DSMicroarray> getDataFile(File file, String compatibilityLabel) throws InputFileFormatException {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        maSet.setCompatibilityLabel(compatibilityLabel);
        getMArraySet(file, maSet);
        return maSet;
    }

    public DSMicroarraySet<DSMicroarray> getMArraySet(File file) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        getMArraySet(file, maSet);
        if (maSet.loadingCancelled)
            return null;
        return maSet;
    }

    private void getMArraySet(File file, CSExprMicroarraySet maSet) {
    	maSet.read(file);
    }

    public DSDataSet<DSMicroarray> getDataFile(File file) {
        return getMArraySet(file);
    }

    public FileFilter getFileFilter() {
        return maFilter;
    }

    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet<DSMicroarray> getDataFile(File[] files) {
        return null;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class AffyFilter extends FileFilter {

        public String getDescription() {
            return getFormatName();
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i)
                if (f.isDirectory() || f.getName().toLowerCase().endsWith(maExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
