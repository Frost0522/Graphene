package src;

public class Type implements UnaryExp {

    private Token typeToken;

    public Type(Token token) {
        this.typeToken = token;
    }

    protected Lexicon getType() {
        return typeToken.getType();
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
