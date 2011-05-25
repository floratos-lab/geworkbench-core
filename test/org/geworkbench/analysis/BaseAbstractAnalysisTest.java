package org.geworkbench.analysis;

import junit.framework.TestCase;

/**
 * 
 * @author keshav
 * @version $Id: BaseAbstractAnalysisTest.java,v 1.1.2.1 2009/01/06 15:51:17
 *          keshav Exp $
 */
public class BaseAbstractAnalysisTest extends TestCase {

	protected static final String TEST_PARAMS_XML = "foo.xml";

	protected static final String PARAMS_SETTING_ID = "test";

	protected AbstractAnalysis analysis = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		analysis = new TestAnalysis();
		// try {
		// File f = new File(TEST_PARAMS_XML).getParentFile();
		// if (!f.exists() && !f.mkdirs()) {
		// JOptionPane.showMessageDialog(null,
		// "Please create the following directory to store the xml file: "
		// + f.getCanonicalPath(), "Parameter Error",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// } catch (Exception e) {
		// log
		// .error("Cannot create temp directory for xml: "
		// + e.getMessage());
		// JOptionPane
		// .showMessageDialog(
		// null,
		// "Please create the following subdirectory to store the xml file:
		// ./temp",
		// "Parameter Error", JOptionPane.ERROR_MESSAGE);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		analysis = null;
	}
}
