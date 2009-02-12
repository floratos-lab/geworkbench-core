package org.geworkbench.analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tests the {@link AbstractAnalysis}.
 * 
 * @author keshav
 * @author ch2514
 * @version $Id: AbstractAnalysisTest.java,v 1.1.2.1 2009/01/06 15:51:17 keshav
 *          Exp $
 */
public class AbstractAnalysisTest extends BaseAbstractAnalysisTest {
	private Log log = LogFactory.getLog(this.getClass());

	private boolean skipTest = false;

	protected void setUp() throws Exception {
		String property = System.getProperty("temporary.files.directory");
		if (StringUtils.isEmpty(property)) {
			skipTest = true;
			return;
		}
		super.setUp();
	}

	/*
	 * The following test methods would be the equivalent of what you would find
	 * in the "calling" code, like the AnalysisPanel.
	 */

	/**
	 * Calling code to save parameters. This would be done in the AnalysisPanel,
	 * save_actionPerformed().
	 */
	public void testSaveParameters() {
		if (skipTest) {
			log
					.info("Skipping test. VM startup property, temporary.files.directory, not found");
		}
		Map<Serializable, Serializable> params = analysis.getParameters();
		log.info("Set the following parameters: " + params.toString());
		analysis.saveParameters(TEST_PARAMS_XML);
		log.info("Saved parameters in file [ "
				+ TEST_PARAMS_XML + " ]");
	}

	/**
	 * Calling code to load saved parameters. This would be done in the
	 * following places: 1) AnalysisPanel, namedParameterSelection_action() 2)
	 * AnalysisPanel, loadNamedParameter()
	 */
	public void testLoadParameters() {
		Map<Serializable, Serializable> params = analysis
				.getParameters();
		log.info("parameters:\n" + params.toString());
		StringBuilder sb = new StringBuilder("Parameter details:\n");
		for (Entry<Serializable, Serializable> entry : params.entrySet()) {
			sb.append(entry.getKey());
			sb.append("\t");
			sb.append(entry.getValue());
			sb.append("\t");			
		}
		log.info(sb.toString());
	}

	// /**
	// * Calling code to delete saved parameters. This would be done in
	// * AnalysisPanel, delete_actionPerformed().
	// */
	// public void testDeleteParameters() {
	// analysis.setParameterSettingID(PARAMS_SETTING_ID);
	// analysis.deleteParameters(TEST_PARAMS_XML);
	// log.info("Deleted parameter setting [ " + PARAMS_SETTING_ID
	// + " ] in file [ " + TEST_PARAMS_XML + " ]");
	// }
}
