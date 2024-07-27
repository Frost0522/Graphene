package src;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    public Stack<Node> nStack = new Stack<>();
    public ArrayList<Token> deadLst = new ArrayList<>();
    public Token lastToken;

    public Parser(Scanner scanner) {

        Stack<Lexicon> pStack = new Stack<Lexicon>();
        ArrayList<Token> tokenLst = scanner.tokenLst;

        pStack.push(Lexicon.$);
        pStack.push(Lexicon.MKPROGRAM);
        pStack.push(Lexicon.PROGRAM);
        
        Token nextToken = tokenLst.get(0);

        try {
            Boolean run = true;
            while (run) {
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

    public static Boolean terminal(Stack<Lexicon> stack) {
        Lexicon type = stack.peek();
        if (Lexicon.isTerminal().contains(type)) {
            return true;
        }
        return false;
    }

    private Token removeTerminal(Stack<Lexicon> pStack, ArrayList<Token> tokenLst, Token nextToken) throws Analyzer {

        while (terminal(pStack)) {
            if (!pStack.peek().equals(tokenLst.get(0).getType())) {new Analyzer(tokenLst, deadLst, pStack);}
            lastToken = tokenLst.get(0);
            nextToken = getNextToken(tokenLst);
            if (lastToken.getType()==Lexicon.MINUS && nextToken.getType()==Lexicon.MINUS) {new Analyzer(tokenLst, deadLst, pStack);}
            pStack.pop();
        }
        return nextToken;
    }

    private Boolean isDone(Stack<Lexicon> pStack, Token nextToken, Boolean run) {
        if (pStack.peek().equals(Lexicon.$) && nextToken.getType().equals(Lexicon.$)) {
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

    protected boolean compareList(int[] intLst, ArrayList<Integer>intArray) {        
        if (intLst.length==intArray.size()) {
            for (int i : intLst) {
                if (!intArray.contains(i)) {
                    return false;
                }
            } return true;
        } else return false;
    }

    private void parseTable(Stack<Lexicon> pStack, ArrayList<Token> tokenList) throws Analyzer {
        Lexicon nextType = tokenList.get(0).getType();
        switch (pStack.peek()) {
            case PROGRAM: {
                if (nextType == Lexicon.FN || nextType == Lexicon.$) {
                    pStack.pop(); pStack.push(Lexicon.DEFINITIONLIST); break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case DEFINITIONLIST: {
                if (nextType == Lexicon.FN) {
                    pStack.push(Lexicon.DEFINITION); break;
                }
                if (nextType == Lexicon.$) {pStack.pop(); break;}
                new Analyzer(tokenList, deadLst, pStack);
            }
            case DEFINITION: {
                if (nextType == Lexicon.FN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.definitionRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case PARAMLIST: {
                if (nextType == Lexicon.RIGHTPAREN) {
                    pStack.pop();
                    break;
                }
                if (nextType == Lexicon.ID && !compareList(Lexicon.getPrint(),tokenList.get(0).getCharList())) {
                    pStack.pop(); pStack.push(Lexicon.FORMALPARAM); break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case FORMALPARAM: {
                if (nextType == Lexicon.ID && !compareList(Lexicon.getPrint(),tokenList.get(0).getCharList())) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalParamRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case FORMALPARAMTAIL: {
                if (nextType == Lexicon.RIGHTPAREN) {
                    pStack.pop(); break;
                }
                if (nextType == Lexicon.COMMA) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalParamTailRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case IDWITHTYPE: {  
                if (nextType == Lexicon.ID) {
                    pStack.pop(); for (Lexicon rule : Lexicon.idWithTypeRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case TYPE: {
                if (nextType == Lexicon.INTEGER) {
                    pStack.pop(); pStack.push(Lexicon.INTEGER); break;
                }
                if (nextType == Lexicon.BOOLEAN) {
                    pStack.pop(); pStack.push(Lexicon.BOOLEAN); break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case BODY: {
                if (compareList(Lexicon.getPrint(),getCharList(tokenList))) {
                    pStack.push(Lexicon.PRINTEXP); break;
                }
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); pStack.push(Lexicon.EXP); break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case PRINTEXP: {
                if (compareList(Lexicon.getPrint(),getCharList(tokenList))) {
                    pStack.pop(); for (Lexicon rule : Lexicon.printExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case EXP: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.expRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case EXPTAIL: {
                if (Lexicon.isExpTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.EQUIVALENT) {
                    pStack.pop(); for (Lexicon rule : Lexicon.equivalentRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.LESSTHAN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.lessThanRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case SIMPLEEXP: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.simpleExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case SIMPLEEXPTAIL: {
                if (Lexicon.isSimpleExpTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.OR) {
                    pStack.pop(); for (Lexicon rule : Lexicon.orRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.PLUS) {
                    pStack.pop(); for (Lexicon rule : Lexicon.plusRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.MINUS) {
                    pStack.pop(); for (Lexicon rule : Lexicon.minusRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case TERM: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.termRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case TERMTAIL: {
                if (Lexicon.isTermTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.AND) {
                    pStack.pop(); for (Lexicon rule : Lexicon.andRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.TIMES) {
                    pStack.pop(); for (Lexicon rule : Lexicon.timesRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.DIVIDE) {
                    pStack.pop(); for (Lexicon rule : Lexicon.divideRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case FACTOR: {
                if (nextType == Lexicon.BOOLEANLITERAL || nextType == Lexicon.INTEGERLITERAL) {
                    pStack.pop(); pStack.push(Lexicon.LITERAL);
                    break;
                }
                if (nextType == Lexicon.LEFTPAREN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.leftParenFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.MINUS) {
                    pStack.pop(); for (Lexicon rule : Lexicon.minusFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.NOT) {
                    pStack.push(Lexicon.NOT); break;
                }
                if (nextType == Lexicon.IF) {
                    pStack.pop(); for (Lexicon rule : Lexicon.ifRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.ID) {
                    pStack.pop(); for (Lexicon rule : Lexicon.idRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case ARGLIST: {
                if (Lexicon.isArgList().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.LEFTPAREN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.leftParenArgRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case FORMALARG: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalArgRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.RIGHTPAREN) {pStack.pop(); break;}
                new Analyzer(tokenList, deadLst, pStack);
            }
            case FORMALARGTAIL: {
                if (nextType == Lexicon.RIGHTPAREN) {pStack.pop(); break;}
                if (nextType == Lexicon.COMMA) {
                    pStack.pop(); for (Lexicon rule : Lexicon.commaRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            case LITERAL: {
                if (nextType == Lexicon.BOOLEANLITERAL) {
                    pStack.pop(); for (Lexicon rule : Lexicon.booleanLitRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.INTEGERLITERAL) {
                    pStack.pop(); for (Lexicon rule : Lexicon.integerLitRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenList, deadLst, pStack);
            }
            default: {break;}
        }
    }

    private void semanticAction(Stack<Node> nStack, Stack<Lexicon> pStack, ArrayList<Token> tokenList) throws Analyzer {
        switch (pStack.lastElement()) {
            case MKID: {pStack.pop(); nStack.push(new Identifier(lastToken).accept(new MakeNode())); break;}
            case MKTYPE: {pStack.pop(); nStack.push(new Type(lastToken).accept(new MakeNode())); break;}
            case MKRETURNTYPE: {pStack.pop(); nStack.push(new ReturnType(lastToken).accept(new MakeNode())); break;}
            case MKPARAM: {pStack.pop(); nStack.push(new Parameter(nStack).accept(new MakeNode())); break;}
            case MKEQ: {pStack.pop(); nStack.push(new Equality(nStack).accept(new MakeNode())); break;}
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
            case MKARGS: {pStack.pop(); nStack.push(new Node()); break;}
            case MKIF: {pStack.pop(); nStack.push(new If(nStack).accept(new MakeNode())); break;}
            case MKEXP: {pStack.pop(); nStack.push(new Expression(nStack).accept(new MakeNode())); break;}
            case MKNEG: {pStack.pop(); Lexicon topNodeType = nStack.peek().valueOf();

                if (topNodeType==Lexicon.ID) {
                    if (compareList(Lexicon.getPrint(),nStack.peek().getId().getCharList())) {
                        throw new Analyzer(Lexicon.MINUS,Lexicon.ID,nStack.peek());
                    }
                    nStack.peek().getId().flipSymbol(); break;
                }
                if (topNodeType==Lexicon.FNCALL) {
                    if (compareList(Lexicon.getPrint(),nStack.peek().getFnCall().getId().getCharList())) {
                        throw new Analyzer(Lexicon.MINUS,Lexicon.FNCALL,nStack.peek());
                    }
                    nStack.peek().getFnCall().flipSymbol(); break;
                }
                if (topNodeType==Lexicon.IF) {
                    throw new Analyzer(Lexicon.MINUS,Lexicon.IF,nStack.peek());
                }
                if (topNodeType==Lexicon.EXP) {nStack.peek().getExp().flipSymbol(); break;}
                if (nStack.peek().getLiteral() instanceof Literal) {
                    if (nStack.peek().getLiteral().getType()==Lexicon.INTEGERLITERAL) {nStack.peek().getLiteral().flipSymbol(); break;}
                }
                throw new Analyzer(tokenList, deadLst, pStack);
            }
            default: {break;}
        }
    }
}
