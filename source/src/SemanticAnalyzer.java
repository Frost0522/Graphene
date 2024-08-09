package src;
import java.util.ArrayList;
import java.util.HashMap;

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
            // So long as the body node is not a print expression, set the funtions semantic type
            if (bodyNode.getSemanticType()!=Lex.PRINTEXP) {
                fnNode.setSemanticType(bodyNode.getSemanticType());
            }
        }
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {

        if (binNode.getLeft()!=null) {binNode.getLeft().accept(this);}
        if (binNode.getRight()!=null) {binNode.getRight().accept(this);}

        Lex leftType = binNode.getLeft().getSemanticType();
        Lex rightType = binNode.getRight().getSemanticType();

        switch (binNode.nodeType()) {
            case PLUS,MINUS,DIVIDE,TIMES,LESSTHAN: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                else if (leftType==Lex.AND||leftType==Lex.OR||leftType==Lex.LESSTHAN||leftType==Lex.EQUIVALENT) {new Analyzer("error");}
                else if (rightType==Lex.AND||rightType==Lex.OR||rightType==Lex.LESSTHAN||rightType==Lex.EQUIVALENT) {new Analyzer("error");}
                else if (leftType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                else if (rightType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                else {binNode.setSemanticType(leftType);}
                break;
            }
            case AND,OR: {
                if (leftType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NULLOPERAND,binNode.getRight());}
                else if (leftType==Lex.PLUS||leftType==Lex.MINUS||leftType==Lex.DIVIDE||leftType==Lex.TIMES) {new Analyzer("error");}
                else if (rightType==Lex.PLUS||rightType==Lex.MINUS||rightType==Lex.DIVIDE||rightType==Lex.TIMES) {new Analyzer("error");}
                else if (leftType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getLeft());}
                else if (rightType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getRight());}
                else {binNode.setSemanticType(leftType);}
                break;
            }
        }
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {

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
        if (idNode.getSemanticType()==null) {
            // If the id node matches a parameter in the parameter list, set it's type
            symbols.setParam(currentFnNode, idNode);
            
        }
        currentIdNode = idNode;
    }

    @Override
    public void visit(LitNode litNode) {
        currentLitNode = litNode;
    }

    private static boolean returnsInt(Node binNode) {
        Lex opType = binNode.nodeType();
        Lex[] typeList = {Lex.PLUS,Lex.MINUS,Lex.DIVIDE,Lex.TIMES,Lex.LESSTHAN};
        for (Lex intOp : typeList) {
            if (intOp==opType) {return true;}
        }
        return false;
    }

    private static boolean returnsBool(Node binNode) {
        Lex opType = binNode.nodeType();
        Lex[] typeList = {Lex.OR,Lex.AND};
        for (Lex boolOp : typeList) {
            if (boolOp==opType) {return true;}
        }
        return false;
    }

    private static boolean allBoolTypes(Node binNode) {
        Lex opType = binNode.nodeType();
        Lex[] typeList = {Lex.OR,Lex.AND,Lex.LESSTHAN,Lex.EQUIVALENT};
        for (Lex boolOp : typeList) {
            if (boolOp==opType) {return true;}
        }
        return false;
    }
}

class SymbolMap implements AstVisitor {

    private HashMap<String,FunctionSymbol> map = new HashMap<>();
    private FunctionSymbol fnSymbol = new FunctionSymbol();
    private FnNode currentFn;
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;

    public SymbolMap(Node node) throws Analyzer {
        node.accept(this);
    }

    protected HashMap<String,FunctionSymbol> getMap() {return map;}
    protected void setParam(FnNode fnNode, IdNode idNode) {
        for (IdNode id : map.get(fnNode.getIdNode().toString()).getParamNodes()) {
            if (id.toString().equals(idNode.toString())) {
                idNode.setSemanticType(id.getSemanticType());
            }
        }
    }

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        fnSymbol = new FunctionSymbol();
        currentFn = fnNode;
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
        fnSymbol.addParamNode(currentIdNode);
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        callNode.getId().accept(this);
        // Add id node of function call node to function symbol
        fnSymbol.addCallNode(currentIdNode);
        for (Node arg : callNode.getArgs()) {
            arg.accept(this);
            // If arg is an id node add it to function symbol
            if (arg.toString().equals(currentIdNode)) {
                fnSymbol.addIdNode(currentIdNode);
            }
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
        private ArrayList<IdNode> paramNodes; 
        private ArrayList<IdNode> callNodes;

        public FunctionSymbol() {
            this.idNodes = new ArrayList<>();
            this.callNodes = new ArrayList<>();
            this.paramNodes = new ArrayList<>();
        }
        
        protected void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        protected void addCallNode(IdNode callNode) {callNodes.add(callNode);}
        protected void addParamNode(IdNode idNode) {paramNodes.add(idNode);}
        protected ArrayList<IdNode> getIdNodes() {return idNodes;}
        protected ArrayList<IdNode> getCallNodes() {return callNodes;}
        protected ArrayList<IdNode> getParamNodes() {return paramNodes;}
    }
}