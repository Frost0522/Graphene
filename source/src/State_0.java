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
        if (currentChar.equals(Lexicon.MINUS.value())) {
            charLst.add(currentChar);
            return stateLst.get(5).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lexicon.DIVIDE.value())) {
            charLst.add(currentChar);
            return stateLst.get(3).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (currentChar.equals(Lexicon.EQUALS.value())) {
            charLst.add(currentChar);
            return stateLst.get(4).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lexicon[]{Lexicon.LEFTPAREN,Lexicon.RIGHTPAREN,Lexicon.COMMA,Lexicon.TIMES,Lexicon.PLUS,Lexicon.COLON,Lexicon.LESSTHAN})) {
            charLst.add(currentChar);
            return new Token(Lexicon.toType((Integer)currentChar), charLst);
        }
        if (isSkippable(currentChar)) {
            if (isNewLine(currentChar)) {
                return new Token(Lexicon.NL, new ArrayList<>());
            }
            return new Token(Lexicon.SKIP, new ArrayList<>());
        }
        if (isEof(currentChar)) {
            return new Token(Lexicon.$, new ArrayList<>());
        }
        charLst.add(currentChar);
        for (int _int : charLst) {
            buff.append((char)_int);
        }
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lexicon.UNDERSCORE.value())) {
            throw new Analyzer("Syntax error: Unexpected characters '" + buff.toString() + "'.");
        }
        throw new Analyzer("Syntax error: Unrecognized characters '" + buff.toString() + "'.");
    }
}
