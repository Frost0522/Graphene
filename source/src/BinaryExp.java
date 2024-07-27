package src;

public interface BinaryExp {
    
    public Node accept(AstVisitor v);
    public String toString();
}
