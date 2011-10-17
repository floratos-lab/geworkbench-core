package org.geworkbench.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Range;

public class MicroarraySetParser {
	private static Log log = LogFactory.getLog(MicroarraySetParser.class);

	private transient CSMicroarraySet miroarraySet;

	private transient int currGeneId = 0;

	private transient File file;

	// this method returns a parsed CSExprMicroarraySet. It could return null in
	// case parsing fails.
	CSMicroarraySet parseCSMicroarraySet(File file) {
		CSMicroarraySet m = new CSMicroarraySet();
		return parseCSMicroarraySet(file, m);
	}

	// this extra layer is only to provide the chance to set compatibilityLabel
	// (annotation file) before parsing
	private CSMicroarraySet parseCSMicroarraySet(File file,
			CSMicroarraySet m) {
		this.file = file;

		miroarraySet = m;
		miroarraySet.setLabel(file.getName());

		if (!readAndParse(ParseType.STRUCTURE,
				"Getting structure information from " + file.getName()))
			return null;
		if (!readAndParse(ParseType.MARKER,
				"Loading Marker Data from " + file.getName()))
			return null;
		miroarraySet.sortMarkers(markerNo);
		readAndParse(ParseType.VALUE,
				"Loading Marker Value from " + file.getName());

		return miroarraySet;
	}

	private enum ParseType {
		STRUCTURE, MARKER, VALUE
	};

