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
        System.out.println();
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {
        if (binNode.getLeft()!=null) {
            binNode.getLeft().accept(this);
            switch (binNode.getLeft().nodeType()) {
                case ID: {
                    for (IdNode idNode : symbols.getParamList()) {
                        if (idNode.getName()==currentIdNode.getName()) {}
                        currentIdNode.semanticType = idNode.semanticType;
                    }
                }
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
        paramNode.getRight().accept(this);
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
        currentIdNode.semanticType = currentTypeNode.getType();
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