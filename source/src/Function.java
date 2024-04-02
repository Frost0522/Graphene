package src;
import java.util.ArrayList;
import java.util.Stack;

public class Function implements Expression {
    
    private Identifier id;
    private ArrayList<Node> parameters = new ArrayList<>();
    private Type returns;
    private ArrayList<Node> body = new ArrayList<>();

    public Function(Stack<Node> stack) {
        while (stack.peek().valueOf() != Lexical.RETURN) {body.add(0, stack.pop());}
        returns = stack.pop().getType();
        while (stack.peek().valueOf() == Lexical.PARAMETERLIST) {parameters.add(0, stack.pop());}
        id = stack.pop().getId();
    }

    protected String getName() {return id.getName();}
    protected String getParameters() {
        StringBuilder paramBuilder = new StringBuilder();
        for (Node node : parameters) {
            if (parameters.indexOf(node) == parameters.size()-1) {paramBuilder.append("\n      "+node.getParam());}
            else {paramBuilder.append("\n      "+node.getParam());}
        }
        return paramBuilder.toString();
    }
    protected Type getReturnType() {return returns;}
    protected ArrayList<Node> getBody() {return body;}

    public String toString() {return "funcion\n   "+getName()+"\n   parameters "+getParameters()+
                                     "\n   "+getReturnType()+"\n   body\n   "+getBody();}

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
