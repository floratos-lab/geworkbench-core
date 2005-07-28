package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSChipchip;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSChipchip;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;

import javax.swing.*;
import java.io.*;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CSChipchipSet extends CSMicroarraySet<DSChipchip> {
    int currGeneId;

    public CSChipchipSet() {
    }

    public void parse(DSMutableMarkerValue marker, String value) {
    }

    public int getPlatformType() {
        return 0;
    }

    private BufferedReader createProgressReader(String display, File file) throws FileNotFoundException {
        BufferedReader reader;
        FileInputStream fileIn = new FileInputStream(file);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, display, fileIn);
        reader = new BufferedReader(new InputStreamReader(progressIn));
        return reader;
    }

    public void readFromFile(File file) {
        currGeneId = 0;
        CSChipchipParser parser = new CSChipchipParser();
        BufferedReader reader;
        this.label = file.getName();
        this.absPath = file.getAbsolutePath();
        try {
            reader = createProgressReader("Getting structure information from " + file.getName(), file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                parser.parseLine(line, this);
            }
            reader.close();
        } catch (Exception ioe) {
            int i = 0;
            return;
        } finally {
        }
        try {

            reader = createProgressReader("Loading Data from " + file.getName(), file);
        } catch (FileNotFoundException fnf) {
            return;
        }
        initialize(parser.microarrayNo, parser.markerNo);
        try {
            int i = 1;
            while ((line = reader.readLine()) != null) {
                parser.executeLine(line, this);
                if (i % 1000 == 0) {
                    System.gc();
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
            System.out.println("Error while parsing line: " + line);
            return;
        } finally {
            //            setPhenotype();

        }
    }

    public void initialize(int maNo, int mrkNo) {
        // this is required so that the microarray vector may create arrays of the right size
        for (int microarrayId = 0; microarrayId < maNo; microarrayId++) {
            add(microarrayId, new CSChipchip(microarrayId, mrkNo));
        }
        //        for (int i = 0; i < mrkNo; i++) {
        //            CSGeneMarker marker = new CSGeneMarker();
        //            markerVector.add(i, marker);
        //        }
    }

    public class CSChipchipParser extends org.geworkbench.bison.datastructure.bioobjects.microarray.microarrayIO.CSMAParser {
        //This is hacked up -- to decide on a real file format -- AM
        public int microarrayNo = 0;
        public int markerNo = 0;

        void parseLine(String line, CSChipchipSet mArraySet) {
            if (line.charAt(0) == '#') {
                return;
            }
            int startindx = line.indexOf('\t');
            if (startindx > 0) {
                if (line.startsWith("TFIDs")) {
                    String[] st = line.split("\t");
                    for (int i = 1; i < st.length; i++) {
                        if (st[i].length() > 0) {
                            microarrayNo++;
                        }
                    }
                    microarrayNo /= 2;
                } else if (line.charAt(0) != '\t' && !line.startsWith("ProbeID")) {
                    markerNo++;
                }
            }
        } //end of parseline()

        void executeLine(String line, CSChipchipSet mArraySet) {
            if (line.charAt(0) == '#') {
                return; //
            }

            String[] st = line.split("\t");

            if (st.length <= 0) {
                return;
            }

            if (st[0].equalsIgnoreCase("TFIDs")) {
                int ctr = 0;
                for (int i = 4; i < st.length; i += 2) {
                    String tfName = new String(st[i]);
                    DSGeneMarker marker = new CSGeneMarker();
                    marker.setGeneName(tfName);
                    get(ctr).setTranscriptionFactor(marker);
                }
            } else if ("ProbeID".equals(st[0])) {
                //ignore the header line
            } else if (st[0].length() > 0 && st.length > 4) {
                // This handles individual gene lines with (value, pvalue) pairs separated by tabs
                int i = 0;
                //If there are multiple probes for the same gene we will only consider the one
                //with the lowest p-value
                addLowestValueForMarker(st);
            }
        }

        void addLowestValueForMarker(String[] st) {
            DSGeneMarker marker = new CSGeneMarker();
            String label = new String(st[0]);
            String geneName = new String(st[1]);
            String geneDescription = new String(st[2]);
            String strGeneId = new String(st[3]);
            if (strGeneId != null && (!"---".equals(strGeneId)) && strGeneId.length() > 0) {
                int geneId = Integer.parseInt(strGeneId);
                marker.setGeneId(geneId);
            }

            marker.setLabel(label);
            marker.setGeneName(geneName);
            marker.setDescription(geneDescription);

            DSGeneMarker newMarker = markerVector.get(marker);
            if (newMarker == null) {
                newMarker = marker;
                markerVector.add(newMarker);
            }

            //This is BS
            int maCtr = 0;
            for (int j = 4; j < 4 + size() + 1; j++) {
                //                    DSMutableMarkerValue markerValue = (DSMutableMarkerValue)
                //                        get(i).
                //                        getMarkerValue(currGeneId);
                DSMutableMarkerValue markerValue = new CSExpressionMarkerValue(0.0f);

                if (j > st.length - 1) {
                    markerValue.setValue(Double.NaN);
                    markerValue.setConfidence(Double.NaN);
                } else {
                    String value = st[j];

                    if (value == null || value.length() == 0) {
                        markerValue.setValue(Double.NaN);
                        markerValue.setConfidence(Double.NaN);
                    } else {
                        markerValue.setValue(Double.parseDouble(value));
                        markerValue.setConfidence(Double.parseDouble(value));
                    }
                }
                j++;

                DSMutableMarkerValue chipMarkerValue = get(maCtr).getMarkerValue(newMarker.getSerial());
                if (chipMarkerValue == null) {
                    //                    chipMarkerValue = markerValue;
                    get(maCtr).setMarkerValue(newMarker.getSerial(), markerValue);
                } else if (markerValue.getValue() < chipMarkerValue.getValue()) {
                    chipMarkerValue.setValue(markerValue.getValue());
                    chipMarkerValue.setConfidence(markerValue.getConfidence());
                }

                maCtr++;
            }

        }

        //This won't work for now
        void addMarker(String[] st, int i) {
            DSGeneMarker marker = new CSGeneMarker();
            markerVector.add(currGeneId, marker);
            //                DSGeneMarker marker = markerVector.get(currGeneId);
            //                if (marker == null) {
            //                    marker = new CSGeneMarker();
            //                }

            //Set the annotation information for the marker
            String label = new String(st[0]);
            String geneName = new String(st[1]);
            String geneDescription = new String(st[2]);
            String strGeneId = new String(st[3]);
            if (strGeneId != null && (!"---".equals(strGeneId)) && strGeneId.length() > 0) {
                int geneId = Integer.parseInt(strGeneId);
                marker.setGeneId(geneId);
            }

            marker.setLabel(label);
            marker.setGeneName(geneName);
            marker.setDescription(geneDescription);
            markerVector.add(currGeneId, marker);

            //Initialize the marker values for the experiments
            //                for (int j = 3; j < st.length; j++) {
            //This is BS
            for (int j = 4; j < 4 + size() + 1; j++) {
                //                    DSMutableMarkerValue markerValue = (DSMutableMarkerValue)
                //                        get(i).
                //                        getMarkerValue(currGeneId);
                DSMutableMarkerValue markerValue = new CSExpressionMarkerValue(0.0f);

                if (j > st.length - 1) {
                    markerValue.setValue(Double.NaN);
                    markerValue.setConfidence(Double.NaN);
                } else {
                    String value = st[j];

                    if (value == null || value.length() == 0) {
                        markerValue.setValue(Double.NaN);
                        markerValue.setConfidence(Double.NaN);
                    } else {
                        markerValue.setValue(Double.parseDouble(value));
                        markerValue.setConfidence(Double.parseDouble(value));
                    }
                }
                j++;
                get(i).setMarkerValue(currGeneId, markerValue);
                i++;
            }
            currGeneId++;
        }
    }
}
