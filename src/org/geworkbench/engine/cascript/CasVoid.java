package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The wrapper class for double
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasVoid.java,v 1.1 2005-08-16 21:28:59 bb2122 Exp $
 * @modified by Behrooz Badii to CasDouble.java
 */
class CasVoid extends CasDataType {
    Object var;

    public CasVoid() {
        var = null;
    }

    public Object getvar() {
        return var;
    }

    public String typename() {
        return "void";
    }

    public CasDataType copy() {
        return new CasVoid();
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.println("is void parameter");
    }
}
