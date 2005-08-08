package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

class CasMatrix extends CasDataType {
    CasDataType [][] var;
    String type[];

    public CasMatrix(int length1, int length2, String typereturn[]) {
        var = new CasDataType[length1][length2];
        type = typereturn;
    }

    public CasMatrix(CasDataType [][]v) {
        var = v;
    }

    public CasDataType[][] getvar() {
        return var;
    }

    public String[] getelementType() {
        return type;
    }

    public String typename() {
        return "CasMatrix";
    }

    public CasDataType copy() {
        return new CasMatrix(var);
    }

    public CasDataType subArrayofMatrix(int i) {
        return new CasArray(var[i]);
    }

    public CasDataType accessMatrix(int i, int j) {
        return var[i][j];
    }

    public void setsubArrayofMatrixValue(CasDataType a, int i) {
        if (a instanceof CasArray) {
            if (((CasArray) a).getelementType()[0].equals(type[0]) && ((CasArray) a).getelementType()[1].equals(type[1])) var[i] = ((CasArray) a).getvar();
            else throw new CasException("you have the wrong type for assigning an array to a subMatrix");
        } else throw new CasException("you are assigning something other than an array to part of the matrix");
    }

    public void setMatrixValue(CasDataType a, int i, int j) {
        if (var[i][j].getClass().isAssignableFrom(a.getClass())) var[i][j] = a;
        else throw new CasException("you are assigning a different type to a value in the matrix" + getName());
    }

    public void initializeMatrix() {
        for (int i = 0; i < var.length; i++) {
            for (int j = 0; j < var[i].length; j++) {
                if (type[0].equals("string")) var[i][j] = new CasString("");
                else if (type[0].equals("float")) var[i][j] = new CasDouble(0);
                else if (type[0].equals("int")) var[i][j] = new CasInt(0);
                else if (type[0].equals("boolean")) var[i][j] = new CasBool(false);
                var[i][j].setPartOf(this.getName());
                var[i][j].setPositions(i, j);
            }
        }
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.println(var.toString());
    }


}
