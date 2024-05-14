package src;
import java.util.ArrayList;
import java.util.Stack;

public class Analyzer extends Exception {

    public Analyzer(String msg) {
        super(msg);
    }
    
    // Error handeling for parsing rules.
    public Analyzer(ArrayList<Token> tokenList, ArrayList<Token> deadLst, Stack<Lexical> stack, int line, int column) throws Analyzer {

        ArrayList<Integer> intLst = new ArrayList<>();
        String topOfStack = stack.peek().toString().toLowerCase();
        Token errorToken = tokenList.remove(0);
        String errorTokenType = errorToken.getType().toString().toLowerCase();
        StringBuilder errorName = new StringBuilder();
        for (int i : errorToken.getCharList()) {errorName.append((char) i);}

        // System.out.println("Error token -> "+errorTokenType+"\nTop of stack -> "+stack.peek()+"\n");

        switch (stack.peek()) {
            case DEFINITIONLIST:
                if (Lexical.definitionListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case DEFINITION:
                if (Lexical.definitionError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case PARAMETERLIST:
                if (errorToken.getType().equals(Lexical.RETURN)) {
                    unPadLst(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing right parenthesis.\n");
                }
                else if (Lexical.parameterListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case ARGUMENTLIST:
                if (Lexical.arugmentListError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case BODY:
                if (errorToken.getType().equals(Lexical.$)) {
                    unPadLst(deadLst); intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Function body not found.\n");
                }
                else if (Lexical.isOperator().contains(errorToken.getType())) {
                    StringBuilder newErrorName = new StringBuilder(); unPadLst(deadLst); ignoreArgs(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = (intLst.get(1) - deadLst.get(deadLst.size()-1).getCharList().size());
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {newErrorName.append((char) i);}
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Primitive '"+newErrorName+
                                       "' encountered unexpected token '"+errorToken.getType().toString().toLowerCase()+"'.\n");
                }
                else if (Lexical.bodyError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case EXPRESSION:
                if (errorToken.getType().equals(Lexical.RIGHT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); deadLst.remove(deadLst.size()-1);
                    column = (intLst.get(1) - deadLst.get(deadLst.size()-1).getCharList().size() - 1); errorName = new StringBuilder();
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {errorName.append((char) i);}
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Body of primitive '"+errorName+"' is empty.\n");
                }
                else if (Lexical.isOperator().contains(errorToken.getType())) {
                    StringBuilder newErrorName = new StringBuilder(); unPadLst(deadLst); ignoreArgs(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = (intLst.get(1) - deadLst.get(deadLst.size()-1).getCharList().size());
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {newErrorName.append((char) i);}
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Primitive '"+newErrorName+
                                       "' encountered unexpected token '"+errorToken.getType().toString().toLowerCase()+"'.\n");
                }
                else if (Lexical.expressionError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule '"+topOfStack+
                                       "' encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case EXPRESSIONTAIL:
                if (Lexical.expressionTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case FACTOR:
                if (Lexical.factorError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case FORMALARGSTAIL:
                if (Lexical.formalArgsTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case FORMALARGUMENTS:
                if (Lexical.formalArgsError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case FORMALPARAMETERS:
                if (Lexical.formalParamError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case FORMALPARAMTAIL:
                if (Lexical.formalParamTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case IDWITHTYPE:
                if (Lexical.idWithTypeError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case LITERAL:
                if (Lexical.literalError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case PRINTEXPRESSION:
                if (Lexical.printExpError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case PROGRAM:
                if (Lexical.programError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case SIMPLEEXPRESSION:
                if (Lexical.simpleExpError().contains(errorToken.getType())) {
                    unPadLst(deadLst); intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = intLst.get(1);
                    if (errorToken.getType().equals(Lexical.$)) {
                        throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case SIMPLEEXPRESSIONTAIL:
                if (Lexical.simpleExpTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case TERM:
                if (Lexical.termError().contains(errorToken.getType())) {
                    unPadLst(deadLst); intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = intLst.get(1);
                    if (errorToken.getType().equals(Lexical.$)) {
                        throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                           " encountered unexpected end of file.\n");
                    }
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}
            case TERMTAIL:
                if (Lexical.termTailError().contains(errorToken.getType())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
                else {break;}   
            case TYPE:
                if (Lexical.typeError().contains(errorToken.getType())) {
                    unPadLst(deadLst);
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing function return type.\n");
                }
                else {break;}
            case IDENTIFIER: 
                if (errorToken.getType().equals(Lexical.LEFT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing function name.\n");
                }
                else {break;}
            case RETURN:
                if (errorToken.getType().equals(Lexical.INTEGER) || errorToken.getType().equals(Lexical.BOOLEAN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing return symbol.\n");                    
                }
                else {break;}                
            case LEFT_PAREN:
                if (errorToken.getType().equals(Lexical.RIGHT_PAREN)) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Missing left parenthesis.\n");
                }
                else if (Lexical.isOperator().contains(errorToken.getType())) {
                    unPadLst(deadLst); intLst = incLineAndColCount(deadLst, line, column);
                    StringBuilder newErrorName = new StringBuilder(); 
                    for (int i : deadLst.get(deadLst.size()-1).getCharList()) {newErrorName.append((char) i);}
                    line = intLst.get(0); column = (intLst.get(1) - deadLst.get(deadLst.size()-1).getCharList().size());
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Keyword '"+newErrorName+
                                       "' encountered unexpected token '"+errorToken.getType().toString().toLowerCase()+"'.\n");
                }
                else {break;}
            case MINUS, PLUS, TIMES, DIVIDE, EQUIVALENT, LESS_THAN, AND, OR:
                if (errorToken.getCharList().equals(Lexical.getPrint())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                   " encountered unexpected keyword "+"'"+errorName+"'.\n");
                }
                else if (errorToken.getCharList().equals(Lexical.getIf())) {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                   " encountered unexpected keyword "+"'"+errorName+"'.\n");
                }
                else {break;}
            default:
                if (errorToken.getType().equals(Lexical.$)) {
                    unPadLst(deadLst); intLst = incLineAndColCount(deadLst, line, column); 
                    line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                   " encountered unexpected end of file.\n");
                }
                else {
                    intLst = incLineAndColCount(deadLst, line, column); line = intLst.get(0); column = intLst.get(1);
                    throw new Analyzer("Line " + line + " Column " + column + "\nParsing Error: Stack rule "+"'"+topOfStack+"'"+
                                       " encountered unexpected "+errorTokenType+" token "+"'"+errorName+"'.\n");
                }
        }
    }

    private void ignoreArgs(ArrayList<Token> deadLst) {
        deadLst.remove(deadLst.size()-1);
        int left = 0; int right = 1;
        while (left != right) {
            if (deadLst.get(deadLst.size()-1).getType().equals(Lexical.LEFT_PAREN)) {
                deadLst.remove(deadLst.size()-1); left += 1;
            }
            else if (deadLst.get(deadLst.size()-1).getType().equals(Lexical.RIGHT_PAREN)) {
                deadLst.remove(deadLst.size()-1); right += 1;
            }
            else {deadLst.remove(deadLst.size()-1);}
        }
    }

    private void unPadLst(ArrayList<Token> deadLst) {
        boolean terminal = false;
        while (!terminal) {
            if (deadLst.get(deadLst.size()-1).getType().equals(Lexical.SKIPPABLE) || 
                  deadLst.get(deadLst.size()-1).getType().equals(Lexical.NEWLINE)) {
                deadLst.remove(deadLst.size()-1);
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