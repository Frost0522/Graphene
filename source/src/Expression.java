package src;
import java.util.Stack;

public class Expression implements Statement {

    private Node exp;
    protected Lexicon symbol = Lexicon.PLUS;
    public Lexicon type;

    public Expression(Stack<Node> stack) {
        exp = stack.pop();
    }

    protected Node getNode() {return exp;}
    protected void flipSymbol() {
        if (symbol==Lexicon.MINUS) {symbol = Lexicon.PLUS;}
        else {symbol = Lexicon.MINUS;}
    }

    public String toString() {return exp.toString();}

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
