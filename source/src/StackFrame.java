package src;
import java.util.ArrayList;
import java.util.HashSet;

public class StackFrame {

    private String name;
    private int argSize, size=argSize+1, tmpSize, ins;
    private HashSet<String> callers = new HashSet<>(), callees = new HashSet<>();
    private ArrayList<Node> params;

    public StackFrame() {}

    public void setName(String name) {this.name=name;}
    public String getName() {return name;}

    public int getStateLoc() {return size()-1;}

    public void addTmp() {tmpSize++;}
    public void removeTmp() {tmpSize--;}
    public int getTmpSize() {return tmpSize;}

    public void setArgSize(int args) {argSize=args;}
    public int getArgSize() {return argSize;}

    public void setIns(Integer val) {ins=val;}
    public int getIns() {return ins;}

    public int size() {return size+argSize+tmpSize;}

    public void addCaller(String caller) {callers.add(caller);}
    public void addCallee(String callee) {callees.add(callee);}
    public HashSet<String> callers() {return callers;}
    public HashSet<String> callees() {return callees;}
    
    public void setParams(ArrayList<Node> params) {this.params=params;}
    public int getParamIndex(String idName) {
        for (int i=0;i<params.size();i++) {
            if (params.get(i).getName().equals(idName)) {
                return i;
            }
        } return -1;
    }

    public StackFrame clone(StackFrame frame) {
        this.argSize=frame.argSize;
        this.size=frame.size;
        this.tmpSize=frame.tmpSize;
        this.name=frame.name;
        this.ins=frame.ins;
        this.callees=frame.callees;
        this.callers=frame.callers;
        this.params=frame.params;
        return this;
    }
}
