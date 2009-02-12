package org.geworkbench.builtin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.management.ComponentResource;
import org.xml.sax.SAXException;

/**
 * Manages the dynamic loading and removal of components.
 * 
 * @author keshav
 * @author tg2321
 * @version $Id: ComponentConfigurationManager.java,v 1.1 2009/02/09 19:54:43
 *          keshav Exp $
 */
public class ComponentConfigurationManager {

	private Log log = LogFactory.getLog(this.getClass());
	private String[] files = null;
	private Digester digester = null;

	/**
	 * 
	 * @param componentsDir
	 */
	public ComponentConfigurationManager(String componentsDir) {
		File dir = new File(componentsDir);
		if (!dir.isDirectory()) {
			log.warn("Supplied components directory is not a directory: "
					+ componentsDir);
			return;
		}
		files = dir.list();
		digester = UILauncher.uiLauncher;// new Digester(new
		// org.apache.xerces.parsers.SAXParser());
	}

	/**
	 * 
	 * @param resource
	 */
	private ComponentResource initializeComponentResource(String resource) {

		if (StringUtils.isEmpty(resource)) {
			log.error("Input resource is null.  Returning ...");
		}
		List<String> list = Arrays.asList(files);

		ComponentResource componentResource = null;
		if (list.contains(resource)) {
			int index = list.indexOf(resource);
			File file = new File(list.get(index));
			try {
				String path = System.getProperty("components.dir")
						+ System.getProperty("file.separator") + file.getPath();
				componentResource = new ComponentResource(path, false);
				log.debug("Created component resource " + file.getName());
			} catch (IOException e) {
				log.error("Could not initialize component resource '"
						+ file.getName() + "'.", e);
			}
			return componentResource;
		}
		log.error("Resource does not exist for " + resource
				+ ".  Returning null.");
		return null;
	}

	/**
	 * Creates a {@link ComponentResource}.
	 * 
	 * @return
	 */
	public ComponentResource createComponentResource(String resource) {
		log.info("Create component resource " + resource);

		return initializeComponentResource(resource);

	}
	
	/**
	 * Parse the component descriptor (cwb.xml).
	 * 
	 * @param componentResource
	 * @throws SAXException
	 * @throws IOException
	 */
	public Object parseComponentDescriptor(ComponentResource componentResource) {
		// TODO change the xml rules so we are using cwb.xml tags.
		digester.addRule("geaw-config/plugin", new PluginRule(
				"org.geworkbench.engine.config.rules.PluginObject"));

		String dir = componentResource.getDir();
		File f = new File(dir);

		Object parsedObject = null;
		try {
			parsedObject = digester.parse(f);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(
					"Error parsing component descriptor for component resource "
							+ componentResource.getName());
		}

		return parsedObject;
	}

	/**
	 * Loads a component.
	 * 
	 * @param resource
	 */
	public void loadComponent(String resource) {

		/* create component resource */
		ComponentResource componentResource = createComponentResource(resource);

		/* add resource to registry */
		Map<String, ComponentResource> resourceMap = ComponentRegistry
				.getRegistry().getComponentResourceMap();
		resourceMap.put(resource, componentResource);

		/* get input stream for ccm.xml */
		String path = "/" + resource + ".ccm.xml";
		InputStream is = ComponentConfigurationManager.class
				.getResourceAsStream(path);

		/* parse using digester */
		try {
			digester.parse(is);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}

		// refreshGui
	}
}
