package src;
import java.util.ArrayList;
import java.util.Stack;

public class Program implements Statement {

    private ArrayList<Node> functionList = new ArrayList<>();

    public Program(Stack<Node> stack) {
        while (!stack.empty()) {functionList.add(0,stack.pop());}
    }

    protected ArrayList<Node> getFunctions() {
        return functionList;
    }
    
    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
