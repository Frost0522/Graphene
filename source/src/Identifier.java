package src;
import java.util.ArrayList;

public class Identifier implements UnaryExp {
    
    private Token idToken;

    public Identifier(Token token) {idToken = token;}

    protected ArrayList<Integer> getCharList() {return idToken.getCharList();}
    protected Lexical getType() {return idToken.getType();}
    protected void flipSymbol() {idToken.flipSymbol();}

    protected Boolean isNegative() {
        if (idToken.getSymbol().equals(Lexical.MINUS)) {
            return true;
        } 
        return false;
    }

    protected String getName() {
        StringBuilder id = new StringBuilder();
        for (int _int : getCharList()) {id.append((char) _int);}
        return "name "+id;
    }

    public String toString() {
        StringBuilder id = new StringBuilder();
        for (int _int : getCharList()) {id.append((char) _int);}
        if (idToken.getSymbol().equals(Lexical.MINUS)) {
            return "(neg) identifier "+id;
        }
        return "identifier "+id;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
