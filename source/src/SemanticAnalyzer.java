package src;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SemanticAnalyzer implements AstVisitor {

    private SymbolMap map;

    @Override
    public void visit(PrgrmNode prgrmNode) {
        map = new SymbolMap(prgrmNode);
        prgrmNode.accept(map); 
        for (Node n : map.getMap().keySet()) {
            System.out.println(n);
        }
    }

}

class SymbolMap implements AstVisitor {

    private ConcurrentHashMap<Node,ArrayList<Node>> symbolMap = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Node,ArrayList<Node>> getMap() {return symbolMap;}

    public SymbolMap(Node node) {
        node.accept(this);
    }

    @Override
    public void visit(PrgrmNode prgrmNode) {
        for (Node node : prgrmNode.getFunctions()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FnNode fnNode) {
        symbolMap.put(fnNode.getIdNode(), new ArrayList<>());
    }

}