package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The wrapper class for unsigned variables
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasVariable.java,v 1.1 2005-08-08 15:57:49 watkin Exp $
 * @modified by Behrooz Badii to CasVariable.java
 */
class CasVariable extends CasDataType {
    public CasVariable(String name) {
        super(name);
    }

    public String typename() {
        return "undefined-variable";
    }

    public CasDataType copy() {
        throw new CasException("Variable " + name + " has not been defined");
    }

    public void print(PrintWriter w) {
        w.println(name + " = <undefined>");
    }
}
