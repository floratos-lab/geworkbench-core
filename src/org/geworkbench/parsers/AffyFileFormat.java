package org.geworkbench.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ProgressMonitorInputStream;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.parsers.AffymetrixParser;
import org.geworkbench.bison.parsers.resources.AffyResource;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.util.AffyAnnotationUtil;

/**
 * <p>
 * Copyright: Copyright (c) 2003 -2004
 * </p>
 * <p>
 * Company: Columbia University
 * </p>
 * 
 * Handles the parsing of Affymetrix .txt files (MAS 5.0). Translates Affymetrix
 * formatted files (MAS 4.0/5.0) into <code>MicroarraySet</code> objects.
 * 
 * @author manjunath at genomecenter dot columbia dot edu
 * @author yc2480
 * @version $Id$
 * 
 */
public class AffyFileFormat extends DataSetFileFormat {
	private static Log log = LogFactory.getLog(AffyFileFormat.class);

	/**
	 * The file extensions expected for Affy files.
	 */
	private String[] affyExtensions = { "txt" };
	private AffyResource resource = new AffyResource();
	private DSMicroarraySet microarraySet = null;
	/**
	 * <code>FileFilter</code> for gating Affy files, based on their extension.
	 */
	private AffymetrixFileFilter affyFileFilter = null;

	/**
	 * Default constructor. Will be invoked by the framework when the
	 * <code>&lt;plugin&gt;</code> line for this format is encountered in the
	 * application configuration file.
	 */
	public AffyFileFormat() {
		formatName = "Affymetrix MAS5/GCOS files"; // Setup the display name for
													// the format.
		affyFileFilter = new AffymetrixFileFilter();
	}

