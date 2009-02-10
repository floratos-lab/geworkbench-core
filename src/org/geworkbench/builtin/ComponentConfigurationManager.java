package org.geworkbench.builtin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.config.rules.PluginRuleCCM;
import org.geworkbench.engine.management.ComponentResource;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

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

	public ComponentConfigurationManager() {
		super();
	}

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
		digester = new Digester(new org.apache.xerces.parsers.SAXParser());
	}

	/**
	 * 
	 * @param resource
	 */
	private ComponentResource initializeComponentResource(String resourceName) {

		if (StringUtils.isEmpty(resourceName)) {
			log.error("Input resource is null.  Returning ...");
		}
		List<String> list = Arrays.asList(files);

		ComponentResource componentResource = null;
		if (list.contains(resourceName)) {
			int index = list.indexOf(resourceName);
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
		log.error("Resource does not exist for " + resourceName
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
	 * @throws SAXException
	 * @throws IOException
	 */
	public Object parseComponentDescriptor(ComponentResource componentResource) {
		// TODO implement me (see how this is currently done in PluginObject's
		// processComponentDescriptor method

		String componentName = componentResource.getName();
		String componentPath = componentResource.getDir();
		
		String configFileArg = null;
        String configFileName = null;
		
	    Digester ccmDigester = new Digester(new org.apache.xerces.parsers.SAXParser());
		configure(ccmDigester);
		

            InputStream inputStream = null;
			try {
				inputStream = Class.forName("org.geworkbench.engine.config.PluginLoader").getResourceAsStream("/" + configFileName);
	            if (inputStream == null) {
//	                exitOnErrorMessage("Invalid or absent configuration file.");
					throw new RuntimeException(
							"Invalid or absent configuration file. "
									+ componentResource.getName());
	            }
			} catch (ClassNotFoundException e) {
				log.error(e, e);
				throw new RuntimeException(
						"ClassNotFoundException: Error parsing component descriptor for component resource "
								+ componentResource.getName());
			}
            
        Object parsedObject = null;
            
        try {
			parsedObject = ccmDigester.parse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e, e);
			throw new RuntimeException(
					"IOException: Error parsing component descriptor for component resource "
							+ componentResource.getName());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			log.error(e, e);
			throw new RuntimeException(
					"SAXException: Error parsing component descriptor for component resource "
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

		ComponentResource componentResource = createComponentResource(resource);

		parseComponentDescriptor(componentResource);

		// refreshGui

	}

	
    
    /**
     * Configure the rules for translating the application configuration file.
     */
    public Digester configure(Digester ccmDigester) {
        ccmDigester.setUseContextClassLoader(true);
        
        // Instantiates a plugin and adds it in the PluginResgistry
        ccmDigester.addRule("plugin", new PluginRuleCCMSimulator("org.geworkbench.engine.config.rules.PluginObject"));
        
        ccmDigester.addCallMethod("plugin/gui-area", "addGUIComponent", 1);
        ccmDigester.addCallParam("plugin/gui-area", 0, "name");
        
        return ccmDigester;
        
    }

    
    public class PluginRuleCCMSimulator extends ObjectCreateRule {

    	Vector pluginObjects = new Vector(); // but this isn't static??????
    	
		public PluginRuleCCMSimulator(String className) {
			super(className);
		}
    	
	    public void begin(String namespace, String name, Attributes attributes) throws Exception {
	        PluginObject pginObj;
	        super.begin(namespace, name, attributes);
	        pginObj = (PluginObject) super.getDigester().peek();
	        pluginObjects.add(pginObj);
	        // We need to instantiate the plugin descriptor before the various
	        // CallMethod rules are called.
	        pginObj.createCCMPlugin(attributes.getValue("id"), attributes.getValue("name"), attributes.getValue("class"), attributes.getValue("source"));
	    }

	    /**
	     * Overrides the corresponding method from <code>ObjectCreateRule</code>.
	     * Called at the end of parsing, in order to finish up. It invokes the
	     * {@link org.geworkbench.engine.config.rules.PluginObject#finish finish} method of each
	     * <code>PluginObject</code> generated through the parsing of the
	     * application configuration file.
	     *
	     * @throws Exception
	     */
	    public void finish() throws Exception {
	        int size = pluginObjects.size();
	        for (int i = 0; i < size; ++i)
	            ((PluginObject) pluginObjects.get(i)).finish();
	    }

    }

    
    
    public class PluginObjectSimulator{
    	
        /**
         * Creates a new plugin in and adds it to the <code>PluginRegistry</code>.
         * The plugin is instantiated by a call to its default constructor.
         *
         * @param id           Id to be used for the new plugin.
         * @param name         Name to be used for the new plugin.
         * @param className    Class name for the new plugin.
         * @param resourceName Resource from which to load the plugin.
         */
        public void createCcmPlugin(String id, String name, String className, String resourceName) {

        }

    	public ComponentResource initializeComponentResource(String resourceName) {

    		return null;
    	}
        
    	
    }
    
    
    
    private void exitOnErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Plugin Load Error", JOptionPane.ERROR_MESSAGE);
    }

	
}
