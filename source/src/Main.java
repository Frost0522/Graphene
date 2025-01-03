package src;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PushbackReader;

public class Main {

    private String scriptType;
    
    public Main(String s) {scriptType = s;}

    public static void main(String[] args) throws IOException, Analyzer {

        if (!args[0].substring(args[0].length()-3).equals(".gr")) {args[0]+=".gr";}
        String fileName = args[0].substring(Math.max(args[0].lastIndexOf("/"),args[0].lastIndexOf("\\"))+1);
        String fileNameTmExt = fileName.substring(0,fileName.length()-3)+
        fileName.substring(fileName.length()-3,fileName.length()).replace(".gr",".tm");
        File file = new File(args[0]);
        Main input = new Main(args[1]);

        if (!file.exists()) {throw new Analyzer("File does not exist.");}

        PushbackReader pushReader = new PushbackReader(new FileReader(file));
        switch (input.scriptType) {
            case "graphenes": {
                Scanner scanner = new Scanner(pushReader);
                System.out.println(scanner);
                break;
            }
            case "graphenef": {
                Scanner scanner = new Scanner(pushReader);
                new Parser(scanner); System.out.println(true);
                break;
            }
            case "graphenep": {
                Scanner scanner = new Scanner(pushReader);
                Parser parser = new Parser(scanner);
                AstPrinter astPrinter = new AstPrinter();
                parser.nStack.pop().accept(astPrinter);
                System.out.println(astPrinter);
                break;
            }
            case "graphenev": {
                Scanner scanner = new Scanner(pushReader);
                Parser parser = new Parser(scanner);
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                parser.nStack.peek().accept(semanticAnalyzer);
                System.out.println(semanticAnalyzer);
                break;
            }
            case "graphenec": {
                Scanner scanner = new Scanner(pushReader);
                Parser parser = new Parser(scanner);
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                parser.nStack.peek().accept(semanticAnalyzer);
                CodeGen codeGen = new CodeGen();
                parser.nStack.peek().accept(codeGen);
                BufferedWriter writer = new BufferedWriter(new FileWriter("../bin/"+fileNameTmExt)); 
                writer.write(codeGen.getTargetStr()); writer.close();
                break;
            }
            default: {
                pushReader.close();
                throw new Analyzer("Unrecognized script type.");
            }
        }
        pushReader.close();
    }
}   

