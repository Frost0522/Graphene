package src;
import java.util.Stack;

public class Equality implements BinaryExp {
    
    private Node leftNode;
    private Node rightNode;
    private String symbol = "==";
    public Lexicon type;

    public Equality(Stack<Node> stack) {
        rightNode = stack.pop();
        leftNode = stack.pop();
    }

    protected Node getLeftNode() {return leftNode;}
    protected Node getRightNode() {return rightNode;}

    public String toString() {return leftNode+" "+symbol+" "+rightNode;}

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
