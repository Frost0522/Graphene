package src;
import java.util.ArrayList;
import java.util.Stack;

abstract class NewNode {
    abstract void accept(NewAstVisitor visitor);
    abstract Lexicon nodeType();
    abstract int[] position();
}

public interface NewAstVisitor {
    default public void visit(IdNode node) {}
    default public void visit(TypeNode node) {}
    default public void visit(ReturnNode node) {}
    default public void visit(LitNode node) {}
    default public void visit(ParamNode node) {}
    default public void visit(BinaryNode node) {}
    default public void visit(EqNode node) {}
    default public void visit(MinusNode node) {}
    default public void visit(PlusNode node) {}
    default public void visit(DivideNode node) {}
    default public void visit(TimesNode node) {}
    default public void visit(AndNode node) {}
    default public void visit(OrNode node) {}
    default public void visit(LessNode node) {}
    default public void visit(FnNode node) {}
    default public void visit(CallNode node) {}
    default public void visit(IfNode node) {}
    default public void visit(PrgrmNode node) {}
    default public void visit(ExpNode node) {}
    default public void visit(NullNode node) {}
}

class NullNode extends NewNode {

    public NullNode() {}
    protected int[] position() {return new int[]{};}
    protected void accept(NewAstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.$;}
}

class IdNode extends NewNode {

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
        return "name "+builder;
    }
    protected void accept(NewAstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.ID;}
}

class TypeNode extends NewNode {

    private Token type;

    public TypeNode(Token t) {
        this.type = t;
    }
    public String toString() {return type.getType().toString().toLowerCase();}

    protected int[] position() {return new int[]{type.line,type.column};}
    protected Lexicon getType() {return type.getType();}
    protected void accept(NewAstVisitor visitor) {visitor.visit(this);}
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

class LitNode extends NewNode {

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
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.LITERAL;
    }
}

class ParamNode extends NewNode {

    private NewNode id;
    private NewNode type;
    private String symbol;

    public ParamNode(Stack<NewNode> stack) {
        this.type = stack.pop(); this.id = stack.pop(); this.symbol = ":";
    }
    public String toString() {
        return new String(id+" "+symbol+" "+type.toString());
    }

    protected int[] position() {return id.position();}
    protected NewNode getLeft() {return id;}
    protected NewNode getRight() {return type;}
    protected void accept(NewAstVisitor visitor) {visitor.visit(this);}
    protected Lexicon nodeType() {return Lexicon.PARAMLIST;}
}

class FnNode extends NewNode {

    private NewNode id;
    private ArrayList<NewNode> parameters = new ArrayList<>();
    private NewNode type;
    private ArrayList<NewNode> body = new ArrayList<>();

    public FnNode(Stack<NewNode> stack) {
        while (stack.peek().nodeType()!=Lexicon.RETURN) {body.add(0, stack.pop());}
        type = stack.pop();
        while (stack.peek().nodeType()==Lexicon.PARAMLIST) {parameters.add(0, stack.pop());}
        id = stack.pop();
    }
    public String toString() {
        return "funcion\n   "+id+"\n   parameters "+getParameters()+
               "\n   "+type+"\n   body\n";
    }

    protected NewNode getIdNode() {return id;}
    protected NewNode getNodeType() {return type;}
    protected ArrayList<NewNode> getParamNodes() {return parameters;}
    protected ArrayList<NewNode> getBodyNodes() {return body;}
    protected String getParameters() {
        StringBuilder builder = new StringBuilder();
        for (NewNode node : parameters) {
            if (parameters.indexOf(node) == parameters.size()-1) {builder.append("\n      "+node.toString());}
            else {builder.append("\n      "+node.toString());}
        }
        return builder.toString();
    }
    protected int[] position() {return id.position();}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.FN;
    }
}

class CallNode extends NewNode {

    private NewNode id;
    private ArrayList<NewNode> args = new ArrayList<>();

