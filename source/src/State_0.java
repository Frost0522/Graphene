package src;
import java.io.IOException;
import java.util.ArrayList;

class State_0 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar)) {
            charLst.add(currentChar);
            return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner_v2.peek()));
        }
        if (isNumeric(currentChar)) {
            charLst.add(currentChar);
            return stateLst.get(2).process(stateLst, charLst, Integer.valueOf(Scanner_v2.peek()));
        }
        if (currentChar.equals(Lexical.MINUS.value())) {
            charLst.add(currentChar);
            return stateLst.get(5).process(stateLst, charLst, Integer.valueOf(Scanner_v2.peek()));
        }
        if (currentChar.equals(Lexical.DIVIDE.value())) {
            charLst.add(currentChar);
            return stateLst.get(3).process(stateLst, charLst, Integer.valueOf(Scanner_v2.peek()));
        }
        if (currentChar.equals(Lexical.EQUALS.value())) {
            charLst.add(currentChar);
            return stateLst.get(4).process(stateLst, charLst, Integer.valueOf(Scanner_v2.peek()));
        }
        if (equalsAll(currentChar, new IntegerList(new Integer[]{Lexical.LEFT_PAREN.value(), Lexical.RIGHT_PAREN.value(), Lexical.COMMA.value(), Lexical.COLON.value(), Lexical.PLUS.value(), Lexical.TIMES.value(), Lexical.LESS_THAN.value()}))) {
            charLst.add(currentChar);
            return new Token(Lexical.toType((Integer)currentChar), charLst);
        }
        if (isSkippable(currentChar)) {
            if (isNewLine(currentChar)) {
                return new Token(Lexical.NEWLINE, new ArrayList<>());
            }
            return new Token(Lexical.SKIPPABLE, new ArrayList<>());
        }
        if (isComment(currentChar)) {
            // currentChar = Scanner_v2.next();
            while (!isNewLine(currentChar)) {
                currentChar = Scanner_v2.next();
                if (isEof(Scanner_v2.peek())) {
                    return new Token(Lexical.$, charLst);
                }
            }
            return new Token(Lexical.NEWLINE, new ArrayList<>());
        }
        if (isEof(currentChar)) {
            return new Token(Lexical.$, new ArrayList<>());
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}
