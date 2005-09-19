package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * CasDataPlug is a rudimentary way of holding datastructures
 *
 * @author Behrooz Badii - badiib@gmail.com
 */

class CasDataPlug extends CasDataType {
    public CasDataPlug() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String type;
    CasDataTypeImport CDTI;
    Object var;

    CasDataPlug(String t) {
        name = null;
        type = t;
        var = null;
    }
    CasDataPlug(String n, String t, CasDataTypeImport C) {
        CDTI = C;
        name = n;
        type = t;
        var = null;
        //fix this, you should be sending in CDTI into the constructor and working with it here.
        try {
            var = Class.forName(type);
            Package p = var.getClass().getPackage();
            System.out.println(p.getName());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new CasException("Class " + type + " not found for datatype " + name);
        }
    }

    public String typename() {
        return "datatype with type " + type;
    }

    public String getType() {
        return type;
    }

    public Object getVar() {
        return var;
    }

    public void setVar(Object a) {
        var = a;
    }
    public CasDataType copy() {
        return new CasDataPlug(name, type, CDTI);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
    }

    private void jbInit() throws Exception {
    }

}
