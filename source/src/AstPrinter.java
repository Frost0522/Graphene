package src;

public class AstPrinter implements AstVisitor {

    private StringBuilder astStr = new StringBuilder();
    private int depth = 0;
    private Boolean formatSwitch = true;
    private String formatStr(String string) {
        if (!formatSwitch) {formatSwitch = true; return string;}
        StringBuilder builder = new StringBuilder();
        String[] strArray = string.split("\n");
        for (String str : strArray) {
            for (int i = 0; i < depth; i++) {
                str = "   "+str;
            }
            builder.append(str+"\n");
        }
        return builder.toString();
    }
    private String formatStr(String string, Boolean newLine) {
        if (!formatSwitch) {formatSwitch = true; return string;}
        if (newLine) {formatStr(string);}
        StringBuilder builder = new StringBuilder();
        String[] strArray = string.split("\n");
        for (String str : strArray) {
            for (int i = 0; i < depth; i++) {
                str = "   "+str;
            }
            builder.append(str+"\n");
        }
        String subStr = builder.toString().substring(0,builder.toString().length()-1);
        return subStr;
    }

    public String toString() {
        return astStr.toString().trim();
    }

    @Override
    public void visit(IdNode idNode) {
        if (depth==0) {depth=2;}
        astStr.append(formatStr(idNode.toString(),false));
    }

    @Override
    public void visit(LitNode litNode) {
        if (depth==0) {depth=2;}
        astStr.append(formatStr(litNode.toString(),false));
    }

    @Override
    public void visit(BinaryNode binNode) {

        if (depth < 2) {
            depth+=2;
            astStr.append(formatStr("operator "+binNode.getSymbol()));
        }
        else {astStr.append(formatStr("operator ",false)+binNode.getSymbol()+"\n");}
        depth++;
        if (binNode.getLeft() != null) {
            astStr.append(formatStr("left ",false));
            formatSwitch = false;
            binNode.getLeft().accept(this);
            astStr.append("\n");
        }

        if (binNode.getRight() != null) {
            astStr.append(formatStr("right ",false));
            formatSwitch = false;
            binNode.getRight().accept(this);
        }
        depth--;
    }

    @Override
    public void visit(CallNode callNode) {
        if (depth < 2) {
            depth+=2;
            astStr.append(formatStr(callNode.toString()));
        }
        else {
            astStr.append(formatStr("function call"+"\n"));
            depth++;
            astStr.append(formatStr(callNode.getId().toString()));
            astStr.append(formatStr("args"));
            depth--;
        }
        depth+=2;
        for (Node arg : callNode.getArgs()) {
            arg.accept(this);
            if (!(arg==callNode.getArgs().getLast())) {astStr.append("\n");}
        }
        depth-=2;

    }

    @Override
    public void visit(IfNode ifNode) {
        if (depth < 2) {
            depth+=2;
            astStr.append(formatStr("if"));
        }
        else {astStr.append(formatStr("if"));}
        depth++; ifNode.getIf().accept(this); depth--;
        astStr.append(formatStr("\nthen"));
        depth++; ifNode.getThen().accept(this); depth--;
        astStr.append(formatStr("\nelse"));
        depth++; ifNode.getElse().accept(this); depth--;
    }

    @Override
    public void visit(FnNode fnNode) {
        astStr.append(fnNode);
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this); 
            astStr.append("\n");
        }
    }

    @Override
    public void visit(PrgrmNode prgrmNode) {
        for (Node fnNode : prgrmNode.getFunctions()) {
            fnNode.accept(this); astStr.append("\n");
        }
    }

    @Override
    public void visit(ExpNode expNode) {
        astStr.append(formatStr("expression\n"));
        depth++; expNode.getNode().accept(this); depth--;
    }
}
