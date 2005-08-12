package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.bioobjects.markers.genotype.CSGenotypeMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenotypicMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Icons;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSMicroarraySet1 extends CSMicroarraySet<DSMicroarray> implements Serializable {
    //static private ImageIcon icon      = new ImageIcon(MicroarrayDataSet.class.getResource("ma.gif"));
    private HashMap properties = new HashMap();
    private ArrayList descriptions = new ArrayList();
    private File file = null;
    private String label = "Undefined";

    public CSMicroarraySet1(String id, String name, String description) {
        super(id, name);
        this.setExperimentInformation(description);
    }

    public ImageIcon getIcon() {
        return Icons._dataSetIcon;
    }

    public File getFile() {
        return file;
    }

    public void readFromFile(File _file) {
        file = _file;
        label = file.getName();
        CMicroarrayParser parser = new CMicroarrayParser();
        BufferedReader reader;
        this.setLabel(file.getName());
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnf) {
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
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnf) {
            return;
        }
        for (int microarrayId = 0; microarrayId < parser.microarrayNo; microarrayId++) {
            DSMicroarray m = new CSMicroarray(parser.markerNo);
            m.setLabel("Test");
            //m.setMicroarraySet(this);
            m.setSerial(microarrayId);
            add(m);
        }
        try {
            while ((line = reader.readLine()) != null) {
                parser.executeLine(line, this);
            }
        } catch (Exception ioe) {
            System.out.println("Error while parsing line: " + line);
            return;
        } finally {
            //setPhenotype();
        }
    }

    public class CMicroarrayParser {
        private org.geworkbench.bison.parsers.DataParseContext dataContext = new org.geworkbench.bison.parsers.DataParseContext();
        private boolean parseByMicroarray = false;
        public int microarrayNo = 0;
        public int markerNo = 0;
        public int propNo = 0;
        public boolean microarrayNoSet = false;
        int currGeneId = 0;
        int currMicroarrayId = 0;
        int maskedSpots = 0;
        int gtBase = 256;
        Vector phenotypes = new Vector();

        void executeLine(String line, CSMicroarraySet1 mArraySet) {
            StringTokenizer st = new StringTokenizer(line, "\t\n\r");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equalsIgnoreCase("NullSigma")) {
                } else if (token.equalsIgnoreCase("GTData")) {
                } else if (token.equalsIgnoreCase("MaxSigma")) {
                } else if (token.equalsIgnoreCase("ColumnsAreMarkers")) {
                } else if (token.equalsIgnoreCase("DeltaSigma")) {
                } else if (token.equalsIgnoreCase("LogValues")) {
                } else if (token.equalsIgnoreCase("AddValue")) {
                } else if (token.equalsIgnoreCase("MinValue")) {
                } else if (token.equalsIgnoreCase("MinSupport")) {
                } else if (token.equalsIgnoreCase("MinMarkers")) {
                } else if (token.equalsIgnoreCase("Phenotype")) {
                } else if (token.equalsIgnoreCase("PDFModel")) {
                } else if (token.equalsIgnoreCase("SortElements")) {
                } else if (token.equalsIgnoreCase("SNPData")) {
                } else if (token.equalsIgnoreCase("AlleleData")) {
                } else if (parseByMicroarray && token.equalsIgnoreCase("Accession")) {
                    int i = 0;
                    int j = 0;
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if ((token.length() > 1) && token.substring(0, 2).equalsIgnoreCase("Ph")) {
                            phenotypes.add(i, token);
                            i++;
                        } else {
                            CSGenotypeMarker ms = new CSGenotypeMarker(j);
                            ms.setDescription(token);
                            markerVector.add(ms);
                            j++;
                        }
                    }
                } else if (!parseByMicroarray && token.equalsIgnoreCase("Description")) {
                    // you can read cell variable here
                    String phLabel = st.nextToken();
                    boolean isAccession = phLabel.equalsIgnoreCase("Accession");
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        String name = phLabel;
                        String value = st.nextToken();
                        get(i).addNameValuePair(name, value);  // Adds the phValue to the microarray
                        if (isAccession) {
                            get(i).setLabel(value);
                        }
                        i++;
                    }
                } else if (token.equalsIgnoreCase("ID")) {
                    int geneId = 0;
                    for (int propId = 0; propId < phenotypes.size(); propId++) {
                        String value = st.nextToken();
                        phenotypes.set(propId, value);
                    }
                    while (st.hasMoreTokens()) {
                        String value = st.nextToken();
                        CSGenotypeMarker gtInfo = new CSGenotypeMarker(geneId);
                        gtInfo.setLabel(token);
                        markerVector.add(gtInfo);
                        geneId++;
                    }
                } else if (line.charAt(0) != '\t') {
                    if (parseByMicroarray) {
                        int geneId = 0;
                        get(currMicroarrayId).setLabel(token);
                        for (int propId = 0; propId < phenotypes.size(); propId++) {
                            String property = (String) phenotypes.get(propId);
                            String value = st.nextToken();
                            get(currMicroarrayId).addNameValuePair(property, value);
                        }
                        while (st.hasMoreTokens()) {
                            String value = st.nextToken();
                            CSGenotypicMarkerValue gt = (CSGenotypicMarkerValue) get(currMicroarrayId).getMarkerValue(geneId);
                            gt.parse(value, gtBase);
                            ((CSGenotypeMarker) getMarkers().get(geneId)).check(gt, false);
                            geneId++;
                        }
                        currMicroarrayId++;
                    } else {
                        int i = 0;
                        CSGenotypeMarker gtInfo = new CSGenotypeMarker(currGeneId);
                        gtInfo.reset(currGeneId, microarrayNo, microarrayNo);
                        gtInfo.setDescription(token);
                        token = st.nextToken();
                        gtInfo.setLabel(token);
                        markerVector.add(gtInfo);
                        while (st.hasMoreTokens()) {
                            CSGenotypicMarkerValue gt = (CSGenotypicMarkerValue) get(i).getMarkerValue(currGeneId);
                            if (gt == null) {
                                gt = new CSGenotypicMarkerValue(0, 0);
                                get(i).setMarkerValue(i, gt);
                            }
                            String value = st.nextToken();
                            String status = st.nextToken();
                            gt.parse(value, status, gtBase);
                            gtInfo.check(gt, false);
                            if (gt.isMasked() || gt.isMissing()) {
                                maskedSpots++;
                            }
                            i++;
                        }
                        currGeneId++;
                    }
                }
            }
        }

        void parseLine(String line, CSMicroarraySet1 mArraySet) {
            StringTokenizer st = new StringTokenizer(line, "\t\n\r");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equalsIgnoreCase("GTData")) {
                    gtBase = Integer.parseInt(st.nextToken());
                } else if (token.equalsIgnoreCase("ColumnsAreMarkers")) {
                    parseByMicroarray = true;
                } else if (token.equalsIgnoreCase("NullSigma")) {
                    dataContext.sigma0 = Double.parseDouble(st.nextToken());
                    mArraySet.put("Sigma0", new Double(dataContext.sigma0));
                } else if (token.equalsIgnoreCase("MaxSigma")) {
                    dataContext.maxSigma = Double.parseDouble(st.nextToken());
                    mArraySet.put("Sigma1", new Double(dataContext.maxSigma));
                } else if (token.equalsIgnoreCase("DeltaSigma")) {
                    dataContext.deltaSigma = Double.parseDouble(st.nextToken());
                    mArraySet.put("DeltaSigma", new Double(dataContext.deltaSigma));
                } else if (token.equalsIgnoreCase("LogValues")) {
                    dataContext.isLog = true;
                    if (dataContext.minValue <= 0) {
                        dataContext.minValue = 1;
                    }
                } else if (token.equalsIgnoreCase("AddValue")) {
                    dataContext.addValue = Double.parseDouble(st.nextToken());
                    mArraySet.put("AddValue", new Double(dataContext.addValue));
                } else if (token.equalsIgnoreCase("MinValue")) {
                    dataContext.minValue = Double.parseDouble(st.nextToken());
                    mArraySet.put("MinValue", new Double(dataContext.minValue));
                } else if (token.equalsIgnoreCase("PDFModel")) {
                    dataContext.pdfMode = st.nextToken();
                    mArraySet.put("PDFMode", dataContext.pdfMode);
                } else if (token.equalsIgnoreCase("MinSupport")) {
                    dataContext.minSupport = Integer.parseInt(st.nextToken());
                    mArraySet.put("MinSupport", new Double(dataContext.minSupport));
                } else if (token.equalsIgnoreCase("MinMarkers")) {
                    dataContext.minMarkers = Integer.parseInt(st.nextToken());
                    mArraySet.put("MinMarkerNo", new Double(dataContext.minMarkers));
                } else if (token.equalsIgnoreCase("Phenotype")) {
                    /** @todo Add correct selection of phenotype tags based on criteria
                     PhenoProperty selection = new PhenoProperty(st.nextToken());
                     Phenotype.SetSelectedPhenoProperty(st.nextToken(), st.nextToken());
                     */
                    st.nextToken();
                } else if (token.equalsIgnoreCase("SortElements")) {
                    dataContext.sortKey = st.nextToken();
                    mArraySet.put("SortKey", dataContext.sortKey);
                } else if (token.equalsIgnoreCase("ID")) {
                } else if (parseByMicroarray && token.equalsIgnoreCase("Accession")) {
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if ((token.length() > 1) && token.substring(0, 2).equalsIgnoreCase("Ph")) {
                            propNo++;
                        } else {
                            markerNo++;
                        }
                    }
                } else if (!parseByMicroarray && token.equalsIgnoreCase("Description")) {
                    // you can read cell variable here
                    String value = st.nextToken();
                    if (!microarrayNoSet && value.equalsIgnoreCase("Accession")) {
                        while (st.hasMoreTokens()) {
                            st.nextToken();
                            microarrayNo++;
                        }
                        microarrayNoSet = true;
                    }
                    propNo++;
                } else if (line.charAt(0) != '\t') {
                    if (parseByMicroarray) {
                        microarrayNo++;
                    } else {
                        markerNo++;
                    }
                }
            }
        }
    }

    public void put(Object key, Object value) {
        properties.put(key, value);
    }

    public Object get(Object key) {
        return properties.get(key);
    }

    public String getDataSetName() {
        return label;
    }

    public String getName() {
        return getDataSetName();
    }

    public int getPlatformType() {
        return AFFYMETRIX_PLATFORM;
    }

    /**
     * writeToFile
     *
     * @param fileName String
     */
    public void writeToFile(String fileName) {
    }


    /**
     * @todo Check out these empty methods
     */
    public int getType() {
        return 0;
    }

    public DSMicroarraySet clone(String newLabel, int newMarkerNo, int newChipNo) {
        return null;
    }

    public DSMicroarraySet toHaplotype() {
        return null;
    }

    public void resetStatistics() {
    }

    public void parse(DSMutableMarkerValue marker, String value) {
    }
}
