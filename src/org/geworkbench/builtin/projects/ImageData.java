package org.geworkbench.builtin.projects;

import java.io.File;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version $Id$
 */

public class ImageData extends CSAncillaryDataSet<DSBioObject> {

    /**
	 *
	 */
	private static final long serialVersionUID = 3899156889448935110L;
	
	static Log log = LogFactory.getLog(ImageData.class);
	
	private File imageFile = null;
    private ImageIcon image = null;     
    private boolean isDirty = false;

    public ImageData(File image) {
        super(null, "Image");
        imageFile = image;
    }

    public File getDataSetFile() {
        return imageFile;
    }

    public void setImageIcon(ImageIcon icon) {
        image = icon;
    }

    public ImageIcon getImageIcon() {
        return image;
    }

    public void setDataSetFile(File image) {
        imageFile = image;
    }

    @SuppressWarnings("unchecked")
	public boolean equals(Object ads) {
        if (ads instanceof DSAncillaryDataSet) {
            return getID().equalsIgnoreCase(((DSAncillaryDataSet<DSBioObject>) ads).getID());
        } else {
            return false;
        }
    }

    public String getDataSetName() {
        if(imageFile!=null){
            return imageFile.getName();
        }
        return null;
    }

    public File getFile() {
        return imageFile;
    }

    public void writeToFile(String fileName) {
        /**@todo Implement this org.geworkbench.builtin.projects.DataSet method*/
         throw new java.lang.UnsupportedOperationException("Method writeToFile() not yet implemented.");
    	
    	 
    	 
    
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

}
