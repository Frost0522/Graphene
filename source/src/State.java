package src;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

abstract class State {
    protected StringBuffer buff = new StringBuffer();

    abstract Token process(ArrayList<State> stateList, ArrayList<Integer> charList, Integer next) throws Analyzer, IOException;

    protected boolean isSkippable(Integer val) {
        int[] skippable = new int[]{9,32,13,10};
        for (int i : skippable) {
            if (i==val) {return true;}
        }
        return false;
    }

    protected boolean isEof(Integer value) throws IOException {
        return value == -1 || value == 65535;
    }

    protected boolean isNewLine(Integer value) throws IOException {
        return value == 10;
    }

    protected boolean isAlpha(Integer val) {
        return val >= 65 && val <= 90 || val >= 97 && val <= 122;
    }

    protected boolean isNumeric(Integer val) {
        return val >= 48 && val <= 57;
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

    protected boolean charInList(int _char, Lex[] lexLst) {        
        for (Lex lex : lexLst) {
            if (lex.value()==_char) {return true;}
        } return false;
    }
}

class State_0 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar)) {
            charLst.add(currentChar);
            return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (isNumeric(currentChar)) {
            charLst.add(currentChar);
            return stateLst.get(2).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lex.MINUS.value())) {
            charLst.add(currentChar);
            return stateLst.get(5).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lex.DIVIDE.value())) {
            charLst.add(currentChar);
            return stateLst.get(3).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lex.EQUALS.value())) {
            charLst.add(currentChar);
            return stateLst.get(4).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lex[]{Lex.LEFTPAREN,Lex.RIGHTPAREN,Lex.COMMA,Lex.TIMES,Lex.PLUS,Lex.COLON,Lex.LESSTHAN})) {
            charLst.add(currentChar);
            return new Token(Lex.toType((Integer)currentChar), charLst);
        }
        if (isSkippable(currentChar)) {
            if (isNewLine(currentChar)) {
                return new Token(Lex.NL, new ArrayList<>());
            }
            return new Token(Lex.SKIP, new ArrayList<>());
        }
        if (isEof(currentChar)) {
            return new Token(Lex.$, new ArrayList<>());
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append((char)_int);
        }
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lex.UNDERSCORE.value())) {
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}

class State_1 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lex.UNDERSCORE.value())) {
            charLst.add(currentChar);
            Scanner.next();
            for (Entry<Lex, int[]> entry : Lex.getMap().entrySet()) {
                if (!compareList(entry.getValue(), charLst)) continue;
                if (isAlpha(Scanner.peek()) || isNumeric(Scanner.peek()) || Scanner.peek() == (Lex.UNDERSCORE.value())) {
                    return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
                }
                switch (entry.getKey()) {
                    case INTEGER: {return new Token(Lex.INTEGER, charLst);}
                    case BOOLEAN: {return new Token(Lex.BOOLEAN, charLst);}
                    case IF: {return new Token(Lex.IF, charLst);}
                    case ELSE: {return new Token(Lex.ELSE, charLst);}
                    case NOT: {return new Token(Lex.NOT, charLst);}
                    case AND: {return new Token(Lex.AND, charLst);}
                    case FN: {return new Token(Lex.FN, charLst);}
                    case OR: {return new Token(Lex.OR, charLst);}
                    case TRUE: {return new Token(Lex.BOOLEANLITERAL, charLst);}
                    case FALSE: {return new Token(Lex.BOOLEANLITERAL, charLst);}
                }
            }
            return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lex[]{Lex.LEFTPAREN,Lex.RIGHTPAREN,Lex.COMMA,Lex.COLON,Lex.MINUS,Lex.PLUS,Lex.TIMES,Lex.LESSTHAN}) || 
            isSkippable(currentChar) || isEof(currentChar) || currentChar.equals(Lex.DIVIDE.value()) || currentChar.equals(Lex.EQUALS.value())) {
            currentChar.equals(13);
            if (charLst.size() > 256) {throw new Analyzer("Syntax error: Identifier length exceeds max value of 256.");}
            return new Token(Lex.ID, charLst);
        }
        throw new Analyzer("Syntax error: Invalid identifier.");
    }
}

class State_2 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (charLst.size() > 10) {
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }
        if (charLst.get(0)==48 && isNumeric(currentChar)) {
            throw new Analyzer("Syntax error: Not a valid integer literal.");
        }
        if (isEof(currentChar)) {
            return new Token(Lex.INTEGERLITERAL, charLst);
        }
        if (isNumeric(currentChar)) {
            Scanner.next();
            charLst.add(currentChar);
            if (charInList(Scanner.peek(), new Lex[]{Lex.RIGHTPAREN,Lex.COMMA,Lex.MINUS,Lex.PLUS,Lex.TIMES,Lex.LESSTHAN,Lex.DIVIDE,Lex.EQUALS})) {
                for (int _int : charLst) {
                    buff.append(Character.toString(_int));
                }
                if (Long.valueOf(buff.toString()) <= Long.MAX_VALUE) {
                    buff = new StringBuffer();
                    return new Token(Lex.INTEGERLITERAL, charLst);
                }
                throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
            }
            return stateLst.get(2).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lex[]{Lex.RIGHTPAREN,Lex.COMMA,Lex.MINUS,Lex.PLUS,Lex.TIMES,Lex.LESSTHAN,Lex.DIVIDE,Lex.EQUALS}) || isSkippable(currentChar)) {
            currentChar.equals(13);
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            if (Long.valueOf(buff.toString()) <= Integer.MAX_VALUE) {
                buff = new StringBuffer();
                return new Token(Lex.INTEGERLITERAL, charLst);
            }
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }

        throw new Analyzer("Syntax error: Not a valid integer literal.");
    }
}

class State_3 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar) || charInList(currentChar, new Lex[]{Lex.LEFTPAREN,Lex.MINUS})) {
            return new Token(Lex.DIVIDE, charLst);
        }
        if (currentChar.equals(Lex.DIVIDE.value())) {
            charLst.clear();
            while (!isNewLine(currentChar)) {
                currentChar = Scanner.next();
                if (isEof(Scanner.peek())) {
                    return new Token(Lex.$, charLst);
                }
            }
            return new Token(Lex.NL, new ArrayList<>());
        }
        if (isEof(currentChar)) {
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}

class State_4 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (currentChar.equals(Lex.EQUALS.value())) {
            charLst.add(currentChar);
            Scanner.next();
            return new Token(Lex.EQUIVALENT, charLst);
        }
        if (isEof(currentChar)) {
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}

class State_5 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar) || currentChar.equals(Lex.LEFTPAREN.value())) {
            if (currentChar==48) {
                throw new Analyzer("Syntax error: Zero cannot be negative.");       
            }
            return new Token(Lex.MINUS, charLst);
        }
        if (currentChar.equals(Lex.GREATERTHAN.value())) {
            charLst.add(currentChar);
            Scanner.next();
            return new Token(Lex.RETURN, charLst);
        }
        if (isEof(currentChar)) {
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}
