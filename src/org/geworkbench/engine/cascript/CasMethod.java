package org.geworkbench.engine.cascript;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/* the wrapper class for Module
*
* @author Behrooz Badii - badiib@gmail.com
*/
class CasMethod extends CasDataType {
    Method m;
    String formodule;
    String othername;
    CasModule association;

    CasMethod(String casname, String casmethod, CasModule a) {
        name = casname + " " + casmethod;
        othername = casmethod;
        formodule = casname;
        association = a;
        m = CaScriptEmulator.getNamedMethod(a.getPlugin(), casmethod);
    }

    public CasDataType copy() {
        return new CasMethod(formodule, othername, association);
    }

    public Method getm() {
        return m;
    }

    public Object getPlugin() {
        return association.pd.getPlugin();
    }

    public Object geta() {
        return association;
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
    }

}
