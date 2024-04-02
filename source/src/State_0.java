package src;
import java.io.IOException;
import java.util.ArrayList;

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
        if (currentChar.equals(Lexical.MINUS.value())) {
            charLst.add(currentChar);
            return stateLst.get(5).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lexical.DIVIDE.value())) {
            charLst.add(currentChar);
            return stateLst.get(3).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lexical.EQUALS.value())) {
            charLst.add(currentChar);
            return stateLst.get(4).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (equalsAll(currentChar, new IntegerList(new Integer[]{Lexical.LEFT_PAREN.value(), Lexical.RIGHT_PAREN.value(), Lexical.COMMA.value(), Lexical.TIMES.value(), Lexical.PLUS.value(), Lexical.COLON.value(), Lexical.LESS_THAN.value()}))) {
            charLst.add(currentChar);
            return new Token(Lexical.toType((Integer)currentChar), charLst);
        }
        if (isSkippable(currentChar)) {
            if (isNewLine(currentChar)) {
                return new Token(Lexical.NEWLINE, new ArrayList<>());
            }
            return new Token(Lexical.SKIPPABLE, new ArrayList<>());
        }
        if (isEof(currentChar)) {
            return new Token(Lexical.$, new ArrayList<>());
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append((char)_int);
        }
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lexical.UNDERSCORE.value())) {
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}