    public CallNode(Stack<NewNode> stack) {
        NewNode node = stack.pop();
        while (!(node instanceof NullNode)) {args.add(0,node); node = stack.pop();}
        id = stack.pop();
    }
    public String toString() {return "function call"+"\n   "+id+"\n   "+"args";}

    protected NewNode getId() {return id;}
    protected ArrayList<NewNode> getArgs() {return args;}
    protected int[] position() {return id.position();}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.FNCALL;
    }
}

class IfNode extends NewNode {

    private NewNode _if;
    private NewNode _then;
    private NewNode _else;

    public IfNode(Stack<NewNode> stack) {
        _else = stack.pop();
        _then = stack.pop();
        _if = stack.pop();
    }
    public String toString() {return "if\n   "+_if+"\nthen\n   "+_then+"\nelse\n   "+_else;}

    protected NewNode getIf() {return _if;}
    protected NewNode getThen() {return _then;}
    protected NewNode getElse() {return _else;}
    protected int[] position() {return _if.position();}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.IF;
    }
}

class PrgrmNode extends NewNode {

    private ArrayList<NewNode> functionList = new ArrayList<>();

    public PrgrmNode(Stack<NewNode> stack) {
        while (!stack.empty()) {functionList.add(0,stack.pop());}
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (NewNode fnNode : functionList) {builder.append(fnNode+"\n");}
        return builder.toString();
    }

    protected ArrayList<NewNode> getFunctions() {return functionList;}
    protected int[] position() {return new int[]{0,0};}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.PROGRAM;
    }
}

class ExpNode extends NewNode {

    private NewNode exp;
    public Lexicon type;

    public ExpNode(Stack<NewNode> stack) {
        exp = stack.pop();
    }
    public String toString() {return exp.toString();}

    protected NewNode getNode() {return exp;}
    protected int[] position() {return new int[]{};}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {
        return Lexicon.EXP;
    }
}

class BinaryNode extends NewNode {

    private NewNode leftNode;
    private NewNode rightNode;
    private String operator;
    private Lexicon nodeType;

    public BinaryNode(Stack<NewNode> stack, String s, Lexicon type) {
        this.rightNode = stack.pop(); this.leftNode = stack.pop();
        this.operator = s; nodeType = type;
    }
    public String toString() {return "operator "+operator+"\n"+"left "+leftNode+"\n"+"right "+rightNode+"\n";}

    protected String getSymbol() {return operator;}
    protected int[] position() {return leftNode.position();}
    protected NewNode getLeft() {return leftNode;}
    protected NewNode getRight() {return rightNode;}
    protected void accept(NewAstVisitor visitor) {
        visitor.visit(this);
    }
    protected Lexicon nodeType() {return nodeType;}
}

class EqNode extends BinaryNode {public EqNode(Stack<NewNode> stack) {super(stack, "==", Lexicon.EQUIVALENT);}}
class MinusNode extends BinaryNode {public MinusNode(Stack<NewNode> stack) {super(stack, "-", Lexicon.MINUS);}}
class PlusNode extends BinaryNode {public PlusNode(Stack<NewNode> stack) {super(stack, "+", Lexicon.PLUS);}}
class DivideNode extends BinaryNode {public DivideNode(Stack<NewNode> stack) {super(stack, "/", Lexicon.DIVIDE);}}
class TimesNode extends BinaryNode {public TimesNode(Stack<NewNode> stack) {super(stack, "*", Lexicon.TIMES);}}
class AndNode extends BinaryNode {public AndNode(Stack<NewNode> stack) {super(stack, "and", Lexicon.AND);}}
class OrNode extends BinaryNode {public OrNode(Stack<NewNode> stack) {super(stack, "or", Lexicon.OR);}}
class LessNode extends BinaryNode {public LessNode(Stack<NewNode> stack) {super(stack, "<", Lexicon.LESSTHAN);}}