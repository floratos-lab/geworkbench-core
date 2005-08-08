package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

class CasArray extends CasDataType {
    CasDataType [] var;
    String type[];

    public CasArray(int length, String typereturn[]) {
        var = new CasDataType[length];
        type = typereturn;
    }

    public CasArray(CasDataType []v) {
        var = v;
    }

    public CasDataType[] getvar() {
        return var;
    }

    public String[] getelementType() {
        return type;
    }

    public String typename() {
        return "CasArray";
    }

    public CasDataType copy() {
        return new CasArray(var);
    }

    public CasDataType accessArray(int i) {
        return var[i];
    }

    public void setArrayValue(CasDataType a, int i) {
        if (var[i].getClass().isAssignableFrom(a.getClass())) var[i] = a;
        else throw new CasException("you are assigning a different type to a value in the array" + getName());
    }

    public void initializeArray() {
        for (int i = 0; i < var.length; i++) {
            if (type[0].equals("string")) var[i] = new CasString("");
            else if (type[0].equals("float")) var[i] = new CasDouble(0);
            else if (type[0].equals("int")) var[i] = new CasInt(0);
            else if (type[0].equals("boolean")) var[i] = new CasBool(false);
            var[i].setPartOf(this.getName());
            var[i].setPosition(i);
        }
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.println(var.toString());
    }

}
