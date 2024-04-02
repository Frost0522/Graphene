package src;
import java.util.ArrayList;
import java.util.Stack;

public class Analyzer extends Exception {

    public Analyzer(String msg) {
        super(msg);
    }
    
    // Error handeling for the parser.
    public Analyzer(Lexical nextType, ArrayList<Token> tokenList, ArrayList<Token> deadLst, Stack<Lexical> stack, int line, int column) throws Analyzer {

        ArrayList<Integer> intLst = new ArrayList<>();
        Token errorToken = tokenList.remove(0);
        StringBuilder errorName = new StringBuilder();
        for (int i : errorToken.getCharList()) {errorName.append((char) i);}

        switch (stack.peek()) {
            case DEFINITIONLIST:
                if (Lexical.definitionListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                       " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case DEFINITION:
                if (Lexical.definitionError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                       " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case PARAMETERLIST:
                if (errorToken.getType().equals(Lexical.RETURN)) {
                    unPadLst(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing right parenthesis.");                    
                }
                else if (Lexical.parameterListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case ARGUMENTLIST:
                if (Lexical.arugmentListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case BODY:
                if (errorToken.getType().equals(Lexical.$)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Function body not found.");
                }
                else if (Lexical.bodyError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case EXPRESSION:
                if (errorToken.getType().equals(Lexical.RIGHT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = (intLst.get(1) + 1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Function body not found.");
                }
                else if (Lexical.expressionError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case EXPRESSIONTAIL:
                if (Lexical.expressionTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case FACTOR:
                if (Lexical.factorError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case FORMALARGSTAIL:
                if (Lexical.formalArgsTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case FORMALARGUMENTS:
                if (Lexical.formalArgsError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case FORMALPARAMETERS:
                if (Lexical.formalParamError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case FORMALPARAMTAIL:
                if (Lexical.formalParamTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case IDWITHTYPE:
                if (Lexical.idWithTypeError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case LITERAL:
                if (Lexical.literalError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case PRINTEXPRESSION:
                if (Lexical.printExpError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case PROGRAM:
                if (Lexical.programError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case SIMPLEEXPRESSION:
                if (Lexical.simpleExpError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case SIMPLEEXPRESSIONTAIL:
                if (Lexical.simpleExpTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case TERM:
                if (Lexical.termError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }
            case TERMTAIL:
                if (Lexical.termTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                    " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
                }            
            case TYPE:
                if (Lexical.typeError().contains(errorToken.getType())) {
                    unPadLst(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing function return type.");
                }
            case IDENTIFIER: 
                if (errorToken.getType().equals(Lexical.LEFT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing function name.");                    
                }
            case RETURN:
                if (errorToken.getType().equals(Lexical.INTEGER) || errorToken.getType().equals(Lexical.BOOLEAN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing return symbol.");                    
                }                            
            case LEFT_PAREN:
                if (errorToken.getType().equals(Lexical.RIGHT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing left parenthesis.");                    
                }            
            default:
                intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+stack.peek().toString().toLowerCase()+
                                   " encountered unexpected "+errorToken.getType().toString().toLowerCase()+" token "+"'"+errorName+"'.");
        }
    }

    private void unPadLst(ArrayList<Token> deadLst) {
        Stack<Lexical> deadStack = new Stack<>();
        boolean terminal = false;
        while (!terminal) {
            if (deadLst.get(deadLst.size()-1).getType().equals(Lexical.SKIPPABLE) || 
                deadLst.get(deadLst.size()-1).getType().equals(Lexical.NEWLINE)) {
                deadStack.push(deadLst.remove(deadLst.size()-1).getType());
            }
            else {terminal = true;}
        }
    }

    private ArrayList<Integer> incLineAndColCount(ArrayList<Token> deadLst, int line, int column) {
        ArrayList<Integer> intLst = new ArrayList<>();
        for (Token token : deadLst) {
            if (token.getType().equals(Lexical.NEWLINE)) {line++; column = 1;}
            else if (token.getType().equals(Lexical.SKIPPABLE)) {column++;}
            column += token.getCharList().size();
        } intLst.add(line); intLst.add(column);
        return intLst;
    }
}