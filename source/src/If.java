package src;
import java.util.Stack;

public class If implements Statement {

    private Node ifStatement;
    private Node thenStatement;
    private Node elseStatement;
    private Identifier idToken;
    public int line;
    public int column;
    public Lexicon type;

    If(Stack<Node> stack) {
        elseStatement = stack.pop();
        thenStatement = stack.pop();
        ifStatement = stack.pop();
        idToken = stack.pop().getId();
        line = idToken.line; column = idToken.column;
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
