package org.geworkbench.engine.test;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.geworkbench.engine.ccm.ComponentConfigurationManager;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.management.ComponentResource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: matt Date: Oct 10, 2005 Time: 3:14:22 PM
 */
public class ComponentClassloaderTest extends TestCase {

	public void testComponentResource() throws IOException {
		ComponentResource cr = new ComponentResource("gears", false);
		ClassLoader loader = cr.getClassLoader();
		assertNotNull(loader);
	}
}
