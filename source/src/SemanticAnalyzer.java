package src;
import java.util.HashMap;

public class SemanticAnalyzer implements AstVisitor {

    private HashMap<String,FnNode> allFunctions = new HashMap<>();
    private HashMap<String,StackFrame> stackFrameMap = new HashMap<>();
    private SymbolTable symbolTable;

    @Override
    public void visit(PrgrmNode prgrmNode) throws Analyzer {
        prgrmNode.accept(new VisitAllFunctions()); symbolTable = new SymbolTable(prgrmNode);
        prgrmNode.setSymbolTable(symbolTable);
    }

    private class VisitAllFunctions implements AstVisitor {

        @Override
        public void visit(PrgrmNode prgrmNode) throws Analyzer {
            for (Node node : prgrmNode.getFunctions()) {node.accept(this);}
            // Confirm existence of main.
            if (!allFunctions.containsKey("main")) {new Analyzer(Lex.NOMAIN,prgrmNode);}
            // Check number of arguments given to main is not less than the number of defined parameters.
            if ((Main.arguments.length-2)<allFunctions.get("main").getParamNodes().size()) {
                new Analyzer(Lex.MISSINGMAINARGS,allFunctions.get("main"));
            }
            // Check number of arguments given to main is not more than the number of defined parameters.
            if ((Main.arguments.length-2)>allFunctions.get("main").getParamNodes().size()) {
                new Analyzer(Lex.EXCESSMAINARGS,allFunctions.get("main"));
            }
            // Verify the arguments given to main match and are of the correct type.
            FnNode main = allFunctions.get("main");
            for (int i=0;i<main.getParamNodes().size();i++) {
                Node paramNode = main.getParamNodes().get(i);
                String arg = Main.arguments[i+2];
                if (paramNode.getSemanticType()==Lex.BOOLEAN && (!(arg.equals("true")||arg.equals("false")))) {
                    throw new Analyzer("Argument '"+arg+"' is not of type "+paramNode.getSemanticType()+"".toLowerCase());
                } else if (paramNode.getSemanticType()==Lex.INTEGER) {
                    for (int j=0;j<arg.length();j++) {
                        if (arg.charAt(j)!=45 && arg.charAt(j) < 48 || arg.charAt(j) > 57 ||
                            arg.length()==1 && arg.charAt(0)==45 ||
                            arg.length()>1 && arg.charAt(0)==48 ||
                            arg.charAt(0)==45 && arg.charAt(1)==48
                        ) {throw new Analyzer("Input '"+arg+"' is not a valid integer");}
                    }
                }
            }
        }

        @Override
        public void visit(FnNode fnNode) throws Analyzer {
            // Creation of stack frames.
            StackFrame frame = new StackFrame();
            frame.setName(fnNode.getName()); frame.setParams(fnNode.getParamNodes());
            frame.setArgSize(fnNode.getParamNodes().size()); stackFrameMap.put(fnNode.getName(),frame);
            // Confirm function has not already been declared.
            if (allFunctions.containsKey(fnNode.getName())) {new Analyzer(Lex.FNNAMECONFLICT,fnNode.getIdNode());}
            // Check for functions named after primitive function calls.
            if (fnNode.getName().equals("print")) {new Analyzer(Lex.PRIMITIVEFN,fnNode.getIdNode());}
            for (Node paramNode : fnNode.getParamNodes()) {
                paramNode.accept(this);
            } allFunctions.put(fnNode.getName(),fnNode);
        }

        @Override
        public void visit(ParamNode paramNode) throws Analyzer {
            // Check for parameters names after primitive function calls.
            if (paramNode.getLeft().getName().equals("print")) {
                new Analyzer(Lex.PRIMITIVEPARAM,paramNode);
            } paramNode.getRight().accept(this);
            // Add parameter and set semantic type.
            paramNode.setSemanticType(paramNode.getRight().getSemanticType());
        }

        @Override
        public void visit(TypeNode typeNode) {typeNode.setSemanticType(typeNode.getType());}
    }

    public class SymbolTable implements AstVisitor {

        private HashMap<String,ParamNode> allCurrentParams;
        private StackFrame frame = new StackFrame();

        public SymbolTable(Node node) throws Analyzer {node.accept(this);}
        
