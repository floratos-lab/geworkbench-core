package org.geworkbench.parsers;

import java.io.File;
import java.io.InterruptedIOException;

import javax.swing.filechooser.FileFilter;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @author my2248
 * @version $Id$
 * 
 * Base class for reading input data files complying to a desired format.
 * Support for any given microarray data format (e.g., Affymetrix MAS 5,
 * GenePix, etc), can be provide as follows:
 * <UL>
 * <LI> Create a new concrete class extending <code>FileFormat</code> and
 * provide method implementations appropriate for the new format. </LI>
 * <LI> In the configuration file of the application, list an object of the
 * new concrete class as a plugin associated with the extension point
 * titled "input-format".</LI>
 * </UL>
 */
public abstract class FileFormat {
    /**
     * The display name of the format.
     */
    protected String formatName = null;

    /**
     * @return The format name.
     */
    public String getFormatName() {
    	StringBuffer sb = new StringBuffer(formatName).append(" (");
    	for(String ext: getFileExtensions()) {
    		sb.append("*.").append(ext).append(", ");
    	}
    	int last = sb.length()-1;
        return sb.replace(last-1, last, ")").toString();
    }

    /**
     * Checks if the contents of the designated file conform to the format.
     *
     * @param file File to check.
     * @return True or false, depending on if the argument is well formed
     *         according to the format or not.
     */
    public abstract boolean checkFormat(File file) throws InterruptedIOException;

    /**
     * Return the list of extensions (if any) for the files following this
     * format.
     *
     * @return
     */
    public abstract String[] getFileExtensions();

    /**
     * @return An (optional) <code>FileFilter</code> to be used in gating the
     *         files offered to the user for selection. Useful, e.g., when the
     *         files of the format at hand have predefined extensions.
     */
    public FileFilter getFileFilter() {
        return null;
    }    
     
    
    public boolean isMergeSupported() {return true;}
    

}

