package org.geworkbench.bison.datastructure.biocollections.microarrays;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.bison.util.Range;

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

    private ReaderMonitor createProgressReader(String display, File file) throws FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, display, fileIn);
        ReaderMonitor retValue = new ReaderMonitor();

        retValue.pm = progressIn.getProgressMonitor();
        retValue.reader = new BufferedReader(new InputStreamReader(progressIn));
        return retValue;
    }

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

    /**
     *  Parsing data file. 
     */
    public void read(File _file) {
        file = _file;
        label = file.getName();

        currGeneId = 0;
        CMicroarrayParser parser = new CMicroarrayParser();

        this.label = file.getName();
        this.absPath = file.getAbsolutePath();

        if (!readAndParse(parser, ParseType.STRUCTURE, "Getting structure information from " + file.getName()))
        	return;
        if (!readAndParse(parser, ParseType.MARKER, "Loading Marker Data from " + file.getName()))
        	return;
        sortMarkers(parser.markerNo);
        readAndParse(parser, ParseType.VALUE, "Loading Marker Value from " + file.getName());
    }

    private enum ParseType {STRUCTURE, MARKER, VALUE};
    private boolean readAndParse(CMicroarrayParser parser, ParseType type, String message){
        currGeneId = 0;
        ReaderMonitor rm = null;
        try {
            rm = createProgressReader(message, file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return false;
        }
        if (type == ParseType.MARKER)
        	initialize(parser.microarrayNo, parser.markerNo);
        String line = null;
        try {
            while ((line = rm.reader.readLine()) != null) {
				if (!line.trim().equalsIgnoreCase("")) {
			    	if (type == ParseType.STRUCTURE) 
			    		parser.parseLine(line.trim(), this);
			    	else if (type == ParseType.MARKER)
			    		parser.executeLine(line.trim(), this);
			    	else if (type == ParseType.VALUE)
			    		parser.parseValue(line.trim());
					if (rm.pm != null && rm.pm.isCanceled()) {
						loadingCancelled = true;
						rm.reader.close();
						return false;
					}
				}
			}
        } catch (InterruptedIOException iioe) {
            loadingCancelled = true;
            return false;
        } catch (Exception ioe) {
            log.error("Error while parsing line: " + line);
            ioe.printStackTrace();
            return false;
        } finally {
			try {
				rm.reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				// nothing further necessary
			}

        }
        return true;
    }

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

    int currGeneId = 0;

    private class CMicroarrayParser {
        //total number of microarry
        public int microarrayNo = 0;
        public int markerNo = 0;

        //total number of properties
        Vector<String> phenotypes = new Vector<String>();
        int phenotypeNo = 0;

        void executeLine(String line, DSMicroarraySet<DSMicroarray> mArraySet) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            if (line.charAt(0) == '#') {
                return; //
            }
            //Ask Manjunath why the tokenizer was replaced with a split
            // Sun advices that: "StringTokenizer is a legacy class that is retained
            // for compatibility reasons although its use is discouraged in new code."
            // http://java.sun.com/j2se/1.4.2/docs/api/java/bisonparsers/StringTokenizer.html
            // - Manju
            // watkin - I changed it back to a StringTokenizer, as it will ignore redundant delimiters (tabs)
            // while String.split will not.
            StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);            
            int n = tokenizer.countTokens();
            String[] st = new String[n];
            for (int i = 0; i < n; i++) {
                st[i] = tokenizer.nextToken();
                if (i == 1 && !st[0].equalsIgnoreCase("PDFModel") && !st[0].equalsIgnoreCase("AffyID") 
                		&& !st[0].equalsIgnoreCase("Description") && line.charAt(0) != '\t')
                	break;
            }

            if (st.length > 0) {
                String token = st[0];
                /**
                 * This handles the first line, which contains the microarray labels
                 * separated by tabs.
                 */
                if (token.equalsIgnoreCase("PDFModel")) {
                } else if (token.equalsIgnoreCase("AffyID")) {
                    boolean isAccession = true; //phLabel.equalsIgnoreCase("Annotation");
                    int i = 0;
                    //read the first line and put label of the arrays in.
                    for (int j = 2; j < st.length; j++) {
                        if (isAccession) {
                            String maLabel = new String(st[j]);
                            get(i++).setLabel(maLabel);
                        }
                    }
                } else if (token.equalsIgnoreCase("Description")) {
                    //This handles all the phenotype definition lines
                    String phLabel = new String(st[1]);
                    DSAnnotationContext<DSMicroarray> context = manager.getContext(mArraySet, phLabel);
                    CSAnnotationContext.initializePhenotypeContext(context);
                    for (int j = 2; j < st.length; j++) {
                        String valueLabel = new String(st[j]);
                        if ((valueLabel != null) && (!valueLabel.equalsIgnoreCase(""))) {
                            if (valueLabel.indexOf("|") > -1)
                            {
                            	for (String tok: valueLabel.split("\\|"))
                            		context.labelItem(mArraySet.get(j - 2), tok);
                            }
                            else
                            	context.labelItem(mArraySet.get(j - 2), valueLabel);
                        }
                    }
                } else if (line.charAt(0) != '\t') {
                    // This handles individual gene lines with (value, pvalue) pairs separated by tabs
                    DSGeneMarker mi = (DSGeneMarker)mArraySet.getMarkers().get(currGeneId);
                    if (markerVector.size() > currGeneId) {
                        mi = markerVector.get(currGeneId);
                    }
                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mi).reset(currGeneId, microarrayNo, microarrayNo);
                    //set the affyid field of current marker.
                    mi.setLabel(token);
                    String label = new String(st[1]);
                    //set the annotation field of current marker
                    mi.setDescription(label);

                    markerVector.add(currGeneId, mi);
                    try {
                        String[] result = AnnotationParser.getInfo(token, AnnotationParser.LOCUSLINK);
                        String locus = " ";
                        if ((result != null) && (!result[0].equals(""))) {

                            locus = result[0];

                        }
                        markerVector.get(currGeneId).getUnigene().set(token);

                        if (locus.compareTo(" ") != 0) {
                            try {
                                markerVector.get(currGeneId).setGeneId(Integer.parseInt(locus.trim()));
                            } catch (NumberFormatException e) {
                                log.debug("Invalid locus link for gene "+currGeneId);
                            }
                        }

                        String[] geneNames = AnnotationParser.getInfo(token, AnnotationParser.ABREV);
                        if (geneNames != null) {
                            markerVector.get(currGeneId).setGeneName(geneNames[0].trim());
                        }
                    } catch (Exception e) {
                        System.out.println("error parsing " + token);
                        e.printStackTrace();
                    }
                    currGeneId++;
                }
            }
        } //end of executeLine()
        
        void parseValue(String line) {
            if (line.charAt(0) == '#') {
                return; //
            }

            StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);            
            int n = tokenizer.countTokens();
            String[] st = new String[n];
            for (int i = 0; i < n; i++) {
                st[i] = tokenizer.nextToken();
            }

            if (st.length > 0) {
                String token = st[0];
                if (!token.equalsIgnoreCase("PDFModel") && !token.equalsIgnoreCase("AffyID") 
                		&& !token.equalsIgnoreCase("Description") && line.charAt(0) != '\t') {
                    // This handles individual gene lines with (value, pvalue) pairs separated by tabs
                    int i = 0;
                    boolean pValueExists = ((st.length - 2) > microarrayNo);
                    for (int j = 2; j < st.length; j++) {
                        CSMarkerValue marker = (CSMarkerValue) get(i).getMarkerValue(newid[currGeneId]);
                        String value = st[j];
                        if ((value == null) || (value.equalsIgnoreCase(""))) { // skip the extra '/t'
                            value = st[++j];
                        }
                        String pValue;
                        // if (Boolean.parseBoolean(System.getProperty("expressionMA.usePValue")) || pValueExists) {
                        if (pValueExists) {                        	
                            j++;
                            pValue = st[j];
                        } else {
//                        	 If no p-value is present, assume that the detection call is "Present"
                            pValue = 0.000001 + ""; 
                        }

                        parse(marker, value, pValue);
                        ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(newid[currGeneId])).check(marker, false);
                        if (marker.isMasked() || marker.isMissing()) {
                            maskedSpots++;
                        }
                        //getIMicroarray(i).setIMarker(i, marker);
                        i++;
                    }
                    currGeneId++;
                }
            }
        } 

        void parseLine(String line, DSMicroarraySet<? extends DSBioObject> mArraySet) {
        	
            if (line.charAt(0) == '#') {
                return;
            }

            int startindx = line.indexOf('\t');
            if (startindx > 0) {

                if (line.startsWith("PDFModel")) {
                } else if (line.substring(0, 6).equalsIgnoreCase("AffyID")) {
                    String[] st = line.split("\t");
                    for (int j = 2; j < st.length; j++) {
                        if ((st[j] != null) && (!st[j].equalsIgnoreCase(""))) {
                            microarrayNo++;
                        }
                    }
                } else {
                    if (line.substring(0, 11).equalsIgnoreCase("Description")) {
                        String[] st = line.split("\t");
                        String phenoLabel = new String(st[1]);
                        phenotypes.add(phenotypeNo, phenoLabel);
                        phenotypeNo++;
                        //                        countMicroarrayNo(st);
                    } else if (line.charAt(0) != '\t') {
                        if (mArraySet.getCompatibilityLabel() == null) {
                            String token = line.substring(0, startindx);
                            if (mArraySet.getCompatibilityLabel() == null) {
                                String chiptype = AnnotationParser.matchChipType(mArraySet, token, false);
                                if (chiptype != null) {
                                    mArraySet.setCompatibilityLabel(chiptype);
                                }
                            }
                        }
                        markerNo++;
                    }
                }
            } //end of parseline()
        } //end of inner class parser
    }

    private void parse(CSMarkerValue marker, String value, String status) {
        if (Character.isLetter(status.charAt(0))) {
            try {
                char c = status.charAt(0);
                if (Character.isLowerCase(c)) {
                    marker.mask();
                }
                switch (Character.toUpperCase(c)) {
                    case 'P':
                        marker.setPresent();
                        break;
                    case 'A':
                        marker.setAbsent();
                        break;
                    case 'M':
                        marker.setMarginal();
                        break;
                    default:
                        marker.setMissing(true);
                        break;
                }
                parse(marker, value);
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        } else {
            try {
                double v = Double.parseDouble(value);
                Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(currGeneId)).getRange();

                marker.setValue(v);
                range.max = Math.max(range.max, v);
                range.min = Math.min(range.min, v);

                double p = Double.parseDouble(status);
                marker.setConfidence(p);
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        }
    }

    private void parse(DSMutableMarkerValue marker, String value) {
        if (marker instanceof CSExpressionMarkerValue) {
            String[] parseableValue = value.split(":");
            String expression = parseableValue[parseableValue.length - 1];
            try {
                double v = Double.parseDouble(expression);
                org.geworkbench.bison.util.Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(currGeneId)).getRange();

                marker.setValue(v);
                range.max = Math.max(range.max, v);
                range.min = Math.min(range.min, v);

            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        } else {
            String[] parseableValue = value.split(":");
            String expression = parseableValue[parseableValue.length - 1];
            try {
                double v = Double.parseDouble(expression);
                marker.setValue(v);
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        }
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

    // Convenience class - used as the return value of method
    // <code>createProgressReader()</code>.
    private class ReaderMonitor {
        BufferedReader reader = null;
        ProgressMonitor pm = null;
    }
}
