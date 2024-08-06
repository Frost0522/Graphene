package src;
import java.util.ArrayList;

public class SemanticAnalyzer implements AstVisitor {

    private boolean hasMain;
    private FnNode currentFnNode;
    private IdNode currentIdNode;
    private LitNode currentLitNode;

    protected SymbolMap symbols;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        symbols = new SymbolMap(prgrmNode);
        try {
            for (Node fnNode : prgrmNode.getFunctions()) {
                fnNode.accept(this);
            }
            // Check if the program has a main function
            if (!hasMain) {new Analyzer(Lexicon.NOMAIN,prgrmNode);}
        }
        catch (Analyzer err) {
            System.out.println(err.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        currentFnNode = fnNode;
        fnNode.getIdNode().accept(this);
        // Check function calls are not named after primitives
        if (currentIdNode.getName().equals("print")) {
            new Analyzer(Lexicon.PRIMITIVEFN, currentIdNode);
        }
        // Set boolean hasMain true if main function is found
        if (currentIdNode.getName().equals("main")) {hasMain=true;}
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
            // Check that the function return type matches that of the body node
            if (currentFnNode.getReturnType().getSemanticType()!=bodyNode.getSemanticType()) {
                new Analyzer(Lexicon.RETURNTYPEERROR,currentFnNode.getReturnType());
            }
        }
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {

        if (binNode.getLeft()!=null) {
            binNode.getLeft().accept(this);
            switch (binNode.getLeft().nodeType()) {
                case ID: {
                    // If the id node matches a parameter in the parameter list, set it's type
                    for (IdNode idNode : symbols.getParamList()) {
                        if (idNode.getName().equals(currentIdNode.getName())) {
                            currentIdNode.setSemanticType(idNode.getSemanticType());
                        }
                    }
                    // If semantic type of the id node is not set, throw an error
                    if (currentIdNode.getSemanticType()==null) {new Analyzer(Lexicon.NULLOPERAND,currentIdNode);}
                    // If the id node (operand) does not match the binary node type (operator), throw an error
                    else if ((Lexicon.isNumericOp().contains(binNode.nodeType())) && (currentIdNode.getSemanticType()==Lexicon.BOOLEAN)) {
                        new Analyzer(Lexicon.INTOPERROR,currentIdNode);
                    }
                    else if ((Lexicon.isBooleanOp().contains(binNode.nodeType())) && (currentIdNode.getSemanticType()==Lexicon.INTEGER)) {
                        new Analyzer(Lexicon.BOOLOPERROR,currentIdNode);
                    }
                    break;
                }
                case INTEGERLITERAL: {
                    if (Lexicon.isBooleanOp().contains(binNode.nodeType())) {
                        new Analyzer(Lexicon.BOOLOPERROR,currentIdNode);
                    }
                    break;
                }
            }
        }

        if (binNode.getRight()!=null) {
            binNode.getRight().accept(this);
            switch (binNode.getRight().nodeType()) {
                case ID: {
                    for (IdNode idNode : symbols.getParamList()) {
                        if (idNode.getName().equals(currentIdNode.getName())) {
                            currentIdNode.setSemanticType(idNode.getSemanticType());
                        }
                    }
                    if (currentIdNode.getSemanticType()==null) {new Analyzer(Lexicon.NULLOPERAND,currentIdNode);}
                    else if ((Lexicon.isNumericOp().contains(binNode.nodeType())) && (currentIdNode.getSemanticType()==Lexicon.BOOLEAN)) {
                        new Analyzer(Lexicon.INTOPERROR,currentIdNode);
                    }
                    else if ((Lexicon.isBooleanOp().contains(binNode.nodeType())) && (currentIdNode.getSemanticType()==Lexicon.INTEGER)) {
                        new Analyzer(Lexicon.BOOLOPERROR,currentIdNode);
                    }
                    break;
                }
                case INTEGERLITERAL: {
                    if (Lexicon.isBooleanOp().contains(binNode.nodeType())) {
                        new Analyzer(Lexicon.BOOLOPERROR,currentIdNode);
                    }
                    break;
                }
            }
            //Finally, set the type of the binary operation, if possible
            if ((Lexicon.isNumericOp().contains(binNode.nodeType()) && (binNode.getLeft().getSemanticType()==binNode.getRight().getSemanticType())) ||
                (Lexicon.isBooleanOp().contains(binNode.nodeType()) && (binNode.getLeft().getSemanticType()==binNode.getRight().getSemanticType()))) {
                binNode.setSemanticType(binNode.getLeft().getSemanticType());
            }
        }
    } 

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        // Check Parameters are not named after primitives
        if (currentIdNode.getName().equals("print")) {
            new Analyzer(Lexicon.PRIMITIVEPARAM, currentIdNode);
        }
    }

    @Override
    public void visit(IdNode idNode) {
        currentIdNode = idNode;
    }

    @Override
    public void visit(LitNode litNode) {
        currentLitNode = litNode;
    }
}

class SymbolMap implements AstVisitor {

    private ArrayList<IdNode> paramList = new ArrayList<>();
    private Node currentFn;
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;

    public SymbolMap(Node node) throws Analyzer {
        node.accept(this);
    }

    protected ArrayList<IdNode> getParamList() {return paramList;}

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        
        currentFn = fnNode;
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        paramList.add(currentIdNode);
        paramNode.getRight().accept(this);
        currentIdNode.setSemanticType(currentTypeNode.getType());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        for (Node arg : callNode.getArgs()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {
        if (binNode.getLeft()!=null) {
            binNode.getLeft().accept(this);
        }
        if (binNode.getRight()!=null) {
            binNode.getRight().accept(this);
        }
    }

    @Override
    public void visit(IfNode ifNode) throws Analyzer {
        ifNode.getIf().accept(this);
        ifNode.getThen().accept(this);
        ifNode.getElse().accept(this);
    }

    @Override
    public void visit(ExpNode expNode) throws Analyzer {
        expNode.getNode().accept(this);
    }

    @Override
    public void visit(IdNode idNode) {
        currentIdNode = idNode;
    }

    @Override
    public void visit(TypeNode typeNode) {
        currentTypeNode = typeNode;
    }
}