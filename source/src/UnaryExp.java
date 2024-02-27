package src;

public interface UnaryExp {
    
    public Node accept(AstVisitor v);
    public String toString();
}
