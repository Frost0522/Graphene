package src;
import java.util.Stack;

public class Or implements BinaryExp {

    private Node leftNode;
    private Node rightNode;
    private String symbol = "or";

    public Or(Stack<Node> stack) {
        rightNode = stack.pop();
        leftNode = stack.pop();
    }

    protected Node getLeftNode() {return leftNode;}
    protected Node getRightNode() {return rightNode;}

    public String toString() {
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();
        switch (leftNode.valueOf()) {
            case IDENTIFIER: left.append(leftNode.getId()); break;
            case LITERAL: left.append(leftNode.getLiteral()); break;
            case FNCALL: left.append(leftNode.getFnCall()); break;
            case DIVIDE: left.append(leftNode.getDivide()); break;
            case TIMES: left.append(leftNode.getTimes()); break;
            case EQUIVALENT: left.append(leftNode.getEquality()); break;
            case MINUS: left.append(leftNode.getMinus()); break;
            case PLUS: left.append(leftNode.getPlus()); break;
            case AND: left.append(leftNode.getAnd()); break;
            case OR: left.append(leftNode.getOr()); break;
            case LESS_THAN: left.append(leftNode.getLessThan()); break;
            default: break;
        }
        switch (rightNode.valueOf()) {
            case IDENTIFIER: right.append(rightNode.getId()); break;
            case LITERAL: right.append(rightNode.getLiteral()); break;
            case FNCALL: right.append(rightNode.getFnCall()); break;
            case DIVIDE: right.append(rightNode.getDivide()); break;
            case TIMES: right.append(rightNode.getTimes()); break;
            case EQUIVALENT: right.append(rightNode.getEquality()); break;
            case MINUS: right.append(rightNode.getMinus()); break;
            case PLUS: right.append(rightNode.getPlus()); break;
            case AND: right.append(rightNode.getAnd()); break;
            case OR: right.append(rightNode.getOr()); break;
            case LESS_THAN: right.append(rightNode.getLessThan()); break;
            default: break;
        }
        return left+" "+symbol+" "+right;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    } 
}
