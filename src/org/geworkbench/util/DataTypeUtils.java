package org.geworkbench.util;

import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

public class DataTypeUtils {

	// using reflection to implement such functionality should be considered an
	// scenario of exception
	// thus better kept outside the main logic
	/**
	 * Check whether the given data type really implements writeTofFile method.
	 * 
	 * @return true if it support.
	 */
	@SuppressWarnings("rawtypes")
	static public boolean supportWriteToFile(
			Class<? extends DSDataSet> datasetType) {
		try {
			Class<?> declaringClass = datasetType.getMethod("writeToFile",
					String.class).getDeclaringClass();
			if (declaringClass.equals(CSDataSet.class))
				return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			// this should not happen because interface DSDataSet requires this
			// method
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
