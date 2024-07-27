package src;
import java.io.IOException;
import java.util.ArrayList;

abstract class State {
    protected StringBuffer buff = new StringBuffer();

    abstract Token process(ArrayList<State> stateList, ArrayList<Integer> charList, Integer next) throws Analyzer, IOException;

    protected boolean isSkippable(Integer val) {
        int[] skippable = new int[]{9,32,13,10};
        for (int i : skippable) {
            if (i==val) {return true;}
        }
        return false;
    }

    protected boolean isEof(Integer value) throws IOException {
        return value == -1 || value == 65535;
    }

    protected boolean isNewLine(Integer value) throws IOException {
        return value == 10;
    }

    protected boolean isAlpha(Integer val) {
        return val >= 65 && val <= 90 || val >= 97 && val <= 122;
    }

    protected boolean isNumeric(Integer val) {
        return val >= 48 && val <= 57;
    }

    protected boolean compareList(int[] intLst, ArrayList<Integer>intArray) {        
        if (intLst.length==intArray.size()) {
            for (int i : intLst) {
                if (!intArray.contains(i)) {
                    return false;
                }
            } return true;
        } else return false;
    }

    protected boolean charInList(int _char, Lexicon[] lexLst) {        
        for (Lexicon lex : lexLst) {
            if (lex.value()==_char) {return true;}
        } return false;
    }
}
