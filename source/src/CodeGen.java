package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import src.SemanticAnalyzer.SymbolTable;
import src.SemanticAnalyzer.StackFrame;

public class CodeGen implements AstVisitor {

    private FnNode currentFn;
    private CodeGen self = this;
    private SymbolTable symbolTable;
    private int insNum, labelNum;
    private ArrayList<Tac> triplesArray = new ArrayList<>();
    private Stack<Integer> labelStack = new Stack<>();
    private HashMap<String,StackFrame> stackFrameMap;
    private StringBuilder targetCode = new StringBuilder();

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbolTable = prgrmNode.getSymbolTable();
        stackFrameMap = symbolTable.getStackFrames();
        write(Lex.BEGINPROLOGUE,"");
        FnNode main = symbolTable.getFunction("main"); currentFn = main;
        write(Lex.CONST,1,5); /* set fp */
        for (Node bodyNode : main.getBodyNodes()) {
            if (bodyNode.getName().equals("print")) {bodyNode.accept(this); /* to-do */}
            if (bodyNode instanceof CallNode) {bodyNode.accept(new FnBodyOpt());}
            else {
                write(Lex.CONST,main.getParamNodes().size()+1,6); /* set tos */ 
                write(Lex.ENDPROLOGUE,""); main.accept(this);
            }
        } for (int i=0;i<triplesArray.size();i++) {System.out.println(i+" "+triplesArray.get(i));}
        System.out.println(); genTargetCode(); System.out.println(targetCode);
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
        ifNode.getIf().accept(new CondHandler());
        ifNode.getThen().accept(this);
        if (!ifNode.getThen().isRecursive()) {write(Lex.RETURN,"");}
        write(Lex.ELSE,"");
    }

    @Override @SuppressWarnings("incomplete-switch")
    public void visit(BinaryNode binNode) throws Analyzer {

        Lex rType = binNode.getRight().nodeType();
        MakeReg reg0 = new MakeReg(0);
        MakeReg reg1 = new MakeReg(1);

        binNode.getLeft().accept(reg0);
        if (rType!=Lex.LITERAL && rType!=Lex.ID) {
            binNode.getRight().accept(this);
            // to-do: add LDA IR to swap register 0 over to register 1,
            // then load the temporary value into register 0.
        } else {binNode.getRight().accept(reg1);}

        switch (binNode.nodeType()) {
            case PLUS: {write(Lex.PLUS,0,1); break;}
        }

        if (!currentFn.getName().equals("main")) {write(Lex.ENDCALL,"");}
    }

    class CondHandler implements AstVisitor {

        @Override @SuppressWarnings("incomplete-switch")
        public void visit(BinaryNode binNode) throws Analyzer {

            Lex rType = binNode.getRight().nodeType();
            MakeReg reg0 = new MakeReg(0);
            MakeReg reg1 = new MakeReg(1);
            binNode.getLeft().accept(reg0);
            if (rType!=Lex.LITERAL && rType!=Lex.ID) {
                binNode.getRight().accept(self);
            } else {binNode.getRight().accept(reg1);}

            switch (binNode.nodeType()) {
                case EQUIVALENT: {
                    if (reg0.isZero) {
                        write(Lex.IF,Lex.EQUIVALENT,1);
                    }
                    else if (reg1.isZero) {
                        write(Lex.IF,Lex.EQUIVALENT,0);
                    }
                    else {
                        write(Lex.MINUS,0,1); 
                        write(Lex.IF,Lex.EQUIVALENT,0);
                    } break;
                }
            }
        }
    }

    class MakeReg implements AstVisitor {

        private int loc;
        private Boolean isZero = false;

        public MakeReg(int loc) {this.loc=loc;}

        @Override
        public void visit(IdNode idNode) {
            int dex = stackFrameMap.get(currentFn.getName()).getParamIndex(idNode.getName());
            write(Lex.LOAD,loc,dex);
        }

        @Override
        public void visit(LitNode litNode) {
            if (litNode.getName().equals("0")) {isZero=true;}
            else {write(Lex.CONST,litNode.getName(),loc);}
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
            StackFrame frame = stackFrameMap.get(callNode.getName());
            // store fp to this frame's control link
            write(Lex.STORE,5,0);
            write(Lex.CONST,frame.size(),6); /* set tos */
            write(Lex.BEGINCALL,"");
            write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
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

    @SuppressWarnings("incomplete-switch")
    private void genTargetCode() {
        Tac prevTac = new Tac();
        String actName="", prevActName="", lastLoadedParam="";
        StackFrame frame = stackFrameMap.get("main"), prevFrame = frame;
        for (Tac tac : triplesArray) {
            switch (tac.op) {
                case ENTRY: {
                    targetCode.append("* "+tac.arg1+"\n");
                    frame=stackFrameMap.get(actName); break;
                }
                case BEGINPROLOGUE: {targetCode.append("* prologue\n"); break;}
                case ENDPROLOGUE: {targetCode.append("\n"); if (!actName.isBlank()) {prevFrame=null;} break;}
                case CALL: {
                    if (actName.isBlank()) {actName = tac.arg1+"";}
                    else {prevActName=actName; actName=tac.arg1+"";} break;
                }
                case CONST: {targetCode.append(insNum+": LDC "+tac.arg2+","+tac.arg1+"(0)\n"); insNum++; break;}
                case IF: {
                    if (tac.arg1==Lex.EQUIVALENT) {
                        targetCode.append(insNum+": JNE "+tac.arg2+",*"); labelStack.push(targetCode.length());
                        targetCode.append("(4)\n"); insNum++;
                    } break;
                }
                case ELSE: {int label = labelStack.pop(); targetCode.replace(label-1,label,insNum+""); break;}
                case EQUIVALENT: {targetCode.append(insNum+": JNE "+tac.arg1+",*(4)\n"); insNum++; break;}
                case PLUS: {targetCode.append(insNum+": ADD 0,"+tac.arg1+","+tac.arg2+"\n"); insNum++; break;}
                case EXIT: {break;}
                case MINUS: {break;}
                case LOAD: {
                    targetCode.append(insNum+": LD "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++;
                    String param = frame.getParam((int)tac.arg2);
                    if (!param.isBlank()) {lastLoadedParam=param;} break;
                }
                case STORE: {targetCode.append(insNum+": ST "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case TEMP: {break;}
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
                case RETURN: {
                    targetCode.append(
                        insNum+": LD 1,0(5)\n"+
                        (insNum+1)+": SUB 2,1,5\n"+
                        (insNum+2)+": JNE 2,"+(insNum+4)+"(4)\n"+
                        (insNum+3)+": LDA, 7,*(4)\n"
                    );
                    if (prevFrame==null) {
                        targetCode.append(
                            (insNum+4)+": LDA 6,0(5)\n"+
                            (insNum+5)+": LDA 5,0(1)\n"+
                            (insNum+6)+": ST 0,"+frame.getParamIndex(lastLoadedParam)+"(5)\n"+
                            (insNum+7)+": LD 7,1(5)\n"
                        );
                    } insNum+=8; break;
                }
            } prevTac=tac;
        }
    }

    private class Tac {

        private Lex op;
        private Object arg1, arg2;

        public Tac(Lex op, Object arg1, Object arg2) {this.op=op; this.arg1=arg1; this.arg2=arg2;}
        public Tac(Lex op, Object arg1) {this.op=op; this.arg1=arg1;}
        public Tac() {}
        public String toString() {if (arg2!=null) {return op+" "+arg1+" "+arg2;} return op+" "+arg1;}
    }

    private void write(Lex op, Object arg1, Object arg2) {triplesArray.add(new Tac(op, arg1, arg2));}
    private void write(Lex op, Object arg1) {triplesArray.add(new Tac(op, arg1));}
}