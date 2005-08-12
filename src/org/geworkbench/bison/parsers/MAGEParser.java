package org.geworkbench.bison.parsers;

import DBInterface.DBInterface;
import MAGE.*;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Handles the parsing of remote files from the MAGE servers.
 */
public class MAGEParser {
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
     *
     */
    private CSExprMicroarraySet maSet = null;

    /**
     * Keeps track of quantitation types defined and their order
     */
    private Hashtable qTMap = new Hashtable();
    private int markerNo = 0;

    /**
     * Creates a new MAGEParser that will parse for the specified datum types.
     *
     * @param ctu - The list of columns to use.
     */
    public MAGEParser(List ctu) {
        columnsToUse = ctu;
    }

    private void executeLine(String order, String line, int index) {
        if (microarray != null) {
            String[] st = line.split("\t");

            if (order.equalsIgnoreCase("BDQ")) {
                DSGeneMarker gm = maSet.getMarkers().get(index);
                DSMutableMarkerValue mmv = (DSMutableMarkerValue) microarray.getMarkerValue(index);
                mmv.setValue(Double.parseDouble(st[((Integer) qTMap.get("QT:ratio")).intValue()]));

                //gm.check(mmv, false);
                if (maSet.getCompatibilityLabel() == null) {
                    String token = gm.getLabel();
                    String chiptype = AnnotationParser.matchChipType(token);

                    if (chiptype != null) {
                        maSet.setCompatibilityLabel(chiptype);
                    }
                }

                //                ( (MutableMarkerValue) microarray.getMarker(index)).setMissing(false);
            }
        }
    }

