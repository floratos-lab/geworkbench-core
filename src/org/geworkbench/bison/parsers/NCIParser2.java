package org.geworkbench.bison.parsers;

import gov.nih.nci.mageom.bean.BioAssay.BioAssayImpl;
import gov.nih.nci.mageom.util.BioAssayDatumData;
import gov.nih.nci.mageom.util.ReporterRelatedData;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSAffyMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.NCIParseContext;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Handles the parsing of remote files from the NCI servers.
 */
public class NCIParser2 {
    /**
     * Defines the list of Quantitation types that the applcications cares to
     * extract among all Quantitation types avaialble at the data source.
     */
    List columnsToUse = null;
    /**
     * The Microarray to populate.
     */
    DSMicroarray microarray = null;

    /**
     * Creates a new NCIParser2 that will parse for the specified datum types.
     *
     * @param ctu - The list of columns to use.
     */
    public NCIParser2(List ctu) {
        columnsToUse = ctu;
    }

    /**
     * Temporary helper method to create Object of the correct type for the
     * quantitation.
     *
     * @param quantType - The type of quantitation to create an object for.
     * @param value     - The String value to create the object from.
     * @return - An object containing the value for the value.  If unable to convert will
     *         return the string "Unknown".
     */
    private static Object getValue(String quantType, String value) {
        Object ret = "Unknown";
        if (quantType.equalsIgnoreCase("Abs Call") || quantType.equalsIgnoreCase("Detection")) {
            ret = new Character(value.charAt(0));
        } else {
            try {
                ret = Double.valueOf(value);
            } catch (Exception e) {
                System.out.println("Error getting value for quantType '" + quantType + "' value '" + value + "': " + e.getMessage());
                e.printStackTrace();
            }

        }

        return ret;
    }

    /**
     * Create a new <code>AffyMicroarraySetImpl</code> object out of the data
     * found in the argument <code>bioAssayData</code>.
     *
     * @param bioAssayData
     * @return
     */
    public DSMicroarray getAffyMicroarray(BioAssayImpl bioAssayImpl) {
        try {
            System.out.println("Getting reporter related data for affy bioassay...");
            ReporterRelatedData[] rrds = bioAssayImpl.getReporterRelatedData();
            System.out.println("Found data for " + rrds.length + " reporters.");
            System.out.println("Finished getting reporter related data for affy bioassay...");
            microarray = new CSMicroarray(rrds.length);
            for (int markerId = 0; markerId < rrds.length; markerId++) {
                CSGeneMarker marker = new CSGeneMarker(rrds[markerId].getReporterName());
                NCIParseContext context = new NCIParseContext(columnsToUse);
                BioAssayDatumData[] badds = rrds[markerId].getBioAssayDatumData();
                HashMap contextData = context.getColumnsToUse();
                for (int y = 0; y < badds.length; y++) {
                    String quantType = badds[y].getType();
                    Object value = getValue(quantType, badds[y].getValue());
                    contextData.put(quantType, value);
                }

                CSAffyMarkerValue markerValue = new CSAffyMarkerValue(context);
                //            markerValue.setMarkerInfo(marker);
                microarray.setMarkerValue(markerId, markerValue);
            }

        } catch (Exception e) {
            System.out.println("Error getting reporter data for affy bioassay: " + e.getMessage());
            e.printStackTrace();
        }

        return microarray;
    }

    /**
     * Create a new <code>GenepixMicroarraySetImpl</code> object out of the data
     * found in the argument <code>bioAssayData</code>.
     *
     * @param bioAssayData
     * @return
     */
    public DSMicroarray getGenepixMicroarray(BioAssayImpl bioAssayImpl) {
        try {
            System.out.println("Getting reporter related data for genepix bioassay...");
            ReporterRelatedData[] rrds = bioAssayImpl.getReporterRelatedData();
            System.out.println("Found data for " + rrds.length + " reporters.");
            System.out.println("Finished getting reporter related data for genepix bioassay...");
            for (int markerId = 0; markerId < rrds.length; markerId++) {
                CSGeneMarker marker = new CSGeneMarker(rrds[markerId].getReporterName());
                NCIParseContext context = new NCIParseContext(columnsToUse);
                BioAssayDatumData[] badds = rrds[markerId].getBioAssayDatumData();
                HashMap contextData = context.getColumnsToUse();
                for (int y = 0; y < badds.length; y++) {
                    String quantType = badds[y].getType();
                    Object value = getValue(quantType, badds[y].getValue());
                    contextData.put(quantType, value);
                }

                CSGenepixMarkerValue markerValue = new CSGenepixMarkerValue(context);
                microarray.setMarkerValue(markerId, markerValue);
            }

        } catch (Exception e) {
            System.out.println("Error getting reporter data for genepix bioassay: " + e.getMessage());
            e.printStackTrace();
        }

        return microarray;
    }

}

