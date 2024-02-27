package src;

public class ReturnType extends Type {

    public ReturnType(Token token) {
        super(token);
    }

    public String toString() {
        return new String("returns "+super.getType().toString().toLowerCase());
    }    
}
