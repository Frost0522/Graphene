package src;
import java.util.ArrayList;

public class Token {
    public int line;
    public int column;
    private Lex type;
    private ArrayList<Integer> charList = new ArrayList<>();

    public Token(Lex type, ArrayList<Integer> charList) {
        this.type = type;
        this.charList = setList(charList);
    }

    public Lex getType() {
        return type;
    }

    public String getName() {
        StringBuilder builder = new StringBuilder();
        for (int i : charList) {
            builder.append((char)i);
        }
        return builder.toString();
    }

    public ArrayList<Integer> getCharList() {
        return charList;
    }

    public int getSize() {
        return charList.size();
    }

    private ArrayList<Integer> setList(ArrayList<Integer> list) {
        for (Integer entry : list) {
            charList.add(entry);
        }
        return charList;
    }
}
