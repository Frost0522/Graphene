package src;
import java.io.IOException;
import java.util.ArrayList;

class State_4 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (currentChar.equals(Lexical.EQUALS.value())) {
            charLst.add(currentChar);
            Scanner.next();
            return new Token(Lexical.EQUIVALENT, charLst);
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
