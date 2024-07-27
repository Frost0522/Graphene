package src;
import java.util.Stack;

public class IfElse implements Expression {

    private Node ifStatement;
    private Node thenStatement;
    private Node elseStatement;

    IfElse(Stack<Node> stack) {
        elseStatement = stack.pop();
        thenStatement = stack.pop();
        ifStatement = stack.pop();
    }

    protected Node getIf() {
        return ifStatement;
    }

    protected Node getThen() {
        return thenStatement;
    }

    protected Node getElse() {
        return elseStatement;
    }

    public String toString() {
        return "if\n   "+ifStatement+
               "\nthen\n   "+thenStatement+
               "\nelse\n   "+elseStatement;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
