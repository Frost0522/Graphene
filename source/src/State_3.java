package src;
import java.io.IOException;
import java.util.ArrayList;

class State_3 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar) || equalsAll(currentChar, new IntegerList(new Integer[]{Lexical.LEFT_PAREN.value(), Lexical.MINUS.value()}))) {
            return new Token(Lexical.DIVIDE, charLst);
        }
        if (currentChar.equals(Lexical.DIVIDE.value())) {
            charLst.clear();
            return stateLst.get(0).process(stateLst, charLst, Integer.valueOf(-10));
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}