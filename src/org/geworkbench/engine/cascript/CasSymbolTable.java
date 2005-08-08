package org.geworkbench.engine.cascript;

import java.util.HashMap;

/**
 * Symbol table class: dual parent supported: static and dynamic
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasSymbolTable.java,v 1.1 2005-08-08 15:57:49 watkin Exp $
 * @modified by Behrooz Badii to CasSymbolTable.java
 */
class CasSymbolTable extends HashMap {
    CasSymbolTable parent;
    boolean read_only;
    int level;

    public CasSymbolTable(CasSymbolTable sparent, int slevel) {
        parent = sparent;
        level = slevel; //this should be -1 for global scope
        read_only = false;
    }

    public CasSymbolTable() {
        parent = null;
        level = -2;
        read_only = false;
    }

    public void setReadOnly() {
        read_only = true;
    }

    public final int getLevel() {
        return level;
    }

    public final CasSymbolTable Parent() {
        return parent;
    }

    public final boolean containsVar(String name) {
        return containsKey(name);
    }

    public final CasDataType findVar(String name) {
        if (this.containsVar(name)) return (CasDataType) get(name);
        if (level == -1) throw new CasException("Variable " + name + " not found");
        else return Parent().findVar(name);
    }

    public final CasSymbolTable getScope(String name) {
        if (this.containsVar(name)) return this;
        if (level == -1) throw new CasException("Variable " + name + " not found");
        else return this.Parent().getScope(name);
    }

    public final boolean exists(String name) {
        if (this.containsVar(name)) return true;
        if (level == -1) return false;
        else return this.Parent().exists(name);
    }

    public final boolean existsinscope(String name) {
        if (this.containsVar(name)) return true;
        return false;
    }

    public final boolean notexists(String name) {
        if (this.containsVar(name)) return false;
        if (level == -1) return true;
        else return this.Parent().notexists(name);
    }

    public final void setVar(String name, CasDataType data) {

        data.name = name;
        CasSymbolTable st = getScope(name);
        System.out.println("Changing value of " + name + " in setVar in CasSymbolTable");
        st.put(name, data);
    }
}
