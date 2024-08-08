package src;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    private ArrayList<Token> deadLst = new ArrayList<>();
    private Token lastToken;
    private Boolean run = true;
    private Token nextToken;
    private Stack<Lex> pStack;
    private ArrayList<Token> tokenLst;
    public Stack<Node> nStack = new Stack<>();

    public Parser(Scanner scanner) {

        pStack = new Stack<Lex>();
        tokenLst = scanner.tokenLst;
        nextToken = tokenLst.get(0);

        pStack.push(Lex.$);
        pStack.push(Lex.MKPROGRAM);
        pStack.push(Lex.PROGRAM);
    
        try {
            while (run) {
                semanticAction();
                parseTable();
                removeTerminal();
                isDone();
            }
        }
        catch (Analyzer err) {
            System.out.println(err.getMessage());
            System.exit(0);
        }
    }

    private void parseTable() throws Analyzer {
        Lex nextType = tokenLst.get(0).getType();
        switch (pStack.peek()) {
            case PROGRAM: {
                if (nextType == Lex.FN || nextType == Lex.$) {
                    pStack.pop(); pStack.push(Lex.DEFINITIONLIST); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case DEFINITIONLIST: {
                if (nextType == Lex.FN) {
                    pStack.push(Lex.DEFINITION); break;
                }
                if (nextType == Lex.$) {pStack.pop(); break;}
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case DEFINITION: {
                if (nextType == Lex.FN) {
                    pStack.pop(); for (Lex rule : Lex.definitionRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case PARAMLIST: {
                if (nextType == Lex.RIGHTPAREN) {
                    pStack.pop();
                    break;
                }
                if (nextType == Lex.ID) {
                    pStack.pop(); pStack.push(Lex.FORMALPARAM); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALPARAM: {
                if (nextType == Lex.ID) {
                    pStack.pop(); for (Lex rule : Lex.formalParamRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALPARAMTAIL: {
                if (nextType == Lex.RIGHTPAREN) {
                    pStack.pop(); break;
                }
                if (nextType == Lex.COMMA) {
                    pStack.pop(); for (Lex rule : Lex.formalParamTailRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case IDWITHTYPE: {  
                if (nextType == Lex.ID) {
                    pStack.pop(); for (Lex rule : Lex.idWithTypeRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case TYPE: {
                if (nextType == Lex.INTEGER) {
                    pStack.pop(); pStack.push(Lex.INTEGER); break;
                }
                if (nextType == Lex.BOOLEAN) {
                    pStack.pop(); pStack.push(Lex.BOOLEAN); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case BODY: {
                if (compareList(Lex.getPrint(),tokenLst.get(0).getCharList())) {
                    pStack.push(Lex.PRINTEXP); break;
                }
                if (listContains(nextType,Lex.isExpression())) {
                    pStack.pop(); pStack.push(Lex.EXP); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case PRINTEXP: {
                if (compareList(Lex.getPrint(),tokenLst.get(0).getCharList())) {
                    pStack.pop(); for (Lex rule : Lex.printExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case EXP: {
                if (listContains(nextType,Lex.isExpression())) {
                    pStack.pop(); for (Lex rule : Lex.expRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case EXPTAIL: {
                if (listContains(nextType,Lex.isExpTail())) {pStack.pop(); break;}
                if (nextType == Lex.EQUIVALENT) {
                    pStack.pop(); for (Lex rule : Lex.equivalentRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.LESSTHAN) {
                    pStack.pop(); for (Lex rule : Lex.lessThanRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case SIMPLEEXP: {
                if (listContains(nextType,Lex.isExpression())) {
                    pStack.pop(); for (Lex rule : Lex.simpleExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case SIMPLEEXPTAIL: {
                if (listContains(nextType,Lex.isSimpleExpTail())) {pStack.pop(); break;}
                if (nextType == Lex.OR) {
                    pStack.pop(); for (Lex rule : Lex.orRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.PLUS) {
                    pStack.pop(); for (Lex rule : Lex.plusRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.MINUS) {
                    pStack.pop(); for (Lex rule : Lex.minusRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case TERM: {
                if (listContains(nextType,Lex.isExpression())) {
                    pStack.pop(); for (Lex rule : Lex.termRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case TERMTAIL: {
                if (listContains(nextType,Lex.isTermTail())) {pStack.pop(); break;}
                if (nextType == Lex.AND) {
                    pStack.pop(); for (Lex rule : Lex.andRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.TIMES) {
                    pStack.pop(); for (Lex rule : Lex.timesRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.DIVIDE) {
                    pStack.pop(); for (Lex rule : Lex.divideRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FACTOR: {
                if (nextType == Lex.BOOLEANLITERAL || nextType == Lex.INTEGERLITERAL) {
                    pStack.pop(); pStack.push(Lex.LITERAL);
                    break;
                }
                if (nextType == Lex.LEFTPAREN) {
                    pStack.pop(); for (Lex rule : Lex.leftParenFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.MINUS) {
                    pStack.pop(); for (Lex rule : Lex.minusFactorRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.NOT) {
                    pStack.push(Lex.NOT); break;
                }
                if (nextType == Lex.IF) {
                    pStack.pop(); for (Lex rule : Lex.ifRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.ID) {
                    pStack.pop(); for (Lex rule : Lex.idRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case ARGLIST: {
                if (listContains(nextType,Lex.isArgList())) {pStack.pop(); break;}
                if (nextType == Lex.LEFTPAREN) {
                    pStack.pop(); for (Lex rule : Lex.leftParenArgRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALARG: {
                if (listContains(nextType,Lex.isExpression())) {
                    pStack.pop(); for (Lex rule : Lex.formalArgRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.RIGHTPAREN) {pStack.pop(); break;}
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALARGTAIL: {
                if (nextType == Lex.RIGHTPAREN) {pStack.pop(); break;}
                if (nextType == Lex.COMMA) {
                    pStack.pop(); for (Lex rule : Lex.commaRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case LITERAL: {
                if (nextType == Lex.BOOLEANLITERAL) {
                    pStack.pop(); for (Lex rule : Lex.booleanLitRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lex.INTEGERLITERAL) {
                    pStack.pop(); for (Lex rule : Lex.integerLitRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            default: {break;}
        }
    }

    public void semanticAction() {
        Lex semanticRule = pStack.peek();
        if (lastToken==null) {semanticRule=Lex.$;}
        switch (semanticRule) {
            case MKID: {pStack.pop(); nStack.push(new IdNode(lastToken)); break;}
            case MKTYPE: {pStack.pop(); nStack.push(new TypeNode(lastToken)); break;}
            case MKRETURNTYPE: {pStack.pop(); nStack.push(new ReturnNode(lastToken)); break;}
            case MKLIT: {pStack.pop(); nStack.push(new LitNode(lastToken)); break;}
            case MKPARAM: {pStack.pop(); nStack.push(new ParamNode(nStack)); break;}
            case MKEQ: {pStack.pop(); nStack.push(new EqNode(nStack)); break;}
            case MKMINUS: {pStack.pop(); nStack.push(new MinusNode(nStack)); break;}
            case MKPLUS: {pStack.pop(); nStack.push(new PlusNode(nStack)); break;}
            case MKDIVIDE: {pStack.pop(); nStack.push(new DivideNode(nStack)); break;}
            case MKTIMES: {pStack.pop(); nStack.push(new TimesNode(nStack)); break;}
            case MKAND: {pStack.pop(); nStack.push(new AndNode(nStack)); break;}
            case MKOR: {pStack.pop(); nStack.push(new OrNode(nStack)); break;}
            case MKLESSTHAN: {pStack.pop(); nStack.push(new LessNode(nStack)); break;}
            case MKPROGRAM: {pStack.pop(); nStack.push(new PrgrmNode(nStack)); break;}
            case MKFN: {pStack.pop(); nStack.push(new FnNode(nStack)); break;}
            case MKFNCALL: {pStack.pop(); nStack.push(new CallNode(nStack)); break;}
            case MKARGS: {pStack.pop(); nStack.push(new NullNode()); break;}
            case MKIF: {pStack.pop(); nStack.push(new IfNode(nStack)); break;}
            case MKEXP: {pStack.pop(); nStack.push(new ExpNode(nStack)); break;}
            case MKNEG: {pStack.pop(); break;}
            default: {break;}
        }
    }

    private Token removeTerminal() throws Analyzer {

        while (listContains(pStack.peek(),Lex.isTerminal())) {
            if (!pStack.peek().equals(tokenLst.get(0).getType())) {new Analyzer(tokenLst, deadLst, pStack);}
            lastToken = tokenLst.get(0);
            nextToken = getNextToken();
            if (lastToken.getType()==Lex.MINUS && nextToken.getType()==Lex.MINUS) {new Analyzer(tokenLst, deadLst, pStack);}
            pStack.pop();
        }
        return nextToken;
    }

    private void isDone() {
        if (nStack.empty() && nextToken.getType().equals(Lex.$)) {System.exit(0);}
        if (pStack.peek().equals(Lex.$) && nextToken.getType().equals(Lex.$)) {
            run = false;
        }
    }

    private static boolean listContains(Lex type,Lex[] list) {
        for (Lex item : list) {if (type==item) {return true;}}
        return false;
    }

    private Token getNextToken() {
        if (tokenLst.size() == 1) {
            return tokenLst.get(0);
        }
        deadLst.add(tokenLst.remove(0));
        return tokenLst.get(0);
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
}
