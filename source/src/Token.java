package src;
import java.util.ArrayList;

public class Token {
    public int line;
    public int column;
    private Lex type;
    private Lex symbol;
    private ArrayList<Integer> charList = new ArrayList<>();

    public Token(Lex type, ArrayList<Integer> charList) {
        this.type = type;
        this.charList = setList(charList);
        symbol = Lex.PLUS;
    }

    public Lex getType() {
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

    public Lex getSymbol() {
        return symbol;
    }

    public void flipSymbol() {
        if (symbol.equals(Lex.PLUS)) {symbol = Lex.MINUS;}
        else {symbol = Lex.PLUS;}
    }

    private ArrayList<Integer> setList(ArrayList<Integer> list) {
        for (Integer entry : list) {
            charList.add(entry);
        }
        return charList;
    }
}
