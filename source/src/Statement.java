package src;

public interface Statement {

    public Node accept(AstVisitor v);
    public String toString();
}
