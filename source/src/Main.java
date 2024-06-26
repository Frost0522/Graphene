package src;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Main {
    private String scriptType;

    public Main(String s) {
        scriptType = s;
    }

    public static void main(String[] args) throws IOException, Analyzer {
        
        Main input = new Main(args[1]);

        try {if (!new File(args[0]+".gr").exists()) {throw new Analyzer("File does not exist.");}} 
        catch (Analyzer err) {System.out.println(err.getMessage()); System.exit(0);}

        FileReader grapheneFile = new FileReader(args[0] + ".gr");
        PushbackReader pushReader = new PushbackReader(grapheneFile);
        switch (input.scriptType) {
            case "graphenes": {
                new Scanner(pushReader);
                break;
            }
            case "graphenef": {
                Scanner scanner = new Scanner(pushReader);
                new Parser(scanner);
                break;
            }
            case "graphenep": {
                Scanner scanner = new Scanner(pushReader);
                Parser parser = new Parser(scanner);
                System.out.println(new AstPrinter(parser.nStack));
                break;
            }
            default: {
                pushReader.close();
                try {throw new Analyzer("Unrecognized script type.");} 
                catch (Analyzer err) {System.out.println(err.getMessage()); System.exit(0);}
            }
        }
        pushReader.close();
    }
}   

