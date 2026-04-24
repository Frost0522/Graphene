package src;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Scanner {
    
    protected ArrayList<State> stateLst = new ArrayList<>();
    protected ArrayList<Integer> charLst = new ArrayList<>();
    protected ArrayList<Token> tokenLst = new ArrayList<>();
    protected int line = 1;
    protected int column = 1;
    protected static PushbackReader pushReader;

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
                else {token.column = column; token.line = line;
                    column += token.getCharList().size(); tokenLst.add(token);
                } if (token.getType().equals(Lex.$)) {run = false;} charLst.clear();
            } catch (Analyzer err) {
                System.out.println("Line " + line + " Column " + column + "\n" + err.getMessage() + "\n");
                System.exit(0);
            }
        }
    }

    protected static int next() throws IOException {return pushReader.read();}
    protected static int peek() throws IOException {
        int value = pushReader.read(); pushReader.unread(value); return value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Token t : tokenLst) {
            switch (t.getType()) {
                case FN,INTEGER,BOOLEAN,IF,ELSE: {builder.append("keyword "+t.getName().toLowerCase()+"\n"); break;}
                case TIMES,DIVIDE,MINUS,PLUS,LESSTHAN,EQUIVALENT,AND,OR,NOT: {builder.append("operator "+t.getName()+"\n"); break;}
                case ID: {builder.append("identifier "+t.getName()+"\n"); break;}
                case LEFTPAREN,RIGHTPAREN,COLON,COMMA,RETURN: {builder.append("punctuation "+t.getName()+"\n"); break;}
                case INTEGERLITERAL: {builder.append("integer literal "+t.getName()+"\n"); break;}
                case BOOLEANLITERAL: {builder.append("boolean literal "+t.getName()+"\n"); break;}
                case $: {builder.append("end of file $"); break;}
                default: break;
            }
        } return builder.toString().trim();
    }
}