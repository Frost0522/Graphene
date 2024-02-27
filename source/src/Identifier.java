package src;
import java.util.ArrayList;

public class Identifier implements UnaryExp {
    
    private Token idToken;

    public Identifier(Token token) {
        this.idToken = token;
    }

    protected ArrayList<Integer> getCharList() {
        return idToken.getCharList();
    }

    protected Lexical getType() {
        return idToken.getType();
    }

    protected String getName() {
        StringBuilder id = new StringBuilder();
        for (int _int : this.getCharList()) {id.append((char) _int);}
        return "name "+id;
    }

    public String toString() {
        StringBuilder id = new StringBuilder();
        for (int _int : this.getCharList()) {id.append((char) _int);}
        return "identifier "+id;
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
