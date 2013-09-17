package org.geworkbench.engine.config;

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version $Id$
 */

/**
 * Registry keeping stock of (1) all the plugins defined for an application, and
 * (2) the association of plugins with extension points.
 */
public class PluginRegistry {
	static private Log log = LogFactory.getLog(PluginRegistry.class);
    // ---------------------------------------------------------------------------
    // --------------- Instance and static variables
    // ---------------------------------------------------------------------------
    /**
     * Stores the application plugins and their mappings to extension points.
     * Every extension point is a key in this HashMap. Its corresponding value
     * is a Vector, containing the <code>PluginDescriptor</code>s for the
     * plugins associated with the extension point.
     */
	// TODO extensionPointsMap should be removed. It is not implemented to do anything.
    private static HashMap<String, Vector<PluginDescriptor>> extensionPointsMap = new HashMap<String, Vector<PluginDescriptor>>();
    /**
     * stores the info about visual area and visual plugin info.
     */
    private static HashMap<VisualPlugin, String> visualAreaMap = new HashMap<VisualPlugin, String>();
    /**
     * stores the info about plugin, so the plugin can query it's name even in it's constructor.
     */
    private static HashMap<String,String> nameMap = new HashMap<String,String>();
    /**
     * Stores all application plugins.
     */
    private static Vector<PluginDescriptor> componentVector = new Vector<PluginDescriptor>(100);
    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Adds a plugin component descriptor at a named extension point.
     *
     * @param compDes  The <code>PluginDescriptor</code> object corresponding
     *                 to the plugin.
     * @param extPoint The name of the extension point where the plugin will be
     *                 added.
     */
    public static void addPlugInAtExtension(PluginDescriptor compDes, String extPoint) {
        if (!extensionPointsMap.containsKey(extPoint)) {
            extensionPointsMap.put(extPoint, new Vector<PluginDescriptor>());
        }

        Vector<PluginDescriptor> extPointVector = extensionPointsMap.get(extPoint);
        if (!extPointVector.contains(compDes)) {
            extPointVector.add(compDes);
        }

    }

    /**
     * Registers the designated plugin.
     *
     * @param compDes Plugin <code>PluginDescriptor</code>.
     */
    public static void addPlugin(PluginDescriptor compDes) {
        if (!componentVector.contains(compDes)) {
            componentVector.add(compDes);
        }

    }

    /**
     * @param id The id of a plugin.
     * @return The plugin from the <code>PluginRegistry</code> that has the designated
     *         ID. Otherwise, if such a plugin does not exist, null.
     */
    public static PluginDescriptor getPluginDescriptor(String id) {
        int size = componentVector.size();
        int i;
        for (i = 0; i < size; ++i) {
            if (((PluginDescriptor) componentVector.get(i)).getID().compareTo(id) == 0) {
                return (PluginDescriptor) componentVector.get(i);
            }

        }

        return null;
    }

    public static void setNameMap(String className, String name){
    	nameMap.remove(className);
    	nameMap.put(className, name);
    }
    
    public static String getNameMap(String className){
    	return nameMap.get(className);
    }

    public static void addVisualAreaInfo(String visualAreaName, VisualPlugin comp) {
        visualAreaMap.put(comp, visualAreaName);
        log.debug("add visual area "+visualAreaName);
    }

	// FIXME: this is needed by CCM to remove plugin. It should be implemented as needed,
	// not to expose this member directly.
	// AND the usage in ComponentConfigurationManager is wrong - messed up PluginDescriptor and VisualPlugin
	@SuppressWarnings("rawtypes")
	public static HashMap getVisualAreaMap(){
		return visualAreaMap;
	}
	
	// TODO: this is needed by CCM to remove plugin or get a particular plugin. It should be implemented as needed,
	// not to expose this member directly.
	public static Vector<PluginDescriptor> getComponentVector(){
		return componentVector;
	}

	// TODO: this should be invoked from PluginDescriptor. no reason to go through this class.
    public static Vector<String> getUsedIds(){
    	return PluginDescriptor.getUsedIds();
    }

}
