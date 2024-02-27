package src;
import java.util.ArrayList;

public class IntegerList {
    public ArrayList<Integer> intArray = new ArrayList<Integer>();

    public IntegerList(Integer ... integers) {
        Integer[] integerArray = integers;
        int n = integers.length;
        int n2 = 0;
        while (n2 < n) {
            Integer _int = integerArray[n2];
            intArray.add(_int);
            ++n2;
        }
    }

    public ArrayList<Integer> get() {
        return intArray;
    }

    public Boolean contains(Integer _int) {
        return intArray.contains(_int);
    }
}
