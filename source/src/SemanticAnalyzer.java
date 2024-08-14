package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

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
            // Exception for primitive function calls to print, continue until non-print node.
            if (bodyNode.getSemanticType().equals(Lex.PRINTEXP)) {continue;}
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
                if (leftType==null) {new Analyzer(Lex.NOID,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NOID,binNode.getRight());}
                else if (leftType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                else if (rightType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.INTEGER);
                break;
            }
            case AND,OR: {
                if (leftType==null) {new Analyzer(Lex.NOID,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NOID,binNode.getRight());}
                else if (leftType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getLeft());}
                else if (rightType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
            case EQUIVALENT: {
                if (leftType==null) {new Analyzer(Lex.NOID,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NOID,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
            case LESSTHAN: {
                if (leftType==null) {new Analyzer(Lex.NOID,binNode.getLeft());}
                else if (rightType==null) {new Analyzer(Lex.NOID,binNode.getRight());}
                else if (leftType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                else if (rightType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                binNode.setSemanticType(Lex.BOOLEAN);
                break;
            }
        }
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        boolean isPrint = callNode.getId().toString().equals("identifier print");
        // First check to see of the funciton call is a primitive print.
        if (isPrint) {
            callNode.setSemanticType(Lex.PRINTEXP);
            for (Node arg : callNode.getArgs()) {
                arg.accept(this);
                // Verify existence of ids in function call arguments.
                if (arg instanceof IdNode && (!table.idExists(currentFnNode, arg))) {
                    new Analyzer(Lex.NOID,arg);
                }
            }
        }
        else {
            FnNode refFunction = currentFnNode;
            // If a function does not exist for this call throw an error.
            boolean found = false;
            for (FnNode key : table.getMap().keySet()) {
                if (key.getIdNode().toString().equals(callNode.getId().toString())) {
                    found = true; refFunction = key;
                }
            } if (!found) {new Analyzer(Lex.NOFNCALL,callNode);}

            // Verify the call's number of arguments equal the number of parameters to the function it is referencing.
            if (refFunction.getParamNodes().size()>callNode.getArgs().size()) {
                new Analyzer(Lex.TOOFEWARGS,callNode);
            }
            else if (refFunction.getParamNodes().size()<callNode.getArgs().size()) {
                new Analyzer(Lex.TOOMANYARGS,callNode);
            }
            callNode.setSemanticType(refFunction.getReturnType().getSemanticType());
            for (Node arg : callNode.getArgs()) {
                arg.accept(this);
                // Verify existence of ids in function call arguments.
                if (arg instanceof IdNode && (!table.idExists(currentFnNode, arg))) {
                    new Analyzer(Lex.NOID,arg);
                }
            }
            // Verify type correctness for the arguments of the call to the function it is referncing.
            for (int i=0; i<=callNode.getArgs().size()-1; i++) {
                if (callNode.getArgs().get(i).getSemanticType()!=table.getMap().get(refFunction).getIdTypes().get(i)) {
                    new Analyzer(Lex.BADARGTYPE,callNode.getArgs().get(i));
                }
            }
        }
    }

    @Override
    public void visit(IfNode ifNode) throws Analyzer {
        // Begin type check of if clause.
        ifNode.getIf().accept(this);
        if (ifNode.getIf().getSemanticType()!=Lex.BOOLEAN) {
            new Analyzer(Lex.IFOPERROR,ifNode.getIf());
        }
        // End of if clause.
        // Begin type checks of then clause.
        ifNode.getThen().accept(this);
        if (ifNode.getThen() instanceof CallNode && ifNode.getThen().getSemanticType()==null) {
            new Analyzer(Lex.NOFNCALL,ifNode.getThen());
        }
        else if (ifNode.getThen() instanceof IdNode && ifNode.getThen().getSemanticType()==null) {
            new Analyzer(Lex.NOID,ifNode.getThen());
        }
        // End of then clause.
        // Begin type checks of else clause.
        ifNode.getElse().accept(this);
        if (ifNode.getElse() instanceof CallNode && ifNode.getElse().getSemanticType()==null) {
            new Analyzer(Lex.NOFNCALL,ifNode.getElse());
        }
        else if (ifNode.getElse() instanceof IdNode && ifNode.getElse().getSemanticType()==null) {
            new Analyzer(Lex.NOID,ifNode.getElse());
        }
        // End of else clause.
        // Finally check then and else clause match, if so set if node semantic type, else error.
        if (ifNode.getThen().getSemanticType()==ifNode.getElse().getSemanticType()) {
            ifNode.setSemanticType(ifNode.getThen().getSemanticType());
        }
        else {new Analyzer(Lex.DIFFCLAUSES,ifNode.getThen());} 

    }

    @Override
    public void visit(ExpNode expNode) throws Analyzer {
        expNode.getNode().accept(this);
        expNode.setSemanticType(expNode.getNode().getSemanticType());
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
        if (idNode.getSemanticType()==null) {
            int idIndex = table.getMap().get(currentFnNode).getIdStrings().indexOf(idNode.toString());
            if (idIndex!=-1) {
                idNode.setSemanticType(table.getMap().get(currentFnNode).getIdNodes().get(idIndex).getSemanticType());
            }
        }
        currentIdNode = idNode;
    }
}

class SymbolTable implements AstVisitor {

    // private ArrayList<FnNode> fnList = new ArrayList<>();
    private HashMap<FnNode,FunctionSymbol> map = new HashMap<>();
    private FunctionSymbol fnSymbol = new FunctionSymbol();
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;

    public SymbolTable(Node node) throws Analyzer {
        node.accept(this);
    }

    protected HashMap<FnNode,FunctionSymbol> getMap() {return map;}
    protected boolean idExists(FnNode fnNode, Node node) {
        if (map.get(fnNode).getIdStrings().contains(node.toString())) {
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
        fnSymbol = new FunctionSymbol();
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
        map.put(fnNode, fnSymbol);
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        paramNode.getRight().accept(this);
        currentIdNode.setSemanticType(currentTypeNode.getType());
        fnSymbol.addIdNode(currentIdNode);
        fnSymbol.addIdType(currentTypeNode.getType());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        callNode.getId().accept(this);
        // Add id node of primitive function call print to function symbol
        if (currentIdNode.getName().equals("print")) {fnSymbol.addPrintNode(currentIdNode);}
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
        private ArrayList<Lex> idTypes;
        private ArrayList<IdNode> printNodes;

        public FunctionSymbol() {
            this.idNodes = new ArrayList<>();
            this.idTypes = new ArrayList<>();
            this.printNodes = new ArrayList<>();
        }
        
        protected void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        protected void addIdType(Lex type) {idTypes.add(type);}
        protected void addPrintNode(IdNode printNode) {printNodes.add(printNode);}
        protected ArrayList<IdNode> getIdNodes() {return idNodes;}
        protected ArrayList<String> getIdStrings() {return getIdNodes().stream().map(IdNode::toString).collect(Collectors.toCollection(ArrayList::new));}
        protected ArrayList<Lex> getIdTypes() {return idTypes;}
        protected ArrayList<IdNode> getPrintNodes() {return printNodes;}
    }
}