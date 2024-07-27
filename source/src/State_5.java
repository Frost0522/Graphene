package src;
import java.io.IOException;
import java.util.ArrayList;

class State_5 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar) || currentChar.equals(Lexicon.LEFTPAREN.value())) {
            if (currentChar==48) {
                throw new Analyzer("Syntax error: Zero cannot be negative.");       
            }
            return new Token(Lexicon.MINUS, charLst);
        }
        if (currentChar.equals(Lexicon.GREATERTHAN.value())) {
            charLst.add(currentChar);
            Scanner.next();
            return new Token(Lexicon.RETURN, charLst);
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
