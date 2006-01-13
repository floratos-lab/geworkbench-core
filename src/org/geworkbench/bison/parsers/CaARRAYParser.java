package org.geworkbench.bison.parsers;

import gov.nih.nci.mageom.domain.BioAssay.BioAssay;
import gov.nih.nci.mageom.domain.BioAssay.impl.DerivedBioAssayImpl;
import gov.nih.nci.mageom.domain.BioAssay.impl.MeasuredBioAssayImpl;
import gov.nih.nci.mageom.domain.BioAssayData.*;
import gov.nih.nci.mageom.domain.BioAssayData.impl.BioDataCubeImpl;
import gov.nih.nci.mageom.domain.BioAssayData.impl.CompositeSequenceDimensionImpl;
import gov.nih.nci.mageom.domain.BioAssayData.impl.FeatureDimensionImpl;
import gov.nih.nci.mageom.domain.DesignElement.DesignElement;
import gov.nih.nci.mageom.domain.QuantitationType.QuantitationType;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Handles the parsing of remote files from the NCI servers.
 */
public class CaARRAYParser {

    /**
     * Defines the list of Quantitation types that the applcications cares to
     * extract among all Quantitation types avaialble at the data source.
     */
    private List columnsToUse = null;

    /**
     * The Microarray to populate.
     */
    private DSMicroarray microarray = null;

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
    public CaARRAYParser(List ctu) {
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
    public DSMicroarray getMicroarray(int ser, BioAssay bioAssayImpl, CSExprMicroarraySet maSet) {
        CaARRAYParseContext context = new CaARRAYParseContext(columnsToUse);
        HashMap contextData = context.getColumnsToUse();
        try {
            if (bioAssayImpl instanceof DerivedBioAssayImpl) {
                DerivedBioAssayData[] dbd = ((DerivedBioAssayImpl) bioAssayImpl).getDerivedBioAssayData();
                DesignElementDimension ded = null;
                DesignElement[] de = null;
                QuantitationType[] qTypes = null;
                BioDataValues bdv = null;
                for (int m = 0; m < dbd.length; m++) {
                    ded = dbd[m].getDesignElementDimension();
                    qTypes = dbd[m].getQuantitationTypeDimension().getQuantitationTypes();
                    bdv = dbd[m].getBioDataValues();
                    if (ded instanceof ReporterDimension) {
                        ReporterDimension rd = (ReporterDimension) ded;
                        de = rd.getReporters();
                        markerNo = de.length;
                        break;
                    } else if (ded instanceof FeatureDimension) {
                        FeatureDimensionImpl fd = (FeatureDimensionImpl) ded;
                        de = fd.getContainedFeatures();
                        markerNo = de.length;
                        break;
                    } else if (ded instanceof CompositeSequenceDimensionImpl) {
                        de = ((CompositeSequenceDimensionImpl) ded).getCompositeSequences();
                        markerNo = de.length;
                    }
                }
                if (!maSet.initialized) {
                    maSet.initialize(0, markerNo);
                    maSet.setCompatibilityLabel(bioAssayImpl.getIdentifier());
                    for (int z = 0; z < markerNo; z++) {
                        maSet.getMarkers().get(z).setGeneName(de[z].getName());
                        maSet.getMarkers().get(z).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                        maSet.getMarkers().get(z).setLabel(de[z].getName());
                        maSet.getMarkers().get(z).setDescription(de[z].getIdentifier());
                    }
                    microarray = new CSMicroarray(ser, markerNo, bioAssayImpl.getName(), null, null, true, DSMicroarraySet.geneExpType);
                    microarray.setLabel("Derived:" + bioAssayImpl.getIdentifier());
                }
                for (int i = 0; i < markerNo; i++) {
                    if (bdv instanceof BioDataCubeImpl) {
                        Object[][][] cube = ((BioDataCubeImpl) bdv).getCube();
                        for (int j = 0; j < qTypes.length; j++) {
                            Object val = getCubeValue(cube, ser, i, j, ((BioDataCubeImpl) bdv).getOrder());
                            if (qTypes[j].getIdentifier().equalsIgnoreCase("Affymetrix:QuantitationType:CHPSignal") && (val instanceof Double)) {
                                ((DSMutableMarkerValue) microarray.getMarkerValue(i)).setValue(((Double) val).doubleValue());
                                break;
                            }
                        }
                    }
                }
            } else if (bioAssayImpl instanceof MeasuredBioAssayImpl) {
                MeasuredBioAssayData[] mbd = ((MeasuredBioAssayImpl) bioAssayImpl).getMeasuredBioAssayData();
                DesignElementDimension ded = null;
                DesignElement[] de = null;
                QuantitationType[] qTypes = null;
                BioDataValues bdv = null;
                for (int m = 0; m < mbd.length; m++) {
                    ded = mbd[m].getDesignElementDimension();
                    qTypes = mbd[m].getQuantitationTypeDimension().getQuantitationTypes();
                    bdv = mbd[m].getBioDataValues();
                    if (ded instanceof ReporterDimension) {
                        ReporterDimension rd = (ReporterDimension) ded;
                        de = rd.getReporters();
                        markerNo = de.length;
                        break;
                    } else if (ded instanceof FeatureDimension) {
                        FeatureDimensionImpl fd = (FeatureDimensionImpl) ded;
                        de = fd.getContainedFeatures();
                        markerNo = fd.getContainedFeaturesCount();
                        break;
                    }
                }
                if (!maSet.initialized) {
                    maSet.initialize(0, markerNo);
                    for (int z = 0; z < markerNo; z++) {
                        maSet.getMarkers().get(z).setGeneName(de[z].getIdentifier());
                        maSet.getMarkers().get(z).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                        maSet.getMarkers().get(z).setDescription(de[z].toString());
                    }
                    microarray = new CSMicroarray(ser, markerNo, bioAssayImpl.getName(), null, null, true, DSMicroarraySet.geneExpType);
                    microarray.setLabel("Measured:" + bioAssayImpl.getName());
                }
                Object[][][] cube = ((BioDataCubeImpl) bdv).getCube();
                for (int i = 0; i < markerNo; i++) {
                    if (bdv instanceof BioDataCubeImpl) {
                        for (int j = 0; j < qTypes.length; j++) {
                            Object val = getCubeValue(cube, ser, i, j, ((BioDataCubeImpl) bdv).getOrder());
                            if (qTypes[j].getIdentifier().equalsIgnoreCase("Affymetrix:QuantitationType:CELIntensity") && (val instanceof Double)) {
                                ((DSMutableMarkerValue) microarray.getMarkerValue(i)).setValue(((Double) val).doubleValue());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting reporter data for bioassay: " + e.getMessage());
            e.printStackTrace();
        }
        return microarray;
    }

    private Object getCubeValue(Object[][][] cube, int b, int d, int q, String order) {
        if (order.equalsIgnoreCase("BDQ"))
            return cube[b][d][q];
        else if (order.equalsIgnoreCase("BQD"))
            return cube[b][q][d];
        else if (order.equalsIgnoreCase("DBQ"))
            return cube[d][b][q];
        else if (order.equalsIgnoreCase("DQB"))
            return cube[d][q][b];
        else if (order.equalsIgnoreCase("QDB"))
            return cube[q][d][b];
        else if (order.equalsIgnoreCase("QBD"))
            return cube[q][b][d];
        else
            return null;
    }
}
