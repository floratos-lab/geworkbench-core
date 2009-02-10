package org.geworkbench.engine.test;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.geworkbench.builtin.ComponentConfigurationManager;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.management.ComponentResource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: matt
 * Date: Oct 10, 2005
 * Time: 3:14:22 PM
 */
public class ComponentClassloaderTest extends TestCase {

    public void testComponentResource() throws IOException {
        ComponentResource cr = new ComponentResource("gears", false);
        ClassLoader loader = cr.getClassLoader();
    }
        
    public void testLoadCmmXmlFile() throws ClassNotFoundException, IOException, SAXException  {
    	Digester emptyDigester = new Digester(new org.apache.xerces.parsers.SAXParser());
    	
    	String componentDir = System.getProperty(UILauncher.COMPONENTS_DIR_PROPERTY);
    	
        if (componentDir == null) {
            componentDir = UILauncher.DEFAULT_COMPONENTS_DIR;
        }

        componentDir += "\\HierClusterViewAppComponent\\";
    	String ccmFileName = "HierClusterViewAppComponent.ccm.xml";
		
    	InputStream inputStream = null;

    	Class localClass  = Class.forName("org.geworkbench.engine.test.ComponentClassloaderTest");
    	inputStream = localClass.getResourceAsStream("/" + ccmFileName);
		
		emptyDigester.parse(inputStream);
    }	
    
    public void testDigesterParse(){

    	Digester digester = new Digester(new org.apache.xerces.parsers.SAXParser());
    	
    	digester.setUseContextClassLoader(true);
        
        // Instantiates a plugin and adds it in the PluginResgistry
    	digester.addRule("plugin", new PluginRule("org.geworkbench.engine.config.rules.PluginObject"));
        
    	digester.addCallMethod("plugin/gui-area", "addGUIComponent", 1);
    	digester.addCallParam("plugin/gui-area", 0, "name");

//    	digester.parse(inputStream);
    }
    
    public void testParseCmmXmlFile() throws IOException {
    	String componentsDir = "c:\\unittest\\";
    	
    	ComponentConfigurationManager ccm = new ComponentConfigurationManager(componentsDir); 

    	ComponentResource componentResource = new ComponentResource("gears", false); 
    	ccm.parseComponentDescriptor(componentResource);
    }	
    
    
}
