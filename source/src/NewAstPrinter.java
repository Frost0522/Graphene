package src;

import java.util.ArrayList;

public class NewAstPrinter implements NewAstVisitor {

    private StringBuilder astStr = new StringBuilder();
    private int depth = 0;
    private String formatStr(String string) {
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
    private String formatStr(ArrayList<NewNode> list) {
        StringBuilder builder = new StringBuilder();
        String[] strArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strArray[i] = list.get(i).toString();
            strArray[i].split("\n");
        }
        for (String str : strArray) {
            for (int i = 0; i < depth; i++) {
                str = "   "+str;
            }
            builder.append(str+"\n");
        }
        return builder.toString();
    }

    public String toString() {
        return astStr.toString().trim();
    }

    @Override
    public void visit(IdNode idNode) {
        astStr.append(formatStr(idNode.toString(),false));
    }

    @Override
    public void visit(LitNode litNode) {
        astStr.append(formatStr(litNode.toString(),false));
    }

    @Override
    public void visit(BinaryNode binNode) {

        if (depth < 2) {
            depth+=2;
            astStr.append(formatStr("operator "+binNode.getSymbol()));
        }
        else {astStr.append(formatStr("operator "+binNode.getSymbol()));}
        depth++;
        if (binNode.getLeft() != null) {
            astStr.append(formatStr("left ",false));
            int prevDepth = depth; depth=0;
            binNode.getLeft().accept(this);
            depth = prevDepth; astStr.append("\n");
        }

        if (binNode.getRight() != null) {
            astStr.append(formatStr("right ",false));
            int prevDepth = depth; depth=0;
            binNode.getRight().accept(this);
            depth = prevDepth;
        }
        depth--;
    }

    @Override
    public void visit(CallNode callNode) {
        if (depth < 2) {
            depth+=2;
            astStr.append(formatStr(callNode.toString()));
        }
        else {astStr.append(formatStr(callNode.toString()));}
        depth+=2;
        for (NewNode arg : callNode.getArgs()) {
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
        for (NewNode bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this); astStr.append("\n\n");
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
        astStr.append(formatStr("expression"));
        depth++; expNode.getNode().accept(this); depth--;
    }
}
