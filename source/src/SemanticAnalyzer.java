package src;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SemanticAnalyzer implements AstVisitor {

    private boolean hasMain;
    private FnNode currentFnNode;
    private IdNode currentIdNode;
    private StringBuilder builder = new StringBuilder();
    private void incDepth(int amount) {for (int i=0;i<amount;i++) {builder.append("   ");}}

    protected SymbolTable table;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        try {
            table = new SymbolTable(prgrmNode);
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
        if (!(table.getCallNames.contains(currentIdNode.getName()) ||
            currentIdNode.getName().equals("main"))) {
            new Analyzer(Lex.UNUSEDFN,currentIdNode);
        }
        // Check functions are not named after primitives
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
            int idIndex = table.getMap().get(currentFnNode).getParamNames().indexOf(idNode.getName());
            if (idIndex!=-1) {
                idNode.setSemanticType(table.getMap().get(currentFnNode).getParamNodes().get(idIndex).getSemanticType());
            }
        }
        currentIdNode = idNode;
    }

    public String toString() {
        for (FnNode fnNode : table.getMap().keySet()) {
            builder.append("function "+fnNode.getIdNode().toString().replace("identifier ","")+"\n");
            incDepth(1);
            builder.append("function type: "+fnNode.getSemanticType()+"\n"); 
            incDepth(1);
            builder.append("parameters:\n");
            for (String param : table.getMap().get(fnNode).getParamNames()) {
                incDepth(2); builder.append(param+"\n");
            }
            incDepth(1);
            builder.append("callee(s):\n");
            for (String call : table.getMap().get(fnNode).getCallNames()) {
                incDepth(2); builder.append(call+"\n");
            }
            incDepth(1);
            builder.append("caller(s):\n");
            for (FnNode fn : table.getMap().keySet()) {
                if (table.getMap().get(fn).getCallNames().contains(fnNode.getIdNode().toString().replace("identifier ",""))) {
                    incDepth(2); builder.append(fn.getIdNode().toString().replace("identifier ","")+"\n");
                }
            }
            builder.append("\n");
        }
        return builder.toString().trim();
    }
}

class SymbolTable implements AstVisitor {

    private HashMap<FnNode,FunctionSymbol> map = new HashMap<>();
    private FunctionSymbol fnSymbol = new FunctionSymbol();
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;

    public SymbolTable(Node node) throws Analyzer {
        node.accept(this);
    }

    protected HashMap<FnNode,FunctionSymbol> getMap() {return map;}
    protected boolean idExists(FnNode fnNode, Node idNode) {
        if (map.get(fnNode).getParamNames().contains(idNode.toString().replace("identifier ",""))) {
            return true;
        } return false;
    }
    protected ArrayList<String> getCallNames = new ArrayList<>();

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        for (FnNode key : map.keySet()) {
            if (key.getIdNode().toString().equals(fnNode.getIdNode().toString())) {
                new Analyzer(Lex.FNNAMECONFLICT,fnNode.getIdNode());
            }
        }
        fnSymbol = new FunctionSymbol();
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
        }
        for (IdNode param : fnSymbol.getParamNodes()) {
            if (!fnSymbol.getidNames().contains(param.getName())) {
                new Analyzer(Lex.UNUSEDPARAM,param);
            }
        }
        map.put(fnNode, fnSymbol);
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        paramNode.getRight().accept(this);
        if (fnSymbol.getParamNames().contains(currentIdNode.getName())) {
            new Analyzer(Lex.PARAMNAMECONFLICT,currentIdNode);
        }
        currentIdNode.setSemanticType(currentTypeNode.getType());
        fnSymbol.addParamNode(currentIdNode);
        fnSymbol.addIdType(currentTypeNode.getType());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        callNode.getId().accept(this);
        fnSymbol.addCallNode(currentIdNode);
        getCallNames.add(currentIdNode.getName());
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
        if (fnSymbol.getParamNames().contains(idNode.getName())) {fnSymbol.addIdNode(idNode);}
        currentIdNode = idNode;
    }

    @Override
    public void visit(TypeNode typeNode) {
        currentTypeNode = typeNode;
    }

    class FunctionSymbol {

        private ArrayList<String> stringArray;
        private ArrayList<IdNode> paramNodes;
        private ArrayList<Lex> idTypes;
        private ArrayList<IdNode> printNodes;
        private ArrayList<IdNode> idNodes;
        private ArrayList<IdNode> callNodes;

        public FunctionSymbol() {
            this.paramNodes = new ArrayList<>();
            this.idNodes = new ArrayList<>();
            this.idTypes = new ArrayList<>();
            this.printNodes = new ArrayList<>();
            this.paramNodes = new ArrayList<>();
            this.callNodes = new ArrayList<>();
        }
        
        private void addParamNode(IdNode paramNode) {paramNodes.add(paramNode);}
        private void addIdType(Lex type) {idTypes.add(type);}
        private void addPrintNode(IdNode printNode) {printNodes.add(printNode);}
        private void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        private void addCallNode(IdNode callNode) {callNodes.add(callNode);}
        private ArrayList<String> getidNames () {
            stringArray = new ArrayList<>();
            for (IdNode id : idNodes) {stringArray.add(id.getName());}
            return stringArray;
        }

        protected ArrayList<String> getParamNames() {
            stringArray = new ArrayList<>();
            for (IdNode param : getParamNodes()) {stringArray.add(param.getName());}
            return stringArray;
        }
        protected ArrayList<String> getCallNames() {
            stringArray = new ArrayList<>();
            for (IdNode call : getCallNodes()) {if (!stringArray.contains(call.getName())) {stringArray.add(call.getName());}}
            return stringArray;
        }
        protected ArrayList<IdNode> getParamNodes() {return paramNodes;}
        protected ArrayList<Lex> getIdTypes() {return idTypes;}
        protected ArrayList<IdNode> getPrintNodes() {return printNodes;}
        protected ArrayList<IdNode> getCallNodes() {return callNodes;}
    }
}