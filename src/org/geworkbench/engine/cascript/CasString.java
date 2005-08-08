package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * the wrapper class for string
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasString.java,v 1.1 2005-08-08 15:57:49 watkin Exp $
 * @modified by Behrooz Badii to CasString.java
 */
class CasString extends CasDataType {
    String var;

    public CasString(String str) {
        this.var = str;
    }

    public String typename() {
        return "string";
    }

    public String getvar() {
        return var;
    }

    public CasDataType copy() {
        return new CasString(var);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.print(var);
        w.println();
    }

    public CasDataType plus(CasDataType b) {
        if (b instanceof CasString) return new CasString(var + ((CasString) b).var);

        return error(b, "+");
    }

    public CasDataType add(CasDataType b) {
        if (b instanceof CasString) {
            var = var + ((CasString) b).var;
            return this;
        }

        return error(b, "+=");
    }
}
