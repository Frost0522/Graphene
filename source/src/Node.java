package src;

public class Node {

    private Identifier id;
    private Type type;
    private Parameter param;
    private Equality equality;
    private Function function;
    private FunctionCall call;
    private Literal literal;
    private Minus minus;
    private Plus plus;
    private Divide divide;
    private Times times;
    private And and;
    private Or or;
    private LessThan less;

    private Lexical value;

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

    protected Identifier getId() {
        return this.id;
    }

    protected Type getType() {
        return this.type;
    }

    protected Parameter getParam() {
        return this.param;
    }

    protected Equality getEquality() {
        return this.equality;
    }

    protected Function getFn() {
        return this.function;
    }

    protected FunctionCall getFnCall() {
        return this.call;
    }

    protected Literal getLiteral() {
        return this.literal;
    }

    protected Minus getMinus() {
        return this.minus;
    }

    protected Plus getPlus() {
        return this.plus;
    }

    protected Divide getDivide() {
        return this.divide;
    }

    protected Times getTimes() {
        return this.times;
    }

    protected And getAnd() {
        return this.and;
    }

    protected Or getOr() {
        return this.or;
    }

    protected LessThan getLessThan() {
        return this.less;
    }

    protected Lexical valueOf() {
        return this.value;
    }
}
