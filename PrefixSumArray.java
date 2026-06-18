import java.util.ArrayList;
import java.util.List;

public final class PrefixSumArray {

    private PrefixSumArray() {
        // Utility class.
    }

    public static List<Integer> prefixSums(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        List<Integer> prefixSums = new ArrayList<>(values.length);
        int runningTotal = 0;

        for (int value : values) {
            runningTotal += value;
            prefixSums.add(runningTotal);
        }

        return prefixSums;
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        List<Integer> prefixSums = prefixSums(values);

        for (int i = 0; i < prefixSums.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(prefixSums.get(i));
        }
        System.out.println();
    }
}
