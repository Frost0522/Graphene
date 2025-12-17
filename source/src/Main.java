package src;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Main {

    private String scriptType;
    
    public Main(String s) {scriptType = s;}
    public static String[] arguments;

    public static void main(String[] args) throws IOException, Analyzer {

        arguments = args;
        if (!args[0].substring(args[0].length()-3).equals(".gr")) {args[0]+=".gr";}
        String fileName = args[0].substring(Math.max(args[0].lastIndexOf("/"),args[0].lastIndexOf("\\"))+1);
        String fileNameTmExt = fileName.substring(0,fileName.length()-3)+
        fileName.substring(fileName.length()-3,fileName.length()).replace(".gr",".tm");
        File file = new File(args[0]); Main input = new Main(args[1]);
        if (!file.exists()) {throw new Analyzer("File does not exist.");}

        PushbackReader pushReader = new PushbackReader(new FileReader(file));
        switch (input.scriptType) {
            case "graphenes": {
                System.out.println(new Scanner(pushReader));
                break;
            }
            case "graphenef": {
                new Parser(new Scanner(pushReader));
                System.out.println(true);
                break;
            }
            case "graphenep": {
                AstPrinter astPrinter = new AstPrinter();
                new Parser(new Scanner(pushReader)).nStack.pop().accept(astPrinter);
                System.out.println(astPrinter);
                break;
            }
            case "graphenev": {
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                new Parser(new Scanner(pushReader)).nStack.peek().accept(semanticAnalyzer);
                System.out.println(semanticAnalyzer);
                break;
            }
            case "graphenec": {
                Parser parser = new Parser(new Scanner(pushReader));
                parser.nStack.peek().accept(new SemanticAnalyzer());
                parser.nStack.peek().accept(new CodeGen());
                // FileWriter writer = new FileWriter("../bin/"+fileNameTmExt);
                // writer.write(codeGen.getTargetCode()); writer.close();
                break;
            } 
            default: {pushReader.close(); throw new Analyzer("Unrecognized script type.");}
        } pushReader.close();
    }
}