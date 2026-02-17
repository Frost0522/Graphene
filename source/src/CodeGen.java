package src;
import java.util.ArrayList;
import java.util.HashMap;
import src.SemanticAnalyzer.SymbolTable;

public class CodeGen implements AstVisitor {

    private int insNum=0;
    private FnNode currentFn;
    private CodeGen self = this;
    private SymbolTable symbolTable;
    private ArrayList<Tac> triplesArray = new ArrayList<>();
    private HashMap<String,StackFrame> stackFrameMap = new HashMap<>();
    private StringBuilder targetCode = new StringBuilder();

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbolTable=prgrmNode.getSymbolTable(); write(Lex.BEGINPROLOGUE,"");
        for (Node fnNode : prgrmNode.getFunctions()) {new GenStackFrames(fnNode);}
        FnNode main = symbolTable.getFunction("main"); currentFn = main;
        write(Lex.CONST,1,5); /* set fp */
        for (Node bodyNode : main.getBodyNodes()) {
            if (bodyNode.getName().equals("print")) {bodyNode.accept(this); /* to-do */}
            if (bodyNode instanceof CallNode) {bodyNode.accept(new FnBodyOpt());}
            else {
                write(Lex.CONST,main.getParamNodes().size()+1,6); /* set tos */ 
                write(Lex.ENDPROLOGUE,""); main.accept(this);
            }
        }
        for (int i=0;i<triplesArray.size();i++) {System.out.println(i+" "+triplesArray.get(i));}
        System.out.println(); genTargetCode();
        System.out.println(targetCode);
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        currentFn = fnNode; write(Lex.ENTRY,fnNode.getName());
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
        write(Lex.EXIT,fnNode.getName());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        write(Lex.BEGINCALL,"");
        write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
        if (callNode.getName().equals("print")) {/* to-do */}
        else {} write(Lex.ENDCALL,"");
    }

    @Override
    public void visit(IfNode ifNode) throws Analyzer {
        ifNode.accept(this);
    }

    @Override @SuppressWarnings("incomplete-switch")
    public void visit(BinaryNode binNode) throws Analyzer {

        Lex leftType = binNode.getLeft().nodeType();
        Lex rightType = binNode.getRight().nodeType();

        switch (binNode.nodeType()) {
            case EQUIVALENT: {

                break;
            }
        }
    }

    // If a function contains a function call it is essentially that function.
    // Swap the arguments, in memory, of the current function to the next and
    // go to that next function. Note that this operation saves space on the call
    // stack but increases the number of instructions.
    class FnBodyOpt implements AstVisitor {

        private ArrayList<Integer> literals = new ArrayList<>();

        @Override
        public void visit(CallNode callNode) throws Analyzer {
            ArrayList<Node> params = currentFn.getParamNodes();
            ArrayList<Node> args = callNode.getArgs();
            write(Lex.CONST,args.size()+1,6); /* set tos */
            Boolean allLiterals = true;
            for (Node arg : args) {if (arg.nodeType()!=Lex.LITERAL) {allLiterals = false; break;}}
            if (allLiterals) {for (int i=0;i<args.size();i++) {literals.add(i);}}
            else {
                literals = new ArrayList<>();
                if (params.size()>1) {swap(params, args);}
                else if (params.size()==1) {mov(params, args);}
            }
            for (int argLoc : literals) {
                write(Lex.CONST,args.get(argLoc).getName(),0); write(Lex.STORE,0,argLoc);
            }
            if (currentFn.getName().equals("main")) {write(Lex.ENDPROLOGUE,"");}
            self.symbolTable.getFunction(callNode.getName()).accept(self);
        }

        private void swap(ArrayList<Node> params, ArrayList<Node> args) {
            for (int i=0;i<params.size();i++) {
                for (int j=0;j<args.size();j++) {
                    if (args.get(j) instanceof LitNode) {literals.add(j);}
                    else if (args.get(j).getName().equals(params.get(i).getName())) {write(Lex.SWAP,i,j);}
                }
            }
        }

        private void mov(ArrayList<Node> params, ArrayList<Node> args) {
            for (int i=0;i<args.size();i++) {
                if (args.get(i) instanceof LitNode) {literals.add(i);}
                else if (args.get(i).getName().equals(params.getFirst().getName())) {write(Lex.MOV,0,i);}
            }
        }
    }

    class GenStackFrames implements AstVisitor {

        public GenStackFrames(Node fnNode) throws Analyzer {fnNode.accept(this);}

        @Override
        public void visit(FnNode fnNode) throws Analyzer {
            StackFrame frame = new StackFrame(); frame.name = fnNode.getName();
            frame.size = fnNode.getParamNodes().size()+1;
            frame.paramNodes = fnNode.getParamNodes();
            stackFrameMap.put(fnNode.getName(),frame);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void genTargetCode() {
        for (Tac tac : triplesArray) {
            switch (tac.op) {
                case ENTRY: {targetCode.append("* "+tac.arg1+"\n"); break;}
                case BEGINPROLOGUE: {targetCode.append("* prologue\n"); break;}
                case ENDPROLOGUE: {targetCode.append("\n"); break;}
                case CALL: {break;}
                case ASSIGN: {break;}
                case CONST: {targetCode.append(insNum+": LDC "+tac.arg2+","+tac.arg1+"(0)\n"); insNum++; break;}
                case IF: {break;}
                case EQUIVALENT: {break;}
                case GOTO: {break;}
                case COND: {break;}
                case LABEL: {break;}
                case PLUS: {break;}
                case EXIT: {break;}
                case MINUS: {break;}
                case STORE: {targetCode.append(insNum+": ST "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case MOV: {
                    targetCode.append(
                        insNum+": LD 0,"+tac.arg1+"(5)\n"+
                        (insNum+1)+": ST 0,"+tac.arg2+"(5)\n"
                    ); insNum+=2; break;
                }
                case SWAP: {
                    targetCode.append(
                        insNum+": LD 0,"+tac.arg1+"(5)\n"+
                        (insNum+1)+": LD 1,"+tac.arg2+"(5)\n"+
                        (insNum+2)+": ST 0,"+tac.arg2+"(5)\n"+
                        (insNum+3)+": ST 1,"+tac.arg1+"(5)\n"
                    ); insNum+=4; break;
                }
            }
        }
    }

    private class Tac {

        private Lex op;
        private String arg1, arg2;

        public Tac(Lex op, Object arg1, Object arg2) {this.op=op; this.arg1=arg1+""; this.arg2=arg2+"";}
        public Tac(Lex op, Object arg1) {this.op=op; this.arg1=arg1+"";}
        public String toString() {if (arg2!=null) {return op+" "+arg1+" "+arg2;} return op+" "+arg1;}
    }

    private class StackFrame {

        private String name;
        private int size;
        private ArrayList<Node> paramNodes;

        public StackFrame() {paramNodes = new ArrayList<>();}

        public int getParamIndex(String idName) {return symbolTable.getIdIndex(name,idName);}
    }

    private void write(Lex op, Object arg1, Object arg2) {triplesArray.add(new Tac(op, arg1, arg2));}
    private void write(Lex op, Object arg1) {triplesArray.add(new Tac(op, arg1));}
}