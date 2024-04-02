package src;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

class State_1 extends State {

    Token process(ArrayList<State> stateLst, ArrayList<Integer> charLst, Integer currentChar) throws Analyzer, IOException {
        if (isAlpha(currentChar) || isNumeric(currentChar) || currentChar.equals(Lexical.UNDERSCORE.value())) {
            charLst.add(currentChar);
            Scanner.next();
            for (Entry<Lexical, ArrayList<Integer>> entry : Scanner.lexicalMap.entrySet()) {
                if (!(entry.getValue()).equals(charLst)) continue;
                if (isAlpha(Scanner.peek()) || isNumeric(Scanner.peek()) || Scanner.peek() == (Lexical.UNDERSCORE.value())) {
                    return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
                }
                switch (entry.getKey()) {
                    case INTEGER: {return new Token(Lexical.INTEGER, charLst);}
                    case BOOLEAN: {return new Token(Lexical.BOOLEAN, charLst);}
                    case IF: {return new Token(Lexical.IF, charLst);}
                    case ELSE: {return new Token(Lexical.ELSE, charLst);}
                    case NOT: {return new Token(Lexical.NOT, charLst);}
                    case AND: {return new Token(Lexical.AND, charLst);}
                    case FN: {return new Token(Lexical.FN, charLst);}
                    case OR: {return new Token(Lexical.OR, charLst);}
                    case TRUE: {return new Token(Lexical.BOOLEAN_LITERAL, charLst);}
                    case FALSE: {return new Token(Lexical.BOOLEAN_LITERAL, charLst);}
                }
            }
            return stateLst.get(1).process(stateLst, charLst, Integer.valueOf(Scanner.peek()));
        }
        if (equalsAll(currentChar, new IntegerList(new Integer[]{Lexical.LEFT_PAREN.value(), Lexical.RIGHT_PAREN.value(), Lexical.COMMA.value(), Lexical.COLON.value(), Lexical.MINUS.value(), Lexical.PLUS.value(), Lexical.TIMES.value(), Lexical.LESS_THAN.value()})) || isSkippable(currentChar) || isEof(currentChar) || currentChar.equals(Lexical.DIVIDE.value()) || currentChar.equals(Lexical.EQUALS.value())) {
            currentChar.equals(13);
            if (charLst.size() > 256) {
                throw new Analyzer("Syntax error: Identifier length exceeds max value of 256.");
            }
            return new Token(Lexical.IDENTIFIER, charLst);
        }
        throw new Analyzer("Syntax error: Invalid identifier.");
    }
}
