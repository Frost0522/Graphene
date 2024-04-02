package src;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {
    
    public ArrayList<State> stateLst = new ArrayList<>();
    public ArrayList<Integer> charLst = new ArrayList<>();
    public ArrayList<Token> tokenLst = new ArrayList<>();
    public int line = 1;
    public int column = 1;
    public static HashMap<Lexical, ArrayList<Integer>> lexicalMap = new HashMap<>();
    public static PushbackReader pushReader;

    public Scanner(PushbackReader reader) throws Analyzer, IOException {
        pushReader = reader;
        Scanner.populateMap();
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
                if (token.getType().equals(Lexical.NEWLINE) && !tokenLst.isEmpty()) {
                    ++line;
                    column = 1;
                    tokenLst.add(token);
                }
                else if (token.getType().equals(Lexical.SKIPPABLE) && !tokenLst.isEmpty()) {
                    ++column;
                    tokenLst.add(token);
                }
                else {
                    column += token.getCharList().size();
                    tokenLst.add(token);
                }
                if (token.getType().equals(Lexical.$)) {
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

    public static void populateMap() {
        lexicalMap.put(Lexical.INTEGER, new IntegerList(new Integer[]{105, 110, 116, 101, 103, 101, 114}).get());
        lexicalMap.put(Lexical.BOOLEAN, new IntegerList(new Integer[]{98, 111, 111, 108, 101, 97, 110}).get());
        lexicalMap.put(Lexical.IF, new IntegerList(new Integer[]{105, 102}).get());
        lexicalMap.put(Lexical.ELSE, new IntegerList(new Integer[]{101, 108, 115, 101}).get());
        lexicalMap.put(Lexical.NOT, new IntegerList(new Integer[]{110, 111, 116}).get());
        lexicalMap.put(Lexical.AND, new IntegerList(new Integer[]{97, 110, 100}).get());
        lexicalMap.put(Lexical.FN, new IntegerList(new Integer[]{102, 110}).get());
        lexicalMap.put(Lexical.OR, new IntegerList(new Integer[]{111, 114}).get());
        lexicalMap.put(Lexical.TRUE, new IntegerList(new Integer[]{116, 114, 117, 101}).get());
        lexicalMap.put(Lexical.FALSE, new IntegerList(new Integer[]{102, 97, 108, 115, 101}).get());
        lexicalMap.put(Lexical.COMMA, new IntegerList(new Integer[]{44}).get());
        lexicalMap.put(Lexical.COLON, new IntegerList(new Integer[]{58}).get());
        lexicalMap.put(Lexical.RETURN, new IntegerList(new Integer[]{45, 62}).get());
        lexicalMap.put(Lexical.PLUS, new IntegerList(new Integer[]{43}).get());
        lexicalMap.put(Lexical.MINUS, new IntegerList(new Integer[]{45}).get());
        lexicalMap.put(Lexical.TIMES, new IntegerList(new Integer[]{42}).get());
        lexicalMap.put(Lexical.DIVIDE, new IntegerList(new Integer[]{47}).get());
        lexicalMap.put(Lexical.LESS_THAN, new IntegerList(new Integer[]{60}).get());
        lexicalMap.put(Lexical.EQUIVALENT, new IntegerList(new Integer[]{61, 61}).get());
    }
}