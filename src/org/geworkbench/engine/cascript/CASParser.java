// $ANTLR 2.7.5 (20050128): "CAS.g" -> "CASParser.java"$
package org.geworkbench.engine.cascript;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class CASParser extends antlr.LLkParser       implements CAStokensTokenTypes
 {

    int nr_error = 0;
    public void reportError( String s ) {
        super.reportError( s );
        nr_error++;
    }
    public void reportError( RecognitionException e ) {
        super.reportError( e );
        nr_error++;
    }

protected CASParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CASParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected CASParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CASParser(TokenStream lexer) {
  this(lexer,2);
}

public CASParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST program_AST = null;
		
		try {      // for error handling
			{
			_loop72:
			do {
				if ((LA(1)==PUBLIC)) {
					pubVarDecl();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop72;
				}
				
			} while (true);
			}
			{
			int _cnt74=0;
			_loop74:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					funcDecl();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt74>=1 ) { break _loop74; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt74++;
			} while (true);
			}
			match(Token.EOF_TYPE);
			program_AST = (AST)currentAST.root;
			program_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PROG,"PROG")).add(program_AST));
			currentAST.root = program_AST;
			currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
				program_AST.getFirstChild() : program_AST;
			currentAST.advanceChildToEnd();
			program_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = program_AST;
	}
	
	public final void pubVarDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pubVarDecl_AST = null;
		
		try {      // for error handling
			match(PUBLIC);
			declareStmt();
			astFactory.addASTChild(currentAST, returnAST);
			pubVarDecl_AST = (AST)currentAST.root;
			pubVarDecl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PUBLICVAR,"PUBLICVAR")).add(pubVarDecl_AST));
			currentAST.root = pubVarDecl_AST;
			currentAST.child = pubVarDecl_AST!=null &&pubVarDecl_AST.getFirstChild()!=null ?
				pubVarDecl_AST.getFirstChild() : pubVarDecl_AST;
			currentAST.advanceChildToEnd();
			pubVarDecl_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = pubVarDecl_AST;
	}
	
	public final void funcDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST funcDecl_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			{
				type();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop79:
				do {
					if ((LA(1)==LEFTBRACKET)) {
						AST tmp77_AST = null;
						tmp77_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp77_AST);
						match(LEFTBRACKET);
						AST tmp78_AST = null;
						tmp78_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp78_AST);
						match(RIGHTBRACKET);
					}
					else {
						break _loop79;
					}
					
				} while (true);
				}
				break;
			}
			case VOID:
			{
				isvoid();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp79_AST = null;
			tmp79_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp79_AST);
			match(ID);
			argDeclarationList();
			astFactory.addASTChild(currentAST, returnAST);
			functionbody();
			astFactory.addASTChild(currentAST, returnAST);
			funcDecl_AST = (AST)currentAST.root;
			funcDecl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION,"FUNCTION")).add(funcDecl_AST));
			currentAST.root = funcDecl_AST;
			currentAST.child = funcDecl_AST!=null &&funcDecl_AST.getFirstChild()!=null ?
				funcDecl_AST.getFirstChild() : funcDecl_AST;
			currentAST.advanceChildToEnd();
			funcDecl_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = funcDecl_AST;
	}
	
	public final void declareStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declareStmt_AST = null;
		
		try {      // for error handling
			declaration();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			declareStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = declareStmt_AST;
	}
	
	public final void type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case INT:
			{
				AST tmp81_AST = null;
				tmp81_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(INT);
				break;
			}
			case FLOAT:
			{
				AST tmp82_AST = null;
				tmp82_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp82_AST);
				match(FLOAT);
				break;
			}
			case BOOLSTR:
			{
				AST tmp83_AST = null;
				tmp83_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp83_AST);
				match(BOOLSTR);
				break;
			}
			case STRING:
			{
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp84_AST);
				match(STRING);
				break;
			}
			case MODULE:
			{
				{
				AST tmp85_AST = null;
				tmp85_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp85_AST);
				match(MODULE);
				AST tmp86_AST = null;
				tmp86_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp86_AST);
				match(ID);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			type_AST = (AST)currentAST.root;
			type_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE,"TYPE")).add(type_AST));
			currentAST.root = type_AST;
			currentAST.child = type_AST!=null &&type_AST.getFirstChild()!=null ?
				type_AST.getFirstChild() : type_AST;
			currentAST.advanceChildToEnd();
			type_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = type_AST;
	}
	
	public final void isvoid() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST isvoid_AST = null;
		
		try {      // for error handling
			AST tmp87_AST = null;
			tmp87_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp87_AST);
			match(VOID);
			isvoid_AST = (AST)currentAST.root;
			isvoid_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE,"TYPE")).add(isvoid_AST));
			currentAST.root = isvoid_AST;
			currentAST.child = isvoid_AST!=null &&isvoid_AST.getFirstChild()!=null ?
				isvoid_AST.getFirstChild() : isvoid_AST;
			currentAST.advanceChildToEnd();
			isvoid_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = isvoid_AST;
	}
	
	public final void argDeclarationList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argDeclarationList_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			{
				type();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp89_AST);
				match(ID);
				{
				_loop165:
				do {
					if ((LA(1)==LEFTBRACKET)) {
						AST tmp90_AST = null;
						tmp90_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp90_AST);
						match(LEFTBRACKET);
						AST tmp91_AST = null;
						tmp91_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp91_AST);
						match(RIGHTBRACKET);
					}
					else {
						break _loop165;
					}
					
				} while (true);
				}
				{
				_loop169:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						type();
						astFactory.addASTChild(currentAST, returnAST);
						AST tmp93_AST = null;
						tmp93_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp93_AST);
						match(ID);
						{
						_loop168:
						do {
							if ((LA(1)==LEFTBRACKET)) {
								AST tmp94_AST = null;
								tmp94_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp94_AST);
								match(LEFTBRACKET);
								AST tmp95_AST = null;
								tmp95_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp95_AST);
								match(RIGHTBRACKET);
							}
							else {
								break _loop168;
							}
							
						} while (true);
						}
					}
					else {
						break _loop169;
					}
					
				} while (true);
				}
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RIGHTPAREN);
			argDeclarationList_AST = (AST)currentAST.root;
			argDeclarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGDEC,"ARGDEC")).add(argDeclarationList_AST));
			currentAST.root = argDeclarationList_AST;
			currentAST.child = argDeclarationList_AST!=null &&argDeclarationList_AST.getFirstChild()!=null ?
				argDeclarationList_AST.getFirstChild() : argDeclarationList_AST;
			currentAST.advanceChildToEnd();
			argDeclarationList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = argDeclarationList_AST;
	}
	
	public final void functionbody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionbody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			functionbody_AST = (AST)currentAST.root;
			functionbody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTIONBODY,"FUNCTIONBODY")).add(functionbody_AST));
			currentAST.root = functionbody_AST;
			currentAST.child = functionbody_AST!=null &&functionbody_AST.getFirstChild()!=null ?
				functionbody_AST.getFirstChild() : functionbody_AST;
			currentAST.advanceChildToEnd();
			functionbody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = functionbody_AST;
	}
	
	public final void bracestatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bracestatement_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEFTBRACE:
			{
				{
				match(LEFTBRACE);
				{
				int _cnt96=0;
				_loop96:
				do {
					if ((_tokenSet_8.member(LA(1)))) {
						statement();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt96>=1 ) { break _loop96; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt96++;
				} while (true);
				}
				match(RIGHTBRACE);
				}
				bracestatement_AST = (AST)currentAST.root;
				bracestatement_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(STATEMENTS,"STATEMENTS")).add(bracestatement_AST));
				currentAST.root = bracestatement_AST;
				currentAST.child = bracestatement_AST!=null &&bracestatement_AST.getFirstChild()!=null ?
					bracestatement_AST.getFirstChild() : bracestatement_AST;
				currentAST.advanceChildToEnd();
				bracestatement_AST = (AST)currentAST.root;
				break;
			}
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case IFSTR:
			case WHILESTR:
			case FORSTR:
			case RETURNSTR:
			case TRUE:
			case FALSE:
			case BREAK:
			case CONTINUE:
			case WAIT:
			case NEW:
			case PRINT:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				statement();
				astFactory.addASTChild(currentAST, returnAST);
				bracestatement_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = bracestatement_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				evaluateStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case BREAK:
			{
				breakStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case CONTINUE:
			{
				continueStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case WHILESTR:
			{
				whileStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case IFSTR:
			{
				ifStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			{
				declareStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case RETURNSTR:
			{
				returnStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case FORSTR:
			{
				forstatement();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case WAIT:
			{
				waitStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case PRINT:
			{
				printStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = statement_AST;
	}
	
	public final void evaluateStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evaluateStmt_AST = null;
		
		try {      // for error handling
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			evaluateStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = evaluateStmt_AST;
	}
	
	public final void breakStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST breakStmt_AST = null;
		
		try {      // for error handling
			AST tmp100_AST = null;
			tmp100_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp100_AST);
			match(BREAK);
			match(SEMICOLON);
			breakStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = breakStmt_AST;
	}
	
	public final void continueStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continueStmt_AST = null;
		
		try {      // for error handling
			AST tmp102_AST = null;
			tmp102_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp102_AST);
			match(CONTINUE);
			match(SEMICOLON);
			continueStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = continueStmt_AST;
	}
	
	public final void whileStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whileStmt_AST = null;
		
		try {      // for error handling
			AST tmp104_AST = null;
			tmp104_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp104_AST);
			match(WHILESTR);
			expressionStmt();
			astFactory.addASTChild(currentAST, returnAST);
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			whileStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = whileStmt_AST;
	}
	
	public final void ifStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifStmt_AST = null;
		
		try {      // for error handling
			AST tmp105_AST = null;
			tmp105_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp105_AST);
			match(IFSTR);
			expressionStmt();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmtBody();
			astFactory.addASTChild(currentAST, returnAST);
			elseStmt();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = ifStmt_AST;
	}
	
	public final void returnStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST returnStmt_AST = null;
		
		try {      // for error handling
			AST tmp106_AST = null;
			tmp106_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp106_AST);
			match(RETURNSTR);
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			returnStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = returnStmt_AST;
	}
	
	public final void forstatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forstatement_AST = null;
		
		try {      // for error handling
			AST tmp108_AST = null;
			tmp108_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp108_AST);
			match(FORSTR);
			forLeft();
			astFactory.addASTChild(currentAST, returnAST);
			forMid();
			astFactory.addASTChild(currentAST, returnAST);
			forRight();
			astFactory.addASTChild(currentAST, returnAST);
			forBody();
			astFactory.addASTChild(currentAST, returnAST);
			forstatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = forstatement_AST;
	}
	
	public final void waitStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST waitStmt_AST = null;
		
		try {      // for error handling
			AST tmp109_AST = null;
			tmp109_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp109_AST);
			match(WAIT);
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			waitStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = waitStmt_AST;
	}
	
	public final void printStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST printStmt_AST = null;
		
		try {      // for error handling
			AST tmp111_AST = null;
			tmp111_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp111_AST);
			match(PRINT);
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			printStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = printStmt_AST;
	}
	
	public final void eval() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST eval_AST = null;
		
		try {      // for error handling
			evalTerm();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop143:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					{
					switch ( LA(1)) {
					case PLUS:
					{
						AST tmp113_AST = null;
						tmp113_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp113_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						AST tmp114_AST = null;
						tmp114_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp114_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					}
					evalTerm();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop143;
				}
				
			} while (true);
			}
			eval_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = eval_AST;
	}
	
	public final void forLeft() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forLeft_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			{
				declaration();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				evaluate();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			forLeft_AST = (AST)currentAST.root;
			forLeft_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORLEFT,"FORLEFT")).add(forLeft_AST));
			currentAST.root = forLeft_AST;
			currentAST.child = forLeft_AST!=null &&forLeft_AST.getFirstChild()!=null ?
				forLeft_AST.getFirstChild() : forLeft_AST;
			currentAST.advanceChildToEnd();
			forLeft_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = forLeft_AST;
	}
	
	public final void forMid() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forMid_AST = null;
		
		try {      // for error handling
			match(SEMICOLON);
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			forMid_AST = (AST)currentAST.root;
			forMid_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORMID,"FORMID")).add(forMid_AST));
			currentAST.root = forMid_AST;
			currentAST.child = forMid_AST!=null &&forMid_AST.getFirstChild()!=null ?
				forMid_AST.getFirstChild() : forMid_AST;
			currentAST.advanceChildToEnd();
			forMid_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = forMid_AST;
	}
	
	public final void forRight() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forRight_AST = null;
		
		try {      // for error handling
			match(SEMICOLON);
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTPAREN);
			forRight_AST = (AST)currentAST.root;
			forRight_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORRIGHT,"FORRIGHT")).add(forRight_AST));
			currentAST.root = forRight_AST;
			currentAST.child = forRight_AST!=null &&forRight_AST.getFirstChild()!=null ?
				forRight_AST.getFirstChild() : forRight_AST;
			currentAST.advanceChildToEnd();
			forRight_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = forRight_AST;
	}
	
	public final void forBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forBody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			forBody_AST = (AST)currentAST.root;
			forBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORBODY,"FORBODY")).add(forBody_AST));
			currentAST.root = forBody_AST;
			currentAST.child = forBody_AST!=null &&forBody_AST.getFirstChild()!=null ?
				forBody_AST.getFirstChild() : forBody_AST;
			currentAST.advanceChildToEnd();
			forBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = forBody_AST;
	}
	
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_AST = null;
		
		try {      // for error handling
			type();
			astFactory.addASTChild(currentAST, returnAST);
			id();
			astFactory.addASTChild(currentAST, returnAST);
			declareType();
			astFactory.addASTChild(currentAST, returnAST);
			declaration_AST = (AST)currentAST.root;
			declaration_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(VARIABLE,"VARIABLE")).add(declaration_AST));
			currentAST.root = declaration_AST;
			currentAST.child = declaration_AST!=null &&declaration_AST.getFirstChild()!=null ?
				declaration_AST.getFirstChild() : declaration_AST;
			currentAST.advanceChildToEnd();
			declaration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = declaration_AST;
	}
	
	public final void evaluate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evaluate_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQUAL:
			{
				match(EQUAL);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				evaluate_AST = (AST)currentAST.root;
				evaluate_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGNMENT,"ASSIGNMENT")).add(evaluate_AST));
				currentAST.root = evaluate_AST;
				currentAST.child = evaluate_AST!=null &&evaluate_AST.getFirstChild()!=null ?
					evaluate_AST.getFirstChild() : evaluate_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case SEMICOLON:
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			evaluate_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = evaluate_AST;
	}
	
	public final void expressionStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expressionStmt_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTPAREN);
			expressionStmt_AST = (AST)currentAST.root;
			expressionStmt_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONDITION,"CONDITION")).add(expressionStmt_AST));
			currentAST.root = expressionStmt_AST;
			currentAST.child = expressionStmt_AST!=null &&expressionStmt_AST.getFirstChild()!=null ?
				expressionStmt_AST.getFirstChild() : expressionStmt_AST;
			currentAST.advanceChildToEnd();
			expressionStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = expressionStmt_AST;
	}
	
	public final void ifStmtBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifStmtBody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmtBody_AST = (AST)currentAST.root;
			ifStmtBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(THEN,"THEN")).add(ifStmtBody_AST));
			currentAST.root = ifStmtBody_AST;
			currentAST.child = ifStmtBody_AST!=null &&ifStmtBody_AST.getFirstChild()!=null ?
				ifStmtBody_AST.getFirstChild() : ifStmtBody_AST;
			currentAST.advanceChildToEnd();
			ifStmtBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = ifStmtBody_AST;
	}
	
	public final void elseStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseStmt_AST = null;
		
		try {      // for error handling
			{
			if ((LA(1)==ELSE) && (_tokenSet_7.member(LA(2)))) {
				AST tmp122_AST = null;
				tmp122_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp122_AST);
				match(ELSE);
				bracestatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_13.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			elseStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = elseStmt_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			compare();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop129:
			do {
				if ((LA(1)==OR)) {
					AST tmp123_AST = null;
					tmp123_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp123_AST);
					match(OR);
					compare();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop129;
				}
				
			} while (true);
			}
			expression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = expression_AST;
	}
	
	public final void id() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST id_AST = null;
		
		try {      // for error handling
			AST tmp124_AST = null;
			tmp124_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp124_AST);
			match(ID);
			index();
			astFactory.addASTChild(currentAST, returnAST);
			id_AST = (AST)currentAST.root;
			id_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(IDENTIFIER,"IDENTIFIER")).add(id_AST));
			currentAST.root = id_AST;
			currentAST.child = id_AST!=null &&id_AST.getFirstChild()!=null ?
				id_AST.getFirstChild() : id_AST;
			currentAST.advanceChildToEnd();
			id_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = id_AST;
	}
	
	public final void declareType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declareType_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case EQUAL:
			{
				assignValue();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMA:
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop121:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					id();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case EQUAL:
					{
						assignValue();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case COMMA:
					case SEMICOLON:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop121;
				}
				
			} while (true);
			}
			declareType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = declareType_AST;
	}
	
	public final void caObj() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caObj_AST = null;
		
		try {      // for error handling
			AST tmp126_AST = null;
			tmp126_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp126_AST);
			match(ID);
			match(PERIOD);
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp128_AST);
			match(ID);
			{
			switch ( LA(1)) {
			case LEFTPAREN:
			{
				argList();
				astFactory.addASTChild(currentAST, returnAST);
				caObj_AST = (AST)currentAST.root;
				caObj_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_CALL,"OBJECT_CALL")).add(caObj_AST));
				currentAST.root = caObj_AST;
				currentAST.child = caObj_AST!=null &&caObj_AST.getFirstChild()!=null ?
					caObj_AST.getFirstChild() : caObj_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				caObj_AST = (AST)currentAST.root;
				caObj_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_VALUE,"OBJECT_VALUE")).add(caObj_AST));
				currentAST.root = caObj_AST;
				currentAST.child = caObj_AST!=null &&caObj_AST.getFirstChild()!=null ?
					caObj_AST.getFirstChild() : caObj_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			caObj_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = caObj_AST;
	}
	
	public final void argList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				}
				{
				_loop160:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						expression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop160;
					}
					
				} while (true);
				}
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RIGHTPAREN);
			argList_AST = (AST)currentAST.root;
			argList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGS,"ARGS")).add(argList_AST));
			currentAST.root = argList_AST;
			currentAST.child = argList_AST!=null &&argList_AST.getFirstChild()!=null ?
				argList_AST.getFirstChild() : argList_AST;
			currentAST.advanceChildToEnd();
			argList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = argList_AST;
	}
	
	public final void caValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caValue_AST = null;
		
		try {      // for error handling
			AST tmp132_AST = null;
			tmp132_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp132_AST);
			match(ID);
			match(PERIOD);
			AST tmp134_AST = null;
			tmp134_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp134_AST);
			match(ID);
			caValue_AST = (AST)currentAST.root;
			caValue_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_VALUE,"OBJECT_VALUE")).add(caValue_AST));
			currentAST.root = caValue_AST;
			currentAST.child = caValue_AST!=null &&caValue_AST.getFirstChild()!=null ?
				caValue_AST.getFirstChild() : caValue_AST;
			currentAST.advanceChildToEnd();
			caValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = caValue_AST;
	}
	
	public final void caCall() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caCall_AST = null;
		
		try {      // for error handling
			AST tmp135_AST = null;
			tmp135_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp135_AST);
			match(ID);
			match(PERIOD);
			AST tmp137_AST = null;
			tmp137_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp137_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			caCall_AST = (AST)currentAST.root;
			caCall_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_CALL,"OBJECT_CALL")).add(caCall_AST));
			currentAST.root = caCall_AST;
			currentAST.child = caCall_AST!=null &&caCall_AST.getFirstChild()!=null ?
				caCall_AST.getFirstChild() : caCall_AST;
			currentAST.advanceChildToEnd();
			caCall_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = caCall_AST;
	}
	
	public final void index() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST index_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				indexValue();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				indexTail();
				astFactory.addASTChild(currentAST, returnAST);
				index_AST = (AST)currentAST.root;
				index_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INDEX,"INDEX")).add(index_AST));
				currentAST.root = index_AST;
				currentAST.child = index_AST!=null &&index_AST.getFirstChild()!=null ?
					index_AST.getFirstChild() : index_AST;
				currentAST.advanceChildToEnd();
				index_AST = (AST)currentAST.root;
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				index_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = index_AST;
	}
	
	public final void indexValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexValue_AST = null;
		
		try {      // for error handling
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			indexValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		returnAST = indexValue_AST;
	}
	
	public final void indexTail() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexTail_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				indexValue();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			indexTail_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = indexTail_AST;
	}
	
	public final void assignValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignValue_AST = null;
		
		try {      // for error handling
			AST tmp142_AST = null;
			tmp142_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp142_AST);
			match(EQUAL);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			assignValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = assignValue_AST;
	}
	
	public final void compare() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compare_AST = null;
		
		try {      // for error handling
			inverse();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop132:
			do {
				if ((LA(1)==AND)) {
					AST tmp143_AST = null;
					tmp143_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp143_AST);
					match(AND);
					inverse();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop132;
				}
				
			} while (true);
			}
			compare_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		returnAST = compare_AST;
	}
	
	public final void inverse() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inverse_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case NOT:
			{
				AST tmp144_AST = null;
				tmp144_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp144_AST);
				match(NOT);
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			compareTo();
			astFactory.addASTChild(currentAST, returnAST);
			inverse_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = inverse_AST;
	}
	
	public final void compareTo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compareTo_AST = null;
		
		try {      // for error handling
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop138:
			do {
				if ((_tokenSet_21.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case LESS:
					{
						AST tmp145_AST = null;
						tmp145_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp145_AST);
						match(LESS);
						break;
					}
					case LESSEQUAL:
					{
						AST tmp146_AST = null;
						tmp146_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp146_AST);
						match(LESSEQUAL);
						break;
					}
					case MORE:
					{
						AST tmp147_AST = null;
						tmp147_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp147_AST);
						match(MORE);
						break;
					}
					case MOREEQUAL:
					{
						AST tmp148_AST = null;
						tmp148_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp148_AST);
						match(MOREEQUAL);
						break;
					}
					case EQUALTO:
					{
						AST tmp149_AST = null;
						tmp149_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp149_AST);
						match(EQUALTO);
						break;
					}
					case NOTEQUAL:
					{
						AST tmp150_AST = null;
						tmp150_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp150_AST);
						match(NOTEQUAL);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					eval();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop138;
				}
				
			} while (true);
			}
			compareTo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = compareTo_AST;
	}
	
	public final void evalTerm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evalTerm_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case ID:
			case String:
			{
				negate();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop147:
				do {
					if (((LA(1) >= SLASH && LA(1) <= TIMES))) {
						{
						switch ( LA(1)) {
						case TIMES:
						{
							AST tmp151_AST = null;
							tmp151_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp151_AST);
							match(TIMES);
							break;
						}
						case SLASH:
						{
							AST tmp152_AST = null;
							tmp152_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp152_AST);
							match(SLASH);
							break;
						}
						case MODULO:
						{
							AST tmp153_AST = null;
							tmp153_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp153_AST);
							match(MODULO);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						negate();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop147;
					}
					
				} while (true);
				}
				evalTerm_AST = (AST)currentAST.root;
				break;
			}
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				negate();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				evalTerm_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = evalTerm_AST;
	}
	
	public final void negate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negate_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				{
				match(MINUS);
				afteratom();
				astFactory.addASTChild(currentAST, returnAST);
				negate_AST = (AST)currentAST.root;
				negate_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NEGATION,"NEGATION")).add(negate_AST));
				currentAST.root = negate_AST;
				currentAST.child = negate_AST!=null &&negate_AST.getFirstChild()!=null ?
					negate_AST.getFirstChild() : negate_AST;
				currentAST.advanceChildToEnd();
				}
				negate_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case ID:
			case String:
			{
				afteratom();
				astFactory.addASTChild(currentAST, returnAST);
				negate_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = negate_AST;
	}
	
	public final void afteratom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST afteratom_AST = null;
		
		try {      // for error handling
			beforeatom();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case PLUSPLUS:
			{
				match(PLUSPLUS);
				afteratom_AST = (AST)currentAST.root;
				afteratom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INCAFTER,"INCAFTER")).add(afteratom_AST));
				currentAST.root = afteratom_AST;
				currentAST.child = afteratom_AST!=null &&afteratom_AST.getFirstChild()!=null ?
					afteratom_AST.getFirstChild() : afteratom_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case MINUSMINUS:
			{
				match(MINUSMINUS);
				afteratom_AST = (AST)currentAST.root;
				afteratom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DECAFTER,"DECAFTER")).add(afteratom_AST));
				currentAST.root = afteratom_AST;
				currentAST.child = afteratom_AST!=null &&afteratom_AST.getFirstChild()!=null ?
					afteratom_AST.getFirstChild() : afteratom_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			afteratom_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = afteratom_AST;
	}
	
	public final void beforeatom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST beforeatom_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PLUSPLUS:
			{
				match(PLUSPLUS);
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				beforeatom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INCBEFORE,"INCBEFORE")).add(beforeatom_AST));
				currentAST.root = beforeatom_AST;
				currentAST.child = beforeatom_AST!=null &&beforeatom_AST.getFirstChild()!=null ?
					beforeatom_AST.getFirstChild() : beforeatom_AST;
				currentAST.advanceChildToEnd();
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			case MINUSMINUS:
			{
				match(MINUSMINUS);
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				beforeatom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DECBEFORE,"DECBEFORE")).add(beforeatom_AST));
				currentAST.root = beforeatom_AST;
				currentAST.child = beforeatom_AST!=null &&beforeatom_AST.getFirstChild()!=null ?
					beforeatom_AST.getFirstChild() : beforeatom_AST;
				currentAST.advanceChildToEnd();
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case LEFTPAREN:
			case ID:
			case String:
			{
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = beforeatom_AST;
	}
	
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			case NUM_FLOAT:
			{
				numberValue();
				astFactory.addASTChild(currentAST, returnAST);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case String:
			{
				AST tmp161_AST = null;
				tmp161_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp161_AST);
				match(String);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp162_AST = null;
				tmp162_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp162_AST);
				match(TRUE);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp163_AST = null;
				tmp163_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp163_AST);
				match(FALSE);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case LEFTPAREN:
			{
				match(LEFTPAREN);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTPAREN);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case NEW:
			{
				newatom();
				astFactory.addASTChild(currentAST, returnAST);
				atom_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==ID) && (_tokenSet_24.member(LA(2)))) {
					id();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==PERIOD)) {
					caObj();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==LEFTPAREN)) {
					callFunction();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = atom_AST;
	}
	
	public final void callFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST callFunction_AST = null;
		
		try {      // for error handling
			AST tmp166_AST = null;
			tmp166_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp166_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			callFunction_AST = (AST)currentAST.root;
			callFunction_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION_CALL,"FUNCTION_CALL")).add(callFunction_AST));
			currentAST.root = callFunction_AST;
			currentAST.child = callFunction_AST!=null &&callFunction_AST.getFirstChild()!=null ?
				callFunction_AST.getFirstChild() : callFunction_AST;
			currentAST.advanceChildToEnd();
			callFunction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = callFunction_AST;
	}
	
	public final void numberValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numberValue_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			{
				AST tmp167_AST = null;
				tmp167_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp167_AST);
				match(NUM_INT);
				numberValue_AST = (AST)currentAST.root;
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp168_AST = null;
				tmp168_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp168_AST);
				match(NUM_FLOAT);
				numberValue_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = numberValue_AST;
	}
	
	public final void newatom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST newatom_AST = null;
		
		try {      // for error handling
			AST tmp169_AST = null;
			tmp169_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp169_AST);
			match(NEW);
			AST tmp170_AST = null;
			tmp170_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp170_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			newatom_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = newatom_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"int\"",
		"\"float\"",
		"\"bool\"",
		"\"string\"",
		"\"module\"",
		"\"if\"",
		"\"else\"",
		"\"while\"",
		"\"for\"",
		"\"return\"",
		"\"true\"",
		"\"false\"",
		"\"public\"",
		"\"function\"",
		"\"break\"",
		"\"continue\"",
		"\"let\"",
		"\"void\"",
		"\"wait\"",
		"\"new\"",
		"\"print\"",
		"NUM_INT",
		"NUM_FLOAT",
		"PERIOD",
		"COMMA",
		"COLON",
		"SEMICOLON",
		"POUND",
		"PLUS",
		"MINUS",
		"SLASH",
		"MODULO",
		"TIMES",
		"LESS",
		"LESSEQUAL",
		"MORE",
		"MOREEQUAL",
		"EQUAL",
		"EQUALTO",
		"NOT",
		"OR",
		"AND",
		"NOTEQUAL",
		"PLUSPLUS",
		"MINUSMINUS",
		"LEFTPAREN",
		"RIGHTPAREN",
		"LEFTBRACKET",
		"RIGHTBRACKET",
		"LEFTBRACE",
		"RIGHTBRACE",
		"LETTER",
		"DIGIT",
		"ID",
		"Number",
		"Exponent",
		"ESC",
		"String",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"PROG",
		"PUBLICVAR",
		"FUNCTION",
		"VARIABLE",
		"TYPE",
		"INDEX",
		"NULL",
		"ASSIGNVALUE",
		"ARGS",
		"OBJECT_VALUE",
		"OBJECT_CALL",
		"NEGATION",
		"CALL_FUNCTION",
		"ASSIGNMENT",
		"RETURN_VAL",
		"THEN",
		"FUNCTION_CALL",
		"CONDITION",
		"MODULECALL",
		"FORLEFT",
		"FORMID",
		"FORRIGHT",
		"FORBODY",
		"INCAFTER",
		"INCBEFORE",
		"DECAFTER",
		"DECBEFORE",
		"DECLARETYPE",
		"FUNCTIONBODY",
		"ARGDEC",
		"STATEMENTS",
		"IDENTIFIER"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2097648L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2163184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 2097650L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 2471218362847199218L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 146366987889541120L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 144115188075855872L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 2462211163590294512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 2453203964335553520L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2471218362847133682L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 5761304832770048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 1073741824L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1125900980584448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 2480357491820265458L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 1128100272275456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 6183650441822208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 6183650441822210L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 4503599627370496L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 1342177280L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 1145692458319872L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 1180876830408704L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 76828374990848L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 5761317717671936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 5761437976756224L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 8435450255507456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	
	}
