package org.geworkbench.builtin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.management.ComponentResource;

/**
 * Manages the dynamic loading and removal of components.
 * 
 * @author keshav
 * @version $Id: ComponentConfigurationManager.java,v 1.1 2009/02/09 19:54:43
 *          keshav Exp $
 */
public class ComponentConfigurationManager {

	private Log log = LogFactory.getLog(this.getClass());
	private String[] files = null;

	/**
	 * 
	 * @param componentsDir
	 */
	public ComponentConfigurationManager(String componentsDir) {
		File dir = new File(componentsDir);
		if (!dir.isDirectory()) {
			log.warn("Component resource path is not a directory: "
					+ componentsDir);
		}
		files = dir.list();
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
				componentResource = new ComponentResource(file.getPath(), false);
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

		return initializeComponentResource(resource);

	}

	/**
	 * Parse the component descriptor (cwb.xml).
	 * 
	 * @param componentResource
	 */
	public void parseComponentDescriptor(ComponentResource componentResource) {
		// TODO implement me (see how this is currently done in PluginObject's
		// processComponentDescriptor method
		throw new UnsupportedOperationException("Method not yet implemented!");
	}

	/**
	 * Loads a component.
	 * 
	 * @param resource
	 */
	public void loadComponent(String resource) {

		ComponentResource componentResource = createComponentResource(resource);

		parseComponentDescriptor(componentResource);

		// refreshGui

	}

}
