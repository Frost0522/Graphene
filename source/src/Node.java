package src;

public class Node {

    private Program program;
    private Identifier id;
    private Type type;
    private Parameter param;
    private Equality equality;
    private Function function;
    private FunctionCall call;
    private IfElse ifState;
    private Literal literal;
    private Minus minus;
    private Plus plus;
    private Divide divide;
    private Times times;
    private And and;
    private Or or;
    private LessThan less;

    private Lexical value;

    public Node() {
        value = Lexical.$;
    }

    public Node(Identifier i) {
        id = i;
        value = Lexical.IDENTIFIER;
    }

    public Node(Type t) {
        type = t;
        if (type instanceof ReturnType) {value = Lexical.RETURN;}
        else {value = Lexical.TYPE;}
    }

    public Node(Parameter p) {
        param = p;
        value = Lexical.PARAMETERLIST;
    }

    public Node(Equality eq) {
        equality = eq;
        value = Lexical.EQUIVALENT;
    }

    public Node(Function f) {
        function = f;
        value = Lexical.FN;
    }

    public Node(FunctionCall fc) {
        call = fc;
        value = Lexical.FNCALL;
    }

    public Node(IfElse i) {
        ifState = i;
        value = Lexical.IF;
    }

    public Node(Literal lit) {
        literal = lit;
        value = Lexical.LITERAL;
    }

    public Node(Minus m) {
        minus = m;
        value = Lexical.MINUS;
    }

    public Node(Plus p) {
        plus = p;
        value = Lexical.PLUS;
    }

    public Node(Divide d) {
        divide = d;
        value = Lexical.DIVIDE;
    }

    public Node(Times t) {
        times = t;
        value = Lexical.TIMES;
    }
    
    public Node(And a) {
        and = a;
        value = Lexical.AND;
    }

    public Node(Or o) {
        or = o;
        value = Lexical.OR;
    }

    public Node(LessThan l) {
        less = l;
        value = Lexical.LESS_THAN;
    }

    public Node(Program p) {
        program = p;
        value = Lexical.PROGRAM;
    } 

    protected Identifier getId() {
        return id;
    }

    protected Type getType() {
        return type;
    }

    protected Parameter getParam() {
        return param;
    }

    protected Equality getEquality() {
        return equality;
    }

    protected Function getFn() {
        return function;
    }

    protected IfElse getIfElse() {
        return ifState;
    }

    protected FunctionCall getFnCall() {
        return call;
    }

    protected Literal getLiteral() {
        return literal;
    }

    protected Minus getMinus() {
        return minus;
    }

    protected Plus getPlus() {
        return plus;
    }

    protected Divide getDivide() {
        return divide;
    }

    protected Times getTimes() {
        return times;
    }

    protected And getAnd() {
        return and;
    }

    protected Or getOr() {
        return or;
    }

    protected LessThan getLessThan() {
        return less;
    }

    protected Program getProgram() {
        return program;
    }

    protected Lexical valueOf() {
        return value;
    }
}
