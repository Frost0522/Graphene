package src;

public interface AstVisitor {
    
    public Node visit(Identifier unary);
    public Node visit(Type unary);
    public Node visit(Literal unary);
    public Node visit(Parameter binary);
    public Node visit(Equality binary);
    public Node visit(Minus binary);
    public Node visit(Plus binary);
    public Node visit(Divide binary);
    public Node visit(Times binary);
    public Node visit(And binary);
    public Node visit(Or binary);
    public Node visit(LessThan binary);
    public Node visit(Function exp);
    public Node visit(FunctionCall exp);
    public Node visit(IfElse exp);
    public Node visit(Program exp);
}
