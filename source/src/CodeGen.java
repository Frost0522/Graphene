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
    private ArrayList<Tac> triplesArray = new ArrayList<>();
    private StringBuilder targetCodeBuilder = new StringBuilder();
    private ArrayList<String> targetCodeArray = new ArrayList<>();
    private Stack<Integer> labelStack = new Stack<>();
    private StackFrame frame = new StackFrame();

    public String getTargetCode() {
        for (String s : targetCodeArray) {targetCodeBuilder.append(s);}
        return targetCodeBuilder.toString();
    }

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
        write(Lex.EPILOGUE,"");

        genTargetCode();

        // for (int i=0;i<triplesArray.size();i++) {System.out.println(i+" "+triplesArray.get(i));}
        // System.out.println(); genTargetCode(); System.out.println(getTargetCode()); System.out.println();
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
        write(Lex.CALL,callNode.getName(),callNode.getArgs().size());
        if (callNode.getName().equals("print")) {
            callNode.getArgs().getFirst().accept(this); /* arg is placed into reg 0 */
            write(Lex.PRINT,"");
        } else if (frame.getName().equals(callNode.getName())) {
            StackFrame newFrame = new StackFrame().clone(stackFrameMap.get(callNode.getName()));
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
        ifNode.getIf().accept(this);
        write(Lex.IF,ifNode.getIf().nodeType(),0);
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
        binNode.getLeft().accept(this);

        if (!rIsTerminal) {
            write(Lex.STORE,0,frame.size()); makeTemp(frame);
            binNode.getRight().accept(this);
            write(Lex.LOAD,1,frame.size()-1); removeTemp(frame);
        } else {new Assign(binNode.getRight(),1);}

        switch (binNode.nodeType()) {
            case PLUS,MINUS,TIMES,DIVIDE,AND,OR,LESSTHAN,EQUIVALENT: {write(binNode.nodeType(),0); break;}
        }
    }

    @Override
    public void visit(ExpNode expNode) throws Analyzer {expNode.getNode().accept(this);}

    @Override
    public void visit(NotNode notNode) throws Analyzer {
        notNode.getNode().accept(this); write(notNode.nodeType(),"0");
    }

    @Override
    public void visit(LitNode litNode) {write(Lex.CONST,litNode.getValue(),0);}

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
        public void visit(LitNode litNode) {write(Lex.CONST,litNode.getValue(),loc);}

        @Override
        public void visit(IdNode idNode) {
            int i = stackFrameMap.get(currentFn.getName()).getParamIndex(idNode.getName());
            write(Lex.LOAD,loc,i);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void genTargetCode() {
        StackFrame frame = new StackFrame().clone(stackFrameMap.get("main")),
        prevFrame = new StackFrame().clone(frame);
        for (Tac tac : triplesArray) {
            switch (tac.op) {
                case ENTRY: {targetCodeArray.add("* "+tac.arg1+"\n"); frame.setIns(insNum); break;}
                case BEGINPROLOGUE: {targetCodeArray.add("* prologue\n"); break;}
                case ENDPROLOGUE: {targetCodeArray.add("\n"); break;}
                case EPILOGUE: {
                    for (int i=0;i<targetCodeArray.size();i++) {
                        targetCodeArray.set(i,targetCodeArray.get(i).replace("$",insNum+""));
                    }
                    targetCodeArray.add("\n* epilogue\n");
                    targetCodeArray.add(insNum+": OUT 0,0,0\n");
                    targetCodeArray.add((insNum+1)+": HALT 0,0,0");
                    insNum+=2; break;
                }
                case CALL: {
                    if (!(tac.arg1.equals("print"))) {
                        prevFrame.clone(frame);
                        frame.clone(stackFrameMap.get(tac.arg1+""));
                        frame.setIns(prevFrame.getIns());
                    } break;
                }
                case BEGINCALL: {
                    /* Advance the fp and tos */ 
                    targetCodeArray.add(insNum+": LDA 5,0(6)\n");
                    targetCodeArray.add((insNum+1)+": LDA 6,"+frame.size()+"(6)\n"+tac.arg1);
                    insNum+=2; break;
                }
                case CONST: {targetCodeArray.add(insNum+": LDC "+tac.arg2+","+tac.arg1+"(0)\n"); insNum++; break;}
                case IF: {
                    targetCodeArray.add(insNum+": JEQ "+tac.arg2+",$(4)\n");
                    labelStack.push(targetCodeArray.size()-1); insNum++; break;
                }
                case ELSE: {
                    if (!labelStack.isEmpty()) {
                        int label = labelStack.pop();
                        targetCodeArray.set(label, targetCodeArray.get(label).replace("$",insNum+""));
                    } break;
                }
                case NOT: {
                    targetCodeArray.add(insNum+": JEQ 0,2(7)\n");
                    targetCodeArray.add((insNum+1)+": LDC 0,0(0)\n");
                    targetCodeArray.add((insNum+2)+": LDA 7,1(7)\n");
                    targetCodeArray.add((insNum+3)+": LDC 0,1(0)\n");
                    insNum+=4; break;
                }
                case EQUIVALENT: {
                    targetCodeArray.add(insNum+": SUB 0,0,1\n");
                    targetCodeArray.add((insNum+1)+": JEQ 0,2(7)\n");
                    targetCodeArray.add((insNum+2)+": LDC 0,0(0)\n");
                    targetCodeArray.add((insNum+3)+": LDA 7,1(7)\n");
                    targetCodeArray.add((insNum+4)+": LDC 0,1(0)\n");
                    insNum+=5; break;
                }
                case LESSTHAN: {
                    targetCodeArray.add(insNum+": SUB 0,0,1\n");
                    targetCodeArray.add((insNum+1)+": JLT 0,2(7)\n");
                    targetCodeArray.add((insNum+2)+": LDC 0,0(0)\n");
                    targetCodeArray.add((insNum+3)+": LDA 7,1(7)\n");
                    targetCodeArray.add((insNum+4)+": LDC 0,1(0)\n");
                    insNum+=5; break;
                }
                case OR: {
                    targetCodeArray.add(insNum+": ADD "+tac.arg1+",0,1\n");
                    targetCodeArray.add((insNum+1)+": JEQ 0,1(7)\n");
                    targetCodeArray.add((insNum+2)+": LDC 0,1(0)\n");
                    insNum+=3; break;
                }
                case PLUS: {targetCodeArray.add(insNum+": ADD "+tac.arg1+",0,1\n"); insNum++; break;}
                case MINUS: {targetCodeArray.add(insNum+": SUB "+tac.arg1+",0,1\n"); insNum++; break;}
                case TIMES,AND: {targetCodeArray.add(insNum+": MUL "+tac.arg1+",0,1\n"); insNum++; break;}
                case DIVIDE: {targetCodeArray.add(insNum+": DIV "+tac.arg1+",0,1\n"); insNum++; break;}
                case LOAD: {targetCodeArray.add(insNum+": LD "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case STORE: {targetCodeArray.add(insNum+": ST "+tac.arg1+","+tac.arg2+"(5)\n"); insNum++; break;}
                case PRINT: {targetCodeArray.add(insNum+": OUT 0,0,0\n"); insNum++; break;}
                case GOTO: {
                    targetCodeArray.add(insNum+": LDA 7,"+frame.getIns()+"(4)\n"); 
                    insNum++; StackFrame tmpFrame = new StackFrame().clone(frame);
                    frame.clone(prevFrame); prevFrame.clone(tmpFrame); break;
                }
                case STEP: {
                    if (tac.arg1.toString().startsWith("-")) {frame.removeTmp();}
                    else {frame.addTmp();}
                    targetCodeArray.add(insNum+": LDA "+tac.arg2+","+tac.arg1+"("+tac.arg2+")\n"); insNum++;
                    break;
                }
                case RESTORE: {
                    targetCodeArray.add(insNum+": LDA 6,0(5)\n");
                    targetCodeArray.add((insNum+1)+": LDA 5,-"+tac.arg1+"(5)\n");
                    insNum+=2; break;
                }
                case MALLOC: {
                    targetCodeArray.add(insNum+": LDA 5,0(6)\n");
                    targetCodeArray.add((insNum+1)+": LDA 6,"+tac.arg1+"(6)\n");
                    targetCodeArray.add((insNum+2)+": LDA 0,2(7)\n");
                    targetCodeArray.add((insNum+3)+": ST 0,2(5)\n");
                    insNum+=4; break;
                }
                case RETURN: {
                    targetCodeArray.add(insNum+": LD 1,"+frame.getStateLoc()+"(5)\n");
                    targetCodeArray.add((insNum+1)+": JNE 1,"+(insNum+3)+"(4)\n");
                    targetCodeArray.add((insNum+2)+": LDA 7,$(4)\n");
                    targetCodeArray.add((insNum+3)+": LDA 7,0(1)\n");
                    insNum+=4; break;
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
    private void makeTemp(StackFrame f) {f.addTmp(); write(Lex.STEP,1,6);}
    private void removeTemp(StackFrame f) {f.removeTmp(); write(Lex.STEP,-1,6);}
}