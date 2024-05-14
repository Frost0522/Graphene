package src;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    public ArrayList<Lexical> subArray = new ArrayList<>();
    public Stack<Node> nStack = new Stack<>();
    public ArrayList<Token> deadLst = new ArrayList<>();
    public Token lastToken;
    public int line = 1;
    public int column = 1;

    public Parser(Scanner scanner) {

        Stack<Lexical> pStack = new Stack<Lexical>();
        ArrayList<Token> tokenLst = scanner.tokenLst;
        tokenLst = skipInitTokens(tokenLst);

        pStack.push(Lexical.$);
        pStack.push(Lexical.MKPROGRAM);
        pStack.push(Lexical.PROGRAM);
        
        Token nextToken = tokenLst.get(0);

        try {
            Boolean run = true;
            while (run) {

                // System.out.println("Top of stack -> "+pStack+"\nNext token type - > "+nextToken.getType().toString()+"\n");
                semanticAction(nStack, pStack, tokenLst);
                parseTable(pStack, tokenLst);
                nextToken = removeTerminal(pStack, tokenLst, nextToken);
                run = isDone(pStack, nextToken, run);
            }
        }
        catch (Analyzer err) {
            System.out.println(err.getMessage());
            System.exit(0);
        }
    }

    public static Boolean terminal(Stack<Lexical> stack) {
        Lexical type = stack.peek();
        if (Lexical.isTerminal().contains(type)) {
            return true;
        }
        return false;
    }

    private ArrayList<Token> skipInitTokens(ArrayList<Token> tokenLst) {
        while (tokenLst.get(0).getType().equals(Lexical.NEWLINE) || tokenLst.get(0).getType().equals(Lexical.SKIPPABLE)) {
            if (tokenLst.get(0).getType().equals(Lexical.NEWLINE)) {line++; column = 1; tokenLst.remove(0);}
            else if (tokenLst.get(0).getType().equals(Lexical.SKIPPABLE)) {column++; tokenLst.remove(0);}
        }
        return tokenLst;
    } 

    private Token removeTerminal(Stack<Lexical> pStack, ArrayList<Token> tokenLst, Token nextToken) throws Analyzer {

        while (terminal(pStack)) {
            // If at some point, while removing terminals, the types from the top of the parse stack and the token
            // at the front of the token list do not match, throw and error.
            if (!pStack.peek().equals(tokenLst.get(0).getType())) {new Analyzer(tokenLst, deadLst, pStack, line, column);}
            lastToken = tokenLst.get(0);
            nextToken = getNextToken(tokenLst);
            nextToken = skipToken(nextToken, tokenLst);
            // Check for improper use of operators, and keywords in boolean/integer operations.
            isOperator(nextToken, tokenLst, pStack);
            // Terminal FN will be removed, check next token is not identifier print, if so throw an error.
            isPrint(lastToken, nextToken, tokenLst, pStack);
            pStack.pop();
        }
        return nextToken;
    }

    private void isOperator(Token nextToken, ArrayList<Token> tokenLst, Stack<Lexical> pStack) throws Analyzer {
        if (lastToken.getType().equals(Lexical.MINUS) && nextToken.getType().equals(Lexical.MINUS)) {
            new Analyzer(tokenLst, deadLst, pStack, line, column);
        }
        else if (Lexical.isOperator().contains(lastToken.getType()) && Lexical.isOperator().contains(nextToken.getType())) {
            new Analyzer(tokenLst, deadLst, pStack, line, column);
        }
        else if (Lexical.isOperator().contains(lastToken.getType()) && nextToken.getCharList().equals(Lexical.getPrint())) {
            new Analyzer(tokenLst, deadLst, pStack, line, column);
        }
        else if (Lexical.isOperator().contains(lastToken.getType()) && nextToken.getCharList().equals(Lexical.getIf())) {
            new Analyzer(tokenLst, deadLst, pStack, line, column);
        }
    }

    private Boolean isDone(Stack<Lexical> pStack, Token nextToken, Boolean run) {
        if (pStack.peek().equals(Lexical.$) && nextToken.getType().equals(Lexical.$)) {
            return false;
        }
        return true;
    }

    private ArrayList<Integer> getCharList(ArrayList<Token> list) {
        return list.get(0).getCharList();
    }

    private Token getNextToken(ArrayList<Token> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        deadLst.add(list.remove(0));
        return list.get(0);
    }

    private Token skipToken(Token token, ArrayList<Token> list) {
        while (token.getType().equals(Lexical.NEWLINE) || token.getType().equals(Lexical.SKIPPABLE)) {
            token = getNextToken(list);
        }
        return token;
    }

    // Check for if print is being used in a function declaration.
    private void isPrint(Token prevToken, Token nextToken, ArrayList<Token> tokenLst, Stack<Lexical> pStack) throws Analyzer {
        if (prevToken.getType().equals(Lexical.FN) && nextToken.getCharList().equals(Lexical.getPrint())) {
            new Analyzer(tokenLst, deadLst, pStack, line, column);
        }
    }

    // Check for if print is being used improperly and we know at which parsing rule this applies.
    private Boolean notPrint(ArrayList<Token> nextToken) {
        if (nextToken.get(0).getCharList().equals(Lexical.getPrint())) {
            return false;
        }
        return true;
    }

    private void parseTable(Stack<Lexical> pStack, ArrayList<Token> tokenList) throws Analyzer {
        Lexical nextType = skipToken(tokenList.get(0), tokenList).getType();
        switch (pStack.peek()) {
            case PROGRAM: {
                if (nextType == Lexical.FN || nextType == Lexical.$) {
                    pStack.pop(); pStack.push(Lexical.DEFINITIONLIST); break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case DEFINITIONLIST: {
                if (nextType == Lexical.FN) {
                    pStack.push(Lexical.DEFINITION); break;
                }
                if (nextType == Lexical.$) {pStack.pop(); break;}
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case DEFINITION: {
                if (nextType == Lexical.FN) {
                    pStack.pop(); for (Lexical rule : Lexical.definitionRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case PARAMETERLIST: {
                if (nextType == Lexical.RIGHT_PAREN) {
                    pStack.pop();
                    break;
                }
                if (nextType == Lexical.IDENTIFIER && notPrint(tokenList)) {
                    pStack.pop(); pStack.push(Lexical.FORMALPARAMETERS); break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case FORMALPARAMETERS: {
                if (nextType == Lexical.IDENTIFIER && notPrint(tokenList)) {
                    pStack.pop(); for (Lexical rule : Lexical.formalParamRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case FORMALPARAMTAIL: {
                if (nextType == Lexical.RIGHT_PAREN) {
                    pStack.pop(); break;
                }
                if (nextType == Lexical.COMMA) {
                    pStack.pop(); for (Lexical rule : Lexical.formalParamTailRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case IDWITHTYPE: {  
                if (nextType == Lexical.IDENTIFIER) {
                    pStack.pop(); for (Lexical rule : Lexical.idWithTypeRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case TYPE: {
                if (nextType == Lexical.INTEGER) {
                    pStack.pop(); pStack.push(Lexical.INTEGER); break;
                }
                if (nextType == Lexical.BOOLEAN) {
                    pStack.pop(); pStack.push(Lexical.BOOLEAN); break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case BODY: {
                if (getCharList(tokenList).equals(Lexical.getPrint())) {
                    pStack.push(Lexical.PRINTEXPRESSION); break;
                }
                if (Lexical.isExpression().contains(nextType)) {
                    pStack.pop(); pStack.push(Lexical.EXPRESSION); break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case PRINTEXPRESSION: {
                if (getCharList(tokenList).equals(Lexical.getPrint())) {
                    pStack.pop(); for (Lexical rule : Lexical.printExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case EXPRESSION: {
                if (Lexical.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexical rule : Lexical.expRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case EXPRESSIONTAIL: {
                if (Lexical.isExpTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexical.EQUIVALENT) {
                    pStack.pop(); for (Lexical rule : Lexical.equivalentRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.LESS_THAN) {
                    pStack.pop(); for (Lexical rule : Lexical.lessThanRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case SIMPLEEXPRESSION: {
                if (Lexical.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexical rule : Lexical.simpleExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case SIMPLEEXPRESSIONTAIL: {
                if (Lexical.isSimpleExpTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexical.OR) {
                    pStack.pop(); for (Lexical rule : Lexical.orRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.PLUS) {
                    pStack.pop(); for (Lexical rule : Lexical.plusRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.MINUS) {
                    pStack.pop(); for (Lexical rule : Lexical.minusRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case TERM: {
                if (Lexical.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexical rule : Lexical.termRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case TERMTAIL: {
                if (Lexical.isTermTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexical.AND) {
                    pStack.pop(); for (Lexical rule : Lexical.andRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.TIMES) {
                    pStack.pop(); for (Lexical rule : Lexical.timesRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.DIVIDE) {
                    pStack.pop(); for (Lexical rule : Lexical.divideRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case FACTOR: {
                if (nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    pStack.pop(); pStack.push(Lexical.LITERAL);
                    break;
                }
                if (nextType == Lexical.LEFT_PAREN) {
                    pStack.pop(); for (Lexical rule : Lexical.leftParenFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.MINUS) {
                    pStack.pop(); for (Lexical rule : Lexical.minusFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.NOT) {
                    pStack.push(Lexical.NOT); break;
                }
                if (nextType == Lexical.IF) {
                    pStack.pop(); for (Lexical rule : Lexical.ifRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.IDENTIFIER) {
                    pStack.pop(); for (Lexical rule : Lexical.idRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case ARGUMENTLIST: {
                if (Lexical.isArgList().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexical.LEFT_PAREN) {
                    pStack.pop(); for (Lexical rule : Lexical.leftParenArgRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case FORMALARGUMENTS: {
                if (Lexical.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexical rule : Lexical.formalArgRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.RIGHT_PAREN) {pStack.pop(); break;}
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case FORMALARGSTAIL: {
                if (nextType == Lexical.RIGHT_PAREN) {pStack.pop(); break;}
                if (nextType == Lexical.COMMA) {
                    pStack.pop(); for (Lexical rule : Lexical.commaRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            case LITERAL: {
                if (nextType == Lexical.BOOLEAN_LITERAL) {
                    pStack.pop(); for (Lexical rule : Lexical.booleanLitRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexical.INTEGER_LITERAL) {
                    pStack.pop(); for (Lexical rule : Lexical.integerLitRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack, line, column);
            }
            default: {
                System.out.println("Next token type -> "+nextType+"\nTop of parse stack -> "+pStack.peek()+"\n");
                break;
            }
        }
    }

    private void semanticAction(Stack<Node> nStack, Stack<Lexical> pStack, ArrayList<Token> tokenList) throws Analyzer {
        switch (pStack.lastElement()) {
            case MKID: {pStack.pop(); nStack.push(new Identifier(lastToken).accept(new MakeNode())); break;}
            case MKTYPE: {pStack.pop(); nStack.push(new Type(lastToken).accept(new MakeNode())); break;}
            case MKRETURNTYPE: {pStack.pop(); nStack.push(new ReturnType(lastToken).accept(new MakeNode())); break;}
            case MKPARAM: {pStack.pop(); nStack.push(new Parameter(nStack).accept(new MakeNode())); break;}
            case MKEQUALITY: {pStack.pop(); nStack.push(new Equality(nStack).accept(new MakeNode())); break;}
            case MKMINUS: {pStack.pop(); nStack.push(new Minus(nStack).accept(new MakeNode()));  break;}
            case MKPLUS: {pStack.pop(); nStack.push(new Plus(nStack).accept(new MakeNode()));  break;}
            case MKDIVIDE: {pStack.pop(); nStack.push(new Divide(nStack).accept(new MakeNode())); break;}
            case MKTIMES: {pStack.pop(); nStack.push(new Times(nStack).accept(new MakeNode()));  break;}
            case MKAND: {pStack.pop(); nStack.push(new And(nStack).accept(new MakeNode())); break;}
            case MKOR: {pStack.pop(); nStack.push(new Or(nStack).accept(new MakeNode()));  break;}
            case MKLESSTHAN: {pStack.pop(); nStack.push(new LessThan(nStack).accept(new MakeNode()));  break;}
            case MKLIT: {pStack.pop(); nStack.push(new Literal(lastToken).accept(new MakeNode())); break;}
            case MKPROGRAM: {pStack.pop(); nStack.push(new Program(nStack).accept(new MakeNode())); break;}
            case MKFN: {pStack.pop(); nStack.push(new Function(nStack).accept(new MakeNode())); break;}
            case MKFNCALL: {pStack.pop(); nStack.push(new FunctionCall(nStack).accept(new MakeNode())); break;}
            case MKARGS: {pStack.pop(); {nStack.push(new Node());} break;}
            case MKIF: {pStack.pop(); nStack.push(new IfElse(nStack).accept(new MakeNode())); break;}
            case MKNEG: {pStack.pop();
                if (nStack.peek().valueOf().equals(Lexical.IDENTIFIER)) {
                    if (nStack.peek().getId().getCharList().equals(Lexical.getPrint())) {
                        new Analyzer(tokenList, deadLst, pStack, line, column);
                    }
                    nStack.peek().getId().flipSymbol();
                }
                else if (nStack.peek().valueOf().equals(Lexical.LITERAL)) {nStack.peek().getLiteral().flipSymbol();}
                else if (nStack.peek().valueOf().equals(Lexical.FNCALL)) {
                    if (nStack.peek().getFnCall().getId().getCharList().equals(Lexical.getPrint())) {
                        new Analyzer(tokenList, deadLst, pStack, line, column);
                    }
                    nStack.peek().getFnCall().flipSymbol();
                }
                break;
            }
            default: {break;}
        }
    }
}