package org.geworkbench.parsers;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import java.io.InterruptedIOException;

import java.io.File;

/**
 * <p>
 * Title: Sequence and Pattern Plugin
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version $Id$
 */

public abstract class DataSetFileFormat extends FileFormat {
	public DataSetFileFormat() {
	}

	abstract public DSDataSet<? extends DSBioObject> getDataFile(File file)
			throws InputFileFormatException, InterruptedIOException;

	public DSDataSet<? extends DSBioObject> getDataFile(File file,
			String compatibilityLabel) throws InputFileFormatException,
			InterruptedIOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	abstract public DSDataSet<? extends DSBioObject> getDataFile(File[] files)
			throws InputFileFormatException;

	/**
	 * Generates and returns a <code>MicrorarraySet</code> from the designated
	 * argument. In <code>file</code> does not conform to the format, returns
	 * <code>null</code>.
	 * 
	 * @param file
	 *            The file containing the input data.
	 * @return The corresponding <code>MicroarraySet</code> object.
	 */
	public abstract DSDataSet<? extends DSBioObject> getMArraySet(File file)
			throws InputFileFormatException, InterruptedIOException;

}
