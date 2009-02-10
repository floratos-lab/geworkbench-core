package org.geworkbench.engine.test;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.geworkbench.builtin.ComponentConfigurationManager;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.management.ComponentResource;

import java.io.File;
import java.io.IOException;

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
        
    public void testLoadCmmXmlFile() throws IOException {

    	String componentsDir = "c:\\unittest\\HierClusterViewAppComponent.ccm.xml";
		File dir = new File(componentsDir);
		
		if (!dir.isDirectory()){
			throw new RuntimeException();
		}
    }	
    
    public void testConfigureDigester(){

    	Digester digester = new Digester(new org.apache.xerces.parsers.SAXParser());
    	
    	ComponentConfigurationManager ccm = new ComponentConfigurationManager();
    	ccm.configure (digester);
    }
    
    public void testParseCmmXmlFile() throws IOException {
    	String componentsDir = "c:\\unittest\\";
    	
    	ComponentConfigurationManager ccm = new ComponentConfigurationManager(componentsDir); 

    	ComponentResource componentResource = new ComponentResource("gears", false); 
    	ccm.parseComponentDescriptor(componentResource);
    }	
    
    
}
