package src;
import java.util.ArrayList;
import java.util.HashMap;
import src.SemanticAnalyzer.SymbolTable;

public class CodeGen implements AstVisitor {
    
    private CodeGen self = this;
    private StringBuilder tm = new StringBuilder();
    private HashMap<String,StackFrame> stackFrameMap;
    private StackFrame frame = new StackFrame();
    private SymbolTable symbolTable;
    private int insNum = 0;

    public String getTargetCode() {return tm.toString();}

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbolTable = prgrmNode.getSymbolTable();
        stackFrameMap = symbolTable.getStackFrames();
        frame = stackFrameMap.get("main");
        tm.append("* prologue\n");
        tm.append(insNum+": LDC 5,1(0)\n"); insNum++;
        tm.append(insNum+": LDC 6,"+(frame.size()+1)+"(0)\n\n"); insNum++;
        for (String fnName : stackFrameMap.keySet()) {
            symbolTable.getFunction(fnName).accept(this);
        } genTM(stackFrameMap.get("main").getIR());
        for (StackFrame f : stackFrameMap.values()) {
            for (String s : f.getTM()) {tm.append(s);}
        }
        tm.append("* epilogue\n");
        tm.append(insNum+": OUT 0,0,0\n"); insNum++;
        tm.append(insNum+": HALT 0,0,0");

        for (StackFrame f : stackFrameMap.values()) {
            for (Tac tac : f.getIR()) {System.out.println(tac);} 
            System.out.println();
        }
        
