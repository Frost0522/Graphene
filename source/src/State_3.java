package src;
import java.io.IOException;
import java.util.ArrayList;

class State_3 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || isSkippable(currentChar) || charInList(currentChar, new Lexicon[]{Lexicon.LEFTPAREN,Lexicon.MINUS})) {
            return new Token(Lexicon.DIVIDE, charLst);
        }
        if (currentChar.equals(Lexicon.DIVIDE.value())) {
            charLst.clear();
            while (!isNewLine(currentChar)) {
                currentChar = Scanner.next();
                if (isEof(Scanner.peek())) {
                    return new Token(Lexicon.$, charLst);
                }
            }
            return new Token(Lexicon.NL, new ArrayList<>());
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