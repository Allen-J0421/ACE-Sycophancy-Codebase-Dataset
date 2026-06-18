import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PrefixSum {

    private PrefixSum() {
    }

    public static List<Integer> build(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

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
        List<Integer> prefixSums = build(values);

        for (int prefixSum : prefixSums) {
            System.out.print(prefixSum + " ");
        }
    }
}
