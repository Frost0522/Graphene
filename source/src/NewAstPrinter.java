package src;

public class NewAstPrinter implements NewAstVisitor {

    private StringBuilder astStr = new StringBuilder();
    private int depth = 0;
    private void formatStr() {
        for (int i = 0; i < depth; i++) {
            astStr.append("   ");
        }
    }
    private String formatStr(String string) {
        StringBuilder builder = new StringBuilder();
        String[] strArray = string.split("\n");
        for (String str : strArray) {
            for (int _int = 0; _int < depth; _int++) {
                str = "   "+str;
            }
            builder.append(str+"\n");
        }
        return builder.toString();
    }

    public String toString() {
        return astStr.toString().trim().indent(1);
    }

    @Override
    public void visit(IdNode idNode) {
        astStr.append(formatStr(idNode.toString()));
    }

    @Override
    public void visit(LitNode litNode) {
        astStr.append(formatStr(litNode.toString()));
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
        }
        depth--;
    }

    @Override
    public void visit(CallNode callNode) {
        if (depth < 1) {depth+=2;}
        String formatedCallNode = formatStr(callNode.toString());
        astStr.append(formatedCallNode);
        depth+=2;
        for (NewNode arg : callNode.getArgs()) {
            arg.accept(this);
        }
        depth=0;
    }

    @Override
    public void visit(IfNode ifNode) {

        if (depth < 1) {
            depth++; formatStr();
            astStr.append("if\n");
            depth++; depth++; formatStr();
            ifNode.getIf().accept(this);
        
            astStr.append("\n");
            depth--; formatStr();
            astStr.append("then\n");
            depth++; formatStr();
            ifNode.getThen().accept(this);
            astStr.append("\n");

            depth--; formatStr();
            astStr.append("else\n");
            depth++; formatStr();
            ifNode.getElse().accept(this);
        }
        else {
            astStr.append("if\n");
            depth++; formatStr();
            ifNode.getIf().accept(this);

            astStr.append("\n");
            depth--; formatStr();
            astStr.append("then\n");
            depth++; formatStr();
            ifNode.getThen().accept(this);

            astStr.append("\n");
            depth--; formatStr();
            astStr.append("else\n");
            depth++; formatStr();
            ifNode.getElse().accept(this);
        }
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
        astStr.append("expression\n");
        depth++; formatStr();
        expNode.getNode().accept(this);
        depth--;
    }
}
