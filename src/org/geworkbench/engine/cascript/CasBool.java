package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * the wrapper class for boolean
 *
 * @author Behrooz Badii -  badiib@gmail.com
 * @version $Id: CasBool.java,v 1.3 2005-10-14 18:38:12 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasBool extends CasDataType {
    boolean var;

    CasBool(boolean var) {
        this.var = var;
    }

    public boolean getvar() {
        return var;
    }

    public String typename() {
        return "bool";
    }

    public CasDataType copy() {
        return new CasBool(var);
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(var ? "true" : "false");
    }


    public CasDataType and(CasDataType b) {
        if (b instanceof CasBool) return new CasBool(var && ((CasBool) b).var);
        return error(b, "and");
    }

    public CasDataType or(CasDataType b) {
        if (b instanceof CasBool) return new CasBool(var || ((CasBool) b).var);
        return error(b, "or");
    }

    public CasDataType not() {
        return new CasBool(!var);
    }

    public CasDataType eq(CasDataType b) {
        // not exclusive or
        if (b instanceof CasBool) return new CasBool((var && ((CasBool) b).var) || (!var && !((CasBool) b).var));
        return error(b, "==");
    }

    public CasDataType ne(CasDataType b) {
        // exclusive or
        if (b instanceof CasBool) return new CasBool((var && !((CasBool) b).var) || (!var && ((CasBool) b).var));
        return error(b, "!=");
    }
}