    private void executeLine(String order, String line, int index, int serial) {
        if (microarray != null) {
            String[] st = line.split("\t");

            if (order.equalsIgnoreCase("BDQ")) {
                if (st.length > (serial + 2)) {
                    DSGeneMarker gm = maSet.getMarkers().get(index);
                    DSMutableMarkerValue mmv = (DSMutableMarkerValue) microarray.getMarkerValue(index);
                    mmv.setValue(Double.parseDouble(st[serial + 2]));

                    //gm.check(mmv, false);
                    if (maSet.getCompatibilityLabel() == null) {
                        String token = gm.getLabel();
                        String chiptype = AnnotationParser.matchChipType(token);

                        if (chiptype != null) {
                            maSet.setCompatibilityLabel(chiptype);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a new <code>Microarray</code> object out of the data
     * found in the argument <code>bioAssayData</code>.
     *
     * @param bioAssayData
     * @return <code>Microarray</code>
     */
    public CSMicroarray getMicroarray(int ser, BioAssay bioAssay, CSExprMicroarraySet maSet) {
        microarray = null;
        this.maSet = maSet;

        try {
            String tmpDir = System.getProperty("temporary.files.directory");
            String host = System.getProperty("arrayexpress.host");
            String port = System.getProperty("arrayexpress.port");
            String dbInstance = System.getProperty("arrayexpress.db");
            String user = System.getProperty("arrayexpress.user");
            String password = System.getProperty("arrayexpress.password");
            DBInterface.DBInitialize(tmpDir + File.separator + "DBLoad.log", tmpDir + File.separator + "del.log", "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbInstance, user, password);
            System.out.println("Getting reporter related data for bioassay...");

            if (bioAssay instanceof DerivedBioAssay) {
                DerivedBioAssay dba = (DerivedBioAssay) bioAssay;

                microarray = new CSMicroarray(ser, markerNo, dba.getName(), null, null, true, DSMicroarraySet.geneExpType);
                microarray.setLabel("Derived:" + dba.getId().toString());
                microarray.setLabel("Derived:" + dba.getId().toString());

                Vector dbad = dba.getDerivedBioAssayData();

                for (int x = 0; x < dbad.size(); x++) {
                    System.out.println("data for derived bioassay...");

                    DerivedBioAssayData dbadt = (DerivedBioAssayData) dbad.get(x);
                    FeatureDimension fe = dbadt.getDesignElementDimension_ref().getFeatureDimension_ref();

                    if (!maSet.initialized) {
                        Vector features = fe.getContainedFeatures();
                        markerNo = features.size();
                        maSet.initialize(maSet.size(), markerNo);

                        for (int z = 0; z < features.size(); z++) {
                            maSet.getMarkers().get(z).setLabel(((Feature) features.get(z)).getIdentifier());
                            ((CSExpressionMarker) maSet.get(z)).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                            maSet.getMarkers().get(z).setDescription(((Feature) features.get(z)).getIdentifier());
                        }

                        microarray = new CSMicroarray(ser, markerNo, dba.getName(), null, null, true, DSMicroarraySet.geneExpType);
                        microarray.setLabel("Derived:" + dba.getId().toString());
                        microarray.setLabel("Derived:" + dba.getId().toString());
                    }

                    Vector qTypes = dbadt.getQuantitationTypeDimension_ref().getQuantitationTypes();
                    String identifier = "Empty";
                    int position = 0;

                    for (int i = 0; i < qTypes.size(); i++) {
                        QuantitationType_POLY qt = (QuantitationType_POLY) qTypes.get(i);
                        position = i;

                        if (qt.getStandardQuantitationType_ref() != null) {
                            StandardQuantitationType_POLY sqt = qt.getStandardQuantitationType_ref();

                            if (sqt.getDerivedSignal_ref() != null) {
                                identifier = sqt.getDerivedSignal_ref().getIdentifier();
                            } else if (sqt.getMeasuredSignal_ref() != null) {
                                identifier = sqt.getMeasuredSignal_ref().getIdentifier();
                            } else if (sqt.getRatio_ref() != null) {
                                identifier = sqt.getRatio_ref().getIdentifier();
                            }
                        } else if (qt.getSpecializedQuantitationType_ref() != null) {
                            SpecializedQuantitationType spqt = qt.getSpecializedQuantitationType_ref();
                            identifier = spqt.getIdentifier();
                        }

                        qTMap.put(identifier, new Integer(position));
                    }

                    BioDataCube cube = dbadt.getBioDataValues().getBioDataCube_ref();
                    String order = cube.getOrder();
                    long cubeId = cube.getId().longValue();
                    Reader r = DBInterface.DBStartClobRead("TT_BioDataCube", 2, "ID", cubeId);
                    BufferedReader reader = new BufferedReader(r);
                    String line = null;
                    int i = 0;

                    while ((line = reader.readLine()) != null) {
                        executeLine(order, line, i);

                        if ((++i % 1000) == 0) {
                            System.gc();
                        }
                    }

                    DBInterface.DBFinishClobRead();
                }
            } else if (bioAssay instanceof MeasuredBioAssay) {
                MeasuredBioAssay mba = (MeasuredBioAssay) bioAssay;

                microarray = new CSMicroarray(ser, markerNo, mba.getName(), null, null, true, DSMicroarraySet.geneExpType);
                microarray.setLabel("Measured:" + mba.getId().toString());
                microarray.setLabel("Measured:" + mba.getId().toString());

                Vector mbad = mba.getMeasuredBioAssayData();

                for (int x = 0; x < mbad.size(); x++) {
                    MeasuredBioAssayData mbadt = (MeasuredBioAssayData) mbad.get(x);
                    FeatureDimension fe = mbadt.getDesignElementDimension_ref().getFeatureDimension_ref();

                    if (!maSet.initialized) {
                        Vector features = fe.getContainedFeatures();
                        markerNo = features.size();
                        maSet.initialize(maSet.size(), markerNo);

                        for (int z = 0; z < features.size(); z++) {
                            maSet.getMarkers().get(z).setLabel(((Feature) features.get(z)).getIdentifier());
                            ((CSExpressionMarker) maSet.get(z)).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                            maSet.getMarkers().get(z).setDescription(((Feature) features.get(z)).getIdentifier());
                        }

                        microarray = new CSMicroarray(ser, markerNo, mba.getName(), null, null, true, DSMicroarraySet.geneExpType);
                        microarray.setLabel("Measured:" + mba.getId().toString());
                        microarray.setLabel("Measured:" + mba.getId().toString());
                    }

                    Vector qTypes = mbadt.getQuantitationTypeDimension_ref().getQuantitationTypes();

                    if (qTypes.size() == 1) {
                        if (((QuantitationType_POLY) qTypes.get(0)).getStandardQuantitationType_ref().getMeasuredSignal_ref() != null) {
                            if (((QuantitationType_POLY) qTypes.get(0)).getStandardQuantitationType_ref().getMeasuredSignal_ref().getIdentifier().equalsIgnoreCase("QT:MeasuredSignal")) {
                                System.out.println("data for measured bioassay...");

                                String identifier = "Empty";
                                int position = 0;
                                BioDataCube cube = mbadt.getBioDataValues().getBioDataCube_ref();
                                String order = cube.getOrder();
                                long cubeId = cube.getId().longValue();
                                Reader r = DBInterface.DBStartClobRead("TT_BioDataCube", 2, "ID", cubeId);
                                BufferedReader reader = new BufferedReader(r);
                                String line = null;
                                int i = 0;

                                while ((line = reader.readLine()) != null) {
                                    if (!line.split("\t")[0].equalsIgnoreCase("Probe Set Index")) {
                                        executeLine(order, line, i, ser);

                                        if ((++i % 1000) == 0) {
                                            System.gc();
                                        }

                                        if (i >= markerNo) {
                                            break;
                                        }
                                    }
                                }

                                DBInterface.DBFinishClobRead();

                                return microarray;
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
}