        System.out.println(tm);
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        String fnName = fnNode.getName(); frame = stackFrameMap.get(fnName);
        write(Lex.ENTRY,fnName);
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
        write(Lex.EXIT,fnName);
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        write(Lex.BEGINCALL,"");
        if (callNode.getName().equals("print")) {
            for (Node arg : callNode.getArgs()) {arg.accept(this);}
            write(Lex.PRINT,0); write(Lex.ENDCALL,"");
        } else {
            if (callNode.getName().equals(frame.getName())) {
                for (int i=0;i<callNode.getArgs().size();i++) {
                    Node arg = callNode.getArgs().get(i);
                    if (!arg.getName().equals(frame.getParamName(i))) {
                        arg.accept(this);
                        if (i!=callNode.getArgs().size()-1) {write(Lex.ALLOC,0); frame.addTmp();}
                        else {write(Lex.STORE,0,i);}
                    }
                }
                for (int i=callNode.getArgs().size()-2;i>=0;i--) {
                    if (!callNode.getArgs().get(i).getName().equals(frame.getParamName(i))) {
                        write(Lex.DALLOC,0); frame.removeTmp(); write(Lex.STORE,0,i);
                    }
                } write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
                write(Lex.ENDCALL,"");
            } else {
                for (int i=0;i<callNode.getArgs().size();i++) {
                    callNode.getArgs().get(i).accept(this); write(Lex.STORE,0,frame.size()+i);
                } write(Lex.STORE,4,callNode.getArgs().size()+frame.size());
                write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
                write(Lex.ENDCALL,"");
            }
        }
    }

    @Override
    public void visit(IfNode ifNode) throws Analyzer {
        ifNode.getIf().accept(this); write(Lex.IF,0); ifNode.getThen().accept(this);
        write(Lex.ELSE,""); ifNode.getElse().accept(this);
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {

        Lex lType = binNode.getLeft().nodeType();
        Lex rType = binNode.getRight().nodeType();

        if (!(lType==Lex.ID || lType==Lex.LITERAL) &&
            !(rType==Lex.ID || rType==Lex.LITERAL)) {
            binNode.getLeft().accept(this);
            write(Lex.ALLOC,0); frame.addTmp();
            binNode.getRight().accept(this);
            write(Lex.MOV,0,1);
            if (frame.getTmpSize()!=0) {write(Lex.DALLOC,0); frame.removeTmp();}
        } 
        else if ((lType==Lex.ID || lType==Lex.LITERAL) &&
                   !(rType==Lex.ID || rType==Lex.LITERAL)) {
            binNode.getRight().accept(this); write(Lex.MOV,0,1); binNode.getLeft().accept(this);
        }
        else {binNode.getLeft().accept(this); new R1(binNode.getRight());}
        write(binNode.nodeType(),0);
    }

    @Override
    public void visit(ExpNode expNode) throws Analyzer {expNode.getNode().accept(this);}

    @Override
    public void visit(NotNode notNode) throws Analyzer {
        notNode.getNode().accept(this); write(notNode.nodeType(),0);
    }

    @Override
    public void visit(IdNode idNode) {write(Lex.LOAD,0,frame.getParamIndex(idNode.getName()));}

    @Override
    public void visit(LitNode litNode) {
        if (litNode.getSign()==Lex.MINUS) {write(Lex.CONST,-litNode.getValue(),0);}
        else {write(Lex.CONST,litNode.getValue(),0);}
    }

    public class Tac {

        private Lex op;
        private Object arg1, arg2;

        public Tac(Lex op, Object arg1, Object arg2) {this.op=op; this.arg1=arg1; this.arg2=arg2;}
        public Tac(Lex op, Object arg1) {this.op=op; this.arg1=arg1;}
        public Tac(Object arg1) {this.arg1=arg1;}
        public String toString() {
            if (arg2!=null) {return op+" "+arg1+" "+arg2;}
            else if (op!=null) {return op+" "+arg1.toString();}
            return arg1.toString();
        }
    }

    private class R1 implements AstVisitor {
            
        public R1(Node n) throws Analyzer {
            if (n.nodeType()!=Lex.ID&&n.nodeType()!=Lex.LITERAL) {n.accept(self);}
            else {n.accept(this);}
        }

        @Override
        public void visit(LitNode litNode) {
            if (litNode.getSign()==Lex.MINUS) {write(Lex.CONST,-litNode.getValue(),1);}
            else {write(Lex.CONST,litNode.getValue(),1);}
        }

        @Override
        public void visit(IdNode idNode) {write(Lex.LOAD,1,frame.getParamIndex(idNode.getName()));}
    }

    @SuppressWarnings("incomplete-switch")
    private void genTM(ArrayList<Tac> tacLst) {
        StackFrame prevFrame; int depth=0;
        ArrayList<Integer> rtrnLocs = new ArrayList<>(), labels = new ArrayList<>();
        for (Tac tac : tacLst) {
            switch (tac.op) {
                case CONST: {emit("LDC "+tac.arg2+","+tac.arg1+"(0)\n"); break;}
                case STORE: {emit("ST "+tac.arg1+","+tac.arg2+"(5)\n"); break;}
                case LOAD: {emit("LD "+tac.arg1+","+tac.arg2+"(5)\n"); break;}
                case PLUS: {emit("ADD "+tac.arg1+",0,1\n"); break;}
                case MINUS: {emit("SUB "+tac.arg1+",0,1\n"); break;}
                case TIMES,AND: {emit("MUL "+tac.arg1+",0,1\n"); break;}
                case DIVIDE: {emit("DIV "+tac.arg1+",0,1\n"); break;}
                case MOV: {emit("LDA "+tac.arg2+",0("+tac.arg1+")\n"); break;}
                case PRINT: {emit("OUT "+tac.arg1+",0,0\n"); break;}
                case IF: {emit("JEQ "+tac.arg1+",$(4)\n"); labels.add(frame.getTM().size()-1); break;}
                case BEGINCALL: {depth++; break;}
                case ENDCALL: {depth--; break;}
                case ELSE: {
                    /* TO-DO: Fix dupilcate returns */
                    int label = labels.removeLast();
                    String newCode = frame.getTM().get(label).replace("$",""+(insNum+1));
                    frame.getTM().set(label, newCode); emit("LDA 7,$(4)\n");
                    rtrnLocs.add(frame.getTM().size()-1); break;
                }
                case NOT: {
                    emit("JEQ 0,2(7)\n"); emit("LDC 0,0(0)\n"); emit("LDA 7,1(7)\n"); 
                    emit("LDC 0,1(0)\n"); break;
                }
                case EQUIVALENT: {
                    emit("SUB 0,0,1\n"); emit("JEQ 0,2(7)\n"); emit("LDC 0,0(0)\n"); 
                    emit("LDA 7,1(7)\n"); emit("LDC 0,1(0)\n"); break;
                }
                case LESSTHAN: {
                    emit("SUB 0,0,1\n"); emit("JLT 0,2(7)\n"); emit("LDC 0,0(0)\n");
                    emit("LDA 7,1(7)\n"); emit("LDC 0,1(0)\n"); break;
                }
                case OR: {
                    emit("ADD "+tac.arg1+",0,1\n"); emit("JEQ 0,1(7)\n"); 
                    emit("LDC 0,1(0)\n"); break;
                }
                case ALLOC: {
                    frame.addTmp(); emit("ST "+tac.arg1+",0(6)\n");
                    emit("LDA 6,1(6)\n"); break;
                }
                case DALLOC: {
                    emit("LD "+tac.arg1+",-1(6)\n"); frame.removeTmp();
                    emit("LDA 6,-1(6)\n"); break;
                }
                case CALL: {
                    StackFrame callee = stackFrameMap.get(tac.arg1); prevFrame = frame;
                    if (callee.getIns()==0) {
                        emit("LDA 5,0(6)\n"); emit("LDA 6,"+((int)tac.arg2+1)+"(6)\n");
                        genTM(callee.getIR()); frame = prevFrame;
                        emit("LDA 6,0(5)\n"); emit("LDA 5,-"+frame.size()+"(5)\n");
                    } else if (depth==1&&tac.arg1.equals(frame.getName())) {
                        emit("LDA 7,"+callee.getIns()+"(4)\n");
                    } else {
                        if (tac.arg1.equals(frame.getName())) {
                            emit("LD 0,"+callee.getStateLoc()+"(5)\n");
                            emit("ST 0,0(6)\n"); frame.addTmp();
                            emit("LDA 6,1(6)\n");
                            emit("LDA 0,2(7)\n"); 
                            emit("ST 0,"+callee.getStateLoc()+"(5)\n");
                            emit("LDA 7,"+callee.getIns()+"(4)\n");
                        } else {
                            emit("LDA 0,4(7)\n"); emit("LDA 5,0(6)\n"); 
                            emit("LDA 6,"+((int)tac.arg2+1)+"(6)\n"); emit("ST 0,"+callee.getStateLoc()+"(5)\n");
                            emit("LDA 7,"+callee.getIns()+"(4)\n"); emit("LDA 6,0(5)\n"); 
                            emit("LDA 5,-"+frame.size()+"(5)\n");
                        }
                    } break;
                }
                case ENTRY: {
                    frame = stackFrameMap.get(tac.arg1); comment(tac.arg1+"\n");
                    frame.setIns(insNum); break;
                }
                case EXIT: {
                    for (int loc : rtrnLocs) {
                        String newCode = frame.getTM().get(loc).replace("$",""+insNum);
                        frame.getTM().set(loc, newCode);
                    }
                    if (!frame.getName().equals("main")) {
                        emit("LD 1,"+frame.getStateLoc()+"(5)\n"); emit("JEQ 1,4(7)\n");
                        emit("LD 2,-1(6)\n"); frame.removeTmp();
                        emit("LDA 6,-1(6)\n"); emit("ST 2,"+frame.getStateLoc()+"(5)\n");
                        emit("LDA 7,0(1)\n");
                    } frame.addTM("\n"); break;
                }
            }
        }
    }

    private void write(Lex op, Object arg1, Object arg2) {frame.addIR(new Tac(op, arg1, arg2));}
    private void write(Lex op, Object arg1) {frame.addIR(new Tac(op, arg1));}
    private void emit(String code) {frame.addTM(insNum+": "+(new Tac(code))); insNum++;}
    private void comment(String code) {frame.addTM("* "+new Tac(code));}
}