package src;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Main {
    private String scriptType;

    public Main(String s) {
        scriptType = s;
    }

    public String getScriptType() {
        return scriptType;
    }

    public static void main(String[] args) throws IOException, Analyzer {
        Main input = new Main(args[1]);
        try {
            FileReader grapheneFile = new FileReader(args[0] + ".gr");
            PushbackReader pushReader = new PushbackReader(grapheneFile);
            switch (input.scriptType) {
                case "graphenes": {
                    Scanner_v2 scanner = new Scanner_v2(pushReader);
                    break;
                }
                case "graphenef": {
                    Parser parser = new Parser(new Scanner_v2(pushReader));
                    break;
                }
                default: {
                    pushReader.close();
                    throw new Analyzer("Unrecognized script type.");
                }
            }
            pushReader.close();
        }
        catch (Analyzer err) {
            System.out.println(err.getMessage());
        }
    }
}
