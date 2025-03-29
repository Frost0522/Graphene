package src;
import java.util.Set;
import java.util.Stack;

public class CodeGen implements AstVisitor {

    private SymbolTable symbolTable;
    private Stack<StackFrame> callStack = new Stack<>();
    private StringBuilder imem = new StringBuilder();
    private int[] rmem = new int[8], dmem = new int [1024];
    private int fp = 1, tos = 1;

    private void advFP() {rmem[5]++; tos++; rmem[6]++; fp++;}
    private void stop() {imem.append(rmem[7]+": HALT 0,0,0\n"); rmem[7]++;}
    private void output(int reg) {imem.append(rmem[7]+": OUT "+reg+",0,0\n"); rmem[7]++;}
    private void link(int newCount) {imem.append(rmem[7]+": LDA 7,"+newCount+"(4)\n"); rmem[7]++;}
    private void jmp(int newCount) {rmem[7] = newCount;}

    private void ldConst(int register, int constant) {
        imem.append(rmem[7]+": LDC "+register+","+constant+"(0)\n");
        rmem[register] = constant; rmem[7]++;
    }

    private void stRtrnAddToReg() {
        imem.append(rmem[7]+": LDA 6,1(7)\n");
        rmem[6] = rmem[7] + 1; rmem[7]++;
    }

    private void stRtrnAddToMem() {
        imem.append(rmem[7]+": ST 6,"+fp+"(4)\n");
        dmem[fp] = rmem[6]; rmem[7]++; advFP();
    }
    
    private void stConst(int r1, int offset, int r2) {
        int address = rmem[r2] + offset;
        imem.append(rmem[7]+": ST "+r1+","+offset+"("+r2+")\n");
        dmem[address] = rmem[r1];
        rmem[7]++; advFP();
    }

    private void callSeq() {}

    private void initEntry() {
        stRtrnAddToReg(); link(rmem[7]+3);
        output(0); stop(); jmp(rmem[7]+3);
    }

    private void storeStaticData() {
        Set<Integer> staticData = symbolTable.getStaticData().keySet();
        if (staticData.size()<5) {
            int reg = 0;
            for (int con : symbolTable.getStaticData().keySet()) {
                ldConst(reg,con); reg++;
            }
        }
        else {
            for (int con : symbolTable.getStaticData().keySet()) {
                ldConst(0,con); stConst(0,0,6);
            }
        }
    }
    
    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        rmem[5] = tos; rmem[6] = fp; dmem[0] = 1023;
        symbolTable = prgrmNode.getSymbolTable();
        storeStaticData();
        initEntry();
        System.out.println(imem);
        for (Node node : symbolTable.get("main").getFnNode().getBodyNodes()) {
            node.accept(this);
            if (node instanceof CallNode) {callSeq();}
        }
    }

    class StackFrame implements AstVisitor {

        private int[] rmem = new int[8];
        private int size = 0;

        public StackFrame() {

        }
        
    }

    // Debbugging purposes
    private void printMem(int[] intArray) {
        StringBuilder b = new StringBuilder();
        for (int i=0;i<intArray.length;i++) {
            if (i==0) {b.append("("+intArray[i]+",");}
            else if (i==intArray.length-1) {b.append(intArray[i]+")\n");}
            else {b.append(intArray[i]+",");}
        }
        System.out.println(b.toString());
    }
}