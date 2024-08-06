package src;
import java.util.ArrayList;
import java.util.Stack;

public class Analyzer extends Exception {

    public Analyzer(String msg) {
        super(msg);
    }

    private void _defaultMsg(int line, int column, String topOfStack, Token errorToken, StringBuilder errorName) throws Analyzer {
        if (errorToken.getType().equals(Lexicon.ID) || errorToken.getType().equals(Lexicon.FNCALL) || 
            errorToken.getType().equals(Lexicon.INTEGERLITERAL) || errorToken.getType().equals(Lexicon.BOOLEANLITERAL)) {
            throw new Analyzer("Line "+line+" Column "+column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                               " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.\n");
        }
        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                           " encountered unexpected token '"+errorName+"'.\n");
    }
    
    // Error handling for parsing rules.
    public Analyzer(ArrayList<Token> tokenList, ArrayList<Token> deadLst, Stack<Lexicon> stack) throws Analyzer {

        String topOfStack = stack.peek().toString().toLowerCase();
        Token errorToken = tokenList.remove(0);
        String errorTokenType = errorToken.getType().toString().toLowerCase();
        StringBuilder errorName = new StringBuilder();
        for (int i : errorToken.getCharList()) {errorName.append((char) i);}

        // for (Token t : deadLst) {System.out.println(t.getType());}
        // System.out.println("Error token -> "+errorTokenType+"\nTop of stack -> "+stack.peek()+"\n");

        switch (stack.peek()) {
            case DEFINITIONLIST:
                if (Lexicon.definitionListError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case DEFINITION:
                if (Lexicon.definitionError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PARAMLIST:
                if (errorToken.getType().equals(Lexicon.RETURN)) {
                    errorToken = deadLst.get(deadLst.size()-1);
                    throw new Analyzer("Line "+errorToken.line+" Column "+(errorToken.column+errorToken.getSize())+"\nParsing Error: Missing right parenthesis.\n");
                }
                else if (Lexicon.parameterListError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case ARGLIST:
                if (Lexicon.arugmentListError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case BODY:
                if (errorToken.getType().equals(Lexicon.$)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Function body not found.\n");
                }
                else if (Lexicon.isOperator().contains(errorToken.getType())) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected operator '"+errorName+"'.\n");
                }
                else if (Lexicon.bodyError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case EXP:
                if (Lexicon.isOperator().contains(errorToken.getType())) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected operator '"+errorName+"'.\n");
                } 
                else if (Lexicon.expressionError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case EXPTAIL:
                if (Lexicon.expressionTailError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FACTOR:
                if (Lexicon.factorError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALARGTAIL:
                if (Lexicon.formalArgsTailError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALARG:
                if (Lexicon.formalArgsError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALPARAM:
                if (Lexicon.formalParamError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case FORMALPARAMTAIL:
                if (Lexicon.formalParamTailError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                }
                break;

            case IDWITHTYPE:
                if (Lexicon.idWithTypeError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case LITERAL:
                if (Lexicon.literalError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PRINTEXP:
                if (Lexicon.printExpError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case PROGRAM:
                if (Lexicon.programError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case SIMPLEEXP:
                if (Lexicon.simpleExpError().contains(errorToken.getType())) {
                    if (errorToken.getType().equals(Lexicon.$)) {
                        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case SIMPLEEXPTAIL:
                if (Lexicon.simpleExpTailError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TERM:
                if (Lexicon.termError().contains(errorToken.getType())) {
                    if (errorToken.getType().equals(Lexicon.$)) {
                        throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TERMTAIL:
                if (Lexicon.termTailError().contains(errorToken.getType())) {
                    _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
                } break;

            case TYPE:
                if (Lexicon.typeError().contains(errorToken.getType())) {
                    errorToken = deadLst.get(deadLst.size()-1);
                    throw new Analyzer("Line "+errorToken.line+" Column "+(errorToken.column+errorToken.getSize())+
                                       "\nParsing Error: Missing function return type.\n");
                } break;

            case ID: 
                if (errorToken.getType().equals(Lexicon.LEFTPAREN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing function name.\n");
                } break;

            case RETURN:
                if (errorToken.getType().equals(Lexicon.INTEGER) || errorToken.getType().equals(Lexicon.BOOLEAN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing return symbol.\n");                    
                } break;

            case LEFTPAREN:
                if (errorToken.getType().equals(Lexicon.RIGHTPAREN)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Missing left parenthesis.\n");
                }
                else if (Lexicon.isOperator().contains(errorToken.getType())) {
                    StringBuilder newErrorName = new StringBuilder(); 
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {newErrorName.append((char) i);}
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Keyword '"+newErrorName+
                                       "' encountered unexpected token '"+errorTokenType+"'.\n");
                } break;

            case MINUS:
                StringBuilder lastTokenName = new StringBuilder();
                for (int i : deadLst.get(deadLst.size()-1).getCharList()) {lastTokenName.append((char) i);}
                if (errorToken.getType().equals(Lexicon.MINUS) && deadLst.get(deadLst.size() - 1).getType().equals(Lexicon.MINUS)) {
                    throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Last token '"+lastTokenName+
                                       "' encountered illegal next token '"+errorName+"'.");
                }
        }
        if (errorToken.getType().equals(Lexicon.$)) {
            throw new Analyzer("Line "+errorToken.line+" Column "+errorToken.column+"\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                               " encountered unexpected end of file.\n");
        }
        else {
            _defaultMsg(errorToken.line, errorToken.column, topOfStack, errorToken, errorName);
        }
    }

    // Error handling for making nodes in the parser.
    public Analyzer(Lexicon opType, Lexicon errorType, Node errorNode) throws Analyzer {
        throw new Analyzer("Parsing error!"); 
    }

// Error handling for the semantic checker.
    public Analyzer(Lexicon errorType, Node node) throws Analyzer {
        int line = node.position()[0]; int column = node.position()[1];
        switch (errorType) {
            case PRIMITIVEFN: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Functions cannot have"+
                                   " the primitive name 'print'.");
            }
            case PRIMITIVEPARAM: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Parameters cannot have"+
                                   " the primitive name 'print'.");
            }
            case NOMAIN: {
                throw new Analyzer("Semantic Error: No main function was declared.");
            }
            case INTOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Incorrect operand type 'boolean'"+
                                   " used in integer operation.");
            }
            case BOOLOPERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Incorrect operand type 'integer'"+
                                   " used in boolean operation.");
            }
            case NULLOPERAND: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Unassigned identifier, check that the"+
                                   " parameter has been declared.");
            }
            case RETURNTYPEERROR: {
                throw new Analyzer("Line "+line+" Column "+column+"\nSemantic Error: Function return type does not match it's body.");
            }
        }
        throw new Analyzer("Unexpected Semantic Error.");
    }
}