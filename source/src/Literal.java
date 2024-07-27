package src;
import java.util.ArrayList;

public class Literal implements UnaryExp {
    
    private Token litToken;
    public int line;
    public int column;
    
    public Literal(Token token) {
        litToken = token;
        line = token.line;
        column = token.column;
    }

    protected ArrayList<Integer> getCharList() {return litToken.getCharList();}
    protected Lexicon getType() {return litToken.getType();}
    protected void flipSymbol() {litToken.flipSymbol();}
    
    public String toString() {
        StringBuilder literal = new StringBuilder();
        for (int _int : getCharList()) {literal.append((char) _int);}
        if (getType().equals(Lexicon.INTEGERLITERAL)) {
            if (litToken.getSymbol().equals(Lexicon.MINUS)) {
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
