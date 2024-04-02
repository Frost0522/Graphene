package src;

public class Type implements UnaryExp {

    private Token typeToken;

    public Type(Token token) {
        this.typeToken = token;
    }

    protected Lexical getType() {
        return typeToken.getType();
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
