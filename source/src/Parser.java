package src;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    public ArrayList<Integer> PRINT = new IntegerList(new Integer[]{112, 114, 105, 110, 116}).get();
    public Token lastToken;
    public int line = 1;
    public int column = 1;
    public Boolean isNegative = false;

    public Parser(Scanner_v2 newScanner) throws Analyzer {
        parse(newScanner);
    }

    public void parse(Scanner_v2 scanner) throws Analyzer {

        Stack<Node> nStack = new Stack<Node>();
        Stack<Lexical> pStack = new Stack<Lexical>();
        ArrayList<Token> tokenLst = scanner.tokenLst;
        while (tokenLst.get(0).getType().equals(Lexical.NEWLINE)) {line++; tokenLst.remove(0);}
        pStack.add(Lexical.$);
        pStack.add(Lexical.PROGRAM);
        Token nextToken = tokenLst.get(0);

        try {
            Boolean run = true;
            while (run) {

                semanticAction(nStack, pStack);
                parseTable(pStack, tokenLst);

                while (terminal(pStack)) {
                    column += nextToken.getCharList().size();
                    lastToken = tokenLst.get(0);
                    if (lastToken.getType().equals(Lexical.MINUS)) {isNegative = true;}
                    nextToken = getNextToken(tokenLst);
                    nextToken = incCount(nextToken, tokenLst);
                    pStack.pop();
                }

                if (isNegative && (nextToken.getType().equals(Lexical.IDENTIFIER) || nextToken.getType().equals(Lexical.INTEGER_LITERAL))) {
                    nextToken.flipSymbol(); isNegative = false;
                }

                if (pStack.peek().equals(Lexical.$) && nextToken.getType().equals(Lexical.$)) {run = false;}
            }
        }
        catch (Analyzer err) {
            System.out.println(err.getMessage());
            System.exit(0);
        }
        System.out.println();
        for (Node node : nStack) {
            switch (node.valueOf()) {
                case FN: {System.out.println(node.getFn()); break;}
                case FNCALL: {System.out.println(node.getFnCall()); break;}
                case IDENTIFIER: System.out.println(node.getId()); break;
                case TYPE: System.out.println(node.getType()); break;
                case LITERAL: System.out.println(node.getLiteral()); break;
                case PARAMETERLIST: System.out.println(node.getParam()); break;
                case EQUIVALENT: System.out.println(node.getEquality()); break;
                case MINUS: System.out.println(node.getMinus()); break;
                case PLUS: System.out.println(node.getPlus()); break;
                case DIVIDE: System.out.println(node.getDivide()); break;
                case TIMES: System.out.println(node.getTimes()); break;
                case AND: System.out.println(node.getAnd()); break;
                case OR: System.out.println(node.getOr()); break;
                case LESS_THAN: System.out.println(node.getLessThan()); break;
                default: break;
            }
        }
    }

    private void epsilon(Stack<Lexical> pStack) {pStack.pop();}
    
    private void addAll(Stack<Lexical> pStack, Lexical ... lexical) {
        pStack.pop();
        Lexical[] lexicalArray = lexical;
        int n = lexical.length;
        int n2 = 0;
        while (n2 < n) {
            Lexical lex = lexicalArray[n2];
            pStack.add(lex);
            ++n2;
        }
    }

    private ArrayList<Integer> getCharList(ArrayList<Token> list) {
        return list.get(0).getCharList();
    }

    private Token getNextToken(ArrayList<Token> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        list.remove(0);
        return list.get(0);
    }

    private Boolean terminal(Stack<Lexical> pStack) {
        Lexical type = pStack.peek();
        if (type == Lexical.FN || type == Lexical.LEFT_PAREN || type == Lexical.RIGHT_PAREN || type == Lexical.RETURN || type == Lexical.COMMA || type == Lexical.COLON || type == Lexical.INTEGER || type == Lexical.BOOLEAN || type == Lexical.EQUIVALENT || type == Lexical.LESS_THAN || type == Lexical.OR || type == Lexical.PLUS || type == Lexical.MINUS || type == Lexical.AND || type == Lexical.TIMES || type == Lexical.DIVIDE || type == Lexical.NOT || type == Lexical.IF || type == Lexical.ELSE || type == Lexical.IDENTIFIER || type == Lexical.BOOLEAN_LITERAL || type == Lexical.INTEGER_LITERAL) {
            return true;
        }
        return false;
    }

    private String tokenToStr(Token token) {
        StringBuffer buff = new StringBuffer();
        for (int _int : token.getCharList()) {
            buff.append(Character.valueOf((char)_int));
        }
        return buff.toString();
    }

    private Token incCount(Token token, ArrayList<Token> list) {
        while (token.getType().equals(Lexical.NEWLINE) || token.getType().equals(Lexical.SKIPPABLE)) {
            if (token.getType().equals(Lexical.NEWLINE)) {
                ++line; column = 1; token = getNextToken(list);
            }
            else if (token.getType().equals(Lexical.SKIPPABLE)) {
                ++column; token = getNextToken(list);
            }
        }
        return token;
    }

    private void errorState(Lexical nextType, ArrayList<Token> tokenList, Stack<Lexical> stack) throws Analyzer {
        
        // System.out.println(tokenList.get(0).getType());
        // System.out.println();
        // System.out.println(stack.peek());

        Token eToken = tokenList.remove(0); // Remove error token from the list.  
        if (nextType.equals(Lexical.$)) {
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function body not found.\n");
        }
        else if (eToken.getType().equals(Lexical.IDENTIFIER) && stack.peek().equals(Lexical.PROGRAM)) {
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function call not defined.\n");
        }
        else if (eToken.getType().equals(Lexical.RETURN) && stack.peek().equals(Lexical.PARAMETERLIST)) {
            column-=1;
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function identifier is undefined.\n");
        }
        else if (eToken.getType().equals(Lexical.RIGHT_PAREN) && stack.peek().equals(Lexical.EXPRESSION)) {
            column+=1;
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function body not found.\n");
        }
        else if ((eToken.getType().equals(Lexical.INTEGER) || eToken.getType().equals(Lexical.BOOLEAN)) && stack.peek().equals(Lexical.RETURN)) {
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function is missing return symbol.\n");
        }
        else if (!eToken.getType().equals(Lexical.TYPE) && stack.peek().equals(Lexical.TYPE)) {
            line-=1; column=1;
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Function return type is undefined.\n");
        }
        else if (tokenList.get(0).getType().equals(Lexical.$)) {
            throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Unexpected " + nextType.toString().toLowerCase() + " '" + tokenToStr(eToken) + "'\n");
        }

        throw new Analyzer("Line " + line + " Column " + column + "\nSyntax Error: Unexpected " + nextType.toString().toLowerCase() + " '" + tokenToStr(eToken) + "'\n");
    }

    private void parseTable(Stack<Lexical> pStack, ArrayList<Token> tokenList) throws Analyzer {
        Lexical nextType = incCount(tokenList.get(0), tokenList).getType();
        switch (pStack.lastElement()) {
            case PROGRAM: {
                if (nextType == Lexical.FN || nextType == Lexical.$) {
                    addAll(pStack, Lexical.DEFINITIONLIST);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case DEFINITIONLIST: {
                if (nextType == Lexical.FN) {
                    pStack.add(Lexical.DEFINITION);
                    break;
                }
                if (nextType == Lexical.$) {
                    epsilon(pStack);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case DEFINITION: {
                if (nextType == Lexical.FN) {
                    addAll(pStack, Lexical.MKFN, Lexical.BODY, Lexical.MKRETURNTYPE, Lexical.TYPE, Lexical.RETURN, Lexical.RIGHT_PAREN, Lexical.PARAMETERLIST, Lexical.LEFT_PAREN, Lexical.MKID, Lexical.IDENTIFIER, Lexical.FN);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case PARAMETERLIST: {
                if (nextType == Lexical.RIGHT_PAREN) {
                    epsilon(pStack);
                    break;
                }
                if (nextType == Lexical.IDENTIFIER) {
                    addAll(pStack, Lexical.FORMALPARAMETERS);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case FORMALPARAMETERS: {
                if (nextType == Lexical.IDENTIFIER) {
                    addAll(pStack, Lexical.FORMALPARAMTAIL, Lexical.MKPARAM, Lexical.IDWITHTYPE);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case FORMALPARAMTAIL: {
                if (nextType == Lexical.RIGHT_PAREN) {
                    epsilon(pStack);
                    break;
                }
                if (nextType == Lexical.COMMA) {
                    addAll(pStack, Lexical.FORMALPARAMETERS, Lexical.COMMA);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case IDWITHTYPE: {  
                if (nextType == Lexical.IDENTIFIER) {
                    addAll(pStack, Lexical.MKTYPE, Lexical.TYPE, Lexical.COLON, Lexical.MKID, Lexical.IDENTIFIER);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case TYPE: {
                if (nextType == Lexical.INTEGER) {
                    addAll(pStack, Lexical.INTEGER);
                    break;
                }
                if (nextType == Lexical.BOOLEAN) {
                    addAll(pStack, Lexical.BOOLEAN);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case BODY: {
                if (getCharList(tokenList).equals(PRINT)) {
                    pStack.add(Lexical.PRINTEXPRESSION);
                    break;
                }
                if (nextType == Lexical.LEFT_PAREN || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.EXPRESSION);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case PRINTEXPRESSION: {
                if (getCharList(tokenList).equals(PRINT)) {
                    addAll(pStack, Lexical.RIGHT_PAREN, Lexical.EXPRESSION, Lexical.LEFT_PAREN, Lexical.IDENTIFIER);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case EXPRESSION: {
                if (nextType == Lexical.LEFT_PAREN || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.EXPRESSIONTAIL, Lexical.SIMPLEEXPRESSION);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case EXPRESSIONTAIL: {
                if (nextType == Lexical.FN || nextType == Lexical.LEFT_PAREN || nextType == Lexical.RIGHT_PAREN || nextType == Lexical.COMMA || nextType == Lexical.OR || nextType == Lexical.PLUS || nextType == Lexical.MINUS || nextType == Lexical.AND || nextType == Lexical.TIMES || nextType == Lexical.DIVIDE || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.ELSE || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL || nextType == Lexical.$) {
                    epsilon(pStack);    
                    break;
                }
                if (nextType == Lexical.EQUIVALENT) {
                    addAll(pStack, Lexical.MKEQUALITY, Lexical.EXPRESSION, Lexical.EQUIVALENT);
                    break;
                }
                if (nextType == Lexical.LESS_THAN) {
                    addAll(pStack, Lexical.MKLESSTHAN, Lexical.EXPRESSION, Lexical.LESS_THAN);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case SIMPLEEXPRESSION: {
                if (nextType == Lexical.LEFT_PAREN || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.SIMPLEEXPRESSIONTAIL, Lexical.TERM);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case SIMPLEEXPRESSIONTAIL: {
                if (nextType == Lexical.FN || nextType == Lexical.LEFT_PAREN || nextType == Lexical.RIGHT_PAREN || nextType == Lexical.COMMA || nextType == Lexical.AND || nextType == Lexical.TIMES || nextType == Lexical.DIVIDE || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.ELSE || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL || nextType == Lexical.EQUIVALENT || nextType == Lexical.LESS_THAN || nextType == Lexical.$) {
                    epsilon(pStack);
                    break;
                }
                if (nextType == Lexical.OR) {
                    addAll(pStack, Lexical.MKOR, Lexical.SIMPLEEXPRESSION, Lexical.OR);
                    break;
                }
                if (nextType == Lexical.PLUS) {
                    addAll(pStack, Lexical.MKPLUS, Lexical.SIMPLEEXPRESSION, Lexical.PLUS);
                    break;
                }
                if (nextType == Lexical.MINUS) {
                    addAll(pStack, Lexical.MKMINUS, Lexical.SIMPLEEXPRESSION, Lexical.MINUS);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case TERM: {
                if (nextType == Lexical.LEFT_PAREN || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.TERMTAIL, Lexical.FACTOR);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case TERMTAIL: {
                if (nextType == Lexical.FN || nextType == Lexical.LEFT_PAREN || nextType == Lexical.RIGHT_PAREN || nextType == Lexical.COMMA || nextType == Lexical.OR || nextType == Lexical.PLUS || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.ELSE || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL || nextType == Lexical.EQUIVALENT || nextType == Lexical.LESS_THAN || nextType == Lexical.$) {
                    epsilon(pStack);
                    break;
                }
                if (nextType == Lexical.AND) {
                    addAll(pStack, Lexical.MKAND, Lexical.TERM, Lexical.AND);
                    break;
                }
                if (nextType == Lexical.TIMES) {
                    addAll(pStack, Lexical.MKTIMES, Lexical.TERM, Lexical.TIMES);
                    break;
                }
                if (nextType == Lexical.DIVIDE) {
                    addAll(pStack, Lexical.MKDIVIDE, Lexical.TERM, Lexical.DIVIDE);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case FACTOR: {
                if (nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.LITERAL);
                    break;
                }
                if (nextType == Lexical.LEFT_PAREN) {
                    addAll(pStack, Lexical.RIGHT_PAREN, Lexical.EXPRESSION, Lexical.LEFT_PAREN);
                    break;
                }
                if (nextType == Lexical.MINUS) {
                    pStack.add(Lexical.MINUS);
                    break;
                }
                if (nextType == Lexical.NOT) {
                    pStack.add(Lexical.NOT);
                    break;
                }
                if (nextType == Lexical.IF) {
                    addAll(pStack, Lexical.EXPRESSION, Lexical.ELSE, Lexical.EXPRESSION, Lexical.RIGHT_PAREN, Lexical.EXPRESSION, Lexical.LEFT_PAREN, Lexical.IF);
                    break;
                }
                if (nextType == Lexical.IDENTIFIER) {
                    addAll(pStack, Lexical.ARGUMENTLIST, Lexical.MKID, Lexical.IDENTIFIER);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case ARGUMENTLIST: {
                if (nextType == Lexical.FN || nextType == Lexical.COMMA || nextType == Lexical.EQUIVALENT || nextType == Lexical.LESS_THAN || nextType == Lexical.OR || nextType == Lexical.PLUS || nextType == Lexical.MINUS || nextType == Lexical.AND || nextType == Lexical.TIMES || nextType == Lexical.DIVIDE || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.ELSE || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL || nextType == Lexical.$) {
                    epsilon(pStack); break;
                }
                if (nextType == Lexical.RIGHT_PAREN) {
                    epsilon(pStack); break;
                }
                if (nextType == Lexical.LEFT_PAREN) {
                    addAll(pStack, Lexical.RIGHT_PAREN, Lexical.FORMALARGUMENTS, Lexical.LEFT_PAREN, Lexical.MKFNCALL); break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case FORMALARGUMENTS: {
                if (nextType == Lexical.LEFT_PAREN || nextType == Lexical.MINUS || nextType == Lexical.NOT || nextType == Lexical.IF || nextType == Lexical.IDENTIFIER || nextType == Lexical.BOOLEAN_LITERAL || nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.FORMALARGSTAIL, Lexical.EXPRESSION);
                    break;
                }
                if (nextType == Lexical.RIGHT_PAREN) {addAll(pStack, Lexical.MKARGS); break;}
                errorState(nextType, tokenList, pStack);
            }
            case FORMALARGSTAIL: {
                if (nextType == Lexical.RIGHT_PAREN) {
                    addAll(pStack, Lexical.MKARGS); break;
                }
                if (nextType == Lexical.COMMA) {
                    addAll(pStack, Lexical.FORMALARGUMENTS, Lexical.COMMA);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            case LITERAL: {
                if (nextType == Lexical.BOOLEAN_LITERAL) {
                    addAll(pStack, Lexical.MKLIT, Lexical.BOOLEAN_LITERAL);
                    break;
                }
                if (nextType == Lexical.INTEGER_LITERAL) {
                    addAll(pStack, Lexical.MKLIT, Lexical.INTEGER_LITERAL);
                    break;
                }
                errorState(nextType, tokenList, pStack);
            }
            default: {
                break;
            }
        }
    }

    private void semanticAction(Stack<Node> nStack, Stack<Lexical> pStack) {
        switch (pStack.lastElement()) {
            case MKID: {epsilon(pStack); nStack.push(new Identifier(lastToken).accept(new MakeNode())); break;}
            case MKTYPE: {epsilon(pStack); nStack.push(new Type(lastToken).accept(new MakeNode())); break;}
            case MKRETURNTYPE: {epsilon(pStack); nStack.push(new ReturnType(lastToken).accept(new MakeNode())); break;}
            case MKPARAM: {epsilon(pStack); nStack.push(new Parameter(nStack).accept(new MakeNode())); break;}
            case MKEQUALITY: {epsilon(pStack); nStack.push(new Equality(nStack).accept(new MakeNode())); break;}
            case MKMINUS: {epsilon(pStack); nStack.push(new Minus(nStack).accept(new MakeNode())); break;}
            case MKPLUS: {epsilon(pStack); nStack.push(new Plus(nStack).accept(new MakeNode())); break;}
            case MKDIVIDE: {epsilon(pStack); nStack.push(new Divide(nStack).accept(new MakeNode())); break;}
            case MKTIMES: {epsilon(pStack); nStack.push(new Times(nStack).accept(new MakeNode())); break;}
            case MKAND: {epsilon(pStack); nStack.push(new And(nStack).accept(new MakeNode())); break;}
            case MKOR: {epsilon(pStack); nStack.push(new Or(nStack).accept(new MakeNode())); break;}
            case MKLESSTHAN: {epsilon(pStack); nStack.push(new LessThan(nStack).accept(new MakeNode())); break;}
            case MKLIT: {epsilon(pStack); nStack.push(new Literal(lastToken).accept(new MakeNode())); break;}
            case MKFN: {epsilon(pStack); nStack.push(new Function(nStack).accept(new MakeNode())); break;}
            case MKFNCALL: {
                epsilon(pStack); Node node = nStack.pop();
                nStack.push(new FunctionCall().accept(new MakeNode())); 
                nStack.push(node); break;
            }
            case MKARGS: {
                epsilon(pStack);
                ArrayList<Node> args = new ArrayList<Node>();
                Boolean flag = true;
                while (flag) {
                    if (nStack.peek().valueOf().equals(Lexical.IDENTIFIER) || nStack.peek().valueOf().equals(Lexical.LITERAL) || nStack.peek().valueOf().equals(Lexical.PLUS)
                     || nStack.peek().valueOf().equals(Lexical.DIVIDE) || nStack.peek().valueOf().equals(Lexical.MINUS) || nStack.peek().valueOf().equals(Lexical.EQUIVALENT) 
                     || nStack.peek().valueOf().equals(Lexical.TIMES) || nStack.peek().valueOf().equals(Lexical.AND) || nStack.peek().valueOf().equals(Lexical.OR) 
                     || nStack.peek().valueOf().equals(Lexical.LESS_THAN)) {
                        args.add(nStack.pop());
                    }
                    else if (nStack.peek().valueOf().equals(Lexical.FNCALL) && !nStack.peek().getFnCall().getName().getCharList().isEmpty()) {
                        args.add(nStack.pop());
                    }
                    else {flag = false;}
                }
                FunctionCall fn = nStack.pop().getFnCall(); 
                fn.setFnName(args.remove(args.size()-1).getId()); fn.setArgs(args);
                nStack.push(new Node(fn)); break;
            }
            default: {break;}
        }
    }
}