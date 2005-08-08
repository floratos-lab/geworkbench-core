package org.geworkbench.engine.cascript;

import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.PluginRegistry;

import java.io.PrintWriter;

/**
 * the wrapper class for Module
 *
 * @author Behrooz Badii - badiib@gmail.com
 */
class CasModule extends CasDataType {
    public CasModule() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    PluginDescriptor pd;
    String type;
    Object var;

    CasModule(String n, String t) {
        name = n;
        type = t;
        pd = PluginRegistry.getPluginDescriptor(t);
        var = pd.getPlugin();
    }

    public String typename() {
        return "module with type " + type;
    }

    public Object getPlugin() {
        return pd.getPlugin();
    }

    public CasDataType copy() {
        return new CasModule(name, type);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
    }

    private void jbInit() throws Exception {
    }

}
