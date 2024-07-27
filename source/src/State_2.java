package src;
import java.io.IOException;
import java.util.ArrayList;

class State_2 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (charLst.size() > 10) {
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }
        if (charLst.get(0)==48 && isNumeric(currentChar)) {
            throw new Analyzer("Syntax error: Not a valid integer literal.");
        }
        if (isEof(currentChar)) {
            return new Token(Lexicon.INTEGERLITERAL, charLst);
        }
        if (isNumeric(currentChar)) {
            Scanner.next();
            charLst.add(currentChar);
            if (charInList(Scanner.peek(), new Lexicon[]{Lexicon.RIGHTPAREN,Lexicon.COMMA,Lexicon.MINUS,Lexicon.PLUS,Lexicon.TIMES,Lexicon.LESSTHAN,Lexicon.DIVIDE,Lexicon.EQUALS})) {
                for (int _int : charLst) {
                    buff.append(Character.toString(_int));
                }
                if (Long.valueOf(buff.toString()) <= Long.MAX_VALUE) {
                    buff = new StringBuffer();
                    return new Token(Lexicon.INTEGERLITERAL, charLst);
                }
                throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
            }
            return stateLst.get(2).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lexicon[]{Lexicon.RIGHTPAREN,Lexicon.COMMA,Lexicon.MINUS,Lexicon.PLUS,Lexicon.TIMES,Lexicon.LESSTHAN,Lexicon.DIVIDE,Lexicon.EQUALS}) || isSkippable(currentChar)) {
            currentChar.equals(13);
            for (int _int : charLst) {
                buff.append(Character.toString(_int));
            }
            if (Long.valueOf(buff.toString()) <= Integer.MAX_VALUE) {
                buff = new StringBuffer();
                return new Token(Lexicon.INTEGERLITERAL, charLst);
            }
            throw new Analyzer("Syntax Error: Integer literal exceeds max value of 2^(31)-1.");
        }

        throw new Analyzer("Syntax error: Not a valid integer literal.");
    }
}
