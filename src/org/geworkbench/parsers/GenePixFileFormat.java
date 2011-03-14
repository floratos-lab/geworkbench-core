package org.geworkbench.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ProgressMonitorInputStream;
import javax.swing.filechooser.FileFilter;

import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.GenePixParser;
import org.geworkbench.bison.parsers.resources.GenepixResource;
import org.geworkbench.bison.parsers.resources.Resource;

/**
 * Handles the parsing of Affymetrix .txt files (MAS 5.0). Translates GenePix
 * (gpr) into <code>MicroarraySet</code> objects.
 * 
 * @author manjunath
 * @author zji
 * @version $Id$
 */
public class GenePixFileFormat extends DataSetFileFormat {
	/**
	 * The file extensions expected for Genepix files.
	 */
	private String[] genepixExtensions = { "gpr" };
	private GenepixResource resource = new GenepixResource();
	/**
	 * <code>FileFilter</code> for gating Genepix files, based on their
	 * extension.
	 */
	private GenePixFileFilter genepixFileFilter = null;

	/**
	 * Default constructor. Will be invoked by the framework when the
	 * <code>&lt;plugin&gt;</code> line for this format is encountered in the
	 * application configuration file.
	 */
	public GenePixFileFormat() {
		formatName = "GenePix .GPR"; // Setup the display name for the
		// format.
		genepixFileFilter = new GenePixFileFilter();
	}

	/**
	 * 
	 */
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
	 * @return The file extensions defined for Genepix data files.
	 */
	public String[] getFileExtensions() {
		return genepixExtensions;
	}

	// FIXME
	// In here we should check (among other things) that:
	// * There are no duplicate markers (ie., no 2 markers have the same name).
	// * The values of the data points respect their expected type.
	public boolean checkFormat(File f) {
		return true;
	}

	/**
	 * Return the <code>FileFilter</code> defined for Genepix files.
	 * 
	 * @return
	 */
	public FileFilter getFileFilter() {
		return genepixFileFilter;
	}

	/**
	 * @param file
	 *            Input data file, expected to be in the Genepix format.
	 * @return A <code>MicroarraySet</code> containing the data in the input
	 *         file.
	 * @throws org.geworkbench.parsers.InputFileFormatException
	 *             When the input file deviates from the Genepix format.
	 */
	private DSDataSet<DSMicroarray> getMArraySet(File file)
			throws org.geworkbench.parsers.InputFileFormatException, InterruptedIOException {
		// Check that the file is OK before starting allocating space for it.
		if (!checkFormat(file))
			throw new org.geworkbench.parsers.InputFileFormatException(
					"GenepixFileFormat::getMArraySet - "
							+ "Attempting to open a file that does not comply with the "
							+ "Genepix format.");
		
		ProgressMonitorInputStream progressIn = null;
		
		DSMicroarraySet<DSMicroarray> microarraySet = null;
		try {
			microarraySet = new CSExprMicroarraySet();
			microarraySet.setFile(file);
			List<String> ctu = new ArrayList<String>();
			ctu.add("Block");
			ctu.add("Column");
			ctu.add("Row");
			ctu.add("Y");
			ctu.add("Dia");
			ctu.add("F635 Median");
			ctu.add("F635 Mean");
			ctu.add("B635 Median");
			ctu.add("B635 Mean");
			ctu.add("F532 Median");
			ctu.add("F532 Mean");
			ctu.add("B532 Median");
			ctu.add("B532 Mean");
			ctu.add("Ratio of Means");
			ctu.add("Flags");
			GenePixParser parser = new GenePixParser(ctu);

			FileInputStream fileIn = new FileInputStream(file);
			progressIn = new ProgressMonitorInputStream(
					null, "Processing File", fileIn);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					progressIn));

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					parser.process(line);
				}
			}

			Vector<String[]> v = parser.getAccessions();
			microarraySet.setLabel(file.getName());
			microarraySet.setCompatibilityLabel("Genepix");
			microarraySet.initialize(1, v.size());
			CSMarkerVector markerVector = (CSMarkerVector) microarraySet
					.getMarkers();
			int count = 0;
			for (String[] acc : v) {
				markerVector.setLabel(count, acc[0]);
				markerVector.get(count).setDisPlayType(
						DSGeneMarker.GENEPIX_TYPE);
				markerVector.get(count++).setDescription(acc[1]);
			}
			reader.close();
			fileIn = new FileInputStream(file);
			progressIn = new ProgressMonitorInputStream(null, "Reading Data",
					fileIn);
			reader = new BufferedReader(new InputStreamReader(progressIn));

			CSMicroarray microarray = new CSMicroarray(0, v.size(), file
					.getName(), null, null, true,
					DSMicroarraySet.genepixGPRType);
			parser.reset();
			parser.setMicroarray(microarray);
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					parser.parseLine(line);
				}
			}
			reader.close();
			microarraySet.add(0, parser.getMicroarray());
		} catch (java.io.InterruptedIOException ie) {
			if ( progressIn.getProgressMonitor().isCanceled())
			{			    
				throw ie;				 
			}			 
			else
			   ie.printStackTrace();
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return microarraySet;
	}

	/**
	 * getDataFile
	 * 
	 * @param file
	 *            File
	 * @return DataSet
	 */
	public DSDataSet<DSMicroarray> getDataFile(File file) throws InterruptedIOException {
		DSDataSet<DSMicroarray> ds = null;
		try {
			ds = getMArraySet(file);
		} catch (InputFileFormatException ife) {
			ife.printStackTrace();
		}
		return ds;
	}

	/**
	 * getDataFile
	 * 
	 * @param files
	 *            File[]
	 * @return DataSet
	 */
	public DSDataSet<DSMicroarray> getDataFile(File[] files) {
		return null;
	}

	/**
	 * Defines a <code>FileFilter</code> to be used when the user is prompted
	 * to select Affymetrix input files. The filter will only display files
	 * whose extension belongs to the list of file extension defined in {@link
	 * #affyExtensions}.
	 */
	class GenePixFileFilter extends FileFilter {
		public String getDescription() {
			return getFormatName();
		}

		public boolean accept(File f) {
			boolean returnVal = false;
			for (int i = 0; i < genepixExtensions.length; ++i)
				if (f.isDirectory()
						|| f.getName().toLowerCase().endsWith(genepixExtensions[i])) {
					return true;
				}
			return returnVal;
		}
	}
}
