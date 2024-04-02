package src;
import java.util.ArrayList;
import java.util.Stack;

public class FunctionCall implements Expression {
    
    private Identifier id;
    private ArrayList<Node> args = new ArrayList<>();

    public FunctionCall(Stack<Node> stack) {
        Node node = stack.pop();
        while (!node.valueOf().equals(Lexical.$)) {args.add(0,node); node = stack.pop();}
        id = stack.pop().getId();
    }

    protected Identifier getId() {return id;}
    protected void flipSymbol() {id.flipSymbol();}
    protected String getName() {return id.getName();}
    protected ArrayList<Node> getArgs() {return args;}
    protected void setArgs(ArrayList<Node> list) {for (Node n : list) {args.add(0,n);}}
    public String toString() {if (id.isNegative()) {return "negative function_call\n   "+getName()+"\n   args\n      "+getArgs();}
    else {return "function_call\n   "+getName()+"\n   args\n      "+getArgs();}}

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
