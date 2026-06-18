import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class PrefixSum {

    private PrefixSum() {
    }

    public static List<Integer> prefSum(int[] arr) {
        return prefixSums(arr);
    }

    public static List<Integer> prefixSums(int[] values) {
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
        int[] arr = {10, 20, 10, 5, 15};
        List<Integer> prefixSum = prefSum(arr);
        for (int i : prefixSum) {
            System.out.print(i + " ");
        }
    }
}
