package src;

public class Node {

    private Program program;
    private Identifier id;
    private Type type;
    private Parameter param;
    private Equality equality;
    private Function function;
    private FunctionCall call;
    private If ifState;
    private Literal literal;
    private Minus minus;
    private Plus plus;
    private Divide divide;
    private Times times;
    private And and;
    private Or or;
    private LessThan less;
    private Expression exp;
    private Lexicon nodeType;

    public Node() {
        nodeType = Lexicon.$;
    }

    public Node(Identifier i) {
        id = i;
        nodeType = Lexicon.ID;
    }

    public Node(Type t) {
        type = t;
        if (type instanceof ReturnType) {nodeType = Lexicon.RETURN;}
        else {nodeType = Lexicon.TYPE;}
    }

    public Node(Parameter p) {
        param = p;
        nodeType = Lexicon.PARAMLIST;
    }

    public Node(Equality eq) {
        equality = eq;
        nodeType = Lexicon.EQUIVALENT;
    }

    public Node(Function f) {
        function = f;
        nodeType = Lexicon.FN;
    }

    public Node(FunctionCall fc) {
        call = fc;
        nodeType = Lexicon.FNCALL;
    }

    public Node(If i) {
        ifState = i;
        nodeType = Lexicon.IF;
    }

    public Node(Literal lit) {
        literal = lit;
        nodeType = Lexicon.LITERAL;
    }

    public Node(Minus m) {
        minus = m;
        nodeType = Lexicon.MINUS;
    }

    public Node(Plus p) {
        plus = p;
        nodeType = Lexicon.PLUS;
    }

    public Node(Divide d) {
        divide = d;
        nodeType = Lexicon.DIVIDE;
    }

    public Node(Times t) {
        times = t;
        nodeType = Lexicon.TIMES;
    }
    
    public Node(And a) {
        and = a;
        nodeType = Lexicon.AND;
    }

    public Node(Or o) {
        or = o;
        nodeType = Lexicon.OR;
    }

    public Node(LessThan l) {
        less = l;
        nodeType = Lexicon.LESSTHAN;
    }

    public Node(Program p) {
        program = p;
        nodeType = Lexicon.PROGRAM;
    }

    public Node(Expression e) {
        exp = e;
        nodeType = Lexicon.EXP;
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

    protected If getIfState() {
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

    protected Expression getExp() {
        return exp;
    }

    protected Lexicon valueOf() {
        return nodeType;
    }
}
