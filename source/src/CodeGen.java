package src;
import java.util.Stack;

public class CodeGen implements AstVisitor {

    private SymbolTable symbolTable;
    private Stack<StackFrame> callStack = new Stack<>();
    private StringBuilder imem = new StringBuilder();
    private int[] rmem = new int[8];
    private int fp = 1, tos = 1;

    private void ldConst(int register, int constant) {
        imem.append(rmem[7]+": LDC "+register+","+constant+"(0)\n");
        rmem[register] = constant; rmem[7]++;
    }
    
    private void stConst(int r1, int offset, int r2) {
        imem.append(rmem[7]+": ST "+r1+","+offset+"("+r2+")\n");
        rmem[7]++; rmem[5]++; tos++; rmem[6]++; fp++;
    }

    private void storeStaticData() {
        int reg = 0;
        for (int con : symbolTable.getStaticData().keySet()) {
            if (reg<4) {ldConst(reg,con); reg++;}
            else {ldConst(reg, con); stConst(reg,0,reg+1);}
        }
    }
    
    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        rmem[5] = tos; rmem[6] = fp;
        symbolTable = prgrmNode.getSymbolTable();
        storeStaticData();
    }

    class StackFrame implements AstVisitor {

        private int[] rmem = new int[8];
        private int size = 0;

        public StackFrame() {

        }
        
    }

    // Debbugging purposes
    private void printRmem() {
        StringBuilder b = new StringBuilder();
        for (int i=0;i<8;i++) {
            if (i==0) {b.append("("+rmem[i]+",");}
            else if (i==7) {b.append(rmem[i]+")\n");}
            else {b.append(rmem[i]+",");}
        }
        System.out.println(b.toString());
    }
}