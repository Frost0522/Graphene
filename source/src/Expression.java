package src;

public interface Expression {

    public Node accept(AstVisitor v);
    public String toString();
}