	public Resource getResource(File file) {
		try {
			resource.setReader(new BufferedReader(new FileReader(file)));
			resource.setInputFile(file);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		return resource;
	}

	/**
	 * @return The file extensions defined for Affy data files.
	 */
	public String[] getFileExtensions() {
		return affyExtensions;
	}

	/**
	 * In this method, we check that: There is a header line exist (keyword
	 * "Probe Set Name" exist) number of columns in header line is same as
	 * number of columns in data. There are no duplicate markers (ie., no 2
	 * markers have the same name).
	 */
	// FIXME
	// In here we should also check (among other things) that:
	// * The values of the data points respect their expected type.
	public boolean checkFormat(File f) {

		boolean headerExist = false;

		boolean noDuplicateMarkers = true;

		BufferedReader reader = null;
		try {
			FileInputStream fileIn = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(fileIn));

			String line = null;
			int totalColumns = 0;
			int accessionIndex = -1;
			Set<String> markers = new HashSet<String>();
			int lineIndex = 0;
			while ((line = reader.readLine()) != null) { // for each line
				if (line.indexOf("Probe Set Name") >= 0) {
					headerExist = true;
				}
				if (headerExist) {// we'll skip anything before header
					String token = null;
					int columnIndex = 0;
					StringTokenizer st = new StringTokenizer(line, "\t\n");
					while (st.hasMoreTokens()) { // for each column
						token = st.nextToken().trim();
						if (token.equals("Probe Set Name")) {
							accessionIndex = columnIndex;
						} else if (headerExist
								&& (columnIndex == accessionIndex)) {
							/*
							 * if this line is after header, then first column
							 * should be our marker name
							 */
							if (markers.contains(token)) {// duplicate markers
								noDuplicateMarkers = false;
								log.info("duplicate markers: " + token);
							} else {
								markers.add(token);
							}
						}
						columnIndex++;
					}
					// check if column match or not
					if (headerExist) { // if this line is real data, we assume
										// lines after header are real data. (we
										// might have bug here)
						if (totalColumns == 0)// not been set yet
							totalColumns = columnIndex;
						else if (columnIndex != totalColumns) {// if not equal
							log.debug("In the file" + f.getName()
									+ ", header contains " + totalColumns
									+ " columns, but line " + lineIndex
									+ " only contains " + columnIndex
									+ " columns.");
						}
					}
				}
				lineIndex++;
			}
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return (headerExist && noDuplicateMarkers);
	}

	/**
	 * Return the <code>FileFilter</code> defined for Affy files.
	 * 
	 * @return
	 */
	public FileFilter getFileFilter() {
		return affyFileFilter;
	}

	/**
	 * @param file
	 *            Input data file, expected to be in the Affy format.
	 * @return A <code>MicroarraySet</code> containing the data in the input
	 *         file.
	 * @throws InputFileFormatException
	 *             When the input file deviates from the Affy format.
	 */
	private void getMArraySet(File file, CSMicroarraySet maSet)
			throws InputFileFormatException, InterruptedIOException {
		// Check that the file is OK before starting allocating space for it.
		if (!checkFormat(file))
			throw new InputFileFormatException(
					"Attempting to open a file that does not comply with the "
							+ "Affy format.");
		log.info(file.getAbsoluteFile() + " passed file format check");
		BufferedReader reader = null;
		ProgressMonitorInputStream progressIn = null;
		try {
			microarraySet = maSet;
			List<String> ctu = new ArrayList<String>();
			ctu.add("Probe Set Name");
			ctu.add("Avg Diff");
			ctu.add("Signal");
			ctu.add("Signal Log Ratio");
			ctu.add("Detection");
			ctu.add("Detection p-value");
			ctu.add("Abs Call");
			AffymetrixParser parser = new AffymetrixParser(ctu);
			FileInputStream fileIn = new FileInputStream(file);
			progressIn = new ProgressMonitorInputStream(null, "Scanning File",
					fileIn);
			reader = new BufferedReader(new InputStreamReader(progressIn));

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					parser.process(line);
				}
			}

			Vector<String> v = parser.getAccessions();
			microarraySet.setLabel(file.getName());
			if (microarraySet.getCompatibilityLabel() == null
					|| microarraySet.getCompatibilityLabel().equals("")) {
				microarraySet.setCompatibilityLabel(AffyAnnotationUtil.matchAffyAnnotationFile(maSet));
			}

			microarraySet.initializeMarkerVector(v.size());

			CSMarkerVector markerVector = (CSMarkerVector) microarraySet
					.getMarkers();
			int count = 0;
			for (String acc : v) {
				markerVector.setLabel(count, acc);
				String[] geneNames = AnnotationParser.getInfo(acc,
						AnnotationParser.ABREV);
				if (geneNames != null) {
					markerVector.get(count).setGeneName(geneNames[0]);
				}
				markerVector.get(count++).setDescription(acc);
			}
			reader.close();

			microarraySet.sortMarkers(v.size());

			// Read again, this time loading data
			fileIn = new FileInputStream(file);
			progressIn = new ProgressMonitorInputStream(null, "Loading Data",
					fileIn);
			reader = new BufferedReader(new InputStreamReader(progressIn));
			CSMicroarray microarray = new CSMicroarray(0, v.size(),
					file.getName(),
					DSMicroarraySet.affyTxtType);
			microarray.setLabel(file.getName());
			parser.reset();
			parser.setMicroarray(microarray);
			parser.setNewMarkerOrder(microarraySet.getNewMarkerOrder());
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					parser.parseLine(line);
				}
			}
			reader.close();
			parser.getMicroarray().setLabel(file.getName());
			microarraySet.add(0, parser.getMicroarray());
			microarraySet.setFile(file);			 
		} catch (java.io.InterruptedIOException ie) {
			if (progressIn.getProgressMonitor().isCanceled()) {
				throw ie;
			} else
				ie.printStackTrace();

		} catch (Exception ec) {
			log.error(ec, ec);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * getDataFile
	 * 
	 * @param file
	 *            File
	 * @return DataSet
	 */
	public DSDataSet<? extends DSBioObject> getDataFile(File file)
			throws InputFileFormatException, InterruptedIOException {
		return getMArraySet(file);
	}

	public DSMicroarraySet getMArraySet(File file)
			throws InputFileFormatException, InterruptedIOException {
		CSMicroarraySet maSet = new CSMicroarraySet();
		getMArraySet(file, maSet);
		maSet.getMarkers().correctMaps();
		return maSet;
	}

	public DSDataSet<? extends DSBioObject> getDataFile(File file,
			String compatibilityLabel) throws InputFileFormatException,
			InterruptedIOException {
		CSMicroarraySet maSet = new CSMicroarraySet();
		maSet.setCompatibilityLabel(compatibilityLabel);
		getMArraySet(file, maSet);
		return maSet;
	}

	/**
	 * getDataFile
	 * 
	 * @param files
	 *            File[]
	 * @return DataSet
	 */
	// TODO probably this should be removed from superclass DataSetFileFormat
	public DSDataSet<? extends DSBioObject> getDataFile(File[] files) {
		return null;
	}

	/**
	 * Defines a <code>FileFilter</code> to be used when the user is prompted to
	 * select Affymetrix input files. The filter will only display files whose
	 * extension belongs to the list of file extension defined in
	 * {@link #affyExtensions}.
	 */
	class AffymetrixFileFilter extends FileFilter {
		public String getDescription() {
			return getFormatName();
		}

		public boolean accept(File f) {
			boolean returnVal = false;
			for (int i = 0; i < affyExtensions.length; ++i)
				if (f.isDirectory()
						|| f.getName().toLowerCase()
								.endsWith(affyExtensions[i])) {
					return true;
				}
			return returnVal;
		}
	}
}
