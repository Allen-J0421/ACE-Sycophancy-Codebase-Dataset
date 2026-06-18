import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PrefixSum {

    private PrefixSum() {
    }

    public static List<Integer> prefixSums(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

        List<Integer> prefixSums = new ArrayList<>(values.length);
        int runningTotal = 0;

        for (int value : values) {
            runningTotal += value;
            prefixSums.add(runningTotal);
        }

        return List.copyOf(prefixSums);
    }

    public static void main(String[] args) {
        PrefixSumDemo.main(args);
    }
}
