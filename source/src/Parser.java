package src;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    private ArrayList<Token> deadLst = new ArrayList<>();
    private Token lastToken;
    private Boolean run = true;
    private Token nextToken;
    private Stack<Lexicon> pStack;
    private ArrayList<Token> tokenLst;
    public Stack<Node> nStack = new Stack<>();

    public Parser(Scanner scanner) {

        pStack = new Stack<Lexicon>();
        tokenLst = scanner.tokenLst;
        nextToken = tokenLst.get(0);

        pStack.push(Lexicon.$);
        pStack.push(Lexicon.MKPROGRAM);
        pStack.push(Lexicon.PROGRAM);
    
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
        Lexicon nextType = tokenLst.get(0).getType();
        switch (pStack.peek()) {
            case PROGRAM: {
                if (nextType == Lexicon.FN || nextType == Lexicon.$) {
                    pStack.pop(); pStack.push(Lexicon.DEFINITIONLIST); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case DEFINITIONLIST: {
                if (nextType == Lexicon.FN) {
                    pStack.push(Lexicon.DEFINITION); break;
                }
                if (nextType == Lexicon.$) {pStack.pop(); break;}
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case DEFINITION: {
                if (nextType == Lexicon.FN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.definitionRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case PARAMLIST: {
                if (nextType == Lexicon.RIGHTPAREN) {
                    pStack.pop();
                    break;
                }
                if (nextType == Lexicon.ID && !compareList(Lexicon.getPrint(),getCharList())) {
                    pStack.pop(); pStack.push(Lexicon.FORMALPARAM); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALPARAM: {
                if (nextType == Lexicon.ID && !compareList(Lexicon.getPrint(),getCharList())) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalParamRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALPARAMTAIL: {
                if (nextType == Lexicon.RIGHTPAREN) {
                    pStack.pop(); break;
                }
                if (nextType == Lexicon.COMMA) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalParamTailRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case IDWITHTYPE: {  
                if (nextType == Lexicon.ID) {
                    pStack.pop(); for (Lexicon rule : Lexicon.idWithTypeRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case TYPE: {
                if (nextType == Lexicon.INTEGER) {
                    pStack.pop(); pStack.push(Lexicon.INTEGER); break;
                }
                if (nextType == Lexicon.BOOLEAN) {
                    pStack.pop(); pStack.push(Lexicon.BOOLEAN); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case BODY: {
                if (compareList(Lexicon.getPrint(),getCharList())) {
                    pStack.push(Lexicon.PRINTEXP); break;
                }
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); pStack.push(Lexicon.EXP); break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case PRINTEXP: {
                if (compareList(Lexicon.getPrint(),getCharList())) {
                    pStack.pop(); for (Lexicon rule : Lexicon.printExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case EXP: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.expRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case EXPTAIL: {
                if (Lexicon.isExpTail().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.EQUIVALENT) {
                    pStack.pop(); for (Lexicon rule : Lexicon.equivalentRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.LESSTHAN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.lessThanRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case SIMPLEEXP: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.simpleExpRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
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
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case TERM: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.termRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
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
                new Analyzer(tokenLst, deadLst, pStack);
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
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case ARGLIST: {
                if (Lexicon.isArgList().contains(nextType)) {pStack.pop(); break;}
                if (nextType == Lexicon.LEFTPAREN) {
                    pStack.pop(); for (Lexicon rule : Lexicon.leftParenArgRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALARG: {
                if (Lexicon.isExpression().contains(nextType)) {
                    pStack.pop(); for (Lexicon rule : Lexicon.formalArgRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.RIGHTPAREN) {pStack.pop(); break;}
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case FORMALARGTAIL: {
                if (nextType == Lexicon.RIGHTPAREN) {pStack.pop(); break;}
                if (nextType == Lexicon.COMMA) {
                    pStack.pop(); for (Lexicon rule : Lexicon.commaRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            case LITERAL: {
                if (nextType == Lexicon.BOOLEANLITERAL) {
                    pStack.pop(); for (Lexicon rule : Lexicon.booleanLitRules()) {pStack.push(rule);} break;
                }
                if (nextType == Lexicon.INTEGERLITERAL) {
                    pStack.pop(); for (Lexicon rule : Lexicon.integerLitRules()) {pStack.push(rule);} break;
                }
                new Analyzer(tokenLst, deadLst, pStack);
            }
            default: {break;}
        }
    }

    public void semanticAction() {
        Lexicon semanticRule = pStack.peek();
        if (lastToken==null) {semanticRule=Lexicon.$;}
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

    public static Boolean terminal(Stack<Lexicon> stack) {
        Lexicon type = stack.peek();
        if (Lexicon.isTerminal().contains(type)) {
            return true;
        }
        return false;
    }

    private Token removeTerminal() throws Analyzer {

        while (terminal(pStack)) {
            if (!pStack.peek().equals(tokenLst.get(0).getType())) {new Analyzer(tokenLst, deadLst, pStack);}
            lastToken = tokenLst.get(0);
            nextToken = getNextToken();
            if (lastToken.getType()==Lexicon.MINUS && nextToken.getType()==Lexicon.MINUS) {new Analyzer(tokenLst, deadLst, pStack);}
            pStack.pop();
        }
        return nextToken;
    }

    private void isDone() {
        if (pStack.peek().equals(Lexicon.$) && nextToken.getType().equals(Lexicon.$)) {
            run = false;
        }
    }

    private ArrayList<Integer> getCharList() {
        return tokenLst.get(0).getCharList();
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
