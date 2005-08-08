package org.geworkbench.engine.cascript;

import antlr.collections.AST;

import java.util.Random;
import java.util.Vector;

/**
 * Interpreter routines that is called directly from the tree walker.
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasInterpreter.java,v 1.1 2005-08-08 15:57:49 watkin Exp $
 * @modified by Behrooz Badii to CasInterpreter.java
 */
class CasInterpreter {
    CasSymbolTable symt;

    final static int fc_none = 0;
    final static int fc_break = 1;
    final static int fc_continue = 2;
    final static int fc_return = 3;

    private int control = fc_none;
    private String label;

    private Random random = new Random();

    public CasInterpreter() {
        symt = new CasSymbolTable(null, -1); //-1 means that we're in a global scope
    }

    //used for variable initialization in the symbol table
    public void putvar(String id, String[] typereturn, Vector<CasDataType> indices, CasDataType value) {
        if (indices == null) {
            if (typereturn[0] == "module") {
                symt.put(id, new CasModule(id, typereturn[1]));
                System.out.println("put in casModule");
                System.out.println("Name: " + symt.findVar(id).name);
                System.out.println("Type: " + ((CasModule) symt.findVar(id)).type);
            } else {
                if (typereturn[0].equals("string")) {
                    symt.put(id, new CasString(""));
                } else if (typereturn[0].equals("float")) {
                    symt.put(id, new CasDouble(0));
                } else if (typereturn[0].equals("int")) {
                    symt.put(id, new CasInt(0));
                } else if (typereturn[0].equals("boolean")) {
                    symt.put(id, new CasBool(false));
                }
                symt.findVar(id).setName(id);
                System.out.println("Name: " + symt.findVar(id).name);
            }
        }
        //time to deal with arrays and matrices
        else if (indices.size() == 1) {
            System.out.println("we are dealing with an array here, with dimensionality of one in assign() call");
            if (indices.elementAt(0) instanceof CasInt) {
                symt.put(id, new CasArray(((CasInt) indices.elementAt(0)).getvar(), typereturn));
                symt.findVar(id).setName(id);
                (symt.findVar(id)).initializeArray(); //this is dependent on setName occurring first
                System.out.println("Name: " + symt.findVar(id).name);
            } else throw new CasException("index of array declaration for " + id + " must be an integer");
        } else if (indices.size() == 2) {
            System.out.println("we are dealing with an array here, with dimensionality of two in assign() call");
            if ((indices.elementAt(0) instanceof CasInt) && (indices.elementAt(1) instanceof CasInt)) {
                symt.put(id, new CasMatrix(((CasInt) indices.elementAt(0)).getvar(), ((CasInt) indices.elementAt(0)).getvar(), typereturn));
                symt.findVar(id).setName(id);
                (symt.findVar(id)).initializeMatrix(); //this is dependent on setName occurring first
                System.out.println("Name: " + symt.findVar(id).name);
            } else throw new CasException("indices of two-dimensional array declaration (matrix declaration) for " + id + " must be integers");
        }

        if (value != null) {
            assign(symt.findVar(id), value);
        }
    }

