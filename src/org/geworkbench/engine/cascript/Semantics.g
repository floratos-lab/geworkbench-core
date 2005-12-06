//for some help - ":." means don't execute, just hold the reference of it
{
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

import java.util.Vector;
}

class CASSemantics extends TreeParser;

options {
importVocab = CAStokens;
}
{
    CasPreInterpreter ipt = new CasPreInterpreter();
    AST mainbody = null;
}

/**
 * the root of the initial tree is going to be walkme, start here!
*/ 
walkme
: #(PROG (publicvar)* (function)+)
  {
    //make new symboltable for main
    ipt.symt = new CasPreSymbolTable(ipt.symt, ipt.symt.getLevel()+1);
    fbody(mainbody);
    //get rid of main's symbol table
    ipt.symt = ipt.symt.Parent();
  }
;

/**
 * public variable declaration
*/
publicvar
: #(PUBLICVAR variable)
;

/**
 * defining a function
*/
function
{ int brackets = 0;
  String id = "";
  CasDataType typereturn = null;
  Vector<CasArgument> argList = null;
  CasArgument temp = null;}
: #(FUNCTION (typereturn = type (LEFTBRACKET RIGHTBRACKET {brackets++;})*) ID {id = #ID.getText();} argList = args fbody:.)
{
  if (id.equals("main")) {
    mainbody = #fbody;
  }
  ipt.makeFunction(id, argList, fbody, ipt.symt, typereturn, brackets);
}
;

/**
 * list of formal arguments in a function definition
*/
args returns [Vector<CasArgument> argList]
{argList = new Vector<CasArgument>();
CasArgument temp = null;
String id = "";
int brackets = 0;
CasDataType typereturn = null;}
: #(ARGDEC ((typereturn = type) ID {id = #ID.getText();} (LEFTBRACKET RIGHTBRACKET {brackets++;})?
{temp = new CasArgument(typereturn, id, brackets);
argList.add(temp);
id = "";
brackets = 0;
typereturn = null;
temp = null;}
)*);//notice the kleene closure.  We can keep finding more parameters and we want all of them

/**
 * this is a variable declaration, like int a = 5; 
*/
variable
{ String id = "";
  CasDataType value = null;
  CasDataType typereturn;
  Vector<CasDataType> indices = null;}
: #(VARIABLE typereturn = type (#(IDENTIFIER ID {id = #ID.getText();} (indices = index)?) (#(EQUAL value = expr))?
{
if (ipt.symt.existsinscope(id)) {
  throw new CasException(id + " already exists as a function or variable");
}
ipt.putvar(id, typereturn, indices, value);
indices = null;
value = null;
})+)
/*notice the plus sign here.  This means that we keep finding an id, an indices value, and a value from expr to
create more entries in the symbol table*/
;

/**
 *index keeps track of all the indices that are defined in an variable declaration
*/
index returns [Vector<CasDataType> v]
{v = new Vector<CasDataType>();
CasDataType aindex = null;}
: #(INDEX (aindex = expr {v.add(aindex);})*)
;


/** 
 * the type of a function or variable returns one, possibly two strings
 * if the type is module, there is a secondary string that defines the type of module
 * if not, then the type is an ordinary primitive
 * type is used for variables, functions, formal parameter lists, arrays, and matrices
*/
type returns [CasDataType typereturn]
{
typereturn = null;
int isdiff = 0;
String id = "";
}
: #(TYPE (n:primitives | (MODULE ID {isdiff = 1; id = #ID.getText();}) | (DATATYPE ID {isdiff = 2; id = #ID.getText();})))
{
if (isdiff == 1) {
  typereturn = new CasModule(id);
}
else if (isdiff == 2) {
  typereturn = new CasDataPlug(id);
}
else {
  String temp = n.getText();
  if (temp.equals("void"))
    typereturn = new CasVoid();
  else if (temp.equals("int"))
    typereturn = new CasInt(0);
  else if (temp.equals("double"))
    typereturn = new CasDouble(0);
  else if (temp.equals("boolean"))
    typereturn = new CasBool(false);
  else if (temp.equals("string"))
    typereturn = new CasString("");
}}
;

