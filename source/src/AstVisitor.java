package src;
import java.util.ArrayList;
import java.util.Stack;

public interface AstVisitor {
    default public void visit(IdNode node) {}
    default public void visit(TypeNode node) {}
    default public void visit(ReturnNode node) {}
    default public void visit(LitNode node) {}
    default public void visit(ParamNode node) throws Analyzer {}
    default public void visit(BinaryNode node) throws Analyzer {}
    default public void visit(EqNode node) {}
    default public void visit(MinusNode node) {}
    default public void visit(PlusNode node) {}
    default public void visit(DivideNode node) {}
    default public void visit(TimesNode node) {}
    default public void visit(AndNode node) {}
    default public void visit(OrNode node) {}
    default public void visit(LessNode node) {}
    default public void visit(FnNode node) throws Analyzer {}
    default public void visit(CallNode node) throws Analyzer {}
    default public void visit(IfNode node) throws Analyzer {}
    default public void visit(PrgrmNode node) throws Analyzer {}
    default public void visit(ExpNode node) throws Analyzer {}
    default public void visit(NullNode node) {}
}

abstract class Node {
    abstract void accept(AstVisitor visitor) throws Analyzer;
    abstract Lexicon nodeType();
    abstract int[] position();
    protected Lexicon semanticType;
}

class NullNode extends Node {

    public NullNode() {}
    protected int[] position() {return new int[]{};}
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.$;}
}

class IdNode extends Node {

    private Token id;

    public IdNode(Token t) {
        this.id = t;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        if (id.getSymbol().equals(Lexicon.MINUS)) {
            return "(neg) identifier "+builder;
        }
        return "identifier "+builder;
    }

    protected int[] position() {return new int[]{id.line,id.column};}
    protected ArrayList<Integer> getCharList() {return id.getCharList();}
    protected void flipSymbol() {id.flipSymbol();}
    protected Boolean isNegative() {
        if (id.getSymbol().equals(Lexicon.MINUS)) {
            return true;
        } 
        return false;
    }
    protected String getName() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        return builder.toString();
    }
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.ID;}
}

class TypeNode extends Node {

    private Token type;

    public TypeNode(Token t) {
        this.type = t;
    }
    public String toString() {return type.getType().toString().toLowerCase();}

    protected int[] position() {return new int[]{type.line,type.column};}
    protected Lexicon getType() {return type.getType();}
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.TYPE;}
}

class ReturnNode extends TypeNode {

    public ReturnNode(Token t) {
        super(t);
    }
    @Override
    public String toString() {
        return "returns "+getType().toString().toLowerCase();
    }

    @Override
    protected Lexicon nodeType() {return Lexicon.RETURN;}
}

class LitNode extends Node {

    private Token literal;
    
    public LitNode(Token t) {
        this.literal = t;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        if (getType().equals(Lexicon.INTEGERLITERAL)) {
            if (literal.getSymbol().equals(Lexicon.MINUS)) {
                return "(neg) integer literal "+builder;
            }
            return "integer literal "+builder;
        }
        return "boolean literal "+builder;
    }

    protected int[] position() {return new int[]{literal.line,literal.column};}
    protected ArrayList<Integer> getCharList() {return literal.getCharList();}
    protected Lexicon getType() {return literal.getType();}
    protected void flipSymbol() {literal.flipSymbol();}
    protected void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.LITERAL;
    }
}

class ParamNode extends Node {

    private Node id;
    private Node type;
    private String symbol;

    public ParamNode(Stack<Node> stack) {
        this.type = stack.pop(); this.id = stack.pop(); this.symbol = ":";
    }
    public String toString() {
        return new String(id+" "+symbol+" "+type.toString());
    }

    protected int[] position() {return id.position();}
    protected Node getLeft() {return id;}
    protected Node getRight() {return type;}
    protected void accept(AstVisitor visitor) throws Analyzer {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.PARAMLIST;}
}

class FnNode extends Node {

    private Node id;
    private ArrayList<Node> parameters = new ArrayList<>();
    private Node type;
    private ArrayList<Node> body = new ArrayList<>();

    public FnNode(Stack<Node> stack) {
        while (stack.peek().nodeType()!=Lexicon.RETURN) {body.add(0, stack.pop());}
        type = stack.pop();
        while (stack.peek().nodeType()==Lexicon.PARAMLIST) {parameters.add(0, stack.pop());}
        id = stack.pop();
    }
    public String toString() {
        return "funcion\n   "+id+"\n   parameters "+getParameters()+
               "\n   "+type+"\n   body\n";
    }

