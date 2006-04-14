/*
 * The geworkbench project
 * 
 * Copyright (c) 2006 Columbia University
 * 
 */
package org.geworkbench.components.normalization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.BaseTestCase;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.model.analysis.AlgorithmExecutionResults;

/**
 * 
 * @author keshav
 * 
 */
public class MarkerCenteringNormalizerTest extends BaseTestCase {

	Log log = LogFactory.getLog(getClass());

	MarkerCenteringNormalizer mcn = null;
	DSMicroarraySet microarraySet = null;
	AlgorithmExecutionResults results = null;

	/**
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		mcn = new MarkerCenteringNormalizer();
		microarraySet = new CSMicroarraySet();

	}

	/**
	 * @throws Exception
	 */
	@Override
	protected void tearDown() throws Exception {
		mcn = null;
		microarraySet = null;
	}

	/**
	 * @throws Exception
	 */
	public void testExecute() throws Exception {

		log.debug("testing execute");

		results = mcn.execute(microarraySet);

		assertNotNull(results);
		assertNotNull(results.getResults());
		assert (results.isExecutionSuccessful());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testExecuteWithNullInput() throws Exception {

		log.debug("testing execute with null input");

		results = mcn.execute(null);
		assertNull(results.getResults());
	}

	/**
	 * Pass on failure
	 * 
	 * @throws Exception
	 */
	public void testExecuteWithInvalidInput() {

		log.debug("testing execute with invalid input");

		boolean fail = true;

		try {
			results = mcn.execute("invalid input object");
		} catch (Exception e) {
			log.debug("Pass on failure.  Error is: ");
			e.printStackTrace();
			fail = false;
			assertNotNull(e);
		}

		if (fail)
			fail("This method should only fail if the method can successfully handle invalid input."
					+ "At that time, remove me.");
	}
}