/**
 * primitive types, including void
*/
primitives
: "void"
| "int"
| "double"
| "boolean"
| "string"
;


fbody returns [CasDataType a]
{a = null;}
: #(FUNCTIONBODY (a = expr))
; 

/**
 * expr is a big deal, it deals with almost everything the language can throw at it
*/
expr returns [CasDataType r] 
{
r = null;
CasDataType a,b;
String id = "";
String id2 = "";
Vector<CasDataType> arglist = null;
}
: NUM_INT                     { r = new CasInt(Integer.parseInt(#NUM_INT.getText())); } //literal integers
| NUM_DOUBLE                   { r = new CasDouble(Double.parseDouble(#NUM_DOUBLE.getText()));} //literal doubles
| TRUE                        { r = new CasBool(true); } //literal "true" value
| FALSE                       { r = new CasBool(false); } //literal "false" value
| #(IDENTIFIER ID {id = #ID.getText();} (arglist = index)?)   //here we are just using arglist to gather indices
{
if (arglist == null)
  r = ipt.getVariable( id );  //identifier, AKA a variable
else
  r = ipt.dimensionAccess(id, arglist); //array access
}
| str:String                  { r = new CasString( str.getText().toString() ); } //literal string value
| variable                    { r = new CasBool(true);} //variable declaration ex. int a = 5;
| #(ASSIGNMENT a=expr b=expr) //remember to extend ipt.assign(a,b) //assignment operation, more complex than it seems
  {r = ipt.assign(a,b);}
| #(OBJECT_VALUE ID {id = #ID.getText();} ID21:ID)
  { id2 = ID21.getText();
    if (ipt.symt.findVar(id) instanceof CasModule) {
      //should you be checking if id a CasModule in the firstplace?
      r = new CasValue(id, id2, ((CasModule)ipt.symt.findVar(id)));
      /*Testing purposes System.out.println("we're in object_value");*/
    }
    else {
      throw new CasException(id + "is not a module, so it can't have any variables");
    }
  }
  //object_value, like a public variable in JAVA ex. genePanel.DataSet
| #(OBJECT_CALL ID {id = #ID.getText();} ID22:ID arglist = param) //modify all this for CasDataPlug, bring this into the interpreter
  { id2 = ID22.getText();
    //r has to be something different, it has to come from MethodCall
    //MethodCall should tell the difference between a CasModule and a CasDataPlug
    /*Testing purposes
    System.out.println("we're in object_call");*/
    r = ipt.checkCasCallReturn(new CasCallReturn(ipt.MethodCall(id, id2, arglist)));
  }
  //object_call, like a function call in JAVA through a object ex. genePanel.createPanel(i++,10,true)
| #(PRINT a = expr)           { r = a; a.print(); } //print statement
| #(IFSTR #(CONDITION a=expr) #(THEN thenif:.) (#(ELSE elseif:.))?)
{
    if ( !( a instanceof CasBool ) )
        return a.error( "if: expression should be bool" );
    if ( ((CasBool)a).var )
        r = expr( #thenif );
    else if ( null != elseif )
        r = expr( #elseif );
}
//conditional statement, pretty straightforward
| #(WHILESTR {ipt.loopInit();} #(CONDITION cond:.) rest:.)
{
  a = expr(#cond);
  if ( !(a instanceof CasBool ))
    return a.error ( "while: expression should be bool" );
  while (!ipt.breakSet() && ((CasBool)a).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#rest);
    if (!ipt.breakSet())
      a = expr(#cond);
    if ( !(a instanceof CasBool ))
        return a.error ( "while: expression should be bool" );
  }
  ipt.loopEnd();
}
//while loop, this probably has some bugs in it
| #(FORSTR {ipt.loopInit(); }#(FORLEFT r = expr) #(FORMID cond2:.) #(FORRIGHT after:.) #(FORBODY forbody:.))
{ a = expr(#cond2);
  if ( !(a instanceof CasBool ))
  return a.error ( "for: expression should be bool" );
  while (!ipt.breakSet() && ((CasBool)a).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#forbody);
    if (!ipt.breakSet()) {
      expr(#after);
      a = expr(#cond2);
    }
    if ( !(a instanceof CasBool ))
        return a.error ( "for: expression should be bool" );
  }
  ipt.loopEnd();
}
//for loop, this probably has some bugs in it, the same bugs the while loop has
| #(STATEMENTS (statement:. { if ( ipt.canProceed() ) r = expr(#statement); } )*) //set of statements
| BREAK                       { r = new CasBreak(); ipt.setBreak();} //break statement, changes control flow
| CONTINUE                    { r = new CasContinue(); ipt.setContinue();} //continue statement, changes control flow
//should the returnstatement have its own DataType? like CasReturn? and you can check that it is that type in functioncall in the interpreter
| #(RETURNSTR a = expr)    { r = new CasReturn(ipt.rvalue( a )); ipt.setReturn();} //return statement, changes control flow
| #(WAIT a = expr)         { r = ipt.stopme(a);} //pauses the program for a given number of seconds
| #(FUNCTION_CALL ID {id = #ID.getText();} arglist = param)
  { r = ipt.funcCall(this, id, arglist);}
  //call a function created by the user
/*
| #(NEW ID arglist = param)
  { id = #ID.getText();
    r = new CasString("new statement" + id);
    //for (int i = 0; i < arglist.size(); i++){
    //  r += arglist.elementAt(i) + " ";
    //}
  }*/

| #(OR a = expr right_or:.)
  {
    if ( a instanceof CasBool )
        r = ( ((CasBool)a).var ? a : expr(#right_or) );
    else
        r = a.or( expr(#right_or) );
  }
//the extra complexity is required because if the first operand is true, there is no need to do the second operand.
| #(AND a = expr right_and:.)
  {
    if ( a instanceof CasBool )
        r = ( ((CasBool)a).var ? expr(#right_and) : a );
    else
        r = a.and( expr(#right_and) );
  }
//the extra complexicty is required because if the first operand is false, there is no need to do the second operand.
| #(NOT a = expr)                        {r = a.not();} //negation of a boolean value
| #(LESS a = expr b = expr)              {r = a.lt(b);} //returns boolean value for a < b
| #(LESSEQUAL a = expr b = expr)         {r = a.le(b);} //returns boolean value for a <=b
| #(MORE a = expr b = expr)              {r = a.gt(b);} //returns boolean value for a > b
| #(MOREEQUAL a = expr b = expr)         {r = a.ge(b);} //returns boolean value for a >=b
| #(EQUALTO a = expr b = expr)           {r = a.eq(b);} //returns boolean value for a==b
| #(NOTEQUAL a = expr b = expr)          {r = a.ne(b);} //returns boolean value for a!=b
| #(PLUS a = expr b = expr)              {r = a.plus(b);} //addition of integers and doubles
| #(MINUS a = expr b = expr)             {r = a.minus(b);} //subtraction of integers and doubles
| #(TIMES a = expr b = expr)             {r = a.times(b);} //multiplication
| #(SLASH a = expr b = expr)             {r = a.lfracts(b);} //division
| #(MODULO a = expr b = expr)            {r = a.modulus(b);} //undefined
| #(NEGATION a = expr)                   {r = a.uminus();} //unary minus operation
| #(INCAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, true);} //incrementation after operation, a++
| #(DECAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, false);} //decrementation after operation, a--
| #(INCBEFORE a = expr)                  {r = ipt.incOrDec(a, true);} //incrementation before operation, ++a
| #(DECBEFORE a = expr)                  {r = ipt.incOrDec(a, false);} //incrementation after operation, --a
;

/**
 * list of parameters for a function call
*/
param returns [ Vector<CasDataType> arglist ]
{ arglist = null;
  CasDataType a;}
: #(ARGS { arglist = new Vector<CasDataType>(); } ( a=expr      { arglist.add( a ); })*)
;

