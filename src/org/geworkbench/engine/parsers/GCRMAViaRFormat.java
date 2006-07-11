package org.geworkbench.engine.parsers;

import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.rosuda.JRclient.Rconnection;
import org.rosuda.JRclient.REXP;
import org.rosuda.JRclient.RSrvException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.filechooser.FileFilter;
import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * GCRMA processing of CEL files via RServe integration
 *
 * @author Matt Hall
 */
public class GCRMAViaRFormat extends DataSetFileFormat {

    static Log log = LogFactory.getLog(GCRMAViaRFormat.class);

    String[] maExtensions = {"cel", "CEL"};
    ExpressionResource resource = new ExpressionResource();
    GCRMAViaRFormat.RMAExpressFilter maFilter = null;

    public GCRMAViaRFormat() {
        formatName = "CEL Files";
        maFilter = new GCRMAViaRFormat.RMAExpressFilter();
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

    public DSDataSet getDataFile(File file) {
        return (DSDataSet) getMArraySet(file);
    }

    public DSMicroarraySet getMArraySet(File file) {

        try {
            String absolutePath = file.getParent().replace('\\', '/');
            log.debug("Loading from: " + absolutePath);
            Rconnection c = new Rconnection();
//        c.eval("setwd(\"C:/code/workbook/data/celdata\")");
            c.eval("setwd(\"" + absolutePath + "\")");
            c.eval("library(\"gcrma\")");
//        c.eval("Data <- ReadAffy()");
//        REXP r = c.eval("expresso(Data, bgcorrect.method=\"rma\",normalize.method=\"constant\",pmcorrect.method=\"pmonly\",summary.method=\"avgdiff\")");
            String bgMethod = "affinities";
            c.eval("eset <- justGCRMA(type=\"" + bgMethod + "\")");

            CSExprMicroarraySet maSet = new CSExprMicroarraySet();
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1) {
                fileName = fileName.substring(0, dotIndex);
            }
            maSet.setLabel(fileName);

            {
                REXP r = c.eval("geneNames(eset)");
                Vector<REXP> results = r.asVector();
                int m = 0;
                for (Iterator<REXP> iterator = results.iterator(); iterator.hasNext();) {
                    REXP rexp = iterator.next();
                    CSExpressionMarker marker = new CSExpressionMarker(m);
                    marker.setLabel(rexp.asString());
                    maSet.getMarkerVector().add(m, marker);
                    m++;
                }
            }
            REXP r = c.eval("sampleNames(eset)");
            int numberMarkers = maSet.getMarkerVector().size();
            log.debug("Number of markers: " + numberMarkers);
            if (r.getType() == REXP.XT_VECTOR) {
                Vector<REXP> results = r.asVector();
                for (int i = 0; i < results.size(); i++) {
                    REXP rexp = results.elementAt(i);
                    CSMicroarray array = new CSMicroarray(i, numberMarkers, r.asString(), null, null, false, DSMicroarraySet.affyTxtType);
                    maSet.add(array);
                }
            } else if (r.getType() == REXP.XT_STR) {
                // Just one array probably
                String name = r.asString();
                System.out.println("Just one array: " + name);
                CSMicroarray array = new CSMicroarray(0, numberMarkers, r.asString(), null, null, false, DSMicroarraySet.affyTxtType);
                maSet.add(array);
            }
            REXP assay = c.eval("assayData(eset)");
            double[][] results = assay.asDoubleMatrix();
            System.out.println(results.length + " by " + results[0].length);
            for (int i = 0; i < results[0].length; i++) {
                double v = results[0][i];
                DSMicroarray ma = maSet.get(i);
                for (int j = 0; j < results.length; j++) {
                    double value = results[j][i];
                    CSExpressionMarkerValue markerValue = new CSExpressionMarkerValue((float) value);
                    markerValue.setPresent();
                    ma.setMarkerValue(j, markerValue);
                }
            }

            // Set chip-type
            String result = null;
            for (int i = 0; i < numberMarkers; i++) {
                result = AnnotationParser.matchChipType(maSet, maSet.getMarkerVector().get(i).getLabel(), false);
                if (result != null) {
                    break;
                }
            }

            System.out.println("New Set: " + maSet.toString());
            c.eval("eset <- NULL");
            c.close();
            return maSet;
        } catch (RSrvException e) {
            log.error("Error in RServer", e);
        }
        return null;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new UnsupportedOperationException("Method getOptions() not yet implemented.");
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
    public DSDataSet getDataFile(File[] files) {
        return null;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class RMAExpressFilter extends FileFilter {

        public String getDescription() {
            return "CEL Files (For batch GCRMA via R processing)";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
