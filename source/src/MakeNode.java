package src;

public class MakeNode implements AstVisitor {

    @Override
    public Node visit(Identifier unary) {
        return new Node(unary);
    }

    @Override
    public Node visit(Type unary) {
        return new Node(unary);
    }

    @Override
    public Node visit(Literal unary) {
        return new Node(unary);
    }

    @Override
    public Node visit(Parameter binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Equality binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Minus binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Plus binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Divide binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Times binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(And binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Or binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(LessThan binary) {
        return new Node(binary);
    }

    @Override
    public Node visit(Function state) {
        return new Node(state);
    }

    @Override
    public Node visit(FunctionCall state) {
        return new Node(state);
    }

    @Override
    public Node visit(If state) {
        return new Node(state);
    }

    @Override
    public Node visit(Program state) {
        return new Node(state);
    }

    @Override
    public Node visit(Expression state) {
        return new Node(state);
    }
}
