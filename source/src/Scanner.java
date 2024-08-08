package src;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Scanner {
    
    public ArrayList<State> stateLst = new ArrayList<>();
    public ArrayList<Integer> charLst = new ArrayList<>();
    public ArrayList<Token> tokenLst = new ArrayList<>();
    public int line = 1;
    public int column = 1;
    public static PushbackReader pushReader;

    public Scanner(PushbackReader reader) throws Analyzer, IOException {
        pushReader = reader;
        Boolean run = true;

        stateLst.add(new State_0());
        stateLst.add(new State_1());
        stateLst.add(new State_2());
        stateLst.add(new State_3());
        stateLst.add(new State_4());
        stateLst.add(new State_5());
        
        State startState = stateLst.get(0);
        while (run) {
            try {
                Token token = startState.process(stateLst, charLst, Scanner.next());
                if (token.getType().equals(Lex.NL)) {++line; column = 1;}
                else if (token.getType().equals(Lex.SKIP)) {++column;}
                else {
                    token.column = column;
                    token.line = line;
                    column += token.getCharList().size();
                    tokenLst.add(token);
                }
                if (token.getType().equals(Lex.$)) {
                    run = false;
                }
                charLst.clear();
            }
            catch (Analyzer err) {
                System.out.println("Line " + line + " Column " + column + "\n" + err.getMessage() + "\n");
                System.exit(0);
            }
        }
    }

    public static int next() throws IOException {
        return pushReader.read();
    }

    public static int peek() throws IOException {
        int value = pushReader.read();
        pushReader.unread(value);
        return value;
    }
}