package org.geworkbench.analysis;

/**
 * @author keshav
 * @author ch2514
 * @version $Id: AbstractAnalysisGuiTest.java,v 1.2 2009-02-12 22:28:15 keshav Exp $
 * 
 */
public class AbstractAnalysisGuiTest extends BaseAbstractAnalysisTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.analysis.AbstractAnalysisTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Creating a gui to test saving/loading parameters api.
	 */
	public void testParametersWithGui() {
		((TestAnalysis) analysis).createGUI();
	}
}
