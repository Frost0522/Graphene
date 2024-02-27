package src;
import java.util.ArrayList;
import java.util.Stack;

public class Function implements Expression {
    
    private Identifier id;
    private ArrayList<Node> parameters = new ArrayList<>();
    private Type returns;
    private ArrayList<Node> body = new ArrayList<>();

    public Function(Stack<Node> stack) {
        while (stack.peek().valueOf() != Lexical.RETURN) {body.add(0, stack.pop());}
        returns = stack.pop().getType();
        while (stack.peek().valueOf() == Lexical.PARAMETERLIST) {parameters.add(0, stack.pop());}
        id = stack.pop().getId();
    }

    public String toString() {

        StringBuilder fnName = new StringBuilder();
        StringBuilder fnParams = new StringBuilder();
        StringBuilder fnReturns = new StringBuilder();
        StringBuilder fnBody = new StringBuilder();
        Boolean leftPad = true;
        Boolean lastNode = false;

        fnName.append("\n   "+id.getName());

        for (Node node : parameters) {
            if (parameters.indexOf(node) == parameters.size()-1) {fnParams.append(node.getParam());}
            else {fnParams.append(node.getParam()+", ");}
        }

        fnReturns.append(returns);
        
        for (Node node : body) {
            if (body.indexOf(node) == body.size()-1) {lastNode = true;}
            switch (node.valueOf()) {
                case FNCALL: {
                    if (leftPad) {fnBody.append("\n      "+node.getFnCall()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n      "); fnBody.append(node.getFnCall()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getFnCall()); break;}
                }
                case IDENTIFIER: {
                    if (leftPad) {fnBody.append("\n      "+node.getId()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getId()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getId()); break;}
                }
                case LITERAL: {
                    if (leftPad) {fnBody.append("\n      "+node.getLiteral()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getLiteral()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getLiteral()); break;}
                }
                case MINUS: {
                    if (leftPad) {fnBody.append("\n      "+node.getMinus()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getMinus()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getMinus()); break;}
                }
                case PLUS: {
                    if (leftPad) {fnBody.append("\n      "+node.getPlus()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getPlus()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getPlus()); break;}
                }
                case DIVIDE: {
                    if (leftPad) {fnBody.append("\n      "+node.getDivide()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getDivide()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getDivide()); break;}
                }
                case TIMES: {
                    if (leftPad) {fnBody.append("\n      "+node.getTimes()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getTimes()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getTimes()); break;}
                }
                case AND: {
                    if (leftPad) {fnBody.append("\n      "+node.getAnd()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getAnd()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getAnd()); break;}
                }
                case OR: {
                    if (leftPad) {fnBody.append("\n      "+node.getOr()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getOr()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getOr()); break;}
                }
                case LESS_THAN: {
                    if (leftPad) {fnBody.append("\n      "+node.getLessThan()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getLessThan()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getLessThan()); break;}
                }
                case EQUIVALENT: {
                    if (leftPad) {fnBody.append("\n      "+node.getEquality()); leftPad = false; break;}
                    else if (lastNode) {fnBody.append("\n"+"      "); fnBody.append(node.getEquality()); break;}
                    else {fnBody.append("\n"+"      "); fnBody.append(node.getEquality()); break;}
                }
                default: {break;}
            }
        }
        return "funcion "+fnName+"\n   parameters "+"\n      "+fnParams+
               "\n   "+fnReturns+"\n   body "+fnBody+"\n";
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
