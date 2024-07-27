package src;
import java.util.Stack;

public class Parameter implements BinaryExp {
    
    private Identifier id;
    private Type type;
    private String symbol = ":";

    public Parameter(Stack<Node> stack) {
        type = stack.pop().getType();
        id = stack.pop().getId();
    }

    protected Identifier getLeft() {return id;}
    protected Type getRight() {return type;}
    public String toString() {
        return new String(id+" "+symbol+" "+type.getType().name().toLowerCase());
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
