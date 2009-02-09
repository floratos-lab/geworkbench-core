package org.geworkbench.builtin;

import junit.framework.TestCase;

import org.geworkbench.engine.management.ComponentResource;

/**
 * A test class for the {@link ComponentConfigurationManager}.
 * 
 * @author keshav
 * @version $Id: ComponentConfigurationManagerTest.java,v 1.1 2009-02-09 19:54:43 keshav Exp $
 */
public class ComponentConfigurationManagerTest extends TestCase {

	/**
	 * Tests creating a {@link ComponentResource}.
	 * 
	 * @throws Exception
	 */
	public void testCreateComponentResource() throws Exception {
		String componentsDir = System.getProperty("components.dir");

		assertNotNull(componentsDir);

		ComponentConfigurationManager ccm = new ComponentConfigurationManager(
				componentsDir);
		String resource = "hierarchicalclustering";
		ComponentResource componentResource = ccm
				.createComponentResource(resource);

		assertNotNull(componentResource);

	}

	/**
	 * Tests parsing the cwb.xml file.
	 * 
	 * @throws Exception
	 */
	public void testParseComponentDescriptor() throws Exception {

	}

	/**
	 * Tests loading a component.
	 * 
	 * @throws Exception
	 */
	public void testLoadComponent() throws Exception {

	}

}
