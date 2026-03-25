package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import src.SemanticAnalyzer.SymbolTable;

public class CodeGen implements AstVisitor {

    private int insNum;
    private FnNode currentFn;
    private CodeGen self = this;
    private SymbolTable symbolTable;
    private HashMap<String,StackFrame> stackFrameMap;
    private ArrayList<Integer> rtrnLocs = new ArrayList<>();
    private ArrayList<Tac> triplesArray = new ArrayList<>();
    private StringBuilder targetCode = new StringBuilder();
    private Stack<Integer> labelStack = new Stack<>();
    private StackFrame frame = new StackFrame();

    public String getTargetCode() {return targetCode.toString();}

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbolTable = prgrmNode.getSymbolTable();
        stackFrameMap = symbolTable.getStackFrames();
        frame.clone(stackFrameMap.get("main"));
        write(Lex.BEGINPROLOGUE,"");
        write(Lex.CONST,1,5); /* set fp */
        write(Lex.CONST,frame.size()+1,6); /* set tos */
        write(Lex.ENDPROLOGUE,"");
        symbolTable.getFunction("main").accept(this);
        write(Lex.EPILOGUE,""); genTargetCode();
        // for (int i=0;i<triplesArray.size();i++) {System.out.println(i+" "+triplesArray.get(i));}
        // System.out.println(); genTargetCode(); 
        // System.out.println(targetCode);
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        currentFn=fnNode; frame.clone(stackFrameMap.get(fnNode.getName()));
        write(Lex.ENTRY,fnNode.getName());
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
        write(Lex.EXIT,fnNode.getName());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        StackFrame newFrame = new StackFrame().clone(stackFrameMap.get(callNode.getName()));
        write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
        if (callNode.getName().equals("print")) {/* to-do */}
        else if (frame.getName().equals(callNode.getName())) {
            for (int i=0;i<callNode.getArgs().size();i++) {
                Node arg = callNode.getArgs().get(i); 
                Lex argType = arg.nodeType(); 
                if (argType==Lex.ID||argType==Lex.LITERAL) {
                    arg.accept(this); /* arg is placed into reg 0 */
                    write(Lex.STORE,0,i); /* arg is stored to current frame */
                } else if (argType==Lex.FNCALL) /* case for nested functions */ {
                    arg.accept(new NestedCallHandler(newFrame));
                    write(Lex.RESTORE,newFrame.size());
                    write(Lex.STORE,0,i);
                } else /* case where temp vars are added to stack frames */ {
                    arg.accept(this); /* arg is placed into reg 0 */
                    write(Lex.STORE,0,newFrame.size()+i); makeTemp(newFrame);
                }
            } // restore temporaries to original locations
            for (int i=0;i<newFrame.getTmpSize();i++) {
                write(Lex.LOAD,0,newFrame.size()-1);
                write(Lex.STORE,0,i); removeTemp(newFrame);
            } write(Lex.GOTO,frame.getName());
        } else {
            // Push new frame and args on to the call stack.
            for (int i=0;i<callNode.getArgs().size();i++) {
                callNode.getArgs().get(i).accept(this); /* arg is placed into reg 0 */
                write(Lex.STORE,0,frame.size()+i); /* arg is stored to new frame */
            } // Set control link and state.
            // Make a call to the function to update fp and tos.
            write(Lex.BEGINCALL,"\n"); /* update fp and tos to new frame */
            symbolTable.getFunction(callNode.getName()).accept(this);
            write(Lex.ENDCALL,""); /* restore prev frame and pointers */
        }
    }

    @Override @SuppressWarnings("incomplete-switch")
    public void visit(IfNode ifNode) throws Analyzer {
        ifNode.getIf().accept(new CondHandler());
        ifNode.getThen().accept(this);
        switch (ifNode.getThen().nodeType()) {
            case PLUS,MINUS,DIVIDE,TIMES,EQUIVALENT,LESSTHAN,AND,OR,ID,LITERAL: {
                write(Lex.RETURN, ""); break;
            }
        }
        write(Lex.ELSE,""); 
        ifNode.getElse().accept(this);
        switch (ifNode.getElse().nodeType()) {
            case PLUS,MINUS,DIVIDE,TIMES,EQUIVALENT,LESSTHAN,AND,OR,ID,LITERAL: {
                write(Lex.RETURN, ""); break;
            }
        }
    }

    @Override @SuppressWarnings("incomplete-switch")
    public void visit(BinaryNode binNode) throws Analyzer {

        Lex rType = binNode.getRight().nodeType();
        Boolean rIsTerminal = (rType==Lex.ID || rType==Lex.LITERAL);

        if (!rIsTerminal) {
            binNode.getLeft().accept(this);
            write(Lex.STORE,0,makeTemp(frame));
            new Assign(binNode.getRight(),1);
            write(Lex.LOAD,0,removeTemp(frame));
        } else {
            binNode.getLeft().accept(this);
            new Assign(binNode.getRight(),1);
        }

        switch (binNode.nodeType()) {
            case PLUS: {write(Lex.PLUS,0,1); break;}
            case MINUS: {write(Lex.MINUS,0,1); break;}
        }
    }

    @Override
    public void visit(LitNode litNode) {
        write(Lex.CONST,litNode.getValue(),0);
    }

    @Override
    public void visit(IdNode idNode) {
        int i = stackFrameMap.get(currentFn.getName()).getParamIndex(idNode.getName());
        write(Lex.LOAD,0,i);
    }

    class NestedCallHandler implements AstVisitor {

        StackFrame outterFrame;

        public NestedCallHandler(StackFrame outterFrame) {this.outterFrame=outterFrame;}

        @Override
        public void visit(CallNode callNode) throws Analyzer {
            StackFrame newFrame = new StackFrame().clone(stackFrameMap.get(callNode.getName()));
            write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
            for (int i=0;i<callNode.getArgs().size();i++) {
                Node arg = callNode.getArgs().get(i); arg.accept(self);
                write(Lex.STORE,0,outterFrame.size()+i);
            } write(Lex.MALLOC,newFrame.size());
            write(Lex.GOTO,newFrame.getName());
        }

    }

    class Assign implements AstVisitor {

        private int loc;
            
        public Assign(Node node,Integer loc) throws Analyzer {
            Lex nType = node.nodeType(); this.loc=loc;
            if (nType!=Lex.ID&&nType!=Lex.LITERAL) {node.accept(self);}
            else {node.accept(this);}
        }

        @Override
        public void visit(LitNode litNode) {
            write(Lex.CONST,litNode.getValue(),loc);
        }

        @Override
        public void visit(IdNode idNode) {
            int i = stackFrameMap.get(currentFn.getName()).getParamIndex(idNode.getName());
            write(Lex.LOAD,loc,i);
        }
    }

    class CondHandler implements AstVisitor {

        @Override @SuppressWarnings("incomplete-switch")
        public void visit(BinaryNode binNode) throws Analyzer {

            // Special cases
            if (binNode.nodeType()==Lex.EQUIVALENT) {
                if (binNode.getLeft().getName().equals("0")) {
                    binNode.getRight().accept(self); write(Lex.IF,Lex.EQUIVALENT,0);
                } else if (binNode.getRight().getName().equals("0")) {
                    binNode.getLeft().accept(self); write(Lex.IF,Lex.EQUIVALENT,0);
                }
            } else {
                Lex rType = binNode.getRight().nodeType();
                if (rType!=Lex.ID&&rType!=Lex.LITERAL) {
                    binNode.getLeft().accept(self);
                    write(Lex.STORE,0,makeTemp(frame));
                    new Assign(binNode.getRight(),1);
                    write(Lex.LOAD,0,removeTemp(frame));
                } else {
                    binNode.getLeft().accept(self);
                    new Assign(binNode.getRight(),1);
                }

                switch (binNode.nodeType()) {
                    case EQUIVALENT: {
                        write(Lex.MINUS,0,1);
                        write(Lex.IF,Lex.EQUIVALENT,0);
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void genTargetCode() {
        StackFrame frame = new StackFrame().clone(stackFrameMap.get("main")),
        prevFrame = new StackFrame().clone(frame);
        for (Tac tac : triplesArray) {
            switch (tac.op) {
                case ENTRY: {targetCode.append("* "+tac.arg1+"\n"); frame.setIns(insNum); break;}
                case BEGINPROLOGUE: {targetCode.append("* prologue\n"); break;}
                case ENDPROLOGUE: {targetCode.append("\n"); break;}
                case EPILOGUE: {
                for (int loc : rtrnLocs) {targetCode.replace(loc, loc+1,insNum+"");}
                    targetCode.append(
                        "\n* epilogue\n"+
                        insNum+": OUT 0,0,0\n"+
                        (insNum+1)+": HALT 0,0,0"
                    ); insNum+=2; break;
                }
                case CALL: {
                    prevFrame.clone(frame);
                    frame.clone(stackFrameMap.get(tac.arg1+""));
                    frame.setIns(prevFrame.getIns()); break;
                }
                case BEGINCALL: {
                    /* Advance the fp and tos */ targetCode.append(
                        insNum+": LDA 5,0(6)\n"+(insNum+1)+": LDA 6,"+frame.size()+"(6)\n"+tac.arg1
                    ); insNum+=2; break;
                }
                case CONST: {targetCode.append(insNum+": LDC "+tac.arg2+","+tac.arg1+"(0)\n"); insNum++; break;}
                case IF: {
                    if (tac.arg1==Lex.EQUIVALENT) {
                        targetCode.append(insNum+": JNE "+tac.arg2+",*"); labelStack.push(targetCode.length());
                        targetCode.append("(4)\n"); insNum++;
                    } break;
                }
                case ELSE: {
                    if (!labelStack.isEmpty()) {
                        int label = labelStack.pop(); targetCode.replace(label-1,label,insNum+"");
                    } break;
                }
                case EQUIVALENT: {targetCode.append(insNum+": JNE "+tac.arg1+",*(4)\n"); insNum++; break;}
                case PLUS: {targetCode.append(insNum+": ADD 0,"+tac.arg1+","+tac.arg2+"\n"); insNum++; break;}
                case EXIT: {break;}
                case MINUS: {targetCode.append(insNum+": SUB 0,"+tac.arg1+","+tac.arg2+"\n"); insNum++; break;}
                case LOAD: {targetCode.append(insNum+": LD "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case STORE: {targetCode.append(insNum+": ST "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case GOTO: {
                    targetCode.append(insNum+": LDA 7,"+frame.getIns()+"(4)\n"); 
                    insNum++; StackFrame tmpFrame = new StackFrame().clone(frame);
                    frame.clone(prevFrame); prevFrame.clone(tmpFrame); break;
                }
                case ALTER: {
                    if (tac.arg1.toString().startsWith("-")) {frame.addTmp();}
                    else {frame.removeTmp();}
                    targetCode.append(insNum+": LDA "+tac.arg2+","+tac.arg1+"("+tac.arg2+")\n"); insNum++;
                    break;
                }
                case RESTORE: {
                    targetCode.append(
                        insNum+": LDA 6,0(5)\n"+
                        (insNum+1)+": LDA 5,-"+tac.arg1+"(5)\n"
                    ); insNum+=2; break;
                }
                case MALLOC: {
                    targetCode.append(
                        insNum+": LDA 5,0(6)\n"+
                        (insNum+1)+": LDA 6,"+tac.arg1+"(6)\n"+
                        (insNum+2)+": LDA 0,2(7)\n"+
                        (insNum+3)+": ST 0,2(5)\n"
                    ); insNum+=4; break;
                }
                case COPY: {
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
                        insNum+": LD 1,"+frame.getStateLoc()+"(5)\n"+
                        (insNum+1)+": JNE 1,"+(insNum+3)+"(4)\n"+
                        (insNum+2)+": LDA 7,*(4)\n"
                    ); insNum+=3; rtrnLocs.add(targetCode.length()-4);
                    targetCode.append(insNum+": LDA 7,0(1)\n"); insNum++; break;
                }
            }
        }
    }

    private class Tac {

        private Lex op;
        private Object arg1, arg2;

        public Tac(Lex op, Object arg1, Object arg2) {this.op=op; this.arg1=arg1; this.arg2=arg2;}
        public Tac(Lex op, Object arg1) {this.op=op; this.arg1=arg1;}
        public String toString() {
            if (arg2!=null) {return op+" "+arg1+" "+arg2;} return op+" "+arg1.toString().trim();
        }
    }

    private void write(Lex op, Object arg1, Object arg2) {triplesArray.add(new Tac(op, arg1, arg2));}
    private void write(Lex op, Object arg1) {triplesArray.add(new Tac(op, arg1));}
    private int makeTemp(StackFrame f) {write(Lex.ALTER,1,6); return f.addTmp();}
    private int removeTemp(StackFrame f) {write(Lex.ALTER,-1,6); return f.removeTmp();}
}