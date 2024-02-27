package src;
import java.io.IOException;
import java.util.ArrayList;

abstract class State {
    protected StringBuffer buff = new StringBuffer();

    abstract Token process(ArrayList<State> var1, ArrayList<Integer> var2, Integer var3) throws Analyzer, IOException;

    protected boolean isSkippable(Integer val) {
        ArrayList<Integer> skippable = new IntegerList(new Integer[]{9, 32, 13, 10}).get();
        return skippable.contains(val);
    }

    protected boolean isEof(Integer value) throws IOException {
        return value == -1 || value == 65535;
    }

    protected boolean isNewLine(Integer value) throws IOException {
        return value == 10;
    }

    protected boolean isComment(Integer value) throws IOException {
        return value == -10;
    }

    protected boolean isAlpha(Integer val) {
        return val >= 65 && val <= 90 || val >= 97 && val <= 122;
    }

    protected boolean isNumeric(Integer val) {
        return val >= 48 && val <= 57;
    }

    protected boolean equalsAll(Integer currentChar, IntegerList list) {
        return list.contains(currentChar) != false;
    }
}