    protected Node getIdNode() {return id;}
    protected Node getNodeType() {return type;}
    protected ArrayList<Node> getParamNodes() {return parameters;}
    protected ArrayList<Node> getBodyNodes() {return body;}
    protected String getParameters() {
        StringBuilder builder = new StringBuilder();
        for (Node node : parameters) {
            if (parameters.indexOf(node) == parameters.size()-1) {builder.append("\n      "+node.toString());}
            else {builder.append("\n      "+node.toString());}
        }
        return builder.toString();
    }
    protected int[] position() {return id.position();}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.FN;
    }
}

class CallNode extends Node {

    private Node id;
    private ArrayList<Node> args = new ArrayList<>();

    public CallNode(Stack<Node> stack) {
        Node node = stack.pop();
        while (!(node instanceof NullNode)) {args.add(0,node); node = stack.pop();}
        id = stack.pop();
    }
    public String toString() {return "function call"+"\n   "+id+"\n   "+"args";}

    protected Node getId() {return id;}
    protected ArrayList<Node> getArgs() {return args;}
    protected int[] position() {return id.position();}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.FNCALL;
    }
}

class IfNode extends Node {

    private Node _if;
    private Node _then;
    private Node _else;

    public IfNode(Stack<Node> stack) {
        _else = stack.pop();
        _then = stack.pop();
        _if = stack.pop();
    }
    public String toString() {return "if\n   "+_if+"\nthen\n   "+_then+"\nelse\n   "+_else;}

    protected Node getIf() {return _if;}
    protected Node getThen() {return _then;}
    protected Node getElse() {return _else;}
    protected int[] position() {return _if.position();}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.IF;
    }
}

class PrgrmNode extends Node {

    private ArrayList<Node> functionList = new ArrayList<>();

    public PrgrmNode(Stack<Node> stack) {
        while (!stack.empty()) {functionList.add(0,stack.pop());}
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Node fnNode : functionList) {builder.append(fnNode+"\n");}
        return builder.toString();
    }

    protected ArrayList<Node> getFunctions() {return functionList;}
    protected int[] position() {return new int[]{0,0};}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.PROGRAM;
    }
}

class ExpNode extends Node {

    private Node exp;
    public Lexicon type;

    public ExpNode(Stack<Node> stack) {
        exp = stack.pop();
    }
    public String toString() {return exp.toString();}

    protected Node getNode() {return exp;}
    protected int[] position() {return new int[]{};}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.EXP;
    }
}

class BinaryNode extends Node {

    private Node leftNode;
    private Node rightNode;
    private String operator;
    private Lexicon nodeType;

    public BinaryNode(Stack<Node> stack, String s, Lexicon type) {
        this.rightNode = stack.pop(); this.leftNode = stack.pop();
        this.operator = s; nodeType = type;
    }
    public String toString() {return "operator "+operator+"\n"+"left "+leftNode+"\n"+"right "+rightNode+"\n";}

    protected String getSymbol() {return operator;}
    protected int[] position() {return leftNode.position();}
    protected Node getLeft() {return leftNode;}
    protected Node getRight() {return rightNode;}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {return nodeType;}
}

class EqNode extends BinaryNode {public EqNode(Stack<Node> stack) {super(stack, "==", Lexicon.EQUIVALENT);}}
class MinusNode extends BinaryNode {public MinusNode(Stack<Node> stack) {super(stack, "-", Lexicon.MINUS);}}
class PlusNode extends BinaryNode {public PlusNode(Stack<Node> stack) {super(stack, "+", Lexicon.PLUS);}}
class DivideNode extends BinaryNode {public DivideNode(Stack<Node> stack) {super(stack, "/", Lexicon.DIVIDE);}}
class TimesNode extends BinaryNode {public TimesNode(Stack<Node> stack) {super(stack, "*", Lexicon.TIMES);}}
class AndNode extends BinaryNode {public AndNode(Stack<Node> stack) {super(stack, "and", Lexicon.AND);}}
class OrNode extends BinaryNode {public OrNode(Stack<Node> stack) {super(stack, "or", Lexicon.OR);}}
class LessNode extends BinaryNode {public LessNode(Stack<Node> stack) {super(stack, "<", Lexicon.LESSTHAN);}}