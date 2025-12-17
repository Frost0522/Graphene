package src;
import java.util.ArrayList;
import java.util.Stack;
import src.SemanticAnalyzer.SymbolTable;

public class CodeGen implements AstVisitor {

    private Stack<StackFrame> callStack = new Stack<>();
    private ArrayList<Tac> triplesArray = new ArrayList<>();
    private int fp, tos, labelNum;
    private SymbolTable symbolTable;
    private StackFrame frame;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        fp=1; tos=Main.arguments.length-1; labelNum=0;
        symbolTable = prgrmNode.getSymbolTable();
        // Create the stack frame of main and add it to the callstack.
        callStack.push(new StackFrame());
        symbolTable.getFunction("main").accept(this);
        for (int i=0;i<triplesArray.size();i++) {System.out.println(i+" "+triplesArray.get(i));}
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        frame = callStack.peek();
        write(Lex.ENTRY,fnNode.getName(),"");
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        write(Lex.CALL,callNode.getName(),callNode.getArgs().size()); advFrame();
        for (Node argNode : callNode.getArgs()) {argNode.accept(this);}
        write(Lex.BEGINCALL,callNode.getName(),"");
        callStack.push(new StackFrame(callNode));
        symbolTable.getFunction(callNode.getName()).accept(this);
    }

    @Override
    public void visit(IfNode ifNode) throws Analyzer {
        
    }

    @Override
    public void visit(LitNode litNode) {write(Lex.ASSIGN,litNode.getValue(),assignVal());}

    @Override
    public void visit(IdNode idNode) {
        // From the symbol table look up the index of the parameter in relation to this id.
        int dex = symbolTable.getIdIndex(frame.name,idNode.getName());
        write(Lex.ASSIGN,frame.args.get(dex),assignVal());
    }

    private void advFrame() {fp=tos;}
    private int assignVal() {int loc = tos; tos++; return loc;}
    private void write(Lex op, Object arg1, Object arg2) {triplesArray.add(new Tac(op, arg1, arg2));}
    private void write(Lex op, Object arg1, Tac arg2) {triplesArray.add(new Tac(op, arg1, arg2));}

    private class StackFrame {

        private String name;
        private int begin, end;
        private ArrayList<String> args = new ArrayList<>();

        public StackFrame() {
            name = "main";
            for (int i=2;i<Main.arguments.length;i++) {args.add(Main.arguments[i]);}
            begin=fp; end=tos;
        }

        public StackFrame(CallNode callNode) {
            name = callNode.getName();
            for (Node arg : callNode.getArgs()) {args.add(arg.getName());}
            begin=fp; end=tos;
        }

    }

    private class Tac {

        private Lex op;
        private String arg1, arg2;
        private Tac tac;

        public Tac(Lex op, Object arg1, Object arg2) {this.op=op; this.arg1=""+arg1; this.arg2=""+arg2;}
        public Tac(Lex op, Object arg1, Tac tac) {this.op=op; this.arg1=""+arg1; this.tac=tac;}
        public Boolean equals(Tac tac) {return (op==tac.op) && (arg1==tac.arg1) && (arg2==tac.arg2);}
        public String toString() {if (arg2.isEmpty()) {return op+" "+arg1;} return op+" "+arg1+" "+arg2;}
    }
}