/**
 * ChipChipFileFormat.java
 *
 * <code>FileFormat</code> describing ChIP-chip experiment data
 *
 * Created on October 6, 2007, 9:47 PM
 */

package org.geworkbench.components.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSChipchipSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;

/**
 * @author manjunath at c2b2 dot org
 */
public class ChipChipFileFormat extends DataSetFileFormat {

    String[] ccExtensions = {"chp"};
    ExpressionResource resource = new ExpressionResource();
    ChipchipFilter ccFilter = null;

    public ChipChipFileFormat() {
        formatName = "ChIP-chip data";
        ccFilter = new ChipchipFilter();
        Arrays.sort(ccExtensions);
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
        return ccExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet getDataFile(File file) {
        return (DSDataSet) getMArraySet(file);
    }


    public DSDataSet getDataFile(File file, String compatibilityLabel) throws InputFileFormatException {
        CSChipchipSet maSet = new CSChipchipSet();
        getDataset(file, maSet);
        return maSet;
    }

    public DSDataSet getMArraySet(File file) {
        CSChipchipSet ccSet = new CSChipchipSet();
        getDataset(file, ccSet);
        return ccSet;
    }

    public void getDataset(File file, CSChipchipSet ccSet) {
        try {
            ccSet.read(file);
        } catch (Exception e) {
        }
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.components.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
    }

    public FileFilter getFileFilter() {
        return ccFilter;
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
     * to select ChIP-chip input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #ccExtensions}.
     */
    class ChipchipFilter extends FileFilter {

        public String getDescription() {
            return getFormatName();
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < ccExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(ccExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}