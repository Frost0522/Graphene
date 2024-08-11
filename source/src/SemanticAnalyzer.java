package src;
import java.util.ArrayList;
import java.util.HashMap;

public class SemanticAnalyzer implements AstVisitor {

    private boolean hasMain;
    private FnNode currentFnNode;
    private IdNode currentIdNode;

    protected SymbolTable table;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        table = new SymbolTable(prgrmNode);
        try {
            for (Node fnNode : prgrmNode.getFunctions()) {
                fnNode.accept(this);
            }
            // Check if the program has a main function
            if (!hasMain) {new Analyzer(Lex.NOMAIN,prgrmNode);}
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
            new Analyzer(Lex.PRIMITIVEFN, currentIdNode);
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
                new Analyzer(Lex.RETURNTYPEERROR,bodyNode);
            }
            fnNode.setSemanticType(bodyNode.getSemanticType());
        }
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {

        if (binNode.getLeft()!=null) {binNode.getLeft().accept(this);}
        if (binNode.getRight()!=null) {binNode.getRight().accept(this);}

        Lex leftType = binNode.getLeft().getSemanticType();
        Lex rightType = binNode.getRight().getSemanticType();

        switch (binNode.nodeType()) {
            case PLUS,MINUS,DIVIDE,TIMES: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                else if (leftType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                else if (rightType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.INTEGER);
                break;
            }
            case AND,OR: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                else if (leftType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getLeft());}
                else if (rightType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
            case EQUIVALENT: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
            case LESSTHAN: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                else if (leftType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                else if (rightType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
        }
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        // If a function does not exist for this call throw an error.
        if(!table.getMap().containsKey(callNode.getId().toString())) {
            new Analyzer(Lex.NOFNCALL,callNode);
        }
        // Since the function does exist, set its type.
        for (FnNode fnNode : table.getFunctions()) {
            if (fnNode.getIdNode().toString().equals(callNode.getId().toString())) {
                callNode.setSemanticType(fnNode.getReturnType().getSemanticType());
            }
        }
        for (Node arg : callNode.getArgs()) {
            arg.accept(this);
            // Verify existence of ids in function call arguments
            if (arg instanceof IdNode && !table.idExists(currentFnNode, arg)) {
                new Analyzer(Lex.NULLOPERAND,arg);
            }
        }
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        // Check Parameters are not named after primitives
        if (currentIdNode.getName().equals("print")) {
            new Analyzer(Lex.PRIMITIVEPARAM, currentIdNode);
        }
    }

    @Override
    public void visit(IdNode idNode) {
        // If the id node matches a parameter in the parameter list, set it's type
        if (idNode.getSemanticType()==null) {table.setIdType(currentFnNode, idNode);}
        currentIdNode = idNode;
    }
}

class SymbolTable implements AstVisitor {

    private ArrayList<FnNode> fnList = new ArrayList<>();
    private HashMap<String,FunctionSymbol> map = new HashMap<>();
    private FunctionSymbol fnSymbol = new FunctionSymbol();
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;

    public SymbolTable(Node node) throws Analyzer {
        node.accept(this);
    }

    protected HashMap<String,FunctionSymbol> getMap() {return map;}
    protected ArrayList<FnNode> getFunctions() {return fnList;}
    protected void setIdType(FnNode fnNode, Node node) {
        if (map.get(fnNode.getIdNode().toString()).getIdNodes().toString().contains(node.toString())) {
            int idIndex = map.get(fnNode.getIdNode().toString()).getIdNodes().toString().indexOf(node.toString());
            node.setSemanticType(map.get(fnNode.getIdNode().toString()).getIdNodes().get(idIndex-1).getSemanticType());
        }
    }
    protected boolean idExists(FnNode fnNode, Node node) {
        if (map.get(fnNode.getIdNode().toString()).getIdNodes().toString().contains(node.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        fnList.add(fnNode);
        fnSymbol = new FunctionSymbol();
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
        map.put(fnNode.getIdNode().toString(), fnSymbol);
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        paramNode.getRight().accept(this);
        currentIdNode.setSemanticType(currentTypeNode.getType());
        fnSymbol.addIdNode(currentIdNode);
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        callNode.getId().accept(this);
        // Add id node of function call node to function symbol
        fnSymbol.addCallNode(currentIdNode);
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

    class FunctionSymbol {

        private ArrayList<IdNode> idNodes;
        private ArrayList<IdNode> callNodes;

        public FunctionSymbol() {
            this.idNodes = new ArrayList<>();
            this.callNodes = new ArrayList<>();
        }
        
        protected void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        protected void addCallNode(IdNode callNode) {callNodes.add(callNode);}
        protected ArrayList<IdNode> getIdNodes() {return idNodes;}
        protected ArrayList<IdNode> getCallNodes() {return callNodes;}
    }
}