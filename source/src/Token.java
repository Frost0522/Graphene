package src;
import java.util.ArrayList;

public class Token {
    public int line;
    public int column;
    private Lexicon type;
    private Lexicon symbol;
    private ArrayList<Integer> charList = new ArrayList<>();

    public Token(Lexicon type, ArrayList<Integer> newCharList) {
        this.type = type;
        charList = setList(newCharList);
        symbol = Lexicon.PLUS;
    }

    public Lexicon getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public ArrayList<Integer> getCharList() {
        return charList;
    }

    public int getSize() {
        return charList.size();
    }

    public Lexicon getSymbol() {
        return symbol;
    }

    public void flipSymbol() {
        if (symbol.equals(Lexicon.PLUS)) {symbol = Lexicon.MINUS;}
        else {symbol = Lexicon.PLUS;}
    }

    private ArrayList<Integer> setList(ArrayList<Integer> list) {
        for (Integer entry : list) {
            charList.add(entry);
        }
        return charList;
    }
}