	private boolean readAndParse(ParseType type, String message) {
		currGeneId = 0;
		ReaderMonitor rm = null;
		try {
			rm = createProgressReader(message, file);
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
			return false;
		}
		if (type == ParseType.MARKER)
			miroarraySet.initialize(microarrayNo, markerNo);
		String line = null;
		try {
			while ((line = rm.reader.readLine()) != null) {
				if (!line.trim().equalsIgnoreCase("")) {
					if (type == ParseType.STRUCTURE)
						parseLine(line.trim(), miroarraySet);
					else if (type == ParseType.MARKER)
						executeLine(line.trim(), miroarraySet);
					else if (type == ParseType.VALUE)
						parseValue(line.trim());
					if (rm.pm != null && rm.pm.isCanceled()) {
						rm.reader.close();
						return false;
					}
				}
			}
		} catch (InterruptedIOException iioe) {
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

	// total number of microarray
	private transient int microarrayNo = 0;
	private transient int markerNo = 0;

	// total number of properties
	private transient Vector<String> phenotypes = new Vector<String>();
	private transient int phenotypeNo = 0;

	private void executeLine(String line, DSMicroarraySet<DSMicroarray> mArraySet) {
		CSAnnotationContextManager manager = CSAnnotationContextManager
				.getInstance();
		if (line.charAt(0) == '#') {
			return; //
		}
		// Ask Manjunath why the tokenizer was replaced with a split
		// Sun advices that: "StringTokenizer is a legacy class that is retained
		// for compatibility reasons although its use is discouraged in new
		// code."
		// http://java.sun.com/j2se/1.4.2/docs/api/java/bisonparsers/StringTokenizer.html
		// - Manju
		// watkin - I changed it back to a StringTokenizer, as it will ignore
		// redundant delimiters (tabs)
		// while String.split will not.
		StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);
		int n = tokenizer.countTokens();
		String[] st = new String[n];
		for (int i = 0; i < n; i++) {
			st[i] = tokenizer.nextToken();
			if (i == 1 && !st[0].equalsIgnoreCase("PDFModel")
					&& !st[0].equalsIgnoreCase("AffyID")
					&& !st[0].equalsIgnoreCase("Description")
					&& line.charAt(0) != '\t')
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
				boolean isAccession = true; // phLabel.equalsIgnoreCase("Annotation");
				int i = 0;
				// read the first line and put label of the arrays in.
				for (int j = 2; j < st.length; j++) {
					if (isAccession) {
						String maLabel = new String(st[j]);
						DSMicroarray microarray = (DSMicroarray)miroarraySet.get(i++); 
						microarray.setLabel(maLabel);
					}
				}
			} else if (token.equalsIgnoreCase("Description")) {
				// This handles all the phenotype definition lines
				String phLabel = new String(st[1]);
				DSAnnotationContext<DSMicroarray> context = manager.getContext(
						mArraySet, phLabel);
				CSAnnotationContext.initializePhenotypeContext(context);
				for (int j = 2; j < st.length; j++) {
					String valueLabel = new String(st[j]);
					if ((valueLabel != null)
							&& (!valueLabel.equalsIgnoreCase(""))) {
						if (valueLabel.indexOf("|") > -1) {
							for (String tok : valueLabel.split("\\|"))
								context.labelItem(mArraySet.get(j - 2), tok);
						} else
							context.labelItem(mArraySet.get(j - 2), valueLabel);
					}
				}
			} else if (line.charAt(0) != '\t') {
				// This handles individual gene lines with (value, pvalue) pairs
				// separated by tabs
				DSGeneMarker mi = (DSGeneMarker) mArraySet.getMarkers().get(
						currGeneId);
				if (this.miroarraySet.getMarkerVector().size() > currGeneId) {
					mi = this.miroarraySet.getMarkerVector().get(
							currGeneId);
				}
				((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mi)
						.reset(currGeneId, microarrayNo, microarrayNo);
				// set the affyid field of current marker.
				mi.setLabel(token);
				String label = new String(st[1]);
				// set the annotation field of current marker
				mi.setDescription(label);

				this.miroarraySet.getMarkerVector().add(currGeneId, mi);
				try {
					String[] result = AnnotationParser.getInfo(token,
							AnnotationParser.LOCUSLINK);
					String locus = " ";
					if ((result != null) && (!result[0].equals(""))) {

						locus = result[0];

					}
					this.miroarraySet.getMarkerVector().get(currGeneId)
							.getUnigene().set(token);

					if (locus.compareTo(" ") != 0) {
						try {
							this.miroarraySet.getMarkerVector()
									.get(currGeneId)
									.setGeneId(Integer.parseInt(locus.trim()));
						} catch (NumberFormatException e) {
							log.debug("Invalid locus link for gene "
									+ currGeneId);
						}
					}

					String[] geneNames = AnnotationParser.getInfo(token,
							AnnotationParser.ABREV);
					if (geneNames != null) {
						this.miroarraySet.getMarkerVector()
								.get(currGeneId)
								.setGeneName(geneNames[0].trim());
					}
				} catch (Exception e) {
					System.out.println("error parsing " + token);
					e.printStackTrace();
				}
				currGeneId++;
			}
		}
	} // end of executeLine()

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
			if (!token.equalsIgnoreCase("PDFModel")
					&& !token.equalsIgnoreCase("AffyID")
					&& !token.equalsIgnoreCase("Description")
					&& line.charAt(0) != '\t') {
				// This handles individual gene lines with (value, pvalue) pairs
				// separated by tabs
				int i = 0;
				boolean pValueExists = ((st.length - 2) > microarrayNo);
				for (int j = 2; j < st.length; j++) {
					DSMicroarray microarray = (DSMicroarray)miroarraySet.get(i);
					CSMarkerValue marker = (CSMarkerValue) microarray.getMarkerValue(
									this.miroarraySet.newid[currGeneId]);
					String value = st[j];
					if ((value == null) || (value.equalsIgnoreCase(""))) { // skip
																			// the
																			// extra
																			// '/t'
						value = st[++j];
					}
					String pValue;
					// if
					// (Boolean.parseBoolean(System.getProperty("expressionMA.usePValue"))
					// || pValueExists) {
					if (pValueExists) {
						j++;
						pValue = st[j];
					} else {
						// If no p-value is present, assume that the detection
						// call is "Present"
						pValue = 0.000001 + "";
					}

					parse(marker, value, pValue);
					((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) this.miroarraySet
							.getMarkerVector().get(
									this.miroarraySet.newid[currGeneId]))
							.check(marker, false);
					if (marker.isMasked() || marker.isMissing()) {
						this.miroarraySet.increaseMaskedSpots();
					}
					// getIMicroarray(i).setIMarker(i, marker);
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
					// countMicroarrayNo(st);
				} else if (line.charAt(0) != '\t') {
					if (mArraySet.getCompatibilityLabel() == null) {
						String token = line.substring(0, startindx);
						if (mArraySet.getCompatibilityLabel() == null) {
							String chiptype = AnnotationParser.matchChipType(
									mArraySet, token, false);
							if (chiptype != null) {
								mArraySet.setCompatibilityLabel(chiptype);
							}
						}
					}
					markerNo++;
				}
			}
		} // end of parseline()
	} // end of inner class parser

	void parse(CSMarkerValue marker, String value, String status) {
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
				Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) miroarraySet
						.getMarkerVector().get(currGeneId)).getRange();

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
				org.geworkbench.bison.util.Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) miroarraySet
						.getMarkerVector().get(currGeneId)).getRange();

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

	// Convenience class - used as the return value of method
	// <code>createProgressReader()</code>.
	private class ReaderMonitor {
		BufferedReader reader = null;
		ProgressMonitor pm = null;
	}

	private ReaderMonitor createProgressReader(String display, File file)
			throws FileNotFoundException {
		FileInputStream fileIn = new FileInputStream(file);
		ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(
				null, display, fileIn);
		ReaderMonitor retValue = new ReaderMonitor();

		retValue.pm = progressIn.getProgressMonitor();
		retValue.reader = new BufferedReader(new InputStreamReader(progressIn));
		return retValue;
	}

	CSMicroarraySet parseCSMicroarraySet(File file2,
			String compatibilityLabel) {
		CSMicroarraySet m = new CSMicroarraySet();
		m.setCompatibilityLabel(compatibilityLabel);
		return parseCSMicroarraySet(file2, m);
	}

}