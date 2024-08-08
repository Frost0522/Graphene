package src;
import java.util.ArrayList;
import java.util.Stack;

public class Analyzer extends Exception {

    public Analyzer(String msg) {
        super(msg);
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
                }
        }
        if (errorToken.getType().equals(Lex.$)) {
            throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                               " encountered unexpected end of file.\n");
        }
        else {
            _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
        }
    }

    // Error handling for the semantic checker.
    public Analyzer(Lex errorType, Node node) throws Analyzer {
        int line = node.position()[0]; int column = node.position()[1];
        switch (errorType) {
            case PRIMITIVEFN: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Functions cannot have "+
                                   "primitive name 'print'.");
            }
            case PRIMITIVEPARAM: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Parameters cannot have "+
                                   "primitive name 'print'.");
            }
            case NOMAIN: {
                throw new Analyzer("Semantic Error: No main function was declared.");
            }
            case INTOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Boolean operand cannot be used in an integer operation.");
            }
            case BOOLOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Integer operand cannot be used in a boolean operation.");
            }
            case NULLOPERAND: {
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
        }
        throw new Analyzer("Unexpected Semantic Error.");
    }

    // Error handling for the semantic checker, special case for binary nodes.
    public Analyzer(Lex errorType, String op, Node childNode) throws Analyzer {
        int line = childNode.position()[0]; int column = childNode.position()[1];
        switch (errorType) {
            case DIFFOPERANDS: {
                if ((op=="+"||op=="-"||op=="/"||op=="*"||op=="<") && childNode.getSemanticType()==Lex.BOOLEAN) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Boolean operand cannot be used in an integer operation.");
                }
                else if ((op=="and"||op=="or") && childNode.getSemanticType()==Lex.INTEGER) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Integer operand cannot be used in a boolean operation.");
                }
                else if (childNode.getSemanticType()==Lex.INTEGER) {
                    throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Integer operand cannot equal boolean.");
                }
                else {throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Boolean operand cannot equal Integer.");}
            }
        }
        throw new Analyzer("Unexpected Semantic Error.");
    }
}