    //this is used in the assignment token in the walker and when you have to initialize a variable
    //REMEMBER TO PUT MORE STUFF IN HERE!!!!
    //YOU REALLY GOTTA FIX THIS METHOD
    public CasDataType assign(CasDataType a, CasDataType b) {
        System.out.println("in assignment");
        if (a.getPartOf() != null) {
            CasDataType x = null;
            System.out.println("in a array or matrix");
            if (a instanceof CasInt && b instanceof CasInt) {
                System.out.println("new value " + ((CasInt) b).getvar());
                x = ((CasInt) rvalue(b));
            }
            if (a instanceof CasDouble) {
                if (b instanceof CasDouble) {
                    System.out.println("new value " + ((CasDouble) b).getvar());
                    x = ((CasDouble) rvalue(b));
                }
                if (b instanceof CasInt) {
                    System.out.println("new value " + ((CasInt) b).getvar());
                    x = ((CasDouble) rvalue(b));
                }
            }
            if (a instanceof CasBool && b instanceof CasBool) {
                System.out.println("new value " + ((CasBool) b).getvar());
                x = ((CasBool) rvalue(b));
            }
            if (a instanceof CasString && b instanceof CasString) {
                System.out.println("new value " + ((CasString) b).getvar());
                x = ((CasString) rvalue(b));
            }
            //here, we have x, a will be replaced by x in the structure itself
            if (symt.findVar(a.getPartOf()) instanceof CasArray && x != null) {
                x.setPosition(a.getPosition());
                x.setPartOf(a.getPartOf());
                symt.findVar(x.getPartOf()).setArrayValue(x, x.getPosition());
            }
            if (symt.findVar(a.getPartOf()) instanceof CasMatrix && x != null) {
                x.setPositions(a.getPosition(), a.getPosition2());
                x.setPartOf(a.getPartOf());
                symt.findVar(x.getPartOf()).setMatrixValue(x, x.getPosition(), x.getPosition2());
            }
            System.out.println("substructure assignment success!");
            return new CasBool(true);
        }
        if (a instanceof CasValue && b instanceof CasValue) {
            //lefthand side of assignment is set, righthand side is get
            //set is the value that is going to be assigned to
            CasValue set = new CasValue(((CasValue) a).formodule, "set" + ((CasValue) a).othername, ((CasValue) a).association);
            //get is the value that set is going to have after this procedure
            CasValue get = new CasValue(((CasValue) b).formodule, "get" + ((CasValue) b).othername, ((CasValue) b).association);
            try {
                Object data = get.getm().invoke(get.getPlugin());
                set.getm().invoke(set.getPlugin(), data);
            } catch (Exception e) {
                System.out.println("error in assign for two CasValues");
                e.printStackTrace();
            }
            return new CasBool(true);
        }
        if (null != a.getName()) {
            if (a instanceof CasInt && b instanceof CasInt) {
                System.out.println(((CasInt) a).name + " has value " + ((CasInt) a).getvar());
                System.out.println("new value " + ((CasInt) b).getvar());
                CasDataType x = rvalue(b);
                x.setName(a.name);
                //symt.setVar( x.name, x, true, 0 );  // scope?
                symt.setVar(a.name, (CasInt) x);
                return new CasBool(true);
            }
            if (a instanceof CasDouble) {
                System.out.println(((CasInt) a).name + " has value " + ((CasDouble) a).getvar());
                if (b instanceof CasDouble) {
                    System.out.println("new value " + ((CasDouble) b).getvar());
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasDouble) x);
                }
                if (b instanceof CasInt) {
                    System.out.println("new value " + ((CasInt) b).getvar());
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasInt) x);
                }
                return new CasBool(true);
            }
            if (a instanceof CasBool && b instanceof CasBool) {
                System.out.println(((CasBool) a).name + " has value " + ((CasBool) a).getvar());
                System.out.println("new value " + ((CasBool) b).getvar());
                CasDataType x = rvalue(b);
                x.setName(a.name);
                symt.setVar(a.name, (CasBool) x);
                return new CasBool(true);
            }
            if (a instanceof CasString && b instanceof CasString) {
                System.out.println(((CasString) a).name + " has value " + ((CasString) a).getvar());
                System.out.println("new value " + ((CasString) b).getvar());
                CasDataType x = rvalue(b);
                x.setName(a.name);
                symt.setVar(a.name, (CasString) x);
                return new CasBool(true);
            }
            //should this be allowed?
            if (a instanceof CasModule && b instanceof CasModule) {
                System.out.println("in Casmodule part of ipt.assign(a,b)");
                CasDataType x = rvalue(b);
                x.setName(a.name);
                symt.setVar(a.name, (CasModule) x);
                return new CasBool(true);
            }
            if (null != b.getName()) return a.error(b, "=");
            else return a.error("=");
        }
        return a.error("=");
    }

    public CasDataType dimensionAccess(String id, Vector<CasDataType> indices) {
        System.out.println("in dimensionAccess() call");
        CasDataType a = symt.findVar(id);
        CasDataType ret;
        if (indices.size() == 1 && a instanceof CasArray) {
            if (indices.elementAt(0) instanceof CasInt) {
                ret = ((CasArray) a).accessArray(((CasInt) indices.elementAt(0)).getvar());
                //ret.setName("from."+id);
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 1 && a instanceof CasMatrix) {
            if (indices.elementAt(0) instanceof CasInt) {
                ret = ((CasMatrix) a).subArrayofMatrix(((CasInt) indices.elementAt(0)).getvar());
                //ret.setName("from."+id);
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 2 && a instanceof CasMatrix) {
            if ((indices.elementAt(0) instanceof CasInt) && (indices.elementAt(1) instanceof CasInt)) {
                ret = ((CasMatrix) a).accessMatrix(((CasInt) indices.elementAt(0)).getvar(), ((CasInt) indices.elementAt(1)).getvar());
                //ret.setName("from."+id);
                return ret;
            } else throw new CasException("indices of two-dimensional array declaration (matrix declaration) for " + a.getName() + " must be integers");
        } else throw new CasException("there are too many indices, only two dimensional arrays are supported");
    }

    //waiting is done in seconds, not milliseconds
    //stopme is called in a WAIT statement
    public CasDataType stopme(CasDataType a) {
        System.out.println("in stopme");
        if (a instanceof CasInt) {
            try {
                Thread.sleep(((CasInt) a).getvar() * 1000);
                return new CasBool(true);
            } catch (Exception e) {
                System.out.println("problem making thread sleep in ipt.stopme() call");
                e.printStackTrace();
            }
        }
        throw new CasException("wait call needs an integer");
    }

    //methodcall occurs when the CasMethod is used, so we will find it in OBJECT_CALL
    public void MethodCall(String casname, String casmethod, Vector<CasDataType> v) {
        Object [] args = vectortoargs(v);
        if (symt.exists(casname + " " + casmethod)) {
            //do something here
        } else {
            symt.put(casname + " " + casmethod, new CasMethod(casname, casmethod, (CasModule) symt.findVar(casname)));
            System.out.println("made new method " + casmethod + " for " + casname);
            CasMethod callme = (CasMethod) symt.findVar(casname + " " + casmethod);
            try {
                callme.m.invoke(callme.getPlugin(), args);
            } catch (Exception e) {
                System.out.println("Error occurred in MethodCall in interpreter for non-pre-existing method");
                e.printStackTrace();
            }
        }
    }

    //helper function for CasInterpreter.MethodCall
    static Object[] vectortoargs(Vector<CasDataType> v) {
        Object args[] = v.toArray();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof CasBool) {
                System.out.println("Boolean" + ((CasBool) args[i]).getvar());
                args[i] = new Boolean(((CasBool) args[i]).getvar());
            } else if (args[i] instanceof CasInt) {
                System.out.println("Int" + ((CasInt) args[i]).getvar());
                args[i] = new Integer(((CasInt) args[i]).getvar());
            } else if (args[i] instanceof CasDouble) {
                System.out.println("Double" + ((CasDouble) args[i]).getvar());
                args[i] = new Double(((CasDouble) args[i]).getvar());
            } else if (args[i] instanceof CasString) {
                System.out.println("String" + ((CasString) args[i]).getvar());
                args[i] = ((CasString) args[i]).getvar();
            } else {
                args[i] = ((CasModule) args[i]).getPlugin();
                System.out.println("This should be an object");
            }
        }
        return args;
    }

    //not used
    public CasDataType[] convertExprList(Vector v) {
        /* Note: expr list can be empty */
        CasDataType[] x = new CasDataType[v.size()];
        for (int i = 0; i < x.length; i++)
            x[i] = (CasDataType) v.elementAt(i);
        return x;
    }

    //not used
    public static String[] convertVarList(Vector v) {
        /* Note: var list can be empty */
        String[] sv = new String[ v.size() ];
        for (int i = 0; i < sv.length; i++)
            sv[i] = (String) v.elementAt(i);
        return sv;
    }

    //gets the variable from the SymbolTable
    public CasDataType getVariable(String s) {
        // default static scoping
        CasDataType x = symt.findVar(s);
        return x;
    }

    //you need to make a copy, so that when i = j, they don't become the same reference
    public CasDataType rvalue(CasDataType a) {
        if (null == a.name) return a;
        return a.copy();
    }

    //for this method to forNext(), these are all used for control flow
    public void setBreak() {
        control = fc_break;
    }

    public boolean breakSet() {
        if (control == fc_break) {
            return true;
        } else return false;
    }

    public void setContinue() {
        control = fc_continue;
    }

    public boolean continueSet() {
        if (control == fc_continue) {
            return true;
        } else return false;
    }

    public void setReturn() {
        control = fc_return;
    }

    public void tryResetFlowControl() {
        control = fc_none;
    }

    public void loopNext() {
        if (control == fc_continue) tryResetFlowControl();
    }

    public void loopEnd() {
        if (control == fc_break) tryResetFlowControl();
        symt = symt.Parent();
    }

    public boolean canProceed() {
        return control == fc_none;
    }

    public void loopInit() {
        // create a new symbol table
        symt = new CasSymbolTable(symt, symt.getLevel() + 1);
    }

    public boolean forCanProceed() {
        if (control != fc_none) return false;
        return true;
    }

    public void forNext() {
        if (control == fc_continue) tryResetFlowControl();
    }

    //creating a user-defined function that can be called by the user later on
    public void makeFunction(String id, Vector<CasArgument> args, AST body, CasSymbolTable s, String[] typereturn, int brackets) {
        //if we take the symboltable that was passed in, it is static
        CasFunction a = new CasFunction(id, args, body, s, typereturn, brackets);
        System.out.println("we're in function " + a.getName() + " with this many brackets:" + a.brackets);
        CasArgument temp;
        for (int i = 0; i < a.args.size(); i++) {
            temp = a.args.elementAt(i);
            System.out.println("Reading arguments: id = " + temp.getId());
            if (temp.getTypeReturn()[0].equals("module")) System.out.println("we have a module of type: " + temp.getTypeReturn()[1]);
            else System.out.println("the type is: " + temp.getTypeReturn()[0]);
            System.out.println("Number of brackets: " + temp.getBrackets());
        }
        if (symt.notexists(a.getName())) {
            symt.put(a.getName(), a);
        } else throw new CasException(a.getName() + " already exists as a function or variable");
    }

    //used for a function call
    public CasDataType funcCall(CASWalker walker, String id, Vector<CasDataType> argList) {
        CasSymbolTable temp;
        CasDataType whatthefunc = symt.findVar(id);
        if (! (whatthefunc instanceof CasFunction)) return whatthefunc.error("not a function");
        Vector<CasArgument> actualargs = ((CasFunction) whatthefunc).getArgs();
        if (actualargs.size() != argList.size()) return whatthefunc.error("wrong number of parameters");
        temp = symt;
        //is this the right symbol table? should we be using the symbol that's part of the CasFunction object?
        symt = new CasSymbolTable(((CasFunction) whatthefunc).getParentSymbolTable(), ((CasFunction) whatthefunc).getParentSymbolTable().getLevel() + 1);

        //remember to check arguments against their correct type
        for (int i = 0; i < actualargs.size(); i++) {
            CasDataType a = rvalue(argList.elementAt(i));
            a.setName(actualargs.elementAt(i).getId());
            if (symt.existsinscope(actualargs.elementAt(i).getId())) throw new CasException(actualargs.elementAt(i).getId() + " already exists");
            else symt.put(actualargs.elementAt(i).getId(), a);
        }
        CasDataType ret = null;
        try {
            ret = walker.fbody(((CasFunction) whatthefunc).getBody());
        } catch (antlr.RecognitionException e) {
            System.out.println("we have a problem in funcCall");
            e.printStackTrace();
        }
        // no break or continue can go through the function
        if (control == fc_break || control == fc_continue) throw new CasException("nowhere to break or continue");

        // if a return was called
        if (control == fc_return) tryResetFlowControl();

        // remove this symbol table and return
        symt = temp;
        //check the return type before passing it back, if it's not the same return type, throw an error
        //you still have to do this!
        return ret;
    }
}
