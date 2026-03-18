package src;
import java.util.ArrayList;
import java.util.HashSet;

public class StackFrame {

    private String name;
        private int argSize, size=argSize, tmpSize, ins, ctrlLink, state;
        private HashSet<String> callers = new HashSet<>(), callees = new HashSet<>();
        private ArrayList<Node> params;

        public StackFrame() {}
        public void setName(String name) {this.name=name;}
        public String getName() {return name;}
        public void setCtrlLinkLoc(int ctrlLink) {this.ctrlLink=ctrlLink;}
        public void setStateLoc(int state) {this.state=state;}
        public int getCtrlLinkLoc() {return argSize;}
        public int getStateLoc() {return argSize+1;}
        public int addTmp() {tmpSize++; return this.size();}
        public int removeTmp() {int prevTmpSize=tmpSize; tmpSize--; return prevTmpSize;}
        public int getTmpSize() {return tmpSize;}
        public void setArgSize(int args) {argSize=args;}
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
    
}
