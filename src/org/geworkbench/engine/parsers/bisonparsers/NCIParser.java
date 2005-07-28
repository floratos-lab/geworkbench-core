package org.geworkbench.engine.parsers.bisonparsers;

import gov.nih.nci.mageom.bean.BioAssay.BioAssayImpl;
import gov.nih.nci.mageom.util.BioAssayDatumData;
import gov.nih.nci.mageom.util.ReporterRelatedData;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;

import java.util.HashMap;
import java.util.List;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Handles the parsing of remote files from the NCI servers.
 */
public class NCIParser {

    /**
     * Defines the list of Quantitation types that the applcications cares to
     * extract among all Quantitation types avaialble at the data source.
     */
    private List columnsToUse = null;

    /**
     * The Microarray to populate.
     */
    private CSMicroarray microarray = null;

    /**
     * Keeps track of the number of markers in the dataset currently being
     * constructed
     */
    private int markerNo = 0;

    /**
     * Creates a new NCIParser that will parse for the specified datum types.
     *
     * @param ctu - The list of columns to use.
     */
    public NCIParser(List ctu) {
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
     * Create a new <code>GenepixMicroarraySetImpl</code> object out of the data
     * found in the argument <code>bioAssayData</code>.
     *
     * @param bioAssayData
     * @return
     */
    public CSMicroarray getMicroarray(int ser, BioAssayImpl bioAssayImpl, CSExprMicroarraySet maSet) {
        try {
            System.out.println("Getting reporter related data for bioassay...");
            ReporterRelatedData[] rrds = bioAssayImpl.getReporterRelatedData();
            System.out.println("Found data for " + rrds.length + " reporters.");
            System.out.println("Finished getting reporter related data for bioassay...");
            for (int x = 0; x < rrds.length; x++) {
                NCIParseContext context = new NCIParseContext(columnsToUse);
                BioAssayDatumData[] badds = rrds[x].getBioAssayDatumData();
                HashMap contextData = context.getColumnsToUse();

                if (!maSet.initialized) {
                    markerNo = badds.length;
                    maSet.initialize(maSet.size(), markerNo);
                    for (int z = 0; z < badds.length; z++) {
                        maSet.getMarkers().get(z).setLabel(badds[z].getValue());
                        ((CSExpressionMarker) maSet.get(z)).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                        maSet.getMarkers().get(z).setDescription(badds[z].getValue());
                    }
                    microarray = new CSMicroarray(ser, markerNo, bioAssayImpl.getName(), null, null, true, DSMicroarraySet.geneExpType);
                    microarray.setLabel("Derived:" + bioAssayImpl.getName());
                    microarray.setLabel("Derived:" + bioAssayImpl.getName());
                }

                for (int y = 0; y < badds.length; y++) {
                    String quantType = badds[y].getType();
                    Object value = getValue(quantType, badds[y].getValue());
                    if (value instanceof Double) {
                        ((DSMutableMarkerValue) microarray.getMarkerValue(y)).setValue(((Double) value).doubleValue());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting reporter data for genepix bioassay: " + e.getMessage());
            e.printStackTrace();
        }
        return microarray;
    }
}
