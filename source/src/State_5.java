package src;
import java.io.IOException;
import java.util.ArrayList;

class State_5 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar)) {
            return new Token(Lexical.MINUS, charLst);
        }
        if (currentChar.equals(Lexical.GREATERTHAN.value())) {
            charLst.add(currentChar);
            Scanner_v2.next();
            return new Token(Lexical.RETURN, charLst);
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append(Character.toString(_int));
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}
