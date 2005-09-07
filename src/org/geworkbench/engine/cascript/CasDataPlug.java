package org.geworkbench.engine.cascript;

import java.io.PrintWriter;
import java.net.URLClassLoader;
import org.geworkbench.engine.management.ClassSearcher;

class CasDataPlug extends CasDataType {
    public CasDataPlug() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String type;
    Object var;

    CasDataPlug(String t) {
        name = null;
        type = t;
        var = null;
    }
    CasDataPlug(String n, String t) {
        name = n;
        type = t;
        var = null;
        try {
            var = Class.forName(type);
            Package p = var.getClass().getPackage();
            System.out.println(p.getName());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new CasException("Class " + type + " not found for datatype " + name);
        }
        //URLClassLoader c = ((URLClassLoader)ClassLoader.getSystemClassLoader());
        //ClassSearcher cs = new ClassSearcher(c.getURLs());
        //cs.getAllClassesAssignableTo(type, true);
        //figure out what you gotta do from here on in.
        //find the class that EQUALS this class. make a new instance, give it to var
        //System.out.println(c.toString());

    }

    public String typename() {
        return "datatype with type " + type;
    }

    public String getType() {
        return type;
    }

    //public Object getDataType() {
    //    return pd.getPlugin();
    //}

    public CasDataType copy() {
        return new CasDataPlug(name, type);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
    }

    private void jbInit() throws Exception {
    }

}
