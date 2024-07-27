package src;
import java.util.ArrayList;
import java.util.Stack;

public class Function implements Statement {
    
    private Node idNode;
    private ArrayList<Node> parameters = new ArrayList<>();
    private Node nodeType;
    private ArrayList<Node> body = new ArrayList<>();
    public Lexicon type;

    public Function(Stack<Node> stack) {
        while (stack.peek().valueOf() != Lexicon.RETURN) {body.add(0, stack.pop());}
        nodeType = stack.pop();
        while (stack.peek().valueOf() == Lexicon.PARAMLIST) {parameters.add(0, stack.pop());}
        idNode = stack.pop();
    }

    protected Node getIdNode() {return idNode;}
    protected Node getNodeType() {return nodeType;}
    protected ArrayList<Node> getParamNodes() {return parameters;}
    protected ArrayList<Node> getBodyNodes() {return body;}

    protected String getName() {return idNode.getId().getName();}
    protected String getParameters() {
        StringBuilder paramBuilder = new StringBuilder();
        for (Node node : parameters) {
            if (parameters.indexOf(node) == parameters.size()-1) {paramBuilder.append("\n      "+node.getParam());}
            else {paramBuilder.append("\n      "+node.getParam());}
        }
        return paramBuilder.toString();
    }

    public String toString() {return "funcion\n   "+getName()+"\n   parameters "+getParameters()+
                                     "\n   "+getNodeType().getType()+"\n   body\n   "+getBodyNodes();}

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
