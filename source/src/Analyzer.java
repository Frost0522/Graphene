package src;
import java.util.ArrayList;
import java.util.Stack;

public class Analyzer extends Throwable {

    public Analyzer(String msg) {
        System.err.println(msg); System.exit(0);
    }

    private void _defaultMsg(int line, int column, String topOfStack, Token errorToken, StringBuilder errorName) throws Analyzer {
        if (errorToken.getType().equals(Lex.ID) || errorToken.getType().equals(Lex.FNCALL) || 
            errorToken.getType().equals(Lex.INTEGERLITERAL) || errorToken.getType().equals(Lex.BOOLEANLITERAL)) {
            throw new Analyzer("Line "+line+" Column "+column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                               " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.\n");
        }
        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                           " encountered unexpected token '"+errorName+"'.\n");
    }

    private static boolean listContains(Lex type,Lex[] list) {
        for (Lex item : list) {if (type==item) {return true;}}
        return false;
    }
    
    // Error handling for parsing rules.
    @SuppressWarnings("incomplete-switch")
    public Analyzer(ArrayList<Token> tokenList, ArrayList<Token> deadLst, Stack<Lex> stack) throws Analyzer {

        String topOfStack = stack.peek().toString().toLowerCase();
        Token errorToken = tokenList.remove(0);
        String errorTokenTypeName = errorToken.getType().toString().toLowerCase();
        Lex errorTokenType = errorToken.getType();
        StringBuilder errorName = new StringBuilder();
        for (int i : errorToken.getCharList()) {errorName.append((char) i);}

        switch (stack.peek()) {
            case DEFINITIONLIST:
                if (listContains(errorTokenType,Lex.definitionListError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case DEFINITION:
                if (listContains(errorTokenType,Lex.definitionError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PARAMLIST:
                if (errorToken.getType().equals(Lex.RETURN)) {
                    errorToken = deadLst.get(deadLst.size()-1);
                    throw new Analyzer("Line "+errorToken.line+" Column "+(errorToken.column+errorToken.getSize())+"\nParsing Error: Missing right parenthesis.\n");
                }
                else if (listContains(errorTokenType,Lex.parameterListError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case ARGLIST:
                if (listContains(errorTokenType,Lex.arugmentListError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case BODY:
                if (errorToken.getType().equals(Lex.$)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Function body not found.\n");
                }
                else if (listContains(errorTokenType,Lex.isOperator())) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected operator '"+errorName+"'.\n");
                }
                else if (listContains(errorTokenType,Lex.bodyError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case EXP:
                if (listContains(errorTokenType,Lex.isOperator())) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected operator '"+errorName+"'.\n");
                } 
                else if (listContains(errorTokenType,Lex.expressionError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case EXPTAIL:
                if (listContains(errorTokenType,Lex.expressionTailError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FACTOR:
                if (listContains(errorTokenType,Lex.factorError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALARGTAIL:
                if (listContains(errorTokenType,Lex.formalArgsTailError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALARG:
                if (listContains(errorTokenType,Lex.formalArgsError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALPARAM:
                if (listContains(errorTokenType,Lex.formalParamError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALPARAMTAIL:
                if (listContains(errorTokenType,Lex.formalParamTailError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                }
                break;

            case IDWITHTYPE:
                if (listContains(errorTokenType,Lex.idWithTypeError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case LITERAL:
                if (listContains(errorTokenType,Lex.literalError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PRINTEXP:
                if (listContains(errorTokenType,Lex.printExpError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PROGRAM:
                if (listContains(errorTokenType,Lex.programError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case SIMPLEEXP:
                if (listContains(errorTokenType,Lex.simpleExpError())) {
                    if (errorToken.getType().equals(Lex.$)) {
                        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case SIMPLEEXPTAIL:
                if (listContains(errorTokenType,Lex.simpleExpTailError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TERM:
                if (listContains(errorTokenType,Lex.termError())) {
                    if (errorToken.getType().equals(Lex.$)) {
                        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TERMTAIL:
                if (listContains(errorTokenType,Lex.termTailError())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TYPE:
                if (listContains(errorTokenType,Lex.typeError())) {
                    errorToken = deadLst.get(deadLst.size()-1);
                    throw new Analyzer("Line "+errorToken.line+" Column "+(errorToken.column+errorToken.getSize())+
                                       "\nParsing Error: Missing function return type.\n");
                } break;

            case ID: 
                if (errorToken.getType().equals(Lex.LEFTPAREN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing function name.\n");
                } break;

            case RETURN:
                if (errorToken.getType().equals(Lex.INTEGER) || errorToken.getType().equals(Lex.BOOLEAN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing return symbol.\n");                    
                } break;

            case LEFTPAREN:
                if (errorToken.getType().equals(Lex.RIGHTPAREN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing left parenthesis.\n");
                }
                else if (listContains(errorTokenType,Lex.isOperator())) {
                    StringBuilder newErrorName = new StringBuilder(); 
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {newErrorName.append((char) i);}
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Keyword '"+newErrorName+
                                       "' encountered unexpected token '"+errorTokenTypeName+"'.\n");
                } break;

            case MINUS:
                StringBuilder lastTokenName = new StringBuilder();
                for (int i : deadLst.get(deadLst.size()-1).getCharList()) {lastTokenName.append((char) i);}
                if (errorToken.getType().equals(Lex.MINUS) && deadLst.get(deadLst.size() - 1).getType().equals(Lex.MINUS)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Last token '"+lastTokenName+
                                       "' encountered illegal next token '"+errorName+"'.");
                } break;
        }
        if (errorToken.getType().equals(Lex.$)) {
            throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                               " encountered unexpected end of file.\n");
        }
        else {
            _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
        }
    }

    // Error handling for the semantic analyzer.
    @SuppressWarnings("incomplete-switch")
    public Analyzer(Lex errorType, Node node) throws Analyzer {
        int line = node.position()[0]; int column = node.position()[1];
        switch (errorType) {
            case PRIMITIVEFN: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Functions must not have "+
                                   "primitive name 'print'.");
            }
            case PRIMITIVEPARAM: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Parameters must not have "+
                                   "primitive name 'print'.");
            }
            case NOMAIN: {
                throw new Analyzer("Semantic Error: No main function was declared.");
            }
            case INTOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Booleans must not be used in an integer operations.");
            }
            case BOOLOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Integers must not be used in a boolean operations.");
            }
            case PRIMITIVEBINARY: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Primitive "+node.getName()+" must not be used in binary expressions.");
            }
            case PRIMITIVEUNARY: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Primitive "+node.getName()+" must not be used in unary expressions.");
            }
            case PRIMITIVEARG: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Primitive "+node.getName()+" must not be passed as an argument.");
            }
            case NOID: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Unassigned "+node.getErrorStr()+", check that the"+
                                   " parameter has been declared.");
            }
            case RETURNTYPEERROR: {
                if (node.nodeType()==Lex.ID && node.getSemanticType()==null) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Unassigned "+node.getErrorStr()+", check that the"+
                                       " parameter has been declared.");
                }
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Type mismatch between function return type and function body.");
            }
            case NOFNCALL: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Unassigned "+node.getErrorStr()+", check that the function "+
                                   "has been declared.");
            }
            case TOOFEWARGS: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: "+node.getErrorStr().substring(0,1).toUpperCase()+
                                   node.getErrorStr().substring(1)+" is missing arguments.");
            }
            case TOOMANYARGS: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: "+node.getErrorStr().substring(0,1).toUpperCase()+
                                   node.getErrorStr().substring(1)+" has too many arguments.");
            }
            case MISSINGMAINARGS: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: The number of arguments given to main is less than the number of parameters declared.");
            }
            case EXCESSMAINARGS: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: The number of arguments given to main is more than the number of parameters declared.");
            }
            case BADARGTYPE: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Function call does not take postional argument "+
                                   node.getSemanticType().toString().toLowerCase()+".");
            }
            case IFOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: If conditions must be of type boolean.");
            }
            case DIFFCLAUSES: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Type mismatch between then and else clauses.");
            }
            case FNNAMECONFLICT: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Function '"+node.toString().replace("identifier ","")+
                                   "' has already been declared.");
            }
            case PARAMNAMECONFLICT: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Parameter '"+node.getName()+
                                   "' has already been declared in this function.");
            }
            case NOTOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Not operations must be of type boolean.");
            }
            case SIGNANDTYPEMISSMATCH: {
                if (node.getName().equals("print")) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Primitive 'print' must not be negative.");
                }
                if (node.nodeType()==Lex.FNCALL) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Function calls of type boolean must not be negative.");
                }
                if (node.nodeType()==Lex.EXP) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Expressions of type boolean must not be negative.");
                }
                if (node.nodeType()==Lex.ID) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Identifiers of type boolean must not be negative.");
                }
                if (node.nodeType()==Lex.LITERAL) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Boolean literals must not be negative.");
                }
            }
        }

        // Warning messages
        if (errorType==Lex.UNUSEDFN) {
            System.err.println("Warning @"+line+":"+column+" -> Function '"+node.getName()+"' is never used.");
        }
        else if (errorType==Lex.UNUSEDPARAM) {
            System.err.println("Warning @"+line+":"+column+" -> Parameter '"+node.toString().replace("identifier ","")+"' is never used.");
        }
        else {throw new Analyzer("Unexpected Semantic Error.");}
    }
}