        public HashMap<String,StackFrame> getStackFrames() {return stackFrameMap;}
        public FnNode getFunction(String name) {return allFunctions.get(name);}
        public String toString() {
            String output = "";
            for (StackFrame frame : stackFrameMap.values()) {
                output+="Function: "+frame.getName()+"\n"+
                "Callees: "+frame.callees()+"\nCallers: "+frame.callers()+"\n\n";
            } return output.trim();
        }

        @Override
        public void visit(PrgrmNode prgrmNode) throws Analyzer {
            for (Node node : prgrmNode.getFunctions()) {
                allCurrentParams = new HashMap<>(); node.accept(this);
            }
            // Confirm that the function return type matches semantic type of body.
            for (FnNode fnNode : allFunctions.values()) {
                // Set semantic type of function to semantic type of it's body.
                fnNode.setSemanticType(fnNode.getBodyNodes().getLast().getSemanticType());
                if (fnNode.getSemanticType()!=fnNode.getReturnType().getSemanticType()) {
                    new Analyzer(Lex.RETURNTYPEERROR,fnNode.getBodyNodes().getLast());        
                }
            }
        }

        @Override
        public void visit(FnNode fnNode) throws Analyzer {
            frame = stackFrameMap.get(fnNode.getName());
            for (Node paramNode : fnNode.getParamNodes()) {paramNode.accept(this);} 
            for (Node bodyNode : fnNode.getBodyNodes()) {bodyNode.accept(this);}
        }

        @Override
        public void visit(ParamNode paramNode) throws Analyzer {
            if (!allCurrentParams.containsKey(paramNode.getName())) {
                allCurrentParams.put(paramNode.getName(),paramNode);
            } else {new Analyzer(Lex.PARAMNAMECONFLICT,paramNode);}
        }

        @Override
        public void visit(CallNode callNode) throws Analyzer {
            // Add callee to current frame.
            frame.addCallee(callNode.getName());
            // If not print, set the call node's semantic type to that of it's declared function return type.
            if (!callNode.getName().equals("print")) {
                // Check that the function has been declared.
                if (!allFunctions.containsKey(callNode.getName())) {new Analyzer(Lex.NOFNCALL,callNode);}
                // Add calling frame as caller to the callee.
                stackFrameMap.get(callNode.getName()).addCaller(frame.getName());
                callNode.setSemanticType(allFunctions.get(callNode.getName()).getReturnType().getSemanticType());
                // If a function call's semantic type is boolean make sure it is not made negative.
                if (callNode.getSemanticType()==Lex.BOOLEAN && callNode.getSign()==Lex.MINUS) {
                    new Analyzer(Lex.SIGNANDTYPEMISSMATCH,callNode);
                }
                // Verify the number of arguments in a function call matches the number of parameters in the function declaration.
                if (allFunctions.get(callNode.getName()).getParamNodes().size()>callNode.getArgs().size()) {
                    new Analyzer(Lex.TOOFEWARGS,callNode);
                }
                if (allFunctions.get(callNode.getName()).getParamNodes().size()<callNode.getArgs().size()) {
                    new Analyzer(Lex.TOOMANYARGS,callNode);
                } 
                for (int i=0;i<callNode.getArgs().size();i++) {
                    Node argNode = callNode.getArgs().get(i); argNode.accept(this);
                    // Make sure the function call arguments are not primitive.
                    if (argNode.getName().equals("print")) {
                        new Analyzer(Lex.PRIMITIVEARG,argNode);
                    }
                    // Check that function call arguments semantically match their parameter declarations.
                    if (allFunctions.get(callNode.getName()).getParamNodes().get(i).getSemanticType()!=
                        argNode.getSemanticType()) {new Analyzer(Lex.BADARGTYPE,argNode);
                    }
                }
            } else {
                for (Node argNode : callNode.getArgs()) {
                    argNode.accept(this);
                    // Make sure the function call arguments are not primitive.
                    if (argNode.getName().equals("print")) {
                        new Analyzer(Lex.PRIMITIVEARG,argNode);
                    }
                }
            }
        }

