package org.geworkbench.builtin;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test class for the {@link ComponentConfigurationManager}.
 * 
 * @author keshav
 * @version $Id: ComponentConfigurationManagerTest.java,v 1.1 2009/02/09
 *          19:54:43 keshav Exp $
 */
public class ComponentConfigurationManagerTest extends TestCase {

	private Log log = LogFactory.getLog(this.getClass());
	private ComponentConfigurationManager ccm = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String componentsDir = System.getProperty("components.dir");

		assertNotNull(
				"Component dir not specified.  Set VM arg -Dcomponents.dir",
				componentsDir);

		ccm = new ComponentConfigurationManager();
		
		assertNotNull("Component Configuration Manager not created", ccm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ccm = null;
	}

	// TODO need to test
	/**
	 * Tests loading a component a valid component.
	 * 
	 * @throws Exception
	 */
	public void testLoadComponentSuccess() throws Exception {
		log.debug("Testing loading a valid component... ");

		String validResource = "hierarchicalclustering";
		String ccmFileName = "hierarchicalclustering.ccm.xml";
		boolean successful = ccm.loadComponent(validResource, ccmFileName);
		assertTrue(successful);
	}

	// TODO need to test
	/**
	 * Tests loading an invalid component.
	 * 
	 * @throws Exception
	 */
	public void testLoadComponentFailure() throws Exception {
		log.debug("Testing loading an invalid component ... You should see a "
				+ java.io.FileNotFoundException.class.getName());

		String invalidResource = "yippeekiyay";
		String ccmFileName = "hierarchicalclustering.ccm.xml";
		boolean successful = ccm.loadComponent(invalidResource, ccmFileName);

		assertFalse(successful);
	}

	/**
	 * Tests removing a component.
	 * 
	 * @throws Exception
	 */
	public void testRemoveComponent() throws Exception {
		log.debug("Testing removing a component ... ");

		// TODO implement me
		log.warn("Method not yet implemented!");
	}
}
