package src;
import java.util.ArrayList;

public class Literal implements UnaryExp {
    
    private Token litToken;
    
    public Literal(Token token) {
        this.litToken = token;
    }

    protected ArrayList<Integer> getCharList() {
        return litToken.getCharList();
    }

    protected Lexical getType() {
        return litToken.getType();
    }
    
    public String toString() {
        StringBuilder literal = new StringBuilder();
        for (int _int : this.getCharList()) {literal.append((char) _int);}
        if (this.getType().equals(Lexical.INTEGER_LITERAL)) {return "integer_literal "+literal;}
        return "boolean_literal "+literal;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
