package src;
import java.util.ArrayList;
import java.util.Stack;

public class CodeGen implements AstVisitor {

    private SymbolTable symbolTable;
    private Stack<StackFrame> controlStack;
    private StringBuilder targetStr;
    private LitNode currentLit;
    private IdNode currentId;
    private int[] dmem, imem;
    private int nextAddress = 1;

    private int getAddress(int val) {
        int address = -1;
        for (int i = 1; nextAddress>=i; i++) {
            if (dmem[i]==val) {address = i; break;}
        } return address;
    }

    protected String getTargetStr() {return targetStr.toString().trim();}
    
    @SuppressWarnings("incomplete-switch")
    private void code(Lex op, int r1, int off, int r2, StringBuilder b) {
        switch (op) {
            case LDC: {
                imem[r1] = off;
                b.append(imem[7]+": "+op.toString()+" "+r1+","+imem[1]+"("+r2+")\n");
                imem[7]+=1; break;
            }
            case LDA: {
                if (r1==7) {
                    int prevR7 = imem[7];
                    int address = off + imem[r2];
                    imem[r1] = address;
                    b.append(prevR7+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                }
                else {
                    int address = off + imem[r2];
                    imem[r1] = address;
                    b.append(imem[7]+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                    imem[7]+=1;
                } break;
            }
            case ST: {
                int address = off + imem[r2];
                dmem[address] = imem[r1];
                b.append(imem[7]+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                imem[7]+=1; nextAddress+=1; break;
            }
            case LD: {
                if (r1==7) {
                    int prevR7 = imem[7];
                    imem[r1] = dmem[off];
                    b.append(prevR7+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                }
                else {
                    imem[r1] = dmem[off];
                    b.append(imem[7]+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                    imem[7]+=1;
                } break;
            }
            case OUT: {
                b.append(imem[7]+": "+op.toString()+" "+r1+","+off+","+r2+"\n");
                imem[7]+=1; break;
            }
            case HALT: {
                b.append(imem[7]+": "+op.toString()+" 0,0,0\n"); break;
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void write(Lex op, int r1, int off, int r2, StringBuilder b) {
        switch (op) {
            case LDA: {
                b.append(imem[7]+": "+op.toString()+" "+r1+","+off+"("+r2+")\n");
                imem[7]+=1; break;
            }
        }
    }

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        
        symbolTable = prgrmNode.getSymbolTable();
        controlStack = new Stack<>();
        targetStr = new StringBuilder();
        dmem = new int[1024]; imem = new int[8];
        symbolTable.get("main").getFnNode().accept(this);
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {

        fnNode.getIdNode().accept(this);
        for (Node paramNode : fnNode.getParamNodes()) {
            // To-Do: Load and store parameters
        }

        code(Lex.LDA,6,1,7,targetStr);
        imem[7]+=3;

        for (Node bodyNode : fnNode.getBodyNodes()) {
            if (bodyNode instanceof CallNode) {
                StackFrame frame = new StackFrame();
                bodyNode.accept(frame);
                controlStack.push(frame);
            }
        }

        StringBuilder returnVal = new StringBuilder();
        int controlLink = imem[7];

        while (!controlStack.isEmpty()) {
            StackFrame frame = controlStack.pop();
            returnVal.append(frame.getReturnVal());
            code(Lex.ST,6,nextAddress,0,returnVal);
            code(Lex.LDA,6,1,7,returnVal);
            write(Lex.LDA,7,frame.controlLink,0,returnVal);
        }

        code(Lex.LD,7,nextAddress-1,0,returnVal);
        write(Lex.LDA,7,controlLink,0,targetStr);
        code(Lex.OUT,1,0,0,targetStr);
        code(Lex.HALT,0,0,0,targetStr);

        targetStr.append(returnVal);
    }

    @Override
    public void visit(LitNode litNode) {currentLit = litNode;}
    @Override
    public void visit(IdNode idNode) {currentId = idNode;}

    class StackFrame implements AstVisitor {

        StringBuilder returnVal = new StringBuilder();
        String name;
        ArrayList<Node> argVals;
        int[] saveState;
        int controlLink;

        @Override
        public void visit(CallNode callNode) throws Analyzer {
            name = callNode.getId().toString().replace("identifier ","");
            argVals = callNode.getArgs();
            saveState = imem;
            controlLink = imem[7];
            if (name.equals("print") && argVals.get(0) instanceof LitNode) {
                argVals.get(0).accept(this);
                code(Lex.LDC,1,currentLit.getValue(),0,returnVal);
                code(Lex.OUT,1,0,0,returnVal);
                write(Lex.LDA,7,0,6,returnVal);
            }
        }

        String getReturnVal() {return returnVal.toString();}

        @Override
        public void visit(LitNode litNode) {currentLit = litNode;}

        @Override
        public void visit(IdNode idNode) {currentId = idNode;}
    }
}