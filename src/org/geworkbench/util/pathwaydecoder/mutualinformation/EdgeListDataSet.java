package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

public class EdgeListDataSet extends CSAncillaryDataSet implements DSAncillaryDataSet {
	
	static Log log = LogFactory.getLog(EdgeListDataSet.class);

	private static final long serialVersionUID = -6835973287728524201L;
    private EdgeList data;
    private String filename;

    public EdgeListDataSet(String label, EdgeList data, String filename) {
        super(null, label);
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

    public EdgeList getData() {
        return data;
    }

    public void setData(EdgeList data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public void writeToFile(String fileName) {
        File file = new File(fileName);

        try {
            file.createNewFile();
            if (!file.canWrite()) {
                JOptionPane.showMessageDialog(null, "Cannot write to specified file.");
                return;
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));         
            writer.write(this.data.print("# Saved from geWorkBench"));            
            writer.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

}
