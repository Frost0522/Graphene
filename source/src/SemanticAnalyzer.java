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
            if (bodyNode.getSemanticType()==Lex.PRINTEXP) {continue;}
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
    public void visit(NotNode notNode) throws Analyzer {
        notNode.getNode().accept(this);
        if (notNode.getNode().getSemanticType()!=Lex.BOOLEAN) {
            new Analyzer(Lex.NOTOPERROR,notNode);
        }
        else {notNode.setSemanticType(notNode.getNode().getSemanticType());}
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
        // Make sure that the sign (negative or positive) and type of the node make sense.
        if (callNode.getSemanticType()==Lex.BOOLEAN && callNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,callNode);
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
        // Make sure that the sign (negative or positive) and type of the node make sense.
        if (expNode.getSemanticType()==Lex.BOOLEAN && expNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,expNode);
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
    public void visit(IdNode idNode) throws Analyzer {
        // If the id node matches a parameter in the parameter list, set it's type
        if (idNode.getSemanticType()==null) {
            int idIndex = table.getMap().get(currentFnNode).getParamNames().indexOf(idNode.getName());
            if (idIndex!=-1) {
                idNode.setSemanticType(table.getMap().get(currentFnNode).getParamNodes().get(idIndex).getSemanticType());
            }
        }
        currentIdNode = idNode;
        // Make sure that the sign (negative or positive) and type of the node make sense.
        if (idNode.getSemanticType()==Lex.BOOLEAN && idNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,idNode);
        }
    }

    @Override
    public void visit(LitNode litNode) throws Analyzer {
        // Make sure that the sign (negative or positive) and type of the node make sense.
        if (litNode.getSemanticType()==Lex.BOOLEAN && litNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,litNode);
        }
    }

    public String toString() {return table.toString().trim();}
}

class SymbolTable implements AstVisitor {

    private HashMap<FnNode,FunctionSymbol> map = new HashMap<>();
    private FunctionSymbol fnSymbol = new FunctionSymbol();
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;
    private StringBuilder builder = new StringBuilder();
    private ArrayList<Node> fnNodes = new ArrayList<>();

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
        fnNodes = prgrmNode.getFunctions();
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        fnSymbol = new FunctionSymbol();
        fnNode.getIdNode().accept(this);
        fnSymbol.setFnId(currentIdNode);
        for (FnNode key : map.keySet()) {
            if (key.getIdNode().toString().equals(fnNode.getIdNode().toString())) {
                new Analyzer(Lex.FNNAMECONFLICT,fnNode.getIdNode());
            }
        }
        for (Node paramNode : fnNode.getParamNodes()) {paramNode.accept(this);}
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
        for (IdNode param : fnSymbol.getParamNodes()) {
            if (!fnSymbol.getidNames().contains(param.getName())) {new Analyzer(Lex.UNUSEDPARAM,param);}
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
        if (!fnSymbol.getCalleeNames().contains(currentIdNode.getName())) {fnSymbol.addCalleeNode(currentIdNode);}
        if (!getCallNames.contains(currentIdNode.getName())) {getCallNames.add(currentIdNode.getName());}
        for (Node arg : callNode.getArgs()) {arg.accept(this);}
    }

    @Override
    public void visit(BinaryNode binNode) throws Analyzer {
        if (binNode.getLeft()!=null) {binNode.getLeft().accept(this);}
        if (binNode.getRight()!=null) {binNode.getRight().accept(this);}
    }

    @Override
    public void visit(NotNode notNode) throws Analyzer {
        notNode.getNode().accept(this);
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

    public String toString() {
        
        for (Node key : fnNodes) {
            builder.append("function: "+map.get(key).getFnIdName()+"\n");
            builder.append("   return type: "+key.getSemanticType().toString().toLowerCase()+"\n");
            builder.append("   parameters:\n");
            for (String param : map.get(key).getParamNames()) {builder.append("      "+param+"\n");}
            builder.append("   callee(s):\n");
            for (String callee : map.get(key).getCalleeNames()) {builder.append("      "+callee+"\n");}
            builder.append("   caller(s):\n");
            for (FnNode callKey : map.keySet()) {
                if (map.get(callKey).getCalleeNames().contains(map.get(key).getFnIdName())) {
                    builder.append("      "+map.get(callKey).getFnIdName()+"\n");
                }
            }
            builder.append("\n");
        }

        int printCount = 0;
        for (FnNode key : map.keySet()) {
            boolean hasPrint = false;
            if (map.get(key).getCalleeNames().contains("print")) {hasPrint=true; printCount++;}
            if (hasPrint && printCount==1) {
                builder.append("function: print\n");
                builder.append("   return type: string\n");
                builder.append("   caller(s):\n");
                builder.append("      "+map.get(key).getFnIdName()+"\n");
            }
            else if (printCount>1) {
                builder.append("      "+map.get(key).getFnIdName()+"\n");
            }
        }
        return builder.toString();
    }

    class FunctionSymbol {

        private ArrayList<String> stringArray;
        private IdNode fnIdNode;
        private ArrayList<IdNode> paramNodes;
        private ArrayList<Lex> idTypes;
        private ArrayList<IdNode> idNodes;
        private ArrayList<IdNode> calleeNodes;

        public FunctionSymbol() {
            this.paramNodes = new ArrayList<>();
            this.idNodes = new ArrayList<>();
            this.idTypes = new ArrayList<>();
            this.paramNodes = new ArrayList<>();
            this.calleeNodes = new ArrayList<>();
        }
        
        private void setFnId(IdNode idNode) {this.fnIdNode = idNode;}
        private void addParamNode(IdNode paramNode) {paramNodes.add(paramNode);}
        private void addIdType(Lex type) {idTypes.add(type);}
        private void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        private void addCalleeNode(IdNode calleeNode) {calleeNodes.add(calleeNode);}
        protected ArrayList<String> getidNames () {
            stringArray = new ArrayList<>();
            for (IdNode id : idNodes) {stringArray.add(id.getName());}
            return stringArray;
        }
        protected ArrayList<String> getParamNames() {
            stringArray = new ArrayList<>();
            for (IdNode param : getParamNodes()) {stringArray.add(param.getName());}
            return stringArray;
        }
        protected ArrayList<String> getCalleeNames() {
            stringArray = new ArrayList<>();
            for (IdNode callee : getCalleeNodes()) {if (!stringArray.contains(callee.toString())) {stringArray.add(callee.getName());}}
            return stringArray;
        }
        protected String getFnIdName() {return this.fnIdNode.getName();}
        protected ArrayList<IdNode> getParamNodes() {return paramNodes;}
        protected ArrayList<Lex> getIdTypes() {return idTypes;}
        protected ArrayList<IdNode> getCalleeNodes() {return calleeNodes;}
    }
}