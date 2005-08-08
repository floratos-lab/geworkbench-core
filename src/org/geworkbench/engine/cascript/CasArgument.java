package org.geworkbench.engine.cascript;

/* the wrapper class for Module
*
* @author Behrooz Badii - badiib@gmail.com
* argument type facilitates argument checking for function calls
*/

class CasArgument {
    int brackets;
    String[] typeReturn;
    String id;

    CasArgument(String[] t, String i, int b) {
        typeReturn = t;
        id = i;
        brackets = b;
    }

    public String[] getTypeReturn() {
        return typeReturn;
    }

    public int getBrackets() {
        return brackets;
    }

    public String getId() {
        return id;
    }
}
