package org.geworkbench.engine.properties;

import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.management.ComponentClassLoader;
import org.geworkbench.engine.management.ComponentResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author John Watkinson
 */
public class PropertiesManager {

    public static final String CONF_DIRECTORY = "conf";
    public static final String GLOBAL_PROPERTIES = "configuration.xml";

    private static PropertiesManager instance = null;
    private String componentsDir;

    public static PropertiesManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new PropertiesManager();
        }
    }

    private PropertiesManager() {
        componentsDir = UILauncher.getComponentsDirectory();
    }

    private File getPropertiesPath(Class component) {
        if (component == null) {
            return new File(CONF_DIRECTORY, GLOBAL_PROPERTIES);
        } else {
            ClassLoader classLoader = component.getClassLoader();
            if (classLoader instanceof ComponentClassLoader) {
                ComponentClassLoader ccl = (ComponentClassLoader) classLoader;
                ComponentResource componentResource = ccl.getComponentResource();
                File parentDir = new File(componentResource.getDir(), CONF_DIRECTORY);
                return new File(parentDir, componentResource.getName() + ".xml");
            } else {
                return new File(CONF_DIRECTORY, GLOBAL_PROPERTIES);
            }
        }
    }

    private Properties getProperties(Class component) throws IOException {
        File confFile = getPropertiesPath(component);
        File dir = confFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (confFile.exists()) {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(confFile);
            try {
                props.loadFromXML(in);
            } finally {
                in.close();
            }
            return props;
        } else {
            return new Properties();
        }
    }

    private void saveProperties(Class component, Properties props) throws IOException {
        File confFile = getPropertiesPath(component);
        FileOutputStream out = new FileOutputStream(confFile);
        try {
            props.storeToXML(out, "Auto-generated by geWorkbench.");
        } finally {
            out.close();
        }
    }

    public void setProperty(Class component, String key, String value) throws IOException {
        Properties props = getProperties(component);
        props.setProperty(key, value);
        saveProperties(component, props);
    }

    public String getProperty(Class component, String key, String defaultValue) throws IOException {
        Properties props = getProperties(component);
        return props.getProperty(key, defaultValue);
    }
}
