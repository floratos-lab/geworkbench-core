package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

/**
 * @author Matt Hall
 * @author oshteynb
 * @version $Id: MindyDataSet.java,v 1.6 2009-04-29 19:59:39 oshteynb Exp $
 */
public class MindyDataSet extends CSAncillaryDataSet implements DSAncillaryDataSet {

    private static final long serialVersionUID = -6835973287728524201L;
    private MindyData data;
    private String filename;


	public MindyDataSet(DSDataSet parent, String label, MindyData data, String filename) {
        super(parent, label);
        this.data = data;
        this.filename = filename;
    }

    public File getDataSetFile() {
        // no-op
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }

    public MindyData getData() {
        return data;
    }

    public void setData(MindyData data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
