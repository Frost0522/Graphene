package src;
import java.util.ArrayList;

public class FunctionCall implements Expression {
    
    private Identifier name;
    private ArrayList<Node> args;

    public FunctionCall() {
        name = new Identifier(new Token(Lexical.IDENTIFIER, new ArrayList<>()));
        args = new ArrayList<Node>();
    }

    protected Identifier getName() {return name;}
    protected ArrayList<Node> getArgs() {return args;}
    protected void setArgs(ArrayList<Node> list) {for (Node n : list) {args.add(0,n);}}
    protected void setFnName(Identifier id) {name = id;}
    public String toString() {

        StringBuilder callName = new StringBuilder();
        StringBuilder callArgs = new StringBuilder();
        callName.append(name.toString().replace("identifier ", ""));
        for (Node node : args) {
            if (node == args.get(args.size()-1)) {
                switch (node.valueOf()) {
                    case FNCALL: callArgs.append(node.getFnCall().name.toString().replace("identifier ", "")+"("+node.getFnCall().argsToString()+")"); break;
                    case IDENTIFIER: callArgs.append(node.getId()); break;
                    case LITERAL: callArgs.append(node.getLiteral()); break;
                    case MINUS: callArgs.append(node.getMinus()); break;
                    case PLUS: callArgs.append(node.getPlus()); break;
                    case DIVIDE: callArgs.append(node.getDivide()); break;
                    case TIMES: callArgs.append(node.getTimes()); break;
                    case AND: callArgs.append(node.getAnd()); break;
                    case OR: callArgs.append(node.getOr()); break;
                    case LESS_THAN: callArgs.append(node.getLessThan()); break;
                    case EQUIVALENT: callArgs.append(node.getEquality()); break;
                    default: break;
                }
            }

            else {
                switch (node.valueOf()) {
                    case FNCALL: callArgs.append(node.getFnCall()+", "); break;
                    case IDENTIFIER: callArgs.append(node.getId()+", "); break;
                    case LITERAL: callArgs.append(node.getLiteral()+", "); break;
                    case MINUS: callArgs.append(node.getMinus()+", "); break;
                    case PLUS: callArgs.append(node.getPlus()+", "); break;
                    case DIVIDE: callArgs.append(node.getDivide()+", "); break;
                    case TIMES: callArgs.append(node.getTimes()+", "); break;
                    case AND: callArgs.append(node.getAnd()+", "); break;
                    case OR: callArgs.append(node.getOr()+", "); break;
                    case LESS_THAN: callArgs.append(node.getLessThan()+", "); break;
                    case EQUIVALENT: callArgs.append(node.getEquality()+", "); break;
                    default: break;
                }
            }
        }
        return "function_call "+"\n         "+"name "+callName+
               "\n         "+"args "+"\n            "+callArgs;
    }

    private String argsToString() {

        StringBuilder nestedCallArgs = new StringBuilder();
        for (Node node : args) {
            if (node == args.get(args.size()-1)) {
                switch (node.valueOf()) {
                    case FNCALL: nestedCallArgs.append(node.getFnCall().name+"("+node.getFnCall()+")"); break;
                    case IDENTIFIER: nestedCallArgs.append(node.getId()); break;
                    case LITERAL: nestedCallArgs.append(node.getLiteral()); break;
                    case MINUS: nestedCallArgs.append(node.getMinus()); break;
                    case PLUS: nestedCallArgs.append(node.getPlus()); break;
                    case DIVIDE: nestedCallArgs.append(node.getDivide()); break;
                    case TIMES: nestedCallArgs.append(node.getTimes()); break;
                    case AND: nestedCallArgs.append(node.getAnd()); break;
                    case OR: nestedCallArgs.append(node.getOr()); break;
                    case LESS_THAN: nestedCallArgs.append(node.getLessThan()); break;
                    case EQUIVALENT: nestedCallArgs.append(node.getEquality()); break;
                    default: break;
                }
            }
            else {
                switch (node.valueOf()) {
                    case FNCALL: nestedCallArgs.append(node.getFnCall().name+"("+", "+node.getFnCall()+")"); break;
                    case IDENTIFIER: nestedCallArgs.append(node.getId()+", "); break;
                    case LITERAL: nestedCallArgs.append(node.getLiteral()+", "); break;
                    case MINUS: nestedCallArgs.append(node.getMinus()+", "); break;
                    case PLUS: nestedCallArgs.append(node.getPlus()+", "); break;
                    case DIVIDE: nestedCallArgs.append(node.getDivide()+", "); break;
                    case TIMES: nestedCallArgs.append(node.getTimes()+", "); break;
                    case AND: nestedCallArgs.append(node.getAnd()+", "); break;
                    case OR: nestedCallArgs.append(node.getOr()+", "); break;
                    case LESS_THAN: nestedCallArgs.append(node.getLessThan()+", "); break;
                    case EQUIVALENT: nestedCallArgs.append(node.getEquality()+", "); break;
                    default: break;
                }
            }
        }
        return nestedCallArgs.toString();
    }

    public Node accept(AstVisitor v) {
        return v.visit(this);
    }
}
