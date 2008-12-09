package org.geworkbench.components.parsers;

import junit.framework.TestCase;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;

import java.io.File;

/**
 * Tests loading of microarray sets, and makes loading services available to
 * other tests.
 * 
 * @author John Watkinson
 * @version $Id: DataSetFileFormatTest.java,v 1.1 2008-12-09 22:09:40 keshav Exp $
 */
public class DataSetFileFormatTest extends TestCase {

	public static final String DEFAULT_MICROARRAY_SET = "data/aTestDataSet.exp";

	/**
	 * 
	 * @param fileName
	 * @param fileFormat
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private DSDataSet loadDataSet(String fileName, DataSetFileFormat fileFormat)
			throws Exception {
		System.out.println("Loading file...");
		DSDataSet dataSet = fileFormat.getDataFile(new File(fileName));
		System.out.println("...file loaded.");
		return dataSet;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private CSExprMicroarraySet loadDefaultMicroarraySet() throws Exception {
		String fileName = DEFAULT_MICROARRAY_SET;
		DataSetFileFormat fileFormat = new org.geworkbench.components.parsers.ExpressionFileFormat();
		DSDataSet dataSet = loadDataSet(fileName, fileFormat);
		return (CSExprMicroarraySet) dataSet;
	}

	/**
	 * Tests loading data.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testLoadData() throws Exception {
		DSDataSet dataSet = loadDefaultMicroarraySet();
		assertNotNull(dataSet);
	}
}
