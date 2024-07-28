package src;

public class NewAstPrinter implements NewAstVisitor {

    private StringBuilder astStr = new StringBuilder();
    private int depth = 0;
    private void formatStr() {
        for (int i = 0; i < depth; i++) {
            astStr.append("   ");
        }
    }

    public String toString() {return astStr.toString();}

    @Override
    public void visit(IdNode idNode) {
        astStr.append(idNode);
    }

    @Override
    public void visit(LitNode litNode) {
        astStr.append(litNode);
    }

    @Override
    public void visit(BinaryNode binNode) {

        if (depth < 1) {
            depth++; formatStr();
            astStr.append("operator "+binNode.getSymbol()+"\n");
        }
        else {depth--; astStr.append("operator "+binNode.getSymbol()+"\n");}

        depth++; depth++; formatStr();
        if (binNode.getLeft() != null) {
            astStr.append("left "); 
            binNode.getLeft().accept(this);
            astStr.append("\n");
        }
        depth--;

        depth++; formatStr();
        if (binNode.getRight() != null) {
            astStr.append("right "); 
            binNode.getRight().accept(this);
            astStr.append("\n");
        }
        depth--;
    }

    @Override
    public void visit(CallNode callNode) {
        if (depth < 1) {
            depth++; formatStr();
            astStr.append(callNode);
        }
        else {
            depth--; astStr.append("function call\n");
            depth++; depth++;
            formatStr(); astStr.append(callNode.getId()+"\n");
            formatStr(); astStr.append("args\n");
            depth++;
            for (NewNode arg : callNode.getArgs()) {
                formatStr(); arg.accept(this); astStr.append("\n");
            }
        }
    }

    @Override
    public void visit(IfNode ifNode) {
        
    }

    @Override
    public void visit(FnNode fnNode) {
        astStr.append(fnNode);
        for (NewNode bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
    }

    @Override
    public void visit(PrgrmNode prgrmNode) {
        for (NewNode fnNode : prgrmNode.getFunctions()) {
            fnNode.accept(this);
        }
    }

    @Override
    public void visit(ExpNode expNode) {
        
    }
}
