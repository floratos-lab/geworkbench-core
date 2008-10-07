package org.geworkbench.components.parsers;

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
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Handles the parsing of Affymetrix .txt files (MAS 5.0). Translates GenePix
 * (gpr) into <code>MicroarraySet</code> objects.
 * 
 * @author manjunath
 * @author zji
 * @version $Id: GenePixFileFormat.java,v 1.4 2008-10-07 19:07:30 jiz Exp $
 */
public class GenePixFileFormat extends DataSetFileFormat {
	/**
	 * The file extensions expected for Genepix files.
	 */
	private String[] genepixExtensions = { "gpr" };
	private GenepixResource resource = new GenepixResource();
	private DSMicroarraySet<DSMicroarray> microarraySet = null;
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
	 * @throws org.geworkbench.components.parsers.InputFileFormatException
	 *             When the input file deviates from the Genepix format.
	 */
	@SuppressWarnings("unchecked")
	public DSMicroarraySet getMArraySet(File file)
			throws org.geworkbench.components.parsers.InputFileFormatException {
		// Check that the file is OK before starting allocating space for it.
		if (!checkFormat(file))
			throw new org.geworkbench.components.parsers.InputFileFormatException(
					"GenepixFileFormat::getMArraySet - "
							+ "Attempting to open a file that does not comply with the "
							+ "Genepix format.");
		try {
			microarraySet = new CSExprMicroarraySet();
			microarraySet.setFile(file);
			List ctu = new ArrayList();
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
			ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(
					null, "Processing File", fileIn);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					progressIn));

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					parser.process(line);
				}
			}

			Vector v = parser.getAccessions();
			microarraySet.setLabel(file.getName());
			microarraySet.setCompatibilityLabel("Genepix");
			microarraySet.initialize(1, v.size());
			CSMarkerVector markerVector = (CSMarkerVector) microarraySet
					.getMarkers();
			int count = 0;
			for (Iterator it = v.iterator(); it.hasNext();) {
				String[] acc = (String[]) (it.next());
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
	@SuppressWarnings("unchecked")
	public DSDataSet<DSMicroarray> getDataFile(File file) {
		DSDataSet<DSMicroarray> ds = null;
		try {
			ds = (DSDataSet<DSMicroarray>) getMArraySet(file);
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
						|| f.getName().endsWith(genepixExtensions[i])) {
					return true;
				}
			return returnVal;
		}
	}
}
