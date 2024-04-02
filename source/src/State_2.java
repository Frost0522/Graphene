package src;
import java.io.IOException;
import java.util.ArrayList;

class State_2 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (charLst.size() > 10) {
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }
        if (charLst.get(0).equals(Lexical.ZERO.value()) && isNumeric(currentChar)) {
            throw new Analyzer("Syntax error: Not a valid integer literal.");
        }
        if (isEof(currentChar)) {
            return new Token(Lexical.INTEGER_LITERAL, charLst);
        }
        if (isNumeric(currentChar)) {
            Scanner.next();
            charLst.add(currentChar);
            if (equalsAll(Scanner.peek(), new IntegerList(new Integer[]{Lexical.RIGHT_PAREN.value(), Lexical.COMMA.value(), Lexical.MINUS.value(), Lexical.PLUS.value(), Lexical.TIMES.value(), Lexical.LESS_THAN.value(), Lexical.DIVIDE.value(), Lexical.EQUALS.value()}))) {
                for (int _int : charLst) {
                    buff.append(Character.toString(_int));
                }
                if (Long.valueOf(buff.toString()) <= Long.MAX_VALUE) {
                    buff = new StringBuffer();
                    return new Token(Lexical.INTEGER_LITERAL, charLst);
                }
                throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
            }
            return stateLst.get(2).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (equalsAll(currentChar, new IntegerList(new Integer[]{Lexical.RIGHT_PAREN.value(), Lexical.COMMA.value(), Lexical.MINUS.value(), Lexical.PLUS.value(), Lexical.TIMES.value(), Lexical.LESS_THAN.value(), Lexical.DIVIDE.value(), Lexical.EQUALS.value()})) || isSkippable(currentChar)) {
            currentChar.equals(13);
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            if (Long.valueOf(buff.toString()) <= Integer.MAX_VALUE) {
                buff = new StringBuffer();
                return new Token(Lexical.INTEGER_LITERAL, charLst);
            }
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }
        throw new Analyzer("Syntax error: Not a valid integer literal.");
    }
}
