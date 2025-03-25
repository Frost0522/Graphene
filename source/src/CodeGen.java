package src;
import java.util.ArrayList;
import java.util.Stack;
import src.SymbolTable.FunctionSymbol;

public class CodeGen implements AstVisitor {

    private SymbolTable symbolTable;
    private Stack<StackFrame> callStack = new Stack<>();
    private StringBuilder imem = new StringBuilder();
    private int fp, counter;

    private void makeFrame(String name) throws Analyzer {
        callStack.push(new StackFrame(symbolTable.get(name)));
    }

    protected String getImem() {return imem.toString();}
    
    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbolTable = prgrmNode.getSymbolTable(); fp = 1;
        makeFrame("main");
        for (String fnName : symbolTable.getFnNames()) {
            if (!fnName.equals("main")) {makeFrame(fnName);}
        }
        for (StackFrame frame : callStack) {
            imem.append(frame.code);
        }
    }

    class StackFrame implements AstVisitor {

        private StringBuilder code = new StringBuilder();
        private int currentInt, controlLink;
        private String frameName;
        private ArrayList<Integer> staticData;

        public StackFrame(FunctionSymbol symbol) throws Analyzer {
            staticData = symbol.getStaticData();
            frameName = symbol.getFnNode().getName();
            if (frameName.equals("main")) {
                // Check symbol for parameters to allocate.
                for (Node param : symbol.getParamNodes()) {/* to-do */}
                if (symbol.getStaticData().size()<6) {
                    Integer register = 0;
                    for (Integer i : symbol.getStaticData()) {
                        code.append(counter+": LDC "+register+","+i+"(0)\n"); counter++;
                        register++;
                    }
                }
                code.append(counter+": LDA 6,1(7)\n"); counter++;
                code.append(counter+": LDA 7,5(1)\n"); counter++;
                code.append(counter+": OUT 0,0,0\n"); counter++;
                code.append(counter+": HALT 0,0,0\n"); counter++;
                for (Node node : symbol.getFnNode().getBodyNodes()) {node.accept(this);}
            }
        }

        @Override
        public void visit(CallNode callNode) throws Analyzer {
            code.append(counter+": ST 6,"+fp+"(1)\n"); counter++;
            controlLink = fp; fp++;
            code.append(counter+": LDA 6,1(7)\n"); counter++;
            code.append(counter+": LDA 7,"+(counter+2)+"(1)\n"); counter++;
            code.append(counter+": LD 7,1("+controlLink+")\n"); counter++;
            if (callNode.getName().equals("print")) {
                Node argNode = callNode.getArgs().getFirst();
                argNode.accept(this);
                if (argNode.getSemanticType().equals(Lex.INTEGER)) {
                    if (staticData.contains(currentInt)) {
                        code.append(counter+": OUT "+staticData.indexOf(currentInt)+",0,0\n"); counter++;
                    }
                    else {
                        code.append(counter+": LDC 4,"+currentInt+"(0)\n"); counter++;
                        code.append(counter+": OUT 4,0,0\n"); counter++;    
                    }
                }
            code.append(counter+": LDA 7,0(6)\n"); counter++;
            }
        }

        @Override
        public void visit(LitNode litNode) throws Analyzer {
            currentInt = litNode.getValue();
        }
    }
}