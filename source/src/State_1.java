package src;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

class State_1 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lexicon.UNDERSCORE.value())) {
            charLst.add(currentChar);
            Scanner.next();
            for (Entry<Lexicon, int[]> entry : Lexicon.getMap().entrySet()) {
                if (!compareList(entry.getValue(), charLst)) continue;
                if (isAlpha(Scanner.peek()) || isNumeric(Scanner.peek()) || Scanner.peek() == (Lexicon.UNDERSCORE.value())) {
                    return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
                }
                switch (entry.getKey()) {
                    case INTEGER: {return new Token(Lexicon.INTEGER, charLst);}
                    case BOOLEAN: {return new Token(Lexicon.BOOLEAN, charLst);}
                    case IF: {return new Token(Lexicon.IF, charLst);}
                    case ELSE: {return new Token(Lexicon.ELSE, charLst);}
                    case NOT: {return new Token(Lexicon.NOT, charLst);}
                    case AND: {return new Token(Lexicon.AND, charLst);}
                    case FN: {return new Token(Lexicon.FN, charLst);}
                    case OR: {return new Token(Lexicon.OR, charLst);}
                    case TRUE: {return new Token(Lexicon.BOOLEANLITERAL, charLst);}
                    case FALSE: {return new Token(Lexicon.BOOLEANLITERAL, charLst);}
                }
            }
            return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (charInList(currentChar, new Lexicon[]{Lexicon.LEFTPAREN,Lexicon.RIGHTPAREN,Lexicon.COMMA,Lexicon.COLON,Lexicon.MINUS,Lexicon.PLUS,Lexicon.TIMES,Lexicon.LESSTHAN}) || 
            isSkippable(currentChar) || isEof(currentChar) || currentChar.equals(Lexicon.DIVIDE.value()) || currentChar.equals(Lexicon.EQUALS.value())) {
            currentChar.equals(13);
            if (charLst.size() > 256) {throw new Analyzer("Syntax error: Identifier length exceeds max value of 256.");}
            return new Token(Lexicon.ID, charLst);
        }
        throw new Analyzer("Syntax error: Invalid identifier.");
    }
}
