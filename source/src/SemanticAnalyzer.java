package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SemanticAnalyzer implements AstVisitor {

    private boolean hasMain;
    private FnNode currentFnNode;
    private String currentFnName;
    private IdNode currentIdNode;

    protected SymbolTable table;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        try {
            table = new SymbolTable(prgrmNode);
            for (Node fnNode : prgrmNode.getFunctions()) {
                fnNode.accept(this);
            }
            if (!hasMain) {new Analyzer(Lex.NOMAIN,prgrmNode);}
            prgrmNode.setSymbolTable(table);
        } catch (Analyzer err) {
            System.err.println(err.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        currentFnNode = fnNode;
        currentFnName = fnNode.getName();
        fnNode.getIdNode().accept(this);
        if (!(table.getCallNames().contains(currentIdNode.getName()) || currentIdNode.getName().equals("main")) && 
            !currentIdNode.getName().equals("print")) {
            new Analyzer(Lex.UNUSEDFN,currentIdNode);
        }
        if (currentIdNode.getName().equals("print")) {
            new Analyzer(Lex.PRIMITIVEFN, currentIdNode);
        }
        if (currentIdNode.getName().equals("main")) {hasMain=true;}
        for (Node paramNode : fnNode.getParamNodes()) {
            paramNode.accept(this);
        }
        for (Node bodyNode : fnNode.getBodyNodes()) {
            bodyNode.accept(this);
            // Exception for primitive function calls to print, continue until non-print node.
            if (bodyNode.getSemanticType()==Lex.PRINTEXP) {continue;}
            if (currentFnNode.getReturnType().getSemanticType()!=bodyNode.getSemanticType()) {
                new Analyzer(Lex.RETURNTYPEERROR,bodyNode);
            }
            fnNode.setSemanticType(bodyNode.getSemanticType());
        }
    }

    @Override @SuppressWarnings("incomplete-switch")
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
        // First check to see of the funciton call is a primitive print.
        if (callNode.getName().equals("print")) {
            callNode.getId().accept(this);
            callNode.setSemanticType(Lex.PRINTEXP);
            for (Node arg : callNode.getArgs()) {
                arg.accept(this);
                // Verify existence of ids in function call arguments.
                if (arg instanceof IdNode && (!table.hasId(currentFnNode, arg))) {
                    new Analyzer(Lex.NOID,arg);
                }
            }
        } else {
            callNode.getId().accept(this);
            // If a function does not exist for this call throw an error.
            if (table.get(currentIdNode.getName())==null) {new Analyzer(Lex.NOFNCALL,callNode);}
            FnNode refFunction = table.get(currentIdNode.getName()).getFnNode();
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
                if (arg instanceof IdNode && (!table.hasId(currentFnNode, arg))) {
                    new Analyzer(Lex.NOID,arg);
                }
            }
            // Verify type correctness for the arguments of the call to the function it is referncing.
            for (int i=0; i<=callNode.getArgs().size()-1; i++) {
                if (callNode.getArgs().get(i).getSemanticType()!=table.get(refFunction.getName()).getIdTypes().get(i)) {
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
        ifNode.getIf().accept(this);
        if (ifNode.getIf().getSemanticType()!=Lex.BOOLEAN) {
            new Analyzer(Lex.IFOPERROR,ifNode.getIf());
        }
        ifNode.getThen().accept(this);
        if (ifNode.getThen() instanceof CallNode && ifNode.getThen().getSemanticType()==null) {
            new Analyzer(Lex.NOFNCALL,ifNode.getThen());
        }
        else if (ifNode.getThen() instanceof IdNode && ifNode.getThen().getSemanticType()==null) {
            new Analyzer(Lex.NOID,ifNode.getThen());
        }
        ifNode.getElse().accept(this);
        if (ifNode.getElse() instanceof CallNode && ifNode.getElse().getSemanticType()==null) {
            new Analyzer(Lex.NOFNCALL,ifNode.getElse());
        }
        else if (ifNode.getElse() instanceof IdNode && ifNode.getElse().getSemanticType()==null) {
            new Analyzer(Lex.NOID,ifNode.getElse());
        }
        if (ifNode.getThen().getSemanticType()==ifNode.getElse().getSemanticType()) {
            ifNode.setSemanticType(ifNode.getThen().getSemanticType());
        } else {new Analyzer(Lex.DIFFCLAUSES,ifNode.getThen());} 
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
        if (currentIdNode.getName().equals("print")) {
            new Analyzer(Lex.PRIMITIVEPARAM, currentIdNode);
        }
    }

    @Override
    public void visit(IdNode idNode) throws Analyzer {
        // If the id node matches a parameter in the parameter list, set it's type.
        if (idNode.getSemanticType()==null) {
            int idIndex = table.get(currentFnName).getParamIdStrs().indexOf(idNode.getName());
            if (idIndex!=-1) {
                idNode.setSemanticType(table.get(currentFnName).getParamNodes().get(idIndex).getSemanticType());
            }
        }
        currentIdNode = idNode;
        if (idNode.getSemanticType()==Lex.BOOLEAN && idNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,idNode);
        }
    }

    @Override
    public void visit(LitNode litNode) throws Analyzer {
        if (litNode.getSemanticType()==Lex.BOOLEAN && litNode.getSign()==Lex.MINUS) {
            new Analyzer(Lex.SIGNANDTYPEMISSMATCH,litNode);
        }
        if (!currentIdNode.getName().equals("print")) {
            currentFnNode.getIdNode().accept(this);
            if (!table.get(currentIdNode.getName()).hasStaticData(litNode)) {
                table.get(currentIdNode.getName()).addStaticData(litNode.getValue());
            }
        }
        currentFnNode.getIdNode().accept(this);
    }

    public String toString() {return table.toString().trim();}
}

class SymbolTable implements AstVisitor {

    private HashMap<String,FunctionSymbol> map = new HashMap<>();
    private LinkedHashMap<Integer,Set<String>> staticData = new LinkedHashMap<>();
    private FunctionSymbol fnSymbol;
    private TypeNode currentTypeNode;
    private IdNode currentIdNode;
    private StringBuilder builder = new StringBuilder();
    private ArrayList<String> callNames = new ArrayList<>();
    private ArrayList<String> fnNameArray = new ArrayList<>();
    private Boolean bodyId;
    private void addStaticData(Map<Integer, Set<String>> map, int key, String name) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(name);
    }

    public SymbolTable(Node node) throws Analyzer {node.accept(this);}
    protected ArrayList<String> getFnNames() {return fnNameArray;}
    protected ArrayList<String> getCallNames() {return callNames;}
    protected FunctionSymbol get(String key) {return map.get(key);}
    protected HashMap<Integer,Set<String>> getStaticData() {return staticData;}
    protected Boolean hasId(FnNode fnNode, Node idNode) {
        if (map.get(fnNode.getName()).getParamIdStrs().contains(idNode.getName())) {
            return true;
        } return false;
    }

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) throws Analyzer {
        bodyId=false; fnSymbol = new FunctionSymbol(fnNode); fnNode.getIdNode().accept(this);
        fnNameArray.add(currentIdNode.getName());
        if (map.keySet().contains(currentIdNode.getName())) {new Analyzer(Lex.FNNAMECONFLICT,fnNode.getIdNode());}
        map.put(currentIdNode.getName(), fnSymbol);
        for (Node paramNode : fnNode.getParamNodes()) {paramNode.accept(this);}
        for (Node bodyNode : fnNode.getBodyNodes()) {bodyId=true; bodyNode.accept(this);}
        for (IdNode param : fnSymbol.getParamNodes()) {
            if (!fnSymbol.getidNames().contains(param.getName())) {new Analyzer(Lex.UNUSEDPARAM,param);}
        }
    }

    @Override
    public void visit(ParamNode paramNode) throws Analyzer {
        paramNode.getLeft().accept(this);
        paramNode.getRight().accept(this);
        if (fnSymbol.getParamIdStrs().contains(currentIdNode.getName())) {
            new Analyzer(Lex.PARAMNAMECONFLICT,currentIdNode);
        }
        currentIdNode.setSemanticType(currentTypeNode.getType());
        fnSymbol.addParamNode(currentIdNode);
        fnSymbol.addIdType(currentTypeNode.getType());
    }

    @Override
    public void visit(CallNode callNode) throws Analyzer {
        callNode.getId().accept(this);
        fnSymbol.addCalleeNode(callNode);
        if (!fnSymbol.getCalleeNames().contains(currentIdNode.getName())) {fnSymbol.addCalleeNode(callNode);}
        if (!callNames.contains(currentIdNode.getName())) {callNames.add(currentIdNode.getName());}
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
        if (bodyId) {fnSymbol.addIdNode(idNode);}
        currentIdNode = idNode;
    }

    @Override
    public void visit(TypeNode typeNode) {
        currentTypeNode = typeNode;
    }

    @Override
    public void visit(LitNode litNode) {
        // The fourth register is reserved for value zero and will always be accessible.
        if (litNode.getValue()!=0) {
            addStaticData(staticData,litNode.getValue(),fnSymbol.getFnNode().getName());
        }
    }

    public String toString() {
        
        for (String key : fnNameArray) {
            builder.append("function: "+key+"\n");
            builder.append("   return type: "+get(key).getFnNode().getSemanticType().toString().toLowerCase()+"\n");
            builder.append("   parameters:\n");
            for (String param : map.get(key).getParamIdStrs()) {builder.append("      "+param+"\n");}
            builder.append("   callee(s):\n");
            for (String callee : map.get(key).getCalleeNames()) {builder.append("      "+callee+"\n");}
            builder.append("   caller(s):\n");
            for (String callKey : map.keySet()) {
                if (map.get(callKey).getCalleeNames().contains(key)) {
                    builder.append("      "+callKey+"\n");
                }
            }
            builder.append("\n");
        }

        int printCount = 0;
        for (String key : map.keySet()) {
            boolean hasPrint = false;
            if (map.get(key).getCalleeNames().contains("print")) {hasPrint=true; printCount++;}
            if (hasPrint && printCount==1) {
                builder.append("primitive function: print\n");
                builder.append("   return type:\n");
                builder.append("   caller(s):\n");
                builder.append("      "+key+"\n");
            }
            else if (printCount>1) {
                builder.append("      "+key+"\n");
            }
        }
        return builder.toString();
    }

    class FunctionSymbol {

        private FnNode fnNode;
        private ArrayList<String> stringArray;
        private ArrayList<IdNode> paramNodes;
        private ArrayList<Lex> idTypes;
        private ArrayList<IdNode> idNodes;
        private ArrayList<CallNode> calleeNodes;
        private ArrayList<Integer> staticData;

        public FunctionSymbol(FnNode fnNode) {
            this.fnNode = fnNode;
            this.paramNodes = new ArrayList<>();
            this.idNodes = new ArrayList<>();
            this.idTypes = new ArrayList<>();
            this.paramNodes = new ArrayList<>();
            this.calleeNodes = new ArrayList<>();
            this.staticData = new ArrayList<>();
        }
        
        private void addParamNode(IdNode paramNode) {paramNodes.add(paramNode);}
        private void addIdType(Lex type) {idTypes.add(type);}
        private void addIdNode(IdNode idNode) {idNodes.add(idNode);}
        private void addCalleeNode(CallNode calleeNode) {calleeNodes.add(calleeNode);}
        protected ArrayList<String> getidNames () {
            stringArray = new ArrayList<>();
            for (IdNode id : idNodes) {stringArray.add(id.getName());}
            return stringArray;
        }
        protected ArrayList<String> getParamIdStrs() {
            stringArray = new ArrayList<>();
            for (IdNode param : getParamNodes()) {stringArray.add(param.getName());}
            return stringArray;
        }
        protected ArrayList<String> getCalleeNames() {
            stringArray = new ArrayList<>();
            for (CallNode callee : getCalleeNodes()) {
                if (!stringArray.contains(callee.getName())) {
                    stringArray.add(callee.getName());
                }
            }
            return stringArray;
        }
        protected Boolean hasStaticData(LitNode litNode) {
            if (staticData.contains(litNode.getValue())) {return true;} 
            return false;
        }

        protected void addStaticData(Integer i) {staticData.add(i);}
        protected FnNode getFnNode() {return this.fnNode;}
        protected ArrayList<Integer> getStaticData() {return staticData;}
        protected ArrayList<IdNode> getParamNodes() {return paramNodes;}
        protected ArrayList<Lex> getIdTypes() {return idTypes;}
        protected ArrayList<CallNode> getCalleeNodes() {return calleeNodes;}
    }
}