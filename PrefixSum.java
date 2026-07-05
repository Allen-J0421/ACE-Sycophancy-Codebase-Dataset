import java.util.Arrays;

public class PrefixSum implements RangeSumQuery {
    private final int[] prefix;

    public static PrefixSum of(int[] arr) {
        return new PrefixSum(arr, PrefixCalculator.iterative());
    }

    public static PrefixSum of(int[] arr, PrefixCalculator calculator) {
        return new PrefixSum(arr, calculator);
    }

    private PrefixSum(int[] arr, PrefixCalculator calculator) {
        if (arr == null) throw new IllegalArgumentException("Input array must not be null");
        if (arr.length == 0) throw new IllegalArgumentException("Input array must not be empty");
        if (calculator == null) throw new IllegalArgumentException("Calculator must not be null");
        prefix = calculator.calculate(arr);
    }

    @Override
    public int rangeSum(int l, int r) {
        if (l < 0 || r >= prefix.length) throw new IndexOutOfBoundsException(
                "Range [" + l + ", " + r + "] out of bounds for length " + prefix.length);
        if (l > r) throw new IllegalArgumentException("l (" + l + ") must not exceed r (" + r + ")");
        return prefix[r] - (l > 0 ? prefix[l - 1] : 0);
    }

    @Override
    public int[] toArray() {
        return Arrays.copyOf(prefix, prefix.length);
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};

        RangeSumQuery iterative = PrefixSum.of(arr);
        RangeSumQuery recursive = PrefixSum.of(arr, PrefixCalculator.recursive());
        RangeSumQuery parallel  = PrefixSum.of(arr, PrefixCalculator.parallel());

        System.out.println(Arrays.toString(iterative.toArray())); // [10, 30, 40, 45, 60]
        System.out.println(Arrays.toString(recursive.toArray()));  // [10, 30, 40, 45, 60]
        System.out.println(Arrays.toString(parallel.toArray()));   // [10, 30, 40, 45, 60]
        System.out.println(iterative.rangeSum(1, 3));              // 35
    }
}
