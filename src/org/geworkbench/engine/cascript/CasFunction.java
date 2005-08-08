package org.geworkbench.engine.cascript;

import antlr.collections.AST;

import java.io.PrintWriter;
import java.util.Vector;

/**
 * The function data type
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasFunction.java,v 1.1 2005-08-08 15:57:48 watkin Exp $
 *          modified by Behrooz Badii into CasFunction.java
 */
class CasFunction extends CasDataType {
    // we need a reference to the AST for the function entry
    Vector<CasArgument> args;
    AST body;            // body = null means an internal function.
    CasSymbolTable pst;   // the symbol table of static parent
    String[] returntype;
    int brackets;

    public CasFunction(String name, Vector<CasArgument> args, AST body, CasSymbolTable pst, String[] r, int b) {
        super(name);
        this.args = args;
        this.body = body;
        this.pst = pst;
        returntype = r;
        brackets = b;
    }

    public String typename() {
        return "function";
    }

    public CasDataType copy() {
        return new CasFunction(name, args, body, pst, returntype, brackets);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.print("<function>(");
        /*for ( int i=0; ; i++ )
            {
                w.print( args[i] );
                if ( i >= args.length - 1 )
                    break;
                w.print( "," );
            }
            w.println( ")" );
        }*/
    }

    public Vector<CasArgument> getArgs() {
        return args;
    }

    public String[] getReturnType() {
        return returntype;
    }

    public int getBrackets() {
        return brackets;
    }

    public String getName() {
        return name;
    }

    public CasSymbolTable getParentSymbolTable() {
        return pst;
    }

    public AST getBody() {
        return body;
    }
}
