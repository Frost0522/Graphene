package src;
import src.NewNode.Lineage;

public class NewAstPrinter implements NewAstVisitor {

    private StringBuilder astStr = new StringBuilder();
    private int strDepth = 0;
    private String updateDepth(int depth, String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = depth; i!=0; i--) {builder.append(" ");}
        return builder.append(str).toString();
    }

    public String toString() {return astStr.toString();}

    @Override
    public void visit(IdNode idNode) {
        astStr.append(updateDepth(strDepth,idNode.toString()));
    }

    @Override
    public void visit(LitNode litNode) {
        astStr.append(updateDepth(strDepth,litNode.toString()));
    }

    @Override
    public void visit(BinaryNode binNode) {
        if (binNode.lineage==Lineage.root) {
            astStr.append(updateDepth(strDepth,"operator ")+binNode.getSymbol()+"\n");
            if (binNode.getLeft() instanceof BinaryNode) {binNode.getLeft().accept(this);}
            else {astStr.append(updateDepth(strDepth+=3,"left "+binNode.getLeft()+"\n"));}
            if (binNode.getRight() instanceof BinaryNode) {binNode.getRight().accept(this);}
            else {astStr.append(updateDepth(strDepth,"right "+binNode.getRight()));}
            strDepth-=3;
        }
        else if ((binNode.lineage==Lineage.left) && binNode.getLeft() instanceof BinaryNode) {
            astStr.append(updateDepth(strDepth+=3,"left operator ")+binNode.getSymbol()+"\n");
            binNode.getLeft().accept(this);
            if (binNode.getRight() instanceof BinaryNode) {binNode.getRight().accept(this);}
            else {astStr.append(updateDepth(strDepth,"right "+binNode.getRight())+"\n");}
        }
        else if (binNode.lineage==Lineage.left && (!(binNode.getLeft() instanceof BinaryNode))) {
            astStr.append(updateDepth(strDepth+=3,"left operator ")+binNode.getSymbol()+"\n");
            astStr.append(updateDepth(strDepth+=3,"left "+binNode.getLeft())+"\n");
            if (binNode.getRight() instanceof BinaryNode) {binNode.getRight().accept(this);}
            else {astStr.append(updateDepth(strDepth,"right "+binNode.getRight())+"\n");}
            strDepth-=3;
        }
        else if ((binNode.lineage==Lineage.right) && binNode.getLeft() instanceof BinaryNode) {
            astStr.append(updateDepth(strDepth,"right operator ")+binNode.getSymbol()+"\n");
            binNode.getLeft().accept(this);
            if (binNode.getRight() instanceof BinaryNode) {binNode.getRight().accept(this);}
            else {astStr.append(updateDepth(strDepth,"right "+binNode.getRight()+"\n"));}
        }
        else if (binNode.lineage==Lineage.right && (!(binNode.getLeft() instanceof BinaryNode))) {
            astStr.append(updateDepth(strDepth,"right operator ")+binNode.getSymbol()+"\n");
            astStr.append(updateDepth(strDepth+=3,"left "+binNode.getLeft()+"\n"));
            if (binNode.getRight() instanceof BinaryNode) {binNode.getRight().accept(this);}
            else {astStr.append(updateDepth(strDepth,"right "+binNode.getRight())+"\n");}
            strDepth-=3;
        }
    }

    @Override
    public void visit(CallNode callNode) {
        astStr.append(updateDepth(strDepth, "function call\n"));
        strDepth += 3;
        astStr.append(updateDepth(strDepth, callNode.getId().toString()+"\n"));
        astStr.append(updateDepth(strDepth, "args\n"));
        strDepth += 3;
        for (NewNode argNode : callNode.getArgs()) {
            argNode.accept(this);
            if (argNode!=callNode.getArgs().getLast()) {
                astStr.append("\n");
            }
        }
        strDepth -= 6;
    }

    @Override
    public void visit(IfNode ifNode) {
        astStr.append(updateDepth(strDepth, "if\n"));
        strDepth += 3;
        ifNode.getIf().accept(this);
        strDepth -=3;
        astStr.append("\n"+updateDepth(strDepth, "then\n"));
        strDepth += 3;
        ifNode.getThen().accept(this);
        strDepth -=3;
        astStr.append("\n"+updateDepth(strDepth, "else\n"));
        strDepth += 3;
        ifNode.getElse().accept(this);
        strDepth -=3;
    }

    @Override
    public void visit(FnNode fnNode) {
        strDepth = 6;
        astStr.append(fnNode);
        for (NewNode bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
    }

    @Override
    public void visit(PrgrmNode prgrmNode) {
        for (NewNode fnNode : prgrmNode.getFunctions()) {
            fnNode.accept(this); astStr.append("\n\n");
        }
    }

    @Override
    public void visit(ExpNode expNode) {
        astStr.append(updateDepth(strDepth, "expression\n"));
        strDepth += 3;
        expNode.getNode().accept(this);
        strDepth -= 3;
    }
}
