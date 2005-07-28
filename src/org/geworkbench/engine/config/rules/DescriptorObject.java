package org.geworkbench.engine.config.rules;

/**
 * @author John Watkinson
 *         <p/>
 *         Passes through requests to the {@link PluginObject}.
 */
public class DescriptorObject {

    private PluginObject pluginObject;

    public DescriptorObject() {
    }

    public PluginObject getPluginObject() {
        return pluginObject;
    }

    public void setPluginObject(PluginObject pluginObject) {
        this.pluginObject = pluginObject;
    }

    public void registerMenuItem(String path, String mode, String var, String icon, String accelerator) throws NotMenuListenerException, NotVisualPluginException, MalformedMenuItemException {
        pluginObject.registerMenuItem(path, mode, var, icon, accelerator);
    }

    public void registerHelpTopic(String pluginHelpSet) {
        pluginObject.registerHelpTopic(pluginHelpSet);
    }
}