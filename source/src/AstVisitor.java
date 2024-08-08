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
    abstract Lex nodeType();
    abstract int[] position();
    abstract Lex getSemanticType();
    abstract void setSemanticType(Lex type);
    abstract String getErrorStr();
}

class NullNode extends Node {

    public NullNode() {}

    protected int[] position() {return new int[]{};}
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lex nodeType() {return Lex.$;}
    protected Lex getSemanticType() {return null;}
    protected void setSemanticType(Lex type) {}
    protected String getErrorStr() {return "";}
}

class IdNode extends Node {

    private Token id;
    private Lex semanticType;

    public IdNode(Token t) {
        this.id = t;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        if (id.getSymbol().equals(Lex.MINUS)) {
            return "(neg) identifier "+builder;
        }
        return "identifier "+builder;
    }

    protected int[] position() {return new int[]{id.line,id.column};}
    protected ArrayList<Integer> getCharList() {return id.getCharList();}
    protected void flipSymbol() {id.flipSymbol();}
    protected Boolean isNegative() {
        if (id.getSymbol().equals(Lex.MINUS)) {
            return true;
        } 
        return false;
    }
    protected String getName() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        return builder.toString();
    }
    protected String getErrorStr() {return this.toString();}
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lex nodeType() {return Lex.ID;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
}

class TypeNode extends Node {

    private Token type;
    private Lex semanticType;

    public TypeNode(Token t) {
        this.type = t;
    }
    public String toString() {return type.getType().toString().toLowerCase();}

    protected int[] position() {return new int[]{type.line,type.column};}
    protected Lex getType() {return type.getType();}
    protected void accept(AstVisitor visitor) {visitor.visit(this);}
    protected Lex nodeType() {return Lex.TYPE;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return this.toString();}
}

class ReturnNode extends TypeNode {

    private Lex semanticType;

    public ReturnNode(Token t) {
        super(t);
        semanticType = t.getType();
    }
    @Override
    public String toString() {
        return "returns "+getType().toString().toLowerCase();
    }

    @Override
    protected Lex getSemanticType() {return semanticType;}
    @Override
    protected Lex nodeType() {return Lex.RETURN;}
}

class LitNode extends Node {

    private Token literal;
    private Lex semanticType;
    
    public LitNode(Token t) {
        this.literal = t;
        if (this.literal.getType()==Lex.INTEGERLITERAL) {
            this.semanticType = Lex.INTEGER;
        }
        else {this.semanticType = Lex.BOOLEAN;}
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int _int : getCharList()) {builder.append((char) _int);}
        if (getType().equals(Lex.INTEGERLITERAL)) {
            if (literal.getSymbol().equals(Lex.MINUS)) {
                return "(neg) integer literal "+builder;
            }
            return "integer literal "+builder;
        }
        return "boolean literal "+builder;
    }

    protected int[] position() {return new int[]{literal.line,literal.column};}
    protected ArrayList<Integer> getCharList() {return literal.getCharList();}
    protected Lex getType() {return literal.getType();}
    protected void flipSymbol() {literal.flipSymbol();}
    protected void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lex nodeType() {return Lex.LITERAL;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return this.toString();}
}

class ParamNode extends Node {

    private Node id;
    private Node type;
    private String symbol;
    private Lex semanticType;

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
    protected Lex nodeType() {return Lex.PARAMLIST;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return this.toString();}
}

class FnNode extends Node {

    private Node id;
    private ArrayList<Node> parameters = new ArrayList<>();
    private Node returnType;
    private ArrayList<Node> body = new ArrayList<>();
    private Lex semanticType;

    public FnNode(Stack<Node> stack) {
        while (stack.peek().nodeType()!=Lex.RETURN) {body.add(0, stack.pop());}
        returnType = stack.pop();
        while (stack.peek().nodeType()==Lex.PARAMLIST) {parameters.add(0, stack.pop());}
        id = stack.pop();
    }
    public String toString() {
        return "funcion\n   "+id+"\n   parameters "+getParameters()+
               "\n   "+returnType+"\n   body\n";
    }

    protected Node getIdNode() {return id;}
    protected Node getReturnType() {return returnType;}
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
    protected Lex nodeType() {return Lex.FN;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return id.toString();}
}

class CallNode extends Node {

    private Node id;
    private ArrayList<Node> args = new ArrayList<>();
    private Lex semanticType;

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
    protected Lex nodeType() {return Lex.FNCALL;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return id.toString();}
}

class IfNode extends Node {

    private Node _if;
    private Node _then;
    private Node _else;
    private Lex semanticType;

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
    protected Lex nodeType() {return Lex.IF;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return "";}
}

class PrgrmNode extends Node {

    private ArrayList<Node> functionList = new ArrayList<>();
    private Lex semanticType;

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
    protected Lex nodeType() {return Lex.PROGRAM;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return "";}
}

class ExpNode extends Node {

    private Node exp;
    private Lex semanticType;

    public Lex type;
    public ExpNode(Stack<Node> stack) {
        exp = stack.pop();
    }
    public String toString() {return exp.toString();}

    protected Node getNode() {return exp;}
    protected int[] position() {return new int[]{};}
    protected void accept(AstVisitor visitor) throws Analyzer {
        visitor.visit(this);
    }
    protected Lex nodeType() {return Lex.EXP;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return "";}
}

class BinaryNode extends Node {

    private Lex semanticType;
    private Node leftNode;
    private Node rightNode;
    private String operator;
    private Lex nodeType;

    public BinaryNode(Stack<Node> stack, String s, Lex type) {
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
    protected Lex nodeType() {return nodeType;}
    protected Lex getSemanticType() {return semanticType;}
    protected void setSemanticType(Lex type) {semanticType = type;}
    protected String getErrorStr() {return "";}
}

class EqNode extends BinaryNode {public EqNode(Stack<Node> stack) {super(stack, "==", Lex.EQUIVALENT);}}
class MinusNode extends BinaryNode {public MinusNode(Stack<Node> stack) {super(stack, "-", Lex.MINUS);}}
class PlusNode extends BinaryNode {public PlusNode(Stack<Node> stack) {super(stack, "+", Lex.PLUS);}}
class DivideNode extends BinaryNode {public DivideNode(Stack<Node> stack) {super(stack, "/", Lex.DIVIDE);}}
class TimesNode extends BinaryNode {public TimesNode(Stack<Node> stack) {super(stack, "*", Lex.TIMES);}}
class AndNode extends BinaryNode {public AndNode(Stack<Node> stack) {super(stack, "and", Lex.AND);}}
class OrNode extends BinaryNode {public OrNode(Stack<Node> stack) {super(stack, "or", Lex.OR);}}
class LessNode extends BinaryNode {public LessNode(Stack<Node> stack) {super(stack, "<", Lex.LESSTHAN);}}