package src;
import java.util.ArrayList;

public class Token {
    private Lexical type;
    private Lexical symbol;
    private ArrayList<Integer> charList = new ArrayList<>();

    public Token(Lexical type, ArrayList<Integer> newCharList) {
        this.type = type;
        charList = setList(newCharList);
        symbol = Lexical.PLUS;
    }

    public Lexical getType() {
        return type;
    }

    public ArrayList<Integer> getCharList() {
        return charList;
    }

    public int getSize() {
        return charList.size();
    }

    public Lexical getSymbol() {
        return symbol;
    }

    public void flipSymbol() {
        if (symbol.equals(Lexical.PLUS)) {symbol = Lexical.MINUS;}
        else {symbol = Lexical.PLUS;}
    }

    private ArrayList<Integer> setList(ArrayList<Integer> list) {
        for (Integer entry : list) {
            charList.add(entry);
        }
        return charList;
    }
}
