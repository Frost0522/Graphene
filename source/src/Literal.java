package src;
import java.util.ArrayList;

public class Literal implements UnaryExp {
    
    private Token litToken;
    
    public Literal(Token token) {
        litToken = token;
    }

    protected ArrayList<Integer> getCharList() {return litToken.getCharList();}
    protected Lexical getType() {return litToken.getType();}
    protected void flipSymbol() {litToken.flipSymbol();}
    
    public String toString() {
        StringBuilder literal = new StringBuilder();
        for (int _int : getCharList()) {literal.append((char) _int);}
        if (getType().equals(Lexical.INTEGER_LITERAL)) {
            if (litToken.getSymbol().equals(Lexical.MINUS)) {
                return "(neg) integer_literal "+literal;
            }
            return "integer_literal "+literal;
        }
        return "boolean_literal "+literal;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
