package org.geworkbench.bison.datastructure.biocollections.microarrays;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.util.RandomNumberGenerator;

/**
 *
 * @author not attributable
 * @author zji
 * @version $Id$
 */
public class CSExprMicroarraySet extends CSMicroarraySet<DSMicroarray> implements Serializable {
	private static final long serialVersionUID = 6763897988197502601L;
    static Log log = LogFactory.getLog(CSExprMicroarraySet.class);

    private ArrayList<String> descriptions = new ArrayList<String>();

    public CSExprMicroarraySet() {
        super(RandomNumberGenerator.getID(), "");
        super.type = DSMicroarraySet.expPvalueType;

        addDescription("Microarray experiment");
        DSAnnotationContext<DSMicroarray> context = CSAnnotationContextManager.getInstance().getCurrentContext(this);
        CSAnnotationContext.initializePhenotypeContext(context);
    }

    public File getFile() {
        return file;
    }

    public String[] getDescriptions() {
        String[] descr = new String[descriptions.size()];
        for (int i = 0; i < descriptions.size(); i++) {
            descr[i] = (String) descriptions.get(i);
        }
        return descr;
    }

    public void removeDescription(String descr) {
        descriptions.remove(descr);
    }

    public void addDescription(String descr) {
        descriptions.add(descr);
    }

    public String getDataSetName() {
        return label;
    }

    public boolean loadingCancelled = false;

    public boolean initialized = false;

    public void initialize(int maNo, int mrkNo) {
        // this is required so that the microarray vector may create arrays of the right size
        for (int microarrayId = 0; microarrayId < maNo; microarrayId++) {
            add(microarrayId, new CSMicroarray(microarrayId, mrkNo, "Test", null, null, false, type));
        }

        for (int i = 0; i < mrkNo; i++) {
            CSExpressionMarker mi = new CSExpressionMarker();
            mi.reset(i, maNo, mrkNo);
            markerVector.add(i, mi);
        }

        initialized = true;
    }

    private void save(File file) throws IOException {
            this.absPath = file.getAbsolutePath();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // start processing the data.
            // Start with the header line, comprising the array names.
            String outLine = "AffyID" + "\t" + "Annotation";
            for (int i = 0; i < size(); ++i) {
                outLine = outLine.concat("\t" + get(i).toString());
            }
            writer.write(outLine);
            writer.newLine();

            DSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            int n = manager.getNumberOfContexts(this);
            for (int i = 0; i < n; i++) {
                DSAnnotationContext<DSMicroarray> context = manager.getContext(this, i);
                StringBuilder line = new StringBuilder("Description" + '\t' + context.getName());
                for (Iterator<DSMicroarray> iterator = this.iterator(); iterator.hasNext();) {
                    DSMicroarray microarray = iterator.next();
                    String label = "";
                    String[] labels = context.getLabelsForItem(microarray);
                    // watkin - Unfortunately, the file format only supports one label per context.
                    if (labels.length > 0) {
                        label = labels[0];
                        if (labels.length > 1)
                        	for (int j = 1; j < labels.length; j++)
                        		label += "|"+labels[j];
                    }
                    line.append('\t' + label);
                }
                writer.write(line.toString());
                writer.newLine();
            }

            ProgressMonitor pm = new ProgressMonitor(null, "Total " + markerVector.size(), "saving ", 0, markerVector.size());
            // Proceed to write one marker at a time
            for (int i = 0; i < markerVector.size(); ++i) {
                pm.setProgress(i);
                pm.setNote("saving " + i);
                outLine = markerVector.get(i).getLabel();
                outLine = outLine.concat('\t' + getMarkers().get(i).getLabel());
                for (int j = 0; j < size(); ++j) {
                    DSMarkerValue mv = get(j).getMarkerValue(i);
                    if (!mv.isMissing())                    		
                        outLine = outLine.concat("\t" + (float) mv.getValue() + '\t') + (float) mv.getConfidence();
                    else
                    	outLine = outLine.concat("\t" + "n/a" + '\t') + (float) mv.getConfidence();
                }
                writer.write(outLine);
                writer.newLine();
            }
            pm.close();
            writer.flush();
            writer.close();
    }

    // this method used to do writing in a separate thread, meaning return immediately
    // that is too dangerous an practice. 
    // let the user of this method to decide to do that if necessary
	public void writeToFile(String fileName) {
		File file = new File(fileName);
		try {
			save(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File " + fileName
					+ " is not saved due to IOException " + e.getMessage(),
					"File Saving Failed", JOptionPane.ERROR_MESSAGE);

		}
	}

	public void increaseMaskedSpots() {
		maskedSpots++;
	}
}