        @Override @SuppressWarnings("incomplete-switch") 
        public void visit(BinaryNode binNode) throws Analyzer {

            binNode.getLeft().accept(this);
            binNode.getRight().accept(this);

            if (binNode.getLeft().getName().equals("print")) {new Analyzer(Lex.PRIMITIVEBINARY,binNode.getLeft());}
            if (binNode.getRight().getName().equals("print")) {new Analyzer(Lex.PRIMITIVEBINARY,binNode.getRight());}

            Lex leftSemanticType = binNode.getLeft().getSemanticType();
            Lex rightSemanticType = binNode.getRight().getSemanticType();

            switch (binNode.nodeType()) {
                case PLUS,MINUS,DIVIDE,TIMES: {
                    if (leftSemanticType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                    if (rightSemanticType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                    binNode.setSemanticType(Lex.INTEGER); break;
                }
                case AND,OR: {
                    if (leftSemanticType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getLeft());}
                    if (rightSemanticType==Lex.INTEGER) {new Analyzer(Lex.BOOLOPERROR,binNode.getRight());}
                    binNode.setSemanticType(Lex.BOOLEAN); break;
                }
                case LESSTHAN: {
                    if (leftSemanticType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getLeft());}
                    if (rightSemanticType==Lex.BOOLEAN) {new Analyzer(Lex.INTOPERROR,binNode.getRight());}
                    binNode.setSemanticType(Lex.BOOLEAN); break;
                } 
                case EQUIVALENT: {
                    if (leftSemanticType!=rightSemanticType) {new Analyzer(Lex.DIFFOPERANDS,binNode);}
                    binNode.setSemanticType(Lex.BOOLEAN); break;
                }
            }
        }

        @Override
        public void visit(NotNode notNode) throws Analyzer {
            notNode.getNode().accept(this);
            // Check to see that primitive print is not being negated.
            if (notNode.getName().equals("print")) {new Analyzer(Lex.PRIMITIVEUNARY,notNode.getNode());}
            // Verify the node being negated is of semantic type boolean.
            if (notNode.getNode().getSemanticType()!=Lex.BOOLEAN) {new Analyzer(Lex.NOTOPERROR,notNode);}
            notNode.setSemanticType(notNode.getNode().getSemanticType());
        }

        @Override
        public void visit(IfNode ifNode) throws Analyzer {
            ifNode.getIf().accept(this);
            // Make sure if condition is of type boolean
            if (ifNode.getIf().getSemanticType()!=Lex.BOOLEAN) {new Analyzer(Lex.IFOPERROR,ifNode.getIf());}
            ifNode.getThen().accept(this); ifNode.getElse().accept(this);
            // Verify semantic types of both then and else clauses match.
            if (ifNode.getThen().getSemanticType()==ifNode.getElse().getSemanticType()) {
                ifNode.setSemanticType(ifNode.getThen().getSemanticType());
            } else {new Analyzer(Lex.DIFFCLAUSES,ifNode.getThen());}
        }

        @Override
        public void visit(ExpNode expNode) throws Analyzer {
            expNode.getNode().accept(this);
            // Verify expressions do not contain primitives.
            if (expNode.getName().equals("print")||expNode.getNode().nodeType()==Lex.IF) {
                new Analyzer(Lex.PRIMITIVEUNARY,expNode.getNode());
            } expNode.setSemanticType(expNode.getNode().getSemanticType());
            // If an expression's semantic type is boolean make sure it is not made negative.
            if (expNode.getSemanticType()==Lex.BOOLEAN && expNode.getSign()==Lex.MINUS) {
                new Analyzer(Lex.SIGNANDTYPEMISSMATCH,expNode);
            }
        }

        @Override
        public void visit(IdNode idNode) throws Analyzer {
            if (allCurrentParams.keySet().contains(idNode.getName())) {
                idNode.setSemanticType(allCurrentParams.get(idNode.getName()).getSemanticType());
            } else {new Analyzer(Lex.NOID,idNode);}
            // If an identifier's semantic type is boolean make sure it is not made negative.
            if (idNode.getSemanticType()==Lex.BOOLEAN && idNode.getSign()==Lex.MINUS) {
                new Analyzer(Lex.SIGNANDTYPEMISSMATCH,idNode);
            }
        }

        @Override
        public void visit(LitNode litNode) throws Analyzer {
            // If a literal's semantic type is boolean make sure it is not made negative.
            if (litNode.getSemanticType()==Lex.BOOLEAN && litNode.getSign()==Lex.MINUS) {
                new Analyzer(Lex.SIGNANDTYPEMISSMATCH,litNode);
            }
        }
    }
}